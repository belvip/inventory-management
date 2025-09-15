package com.belvinard.inventory_management.service.impl;

import com.belvinard.inventory_management.config.AppConstant;
import com.belvinard.inventory_management.dto.CompanyRequestDto;
import com.belvinard.inventory_management.dto.CompanyResponseDto;
import com.belvinard.inventory_management.dto.PagedResponse;
import com.belvinard.inventory_management.exception.APIException;
import com.belvinard.inventory_management.exception.ResourceConflictException;
import com.belvinard.inventory_management.mapper.CompanyMapper;
import com.belvinard.inventory_management.model.Company;
import com.belvinard.inventory_management.repository.CompanyRepository;
import com.belvinard.inventory_management.service.CompanyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    @Override
    public CompanyResponseDto createCompany(CompanyRequestDto dto) {
        // Check if a company with the same name already exists
        if (companyRepository.existsByName(dto.name())) {
            throw new ResourceConflictException("Company with the same name already exists");
        }

        // Check if a company with the same email already exists
        if (companyRepository.existsByEmail(dto.email())) {
            throw new ResourceConflictException("Company with the same email already exists");
        }

        // Convert DTO to entity
        Company company = companyMapper.toEntity(dto);

        // Save the company. BaseEntity will auto-populate createdDate and updatedDate
        Company savedCompany = companyRepository.save(company);

        // Convert back to response DTO
        return companyMapper.toResponseDto(savedCompany);
    }

    @Override
    public PagedResponse<CompanyResponseDto> getAllCompanies(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Company> companyPage = companyRepository.findAll(pageable);

        if (companyPage.isEmpty()) {
            throw new APIException("No companies found");
        }

        List<CompanyResponseDto> content = companyPage
                .getContent()
                .stream()
                .map(companyMapper::toResponseDto)
                .toList();

        return new PagedResponse<>(
                content,
                companyPage.getNumber(),        // current page number
                companyPage.getSize(),          // page size
                companyPage.getTotalElements(), // total elements
                companyPage.getTotalPages(),    // total pages
                companyPage.isLast()            // true if this is the last page
        );
    }



}
