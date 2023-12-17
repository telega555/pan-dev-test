package com.pandev.service;

import com.pandev.entity.CategoryEntity;

import java.util.Optional;

public interface CategoryService {
    String addCategory(String name, Long parentId);
    String removeCategoryByName(String name);
    String viewTreeCategory();
    Optional<CategoryEntity> findByName(String name);
}
