package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.CategoryDto;
import com.touchMind.core.mongo.dto.CategoryWsDto;
import com.touchMind.core.mongo.model.Category;
import com.touchMind.core.mongo.repository.CategoryRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CategoryService;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
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
            if (category.isAdd() && baseService.validateIdentifier(EntityConstants.CATEGORY, category.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            requestData = categoryRepository.findByIdentifier(category.getIdentifier());
            if (requestData != null) {
                modelMapper.map(category, requestData);
            } else {
                requestData = modelMapper.map(category, Category.class);
            }
            baseService.populateCommonData(requestData);
            categoryRepository.save(requestData);
            categoryList.add(requestData);
            categoryWsDto.setBaseUrl(ADMIN_CATEGORY);
            categoryWsDto.setMessage("Category updated Successfully!!");
        }
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        categoryWsDto.setCategories(modelMapper.map(categoryList, listType));
        return categoryWsDto;
    }
}
