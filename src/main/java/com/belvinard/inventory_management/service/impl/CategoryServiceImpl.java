package com.belvinard.inventory_management.service.impl;

import com.belvinard.inventory_management.dto.request.CategoryRequestDto;
import com.belvinard.inventory_management.dto.response.CategoryResponseDto;
import com.belvinard.inventory_management.dto.response.PagedResponse;
import com.belvinard.inventory_management.exception.APIException;
import com.belvinard.inventory_management.exception.ResourceConflictException;
import com.belvinard.inventory_management.exception.ResourceNotFoundException;
import com.belvinard.inventory_management.mapper.CategoryMapper;
import com.belvinard.inventory_management.model.Category;
import com.belvinard.inventory_management.model.Company;
import com.belvinard.inventory_management.repository.CategoryRepository;
import com.belvinard.inventory_management.repository.CompanyRepository;
import com.belvinard.inventory_management.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CompanyRepository companyRepository;

    @Override
    public CategoryResponseDto createCategory(CategoryRequestDto dto, Long companyId) {
        if(categoryRepository.findByCode(dto.code()).isPresent()){
            throw new ResourceConflictException("Category with the same code " + dto.code() + " already exists ");
        }

        Company company = companyRepository.findById(dto.companyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + dto.companyId()));

        Category category = categoryMapper.toEntity(dto);
        category.setCompany(companyRepository.findById(companyId).get());
        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toResponseDto(savedCategory);
    }

    @Override
    public CategoryResponseDto getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        return categoryMapper.toResponseDto(category);
    }

    @Override
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto dto) {

        Category categoryFromDb = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Validate unique code (but allow same code for the same category)
        categoryRepository.findByCode(dto.code())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ResourceConflictException("Category code already exists: " + dto.code());
                });

        Company company = companyRepository.findById(dto.companyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + dto.companyId()));

        // Update category fields
        categoryFromDb.setDesignation(dto.designation());
        categoryFromDb.setCode(dto.code());
        categoryFromDb.setCompany(company);


        Category updatedCategory = categoryRepository.save(categoryFromDb);
        return categoryMapper.toResponseDto(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        categoryRepository.delete(category);
    }

    @Override
    public CategoryResponseDto getCategoryByCompanyId(Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));


        Category category = categoryRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("No category found for company with id: " + companyId));

        return categoryMapper.toResponseDto(category);
    }

    @Override
    public PagedResponse<CategoryResponseDto> getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        if (categoryPage.isEmpty()){
            throw new APIException("No categories found");
        }

        List<CategoryResponseDto> content = categoryPage
                .getContent()
                .stream()
                .map(categoryMapper::toResponseDto)
                .toList();

        return new PagedResponse<>(
                content,
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages(),
                categoryPage.isLast()
        );

    }
}
