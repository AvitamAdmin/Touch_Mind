package com.touchmind.web.controllers;

import com.touchmind.core.event.OnRegistrationCompleteEvent;
import com.touchmind.core.mongo.dto.UserDto;
import com.touchmind.core.mongo.dto.UserWsDto;
import com.touchmind.core.mongo.model.User;
import com.touchmind.core.mongo.repository.UserRepository;
import com.touchmind.core.service.UserService;
import com.touchmind.mail.service.EMail;
import com.touchmind.mail.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springdoc.core.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
@CrossOrigin
public class SecurityController extends BaseController {

    @Autowired
    ApplicationEventPublisher eventPublisher;
    @Autowired
    private UserService userService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private MailService mailService;

    @Autowired
    private Environment env;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/forgotpassword")
    @ResponseBody
    public String showForgotPasswordForm() {
        return "security/forgotpassword";
    }

    @PostMapping("/forgotpassword")
    @ResponseBody
    public UserWsDto processForgotPassword(@RequestBody UserDto userDto) {
        UserWsDto userWsDto = new UserWsDto();
        String email = userDto.getUsername();
        String token = UUID.randomUUID().toString();

        try {
            boolean success = userService.updateResetPasswordToken(token, email);
            if (success) {
                String appUrl = env.getProperty("server.url");
                String uiUrl = env.getProperty("server.ui.url") + "/login/updatepassword?token=" + token;

                String subject = "Here's the link to reset your password";

                String content = "<p>Hello,</p>"
                        + "<p>You have requested to reset your password.</p>"
                        + "<p>Click the link below to change your password:</p>"
                        + "<p><a href=\"" + uiUrl + "\">Change my password</a></p>"
                        + "<br>"
                        + "<p>Ignore this email if you do remember your password, "
                        + "or you have not made the request.</p>";

                sendEmail(email, subject, content);
                userWsDto.setMessage("We have sent a reset password link to your email. Please check.");
                userWsDto.setSuccess(true);
            } else {
                userWsDto.setMessage("User Not Registered. Please enter valid email id");
                userWsDto.setSuccess(false);
            }
            return userWsDto;

        } catch (UnsupportedEncodingException | MessagingException e) {
            userWsDto.setMessage("Error while sending email" + e.getMessage());
            userWsDto.setSuccess(false);
            return userWsDto;
        }
    }

    @GetMapping("/otplogin")
    @ResponseBody
    public String otpLogin(@RequestBody UserDto userDto) {

        try {
            String email = userDto.getUsername();
            String token = new DecimalFormat("000000").format(new Random().nextInt(999999));
            boolean success = userService.updateOtp(token, email);
            if (success) {
                String subject = "Here's the otp for your login";
                String content = "<p>Please find the otp below "
                        + "<br>" + token;
                sendEmail(email, subject, content);
            } else {
                return "Please enter valid email id";
            }

        } catch (UnsupportedEncodingException | MessagingException e) {
            return "Error while sending email";
        }
        return "Otp sent successfully";
    }

    @PostMapping("/otplogin")
    @ResponseBody
    public String submit(@RequestBody UserDto userDto, HttpServletRequest request) {
        String email = userDto.getUsername();
        String password = userDto.getPassword();
        String otp = userDto.getOtp();
        User user = userService.findByUsername(email);
        if (otp.equals(user.getOtp())) {
          //  securityService.autoLogin(user.getUsername(), password, request);
            return "Success";
        } else {
            return "Invalid OTP. Please try again";
        }
    }

    public void sendEmail(String recipientEmail, String subject, String content)
            throws MessagingException, UnsupportedEncodingException {
        EMail eMail = new EMail();

        //eMail.setFrom("healthcheck@cheil.com");
        eMail.setTo(recipientEmail);

        eMail.setSubject(subject);
        eMail.setContent(content);
        mailService.sendEmail(eMail);
    }

    @GetMapping("/resetpassword")
    @ResponseBody
    public UserDto showResetPasswordForm(@RequestBody UserDto userDto) {
        User user = userService.getByResetPasswordToken(userDto.getResetPasswordToken());
        if (user != null) {
            return modelMapper.map(user, UserDto.class);
        }
        return new UserDto();
    }

    @PostMapping("/resetpassword")
    @ResponseBody
    public UserWsDto processResetPassword(@RequestBody UserDto userDto) {
        String token = userDto.getResetPasswordToken();
        String password = userDto.getPassword();
        UserWsDto userWsDto = new UserWsDto();

        User user = userService.getByResetPasswordToken(token);

        if (user == null) {
            userWsDto.setMessage("Invalid Token");
            userWsDto.setSuccess(false);
            return userWsDto;
        } else {
            userService.updatePassword(user, password);
            userWsDto.setSuccess(true);
            userWsDto.setMessage("You have successfully changed your password.");
        }
        return userWsDto;
    }

    //@GetMapping("")
    public String viewHomePage() {
        return "index";
    }

    @GetMapping("/register")
    @ResponseBody
    public UserWsDto showRegistrationForm() {
        UserWsDto userWsDto = new UserWsDto();
        userService.populateCommonFields(userWsDto);
        userWsDto.setRedirectUrl("/register");
        return userWsDto;
    }

    @PostMapping("/register")
    @ResponseBody
    public UserWsDto processRegister(@RequestBody UserWsDto userWsDto) {
        userService.save(userWsDto);
        userWsDto.setMessage("Registration Successful");
        String appUrl = env.getProperty("server.ui.url");
        User user = userRepository.findByUsername(userWsDto.getUsers().get(0).getUsername());
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, appUrl, "New user Registration", "New user " + user.getUsername() + " as registered, Kindly approve the same by clicking the link below", "hybris.sup@cheil.com", "1"));
        userWsDto.setMessage("You have signed up successfully! We will notify once account is approved");
        return userWsDto;
    }

    @GetMapping("/login")
    @ResponseBody
    public String login(Model model, String error, String logout, String quota) {
        model.addAttribute("otpEnabled", env.getProperty("otp.enabled", "false"));
        if (error != null) {
            model.addAttribute("message", "Your username and password is invalid.");
        }
        if (quota != null) {
            model.addAttribute("message", "Allotted Quota Exceeded");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        return "login";
    }

    @GetMapping("/registrationConfirm")
    @ResponseBody
    public String confirmRegistration(@RequestBody UserDto userDto) throws UnsupportedEncodingException {
        final String result = userService.validateVerificationToken(userDto.getResetPasswordToken());
        String appUrl = env.getProperty("server.ui.url");
        String level = userDto.getLevel();
        if (result.equals("valid")) {
            final User user = userService.getUser(userDto.getResetPasswordToken());
            if (level.equals("1")) {
                eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, appUrl, "New user Registration", "New user " + user.getUsername() + " has been approved by admin, Kindly approve by clicking the link below", user.getReferredBy(), "2"));
                return "User Approved";
            } else if (level.equals("2")) {
                eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, appUrl, "Registration Successful", "Registration successful, Kindly click link below to verify your account", user.getUsername(), "3"));
                return "User Approved";
            } else if (level.equals("3")) {
                user.setStatus(true);
                userRepository.save(user);
                return "You have signed up successfully!";
            }
        }
        return "Failed";
    }

    @GetMapping("/profile")
    @ResponseBody
    public UserWsDto profile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User principalObject = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        User user = userRepository.findByUsername(principalObject.getUsername());
        UserWsDto userWsDto = new UserWsDto();
        userService.populateCommonFields(userWsDto);
        userWsDto.setUsers(List.of(modelMapper.map(user, UserDto.class)));
        return userWsDto;
    }
}
