package com.belvinard.inventory_management.service.impl;

import com.belvinard.inventory_management.dto.request.CompanyRequestDto;
import com.belvinard.inventory_management.dto.response.CompanyResponseDto;
import com.belvinard.inventory_management.dto.response.PagedResponse;
import com.belvinard.inventory_management.exception.APIException;
import com.belvinard.inventory_management.exception.ResourceConflictException;
import com.belvinard.inventory_management.exception.ResourceNotFoundException;
import com.belvinard.inventory_management.mapper.CompanyMapper;
import com.belvinard.inventory_management.model.Company;
import com.belvinard.inventory_management.repository.CompanyRepository;
import com.belvinard.inventory_management.service.CompanyService;
import com.belvinard.inventory_management.service.MinioService;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
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
    private final MinioService minioService;

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
                companyPage.getNumber(),
                companyPage.getSize(),
                companyPage.getTotalElements(),
                companyPage.getTotalPages(),
                companyPage.isLast()
        );
    }

    @Override
    public CompanyResponseDto getCompanyById(Long id) {
        // Fetch the company by ID or throw exception if not found
        Company companyFromDb = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));

        // Map entity to response DTO
        return companyMapper.toResponseDto(companyFromDb);
    }

    @Override
    public CompanyResponseDto updateCompany(Long id, CompanyRequestDto dto) {
        // Fetch the company or throw if not found
        Company companyFromDb = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));

        // Check for name conflict only if name is changing
        if (!companyFromDb.getName().equals(dto.name()) &&
                companyRepository.existsByName(dto.name())) {
            throw new ResourceConflictException("Another company with the same name already exists");
        }

        // Check for email conflict only if email is changing
        if (!companyFromDb.getEmail().equals(dto.email()) &&
                companyRepository.existsByEmail(dto.email())) {
            throw new ResourceConflictException("Another company with the same email already exists");
        }

        // Update fields
        companyFromDb.setName(dto.name());
        companyFromDb.setDescription(dto.description());
        companyFromDb.setAddress(dto.address());
        companyFromDb.setFiscalCode(dto.fiscalCode());
        companyFromDb.setImage(dto.image());
        companyFromDb.setEmail(dto.email());
        companyFromDb.setPhoneNumber(dto.phoneNumber());
        companyFromDb.setWebsite(dto.website());

        // Save and return
        return companyMapper.toResponseDto(companyRepository.save(companyFromDb));
    }

    @Override
    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Company not found with id: " + id);
        }
        companyRepository.deleteById(id);
    }

    @Override
    public CompanyResponseDto updateCompanyImage(Long id, MultipartFile image) throws IOException {
        // Fetch the company or throw if not found
        Company companyFromDb = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));

        String fileName = minioService.uploadImage(image);
        companyFromDb.setImage(fileName);
        String imageUrl = minioService.getPreSignedUrl(fileName, 15);
        Company updatedCompany = companyRepository.save(companyFromDb);

        return companyMapper.toResponseDto(updatedCompany);
    }



}
