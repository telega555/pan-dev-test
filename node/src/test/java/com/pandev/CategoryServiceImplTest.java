package com.pandev;

import static org.junit.jupiter.api.Assertions.*;

import com.pandev.entity.CategoryEntity;
import com.pandev.repository.CategoryRepository;
import com.pandev.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private static final String ROOT_CATEGORY_NAME = "Бытовая_техника";
    private static final String CHILD_CATEGORY_NAME = "Пылесос";
    private static final String EXISTING_CATEGORY_NAME = "Бытовая_техника";
    private static final String NONEXISTENT_CATEGORY_NAME = "Напитки";

    @Test
    void testAddRootCategory() {
        // given
        String categoryName = ROOT_CATEGORY_NAME;
        // when
        Mockito.when(categoryRepository.save(Mockito.any())).thenReturn(new CategoryEntity());

        String result = categoryService.addCategory(categoryName, null);
        // then
        assertEquals(CategoryServiceImpl.CATEGORY_ADDED, result);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void testAddCategoryWithParent() {
        // given
        String categoryName = CHILD_CATEGORY_NAME;
        Long parentId = 1L;

        CategoryEntity parentCategory = new CategoryEntity();
        parentCategory.setId(parentId);
        Optional<CategoryEntity> parentOptional = Optional.of(parentCategory);
        // when
        Mockito.when(categoryRepository.findById(parentId)).thenReturn(parentOptional);
        Mockito.when(categoryRepository.save(Mockito.any())).thenReturn(new CategoryEntity());

        String result = categoryService.addCategory(categoryName, parentId);
        // then
        assertEquals(CategoryServiceImpl.CATEGORY_ADDED, result);
        Mockito.verify(categoryRepository, Mockito.times(1)).findById(parentId);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void testAddCategoryWithExistingName() {
        // given
        String categoryName = EXISTING_CATEGORY_NAME;
        Mockito.when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(new CategoryEntity()));
        // when
        String result = categoryService.addCategory(categoryName, null);
        // then
        assertEquals(CategoryServiceImpl.CATEGORY_ALREADY_EXISTS, result);
        Mockito.verify(categoryRepository, Mockito.times(1)).findByName(categoryName);
        Mockito.verify(categoryRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void testRemoveCategoryByName() {
        // given
        String categoryName = ROOT_CATEGORY_NAME;
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(1L);
        Optional<CategoryEntity> categoryOptional = Optional.of(categoryEntity);
        // when
        Mockito.when(categoryRepository.findByName(categoryName)).thenReturn(categoryOptional);

        String result = categoryService.removeCategoryByName(categoryName);
        // then
        assertEquals(CategoryServiceImpl.CATEGORY_REMOVED, result);
        Mockito.verify(categoryRepository, Mockito.times(1)).findByName(categoryName);
        Mockito.verify(categoryRepository, Mockito.times(1)).deleteById(categoryEntity.getId());
    }

    @Test
    void testRemoveNonexistentCategory() {
        // given
        String categoryName = NONEXISTENT_CATEGORY_NAME;
        // when
        Mockito.when(categoryRepository.findByName(categoryName)).thenReturn(Optional.empty());

        String result = categoryService.removeCategoryByName(categoryName);
        // then
        assertEquals("Не найдена категория с таким именем: " + categoryName, result);
        Mockito.verify(categoryRepository, Mockito.times(1)).findByName(categoryName);
        Mockito.verify(categoryRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }
}
