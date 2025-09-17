package com.belvinard.inventory_management.controller;

import com.belvinard.inventory_management.config.AppConstant;
import com.belvinard.inventory_management.dto.request.CompanyRequestDto;
import com.belvinard.inventory_management.dto.response.CompanyResponseDto;
import com.belvinard.inventory_management.dto.response.PagedResponse;
import com.belvinard.inventory_management.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("${api.prefix}/companies")
@RequiredArgsConstructor
@Tag(name = "Companies", description = "Endpoints for managing companies")
public class CompanyController {

    private final CompanyService companyService;

    // ===========================================================
    // CREATE COMPANY
    // ===========================================================
    @Operation(summary = "Create a new company - ADMIN or MANAGER",
               description = "Creates a company with name, email, address, and other details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Company created successfully"),
            @ApiResponse(responseCode = "409", description = "Company with same name or email already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PostMapping("/create")
    public ResponseEntity<CompanyResponseDto> createCompany(
            @Valid @RequestBody CompanyRequestDto companyRequestDto
    ) {
        CompanyResponseDto createdCompany = companyService.createCompany(companyRequestDto);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

    // ===========================================================
    // GET ALL COMPANIES
    // ===========================================================
    @Operation(
            summary = "Get all companies (paginated) - public",
            description = "Retrieve a paginated and sortable list of companies. Default pageNumber=0, pageSize=50, sortBy=id, sortOrder=asc"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved companies",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PagedResponse.class))),
            @ApiResponse(responseCode = "400", description = "No companies found",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/all")
    public ResponseEntity<PagedResponse<CompanyResponseDto>> getAllCompanies(
            @RequestParam(defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(defaultValue = AppConstant.PAGE_SIZE) Integer pageSize,
            @RequestParam(defaultValue = AppConstant.SORT_COMPANIES_BY) String sortBy,

            @RequestParam(defaultValue = "asc") String sortOrder
    ) {
        PagedResponse<CompanyResponseDto> response = companyService.getAllCompanies(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(response);
    }

    // ===========================================================
    // GET COMPANY BY ID
    // ===========================================================
    @Operation(summary = "Get company by ID - ADMIN or MANAGER",
               description = "Retrieve a company by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company found"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDto> getCompanyById(@PathVariable Long id) {
         CompanyResponseDto company = companyService.getCompanyById(id);
         return ResponseEntity.ok(company);
    }

    // ===========================================================
    // UPDATE COMPANY
    // ===========================================================
    @Operation(
            summary = "Update an existing company - only admin",
            description = "Updates the details of a company based on the provided company ID. " +
                    "Returns the updated company data if successful.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "JSON payload with updated company information",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CompanyRequestDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Company updated successfully",
                            content = @Content(schema = @Schema(implementation = CompanyResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Company not found"),
                    @ApiResponse(responseCode = "409", description = "Conflict: Company name or email already exists"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponseDto> updateCompany(
            @Parameter(description = "ID of the company to update", required = true)
            @PathVariable Long id,

            @Valid @RequestBody CompanyRequestDto dto
    ) {
        CompanyResponseDto updatedCompany = companyService.updateCompany(id, dto);
        return ResponseEntity.ok(updatedCompany);
    }

    // ===========================================================
    // DELETE COMPANY
    // ===========================================================
    @Operation(
            summary = "Delete a company - only ADMIN",
            description = "Deletes a company by its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Company successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }

    // ===========================================================
    // UPDATE COMPANY IMAGE
    // ===========================================================
    @Operation(summary = "Update company image - Only admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image update succesfully",
                    content = @Content(schema = @Schema(implementation = CompanyController.class))),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CompanyResponseDto> updateCompanyImage(
            @PathVariable Long id,
            @Parameter(description = "File to upload", required = true)
            @RequestPart("image") MultipartFile image
    ) throws Exception {
        if(image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image cannot be empty");
        }

        CompanyResponseDto updatedCompany = companyService.updateCompanyImage(id, image);
        return new ResponseEntity<>(updatedCompany, HttpStatus.OK);
    }



}
