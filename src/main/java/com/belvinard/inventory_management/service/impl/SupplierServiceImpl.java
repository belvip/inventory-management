package com.belvinard.inventory_management.service.impl;

import com.belvinard.inventory_management.dto.request.SupplierRequestDto;
import com.belvinard.inventory_management.dto.response.SupplierResponseDto;
import com.belvinard.inventory_management.exception.ResourceNotFoundException;
import com.belvinard.inventory_management.mapper.SupplierMapper;
import com.belvinard.inventory_management.model.Company;
import com.belvinard.inventory_management.model.Supplier;
import com.belvinard.inventory_management.repository.CompanyRepository;
import com.belvinard.inventory_management.repository.SupplierRepository;
import com.belvinard.inventory_management.service.SupplierService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private final CompanyRepository companyRepository;

    @Override
    @Transactional
    public SupplierResponseDto createSupplier(SupplierRequestDto dto, Long companyId) {
        if (supplierRepository.existsByName(dto.name())){
            throw new ResourceNotFoundException("Supplier already exists with name: " + dto.name());
        }

        Company companyFromDb = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        Supplier supplier = supplierMapper.toEntity(dto);
        supplier.setCompany(companyFromDb);
        Supplier savedSupplier = supplierRepository.save(supplier);

        return supplierMapper.toResponseDto(savedSupplier);

    }
}
