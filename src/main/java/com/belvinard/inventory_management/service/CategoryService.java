package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.dto.request.CategoryRequestDto;
import com.belvinard.inventory_management.dto.response.CategoryResponseDto;
import com.belvinard.inventory_management.dto.response.PagedResponse;

public interface CategoryService {
    CategoryResponseDto createCategory(CategoryRequestDto dto, Long companyId);
    CategoryResponseDto getCategoryById(Long id);
    CategoryResponseDto updateCategory(Long id, CategoryRequestDto dto);
    void deleteCategory(Long id);
    CategoryResponseDto getCategoryByCompanyId(Long companyId);

    PagedResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

}
