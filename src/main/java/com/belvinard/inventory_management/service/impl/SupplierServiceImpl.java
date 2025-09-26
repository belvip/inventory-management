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
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

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

    @Override
    public SupplierResponseDto getSupplierById(Long id) {

        Supplier supplierFromDb = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id : " + id));

        return supplierMapper.toResponseDto(supplierFromDb);
    }

    @Override
    @Transactional
    public SupplierResponseDto updateSupplier(Long id, SupplierRequestDto dto) {
        Supplier supplierFromDb = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id : " + id));

        supplierRepository.findByName(dto.name())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ResourceNotFoundException("Supplier already exists with name: " + dto.name());
                });

        supplierFromDb.setName(dto.name());
        supplierFromDb.setPhoneNumber(dto.phoneNumber());
        
        Supplier updatedSupplier = supplierRepository.save(supplierFromDb);
        return supplierMapper.toResponseDto(updatedSupplier);
    }

    @Override
    public List<SupplierResponseDto> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(supplierMapper::toResponseDto)
                .toList();
    }

    @Override
    public void deleteSupplier(Long id) {
        Supplier supplierFromDb = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id : " + id));
        supplierRepository.delete(supplierFromDb);

    }


}
