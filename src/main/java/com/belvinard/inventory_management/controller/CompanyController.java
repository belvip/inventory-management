package com.belvinard.inventory_management.controller;

import com.belvinard.inventory_management.config.AppConstant;
import com.belvinard.inventory_management.dto.CompanyRequestDto;
import com.belvinard.inventory_management.dto.CompanyResponseDto;
import com.belvinard.inventory_management.dto.PagedResponse;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/companies")
@RequiredArgsConstructor
@Tag(name = "Companies", description = "Endpoints for managing companies")
public class CompanyController {

    private final CompanyService companyService;

    // ===========================================================
    // CREATE COMPANY
    // ===========================================================
    @Operation(summary = "Create a new company",
               description = "Creates a company with name, email, address, and other details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Company created successfully"),
            @ApiResponse(responseCode = "409", description = "Company with same name or email already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
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
            summary = "Get all companies (paginated)",
            description = "Retrieve a paginated and sortable list of companies. Default pageNumber=0, pageSize=50, sortBy=id, sortOrder=asc"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved companies",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PagedResponse.class))),
            @ApiResponse(responseCode = "400", description = "No companies found",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public ResponseEntity<PagedResponse<CompanyResponseDto>> getAllCompanies(
            @RequestParam(defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(defaultValue = AppConstant.PAGE_SIZE) Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ) {
        PagedResponse<CompanyResponseDto> response = companyService.getAllCompanies(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(response);
    }

    // ===========================================================
    // GET COMPANY BY ID (Optional example)
    // ===========================================================
    /*@Operation(summary = "Get company by ID",
               description = "Retrieve a company by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company found"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDto> getCompanyById(@PathVariable Long id) {
         CompanyResponseDto company = companyService.getCompanyById(id);
         return ResponseEntity.ok(company);
    }*/
}
