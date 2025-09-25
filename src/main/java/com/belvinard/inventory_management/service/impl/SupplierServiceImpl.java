package com.belvinard.inventory_management.service.impl;

import com.belvinard.inventory_management.dto.response.SupplierResponseDto;
import com.belvinard.inventory_management.exception.ResourceNotFoundException;
import com.belvinard.inventory_management.mapper.SupplierMapper;
import com.belvinard.inventory_management.model.Supplier;
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

    @Override
    @Transactional
    public SupplierResponseDto createSupplier(SupplierResponseDto dto) {
        if (supplierRepository.existByName(dto.name())){
            throw new ResourceNotFoundException("Supplier already exists with name: " + dto.name());
        }

        Supplier supplier = supplierMapper.toEntity(dto);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toResponseDto(savedSupplier);

    }
}
