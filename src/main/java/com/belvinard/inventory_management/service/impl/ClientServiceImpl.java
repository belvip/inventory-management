package com.belvinard.inventory_management.service.impl;

import com.belvinard.inventory_management.dto.request.ClientRequestDto;
import com.belvinard.inventory_management.dto.response.ClientResponseDto;
import com.belvinard.inventory_management.dto.response.PagedResponse;
import com.belvinard.inventory_management.exception.DuplicateResourceException;
import com.belvinard.inventory_management.exception.ResourceConflictException;
import com.belvinard.inventory_management.exception.ResourceNotFoundException;
import com.belvinard.inventory_management.mapper.ClientMapper;
import com.belvinard.inventory_management.model.Client;
import com.belvinard.inventory_management.repository.ClientRepository;
import com.belvinard.inventory_management.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public ClientResponseDto createClient(ClientRequestDto dto) {
        // ✅ Check for duplicate (by email or phone number)
        if (clientRepository.existsByEmail(dto.email())) {
            throw new ResourceConflictException ("A client with email " + dto.email() + " already exists.");
        }
        if (clientRepository.existsByPhoneNumber(dto.phoneNumber())) {
            throw new ResourceConflictException("A client with phone number " + dto.phoneNumber() + " already exists.");
        }

        Client client = clientMapper.toEntity(dto);
        Client savedArticle = clientRepository.save(client);
        return clientMapper.toResponseDto(savedArticle);
    }

    @Override
    public ClientResponseDto getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + id));
        return clientMapper.toResponseDto(client);
    }

    @Override
    public PagedResponse<ClientResponseDto> getAllClients(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Client> page = clientRepository.findAll(pageable);

        return new PagedResponse<>(
                page.getContent().stream().map(clientMapper::toResponseDto).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Override
    public ClientResponseDto updateClient(Long id, ClientRequestDto dto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + id));

        // ✅ Prevent duplicate email or phone (only if changed)
        if (!client.getEmail().equalsIgnoreCase(dto.email()) &&
                clientRepository.existsByEmail(dto.email())) {
            throw new DuplicateResourceException("A client with email " + dto.email() + " already exists.");
        }
        if (!client.getPhoneNumber().equals(dto.phoneNumber()) &&
                clientRepository.existsByPhoneNumber(dto.phoneNumber())) {
            throw new DuplicateResourceException("A client with phone number " + dto.phoneNumber() + " already exists.");
        }

        client.setName(dto.name());
        client.setAddress(dto.address());
        client.setEmail(dto.email());
        client.setPhoneNumber(dto.phoneNumber());

        Client updated = clientRepository.save(client);
        return clientMapper.toResponseDto(updated);
    }

    @Override
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + id));
        clientRepository.delete(client);
    }
}
