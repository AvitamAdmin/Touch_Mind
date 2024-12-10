package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.dto.CategoryDto;
import com.touchmind.core.mongo.dto.CategoryWsDto;
import com.touchmind.core.mongo.model.Category;
import com.touchmind.core.mongo.repository.CategoryRepository;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    public static final String ADMIN_CATEGORY = "/admin/category";
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BaseService baseService;

    @Override
    public CategoryWsDto handleEdit(CategoryWsDto request) {
        CategoryWsDto categoryWsDto = new CategoryWsDto();
        //TODO why it is declared and not used ?
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User principalObject = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        Category requestData = null;
        List<CategoryDto> categories = request.getCategories();
        List<Category> categoryList = new ArrayList<>();
        for (CategoryDto category : categories) {
            if (category.getRecordId() != null) {
                requestData = categoryRepository.findByRecordId(category.getRecordId());
                modelMapper.map(category, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.CATEGORY, category.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(category, Category.class);
            }
            baseService.populateCommonData(requestData);
            categoryRepository.save(requestData);
            if (category.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            categoryRepository.save(requestData);
            categoryList.add(requestData);
            categoryWsDto.setBaseUrl(ADMIN_CATEGORY);
            categoryWsDto.setMessage("Category was updated Successfully!!");
        }
        categoryWsDto.setCategories(modelMapper.map(categoryList, List.class));
        return categoryWsDto;
    }
}
