package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.dto.request.CompanyRequestDto;
import com.belvinard.inventory_management.dto.response.CompanyResponseDto;
import com.belvinard.inventory_management.dto.response.PagedResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CompanyService {
    CompanyResponseDto createCompany(CompanyRequestDto dto);

    PagedResponse getAllCompanies(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    CompanyResponseDto getCompanyById(Long id);

    CompanyResponseDto updateCompany(Long id, CompanyRequestDto dto);

    void deleteCompany(Long id);

    CompanyResponseDto updateCompanyImage(Long id, MultipartFile image) throws java.io.IOException;
}
