package com.belvinard.inventory_management.controller;

import com.belvinard.inventory_management.config.AppConstant;
import com.belvinard.inventory_management.dto.request.CategoryRequestDto;
import com.belvinard.inventory_management.dto.response.CategoryResponseDto;
import com.belvinard.inventory_management.dto.response.PagedResponse;
import com.belvinard.inventory_management.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Manage categories for companies")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
        summary = "Create a new category - ADMIN and MANAGER",
        description = "Creates a new category and associates it with the company specified in the request body.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Category created successfully",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoryResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Category already exists or company not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
        }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PostMapping("/create")
    public ResponseEntity<CategoryResponseDto> createCategory(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Category information to be created (including companyId)",
                required = true,
                content = @Content(schema = @Schema(implementation = CategoryRequestDto.class))
            )
            @Valid @RequestBody CategoryRequestDto dto) {

        CategoryResponseDto savedCategory = categoryService.createCategory(dto, dto.companyId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    @Operation(
            summary = "Get Category by ID - Only ADMIN, MANAGER, SALES",
            description = "Fetch a single category using its unique identifier"
    )
    @ApiResponse(responseCode = "200", description = "Category found")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SALES') or hasRole('ROLE_MANAGER') or hasRole('ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    /**
     * Update an existing category.
     */
    @Operation(
            summary = "Update Category - ADMIN and MANAGER",
            description = "Update the details of an existing category"
    )
    @ApiResponse(responseCode = "200", description = "Category updated successfully")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @ApiResponse(responseCode = "409", description = "Category with the same code already exists")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @Parameter(description = "Category ID to update", example = "1")
            @PathVariable Long id,
            @RequestBody @Valid CategoryRequestDto dto
    ) {
        return ResponseEntity.ok(categoryService.updateCategory(id, dto));
    }

    /**
     * Delete a category by ID.
     */
    @Operation(
            summary = "Delete Category - ONLY ADMIN",
            description = "Delete an existing category by its ID"
    )
    @ApiResponse(responseCode = "204", description = "Category deleted successfully")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Category ID to delete", example = "1")
            @PathVariable Long id
    ) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get category by Company ID.
     */
    @Operation(
            summary = "Get Category by Company ID - ADMIN, MANAGER - SALES",
            description = "Fetch a category that belongs to a specific company"
    )
    @ApiResponse(responseCode = "200", description = "Category found for company")
    @ApiResponse(responseCode = "404", description = "Company or category not found")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SALES') or hasRole('ROLE_MANAGER') or hasRole('ROLE_USER')")
    @GetMapping("/by-company/{companyId}")
    public ResponseEntity<CategoryResponseDto> getCategoryByCompanyId(
            @Parameter(description = "Company ID to search for categories", example = "5")
            @PathVariable Long companyId
    ) {
        return ResponseEntity.ok(categoryService.getCategoryByCompanyId(companyId));
    }

    @Operation(
            summary = "Get All Categories - ADMIN, MANAGER, SALES, USER",
            description = "Fetch all categories with pagination and sorting (default sort by companyId)"
    )
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES') or hasRole('ROLE_USER')")
    @GetMapping("/all")
    public ResponseEntity<PagedResponse<CategoryResponseDto>> getAllCategories(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(value = "pageNumber", defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(value = "pageSize", defaultValue = AppConstant.PAGE_SIZE, required = false) Integer pageSize,
            @Parameter(description = "Sort field", example = "designation")
            @RequestParam(value = "sortBy", defaultValue = AppConstant.SORT_CATEGORIES_BY, required = false) String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc")
            @RequestParam(value = "sortOrder", defaultValue = AppConstant.SORT_DIR, required = false) String sortOrder
    ) {
        PagedResponse<CategoryResponseDto> pagedResponse = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(pagedResponse);
    }
}
