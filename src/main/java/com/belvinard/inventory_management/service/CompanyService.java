package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.dto.CompanyRequestDto;
import com.belvinard.inventory_management.dto.CompanyResponseDto;
import com.belvinard.inventory_management.dto.PagedResponse;

import java.util.List;

public interface CompanyService {
    CompanyResponseDto createCompany(CompanyRequestDto dto);

    PagedResponse getAllCompanies(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
}
