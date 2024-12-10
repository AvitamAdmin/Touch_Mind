package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.dto.UserDto;
import com.touchmind.core.mongo.dto.UserWsDto;
import com.touchmind.core.mongo.model.Role;
import com.touchmind.core.mongo.model.Subsidiary;
import com.touchmind.core.mongo.model.User;
import com.touchmind.core.mongo.model.VerificationToken;
import com.touchmind.core.mongo.repository.RoleRepository;
import com.touchmind.core.mongo.repository.SubsidiaryRepository;
import com.touchmind.core.mongo.repository.UserRepository;
import com.touchmind.core.mongo.repository.VerificationTokenRepository;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.CoreService;
import com.touchmind.core.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    public static final String TOKEN_INVALID = "invalidToken";
    public static final String TOKEN_EXPIRED = "expired";
    public static final String TOKEN_VALID = "valid";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private SubsidiaryRepository subsidiaryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BaseService baseService;

    @Autowired
    private CoreService coreService;

    @Override
    public void save(UserWsDto userWsDto) {
        for (UserDto requestUser : userWsDto.getUsers()) {
            User user = null;
            //String userName = coreService.getCurrentUser().getUsername();
            if (requestUser.getRecordId() != null) {
                user = userRepository.findByRecordId(requestUser.getRecordId());
                if (StringUtils.isEmpty(requestUser.getPassword())) {
                    requestUser.setPassword(user.getPassword());
                    requestUser.setPasswordConfirm(user.getPassword());
                }
                modelMapper.map(requestUser, user);
            } else {
//                if (baseService.validateIdentifier(EntityConstants.USER, requestUser.getIdentifier()) != null) {
//                    userWsDto.setSuccess(false);
//                    userWsDto.setMessage("Identifier already present");
//                }
                user = modelMapper.map(requestUser, User.class);
                user.setStatus(true);
                //user.setCreator(userName);
                user.setCreationTime(new Date());
            }
            if (StringUtils.isNotEmpty(requestUser.getPassword())) {
                user.setPassword(bCryptPasswordEncoder.encode(requestUser.getPassword()));
                user.setPasswordConfirm(bCryptPasswordEncoder.encode(requestUser.getPasswordConfirm()));
            }
            Set<String> roles = requestUser.getRoles();
            if (CollectionUtils.isNotEmpty(roles)) {
                Set<Role> roleList = new HashSet<>();
                for (String role : roles) {
                    roleList.add(roleRepository.findByRecordId(role));
                }
                user.setRoles(roleList);
            }
            if (CollectionUtils.isNotEmpty(requestUser.getSubsidiaries())) {
                Set<Subsidiary> subsidiaries = new HashSet<>();
                for (String subsidiary : requestUser.getSubsidiaries()) {
                    subsidiaries.add(subsidiaryRepository.findByRecordId(subsidiary));
                }
                user.setSubsidiaries(subsidiaries);
            }
            //user.setModifiedBy(userName);
            user.setLastModified(new Date());
            userRepository.save(user);
            if (user.getRecordId() == null) {
                user.setRecordId(String.valueOf(user.getId().getTimestamp()));
                userRepository.save(user);
            }
        }
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken();
        myToken.setToken(token);
        myToken.setUser(user);
        tokenRepository.save(myToken);
    }

    @Override
    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    @Override
    public void saveRegisteredUser(User user) {
        userRepository.save(user);
    }

    @Override
    public String validateVerificationToken(String token) {
        final VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            return TOKEN_INVALID;
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate()
                .getTime() - cal.getTime()
                .getTime()) <= 0) {
            tokenRepository.delete(verificationToken);
            return TOKEN_EXPIRED;
        }

        //user.setStatus(true);
        // tokenRepository.delete(verificationToken);
        userRepository.save(user);
        return TOKEN_VALID;
    }

    @Override
    public User getUser(final String verificationToken) {
        final VerificationToken token = tokenRepository.findByToken(verificationToken);
        if (token != null) {
            return token.getUser();
        }
        return null;
    }

    @Override
    public boolean isAdminRole() {
        Set<Role> roles = getCurrentUser().getRoles();
        if (CollectionUtils.isNotEmpty(roles)) {
            for (Role role : roles) {
                if ("ROLE_ADMIN".equals(role.getIdentifier())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User principalObject = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        return userRepository.findByUsername(principalObject.getUsername());
    }

    public boolean updateResetPasswordToken(String token, String email) {
        User user = userRepository.findByUsername(email);
        if (user != null) {
            user.setResetPasswordToken(token);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public User getByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    public void updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        user.setResetPasswordToken(null);
        userRepository.save(user);
    }

    @Override
    public boolean updateOtp(String token, String email) {
        User user = userRepository.findByUsername(email);
        if (user != null) {
            user.setOtp(token);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void populateCommonFields(UserWsDto userWsDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User principalObject = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        User currentUser = userRepository.findByUsername(principalObject.getUsername());
        Set<Role> roles = currentUser.getRoles();
        if (CollectionUtils.isNotEmpty(roles)) {
            userWsDto.setAdmin(roles.stream().filter(role -> role.getIdentifier().equalsIgnoreCase("ROLE_ADMIN")).findAny().isPresent());
        }
        userWsDto.setUserRoles(modelMapper.map(roleRepository.findAll(), List.class));
        userWsDto.setLocales(Locale.getAvailableLocales());
    }
}
