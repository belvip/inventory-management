package com.belvinard.inventory_management.controller;

import com.belvinard.inventory_management.dto.request.ClientRequestDto;
import com.belvinard.inventory_management.dto.response.ClientResponseDto;
import com.belvinard.inventory_management.dto.response.PagedResponse;
import com.belvinard.inventory_management.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("${api.prefix}/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "CRUD operations for managing clients")
public class ClientController {

    private final ClientService clientService;

    @Operation(summary = "Create a new client - ADMIN or MANAGER or SALES", description = "Creates a new client and returns the created client details")
    @ApiResponse(responseCode = "201", description = "Client created successfully",
            content = @Content(schema = @Schema(implementation = ClientResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @PostMapping("create")
    public ResponseEntity<ClientResponseDto> createClient(@Valid @RequestBody ClientRequestDto dto) {
        ClientResponseDto createdClient = clientService.createClient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdClient);
    }

    @Operation(summary = "Get client by ID - ADMIN or MANAGER or SALES", description = "Fetches a single client by its unique ID")
    @ApiResponse(responseCode = "200", description = "Client found",
            content = @Content(schema = @Schema(implementation = ClientResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "Client not found")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDto> getClientById(
            @Parameter(description = "ID of the client to fetch", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @Operation(summary = "Get all clients with pagination - ADMIN or MANAGER or SALES", description = "Fetches all clients with optional pagination and sorting")
    @ApiResponse(responseCode = "200", description = "Clients fetched successfully",
            content = @Content(schema = @Schema(implementation = PagedResponse.class)))
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/all")
    public ResponseEntity<PagedResponse<ClientResponseDto>> getAllClients(
            @Parameter(description = "Page number (default: 0)") @RequestParam(defaultValue = "0") Integer pageNumber,
            @Parameter(description = "Page size (default: 10)") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "Sort by field (default: name)") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction: asc or desc (default: asc)") @RequestParam(defaultValue = "asc") String sortOrder
    ) {
        return ResponseEntity.ok(clientService.getAllClients(pageNumber, pageSize, sortBy, sortOrder));
    }

    @Operation(summary = "Update client by ID - ADMIN or MANAGER or SALES", description = "Updates an existing client's information")
    @ApiResponse(responseCode = "200", description = "Client updated successfully",
            content = @Content(schema = @Schema(implementation = ClientResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "Client not found")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDto> updateClient(
            @Parameter(description = "ID of the client to update", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ClientRequestDto dto) {
        return ResponseEntity.ok(clientService.updateClient(id, dto));
    }

    @Operation(summary = "Delete client by ID - ADMIN or MANAGER or SALES", description = "Deletes an existing client from the system")
    @ApiResponse(responseCode = "204", description = "Client deleted successfully")
    @ApiResponse(responseCode = "404", description = "Client not found")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(
            @Parameter(description = "ID of the client to delete", required = true)
            @PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
