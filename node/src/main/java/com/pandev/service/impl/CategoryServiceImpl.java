package com.pandev.service.impl;

import com.pandev.entity.CategoryEntity;
import com.pandev.repository.CategoryRepository;
import com.pandev.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    public static final String CATEGORY_ADDED = "Категория успешно добавлена";
    public static final String CATEGORY_ALREADY_EXISTS = "Категория с таким именем уже существует";
    public static final String CATEGORY_REMOVED = "Категория успешно удалена";

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public String addCategory(String name, Long parentId) {
        if (categoryRepository.findByName(name).isPresent()) {
            return CATEGORY_ALREADY_EXISTS;
        }

        if (parentId != null) {
            Optional<CategoryEntity> parentOpt = categoryRepository.findById(parentId);
            if (parentOpt.isEmpty()) {
                return "Не найдена категория с таким id: " + parentId;
            }
            return addCategoryWithParent(name, parentOpt.get());
        } else {
            return addRootCategory(name);
        }
    }

    @Override
    @Transactional
    public String removeCategoryByName(String name) {
        Optional<CategoryEntity> categoryOpt = categoryRepository.findByName(name);
        if (categoryOpt.isEmpty()) {
            return "Не найдена категория с таким именем: " + name;
        }
        categoryRepository.deleteById(categoryOpt.get().getId());
        return CATEGORY_REMOVED;
    }

    @Override
    @Transactional(readOnly = true)
    public String viewTreeCategory() {
        List<CategoryEntity> categories = categoryRepository.findAll();
        return buildTree(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryEntity> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    private String addRootCategory(String name) {
        CategoryEntity newCategory = new CategoryEntity();
        newCategory.setName(name);
        categoryRepository.save(newCategory);
        return CATEGORY_ADDED;
    }

    private String addCategoryWithParent(String name, CategoryEntity parent) {
        CategoryEntity newCategory = new CategoryEntity();
        newCategory.setName(name);
        newCategory.setParent(parent);
        categoryRepository.save(newCategory);
        return CATEGORY_ADDED;
    }


    /**
     * Builds a string representation of the category tree.
     * <p>
     * This method takes a list of all category entities and organizes them into a hierarchical tree structure.
     * It first creates a map of children for each parent category, then it initiates the recursive building of the tree
     * starting from the root categories (categories without a parent).
     *
     * @param categories A list of all categories to be organized into a tree.
     * @return A string representation of the category tree, where each level of the hierarchy is represented by indentation.
     */
    private String buildTree(List<CategoryEntity> categories) {
        Map<Long, List<CategoryEntity>> childrenMap = categories.stream()
                .filter(c -> c.getParent() != null)
                .collect(Collectors.groupingBy(c -> c.getParent().getId()));

        return categories.stream()
                .filter(c -> c.getParent() == null)
                .map(c -> buildTreeRecursive(c, childrenMap, ""))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Recursively builds the category tree.
     * <p>
     * This is a helper method for {@link #buildTree(List)}. It uses recursion to build a string representation
     * of the category tree. Each call to this method appends the current category's name, indented to represent
     * its level in the hierarchy, and then processes its children, if any.
     *
     * @param category    The current category to process.
     * @param childrenMap A map of category IDs to their respective list of child categories.
     * @param indent      A string representing the current level of indentation (depth) in the tree.
     * @return A string representation of the category tree starting from the current category.
     */
    private String buildTreeRecursive(CategoryEntity category, Map<Long, List<CategoryEntity>> childrenMap, String indent) {
        StringBuilder builder = new StringBuilder(indent + category.getName() + "\n");
        List<CategoryEntity> children = childrenMap.get(category.getId());
        if (children != null) {
            for (CategoryEntity child : children) {
                builder.append(buildTreeRecursive(child, childrenMap, indent + "* "));
            }
        }
        return builder.toString();
    }
}
