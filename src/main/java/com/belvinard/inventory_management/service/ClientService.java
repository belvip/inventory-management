package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.dto.request.ClientRequestDto;
import com.belvinard.inventory_management.dto.response.ClientResponseDto;
import com.belvinard.inventory_management.dto.response.PagedResponse;


public interface ClientService {
    ClientResponseDto createClient(ClientRequestDto dto);
    ClientResponseDto getClientById(Long id);
    PagedResponse<ClientResponseDto> getAllClients(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    ClientResponseDto updateClient(Long id, ClientRequestDto dto);
    void deleteClient(Long id);
}
