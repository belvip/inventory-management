package com.belvinard.inventory_management.controller;

import com.belvinard.inventory_management.dto.request.ClientOrderRequestDto;
import com.belvinard.inventory_management.dto.response.ClientOrderResponseDto;
import com.belvinard.inventory_management.exception.ErrorResponse;
import com.belvinard.inventory_management.model.OrderStatus;
import com.belvinard.inventory_management.service.ClientOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
@Tag(name = "Client Orders", description = "Endpoints for managing client orders - ADMIN and manager or SALES")
public class ClientOrderController {

    private final ClientOrderService clientOrderService;

    @Operation(
            summary = "Create a new client order",
            description = "Creates a new order for a specific client. " +
                          "The order status will always be set to IN_PREPARATION on creation."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Order created successfully",
            content = @Content(schema = @Schema(implementation = ClientOrderResponseDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Validation error or bad request"
    )
    @ApiResponse(
            responseCode = "409",
            description = "Order code already exists"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @PostMapping("/create")
    public ResponseEntity<ClientOrderResponseDto> createOrder(
            @Valid
            @RequestBody
            @Parameter(description = "Order data to create a new order", required = true)
            ClientOrderRequestDto requestDto) {

        ClientOrderResponseDto createdOrder = clientOrderService.createOrder(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }


    @Operation(
            summary = "Get order by ID",
            description = "Retrieve an order by its unique identifier.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order found",
                            content = @Content(schema = @Schema(implementation = ClientOrderResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/{id}")
    public ResponseEntity<ClientOrderResponseDto> getOrderById(
            @Parameter(description = "ID of the order to retrieve", required = true)
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(clientOrderService.getOrderById(id));
    }


    @Operation(
            summary = "Update an order",
            description = "Modify an existing order. You can update its code, comments, orderDate, and stateOrder.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order updated",
                            content = @Content(schema = @Schema(implementation = ClientOrderResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found"),
                    @ApiResponse(responseCode = "409", description = "Duplicate order code")
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @PutMapping("/{id}")
    public ResponseEntity<ClientOrderResponseDto> updateOrder(
            @Parameter(description = "ID of the order to update", required = true)
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Order update request body",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ClientOrderRequestDto.class))
            )
            @Valid @RequestBody ClientOrderRequestDto dto
    ) {
        return ResponseEntity.ok(clientOrderService.updateOrder(id, dto));
    }


    @Operation(
            summary = "Get orders by client",
            description = "Retrieve all orders that belong to a specific client.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orders retrieved",
                            content = @Content(schema = @Schema(implementation = ClientOrderResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Client not found")
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ClientOrderResponseDto>> getOrdersByClient(
            @Parameter(description = "ID of the client", required = true)
            @PathVariable Long clientId
    ) {
        return ResponseEntity.ok(clientOrderService.getOrdersByClient(clientId));
    }


    @Operation(
            summary = "Delete an order",
            description = "Delete an order by its ID. Orders with status DELIVERED or CANCELED cannot be deleted.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Order not found"),
                    @ApiResponse(responseCode = "400", description = "Cannot delete delivered or canceled orders")
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "ID of the order to delete", required = true)
            @PathVariable Long id
    ) {
        clientOrderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Update order status", description = "Change the status of an order following allowed transitions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status transition"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ClientOrderResponseDto> updateOrderStatus(
            @PathVariable Long id,
            @Parameter(
                    description = "New status of the order. Allowed values: in_preparation, validated, delivered, canceled",
                    required = true,
                    schema = @Schema(implementation = OrderStatus.class)
            )
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(clientOrderService.updateOrderStatus(id, status));
    }


    @Operation(
            summary = "Get orders by status",
            description = "Retrieve all orders with the specified status.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orders found"),
                    @ApiResponse(responseCode = "404", description = "No orders found for the given status")
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ClientOrderResponseDto>> getOrdersByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(clientOrderService.getOrdersByStatus(status));
    }

    @Operation(
            summary = "Get all orders",
            description = "Retrieve all orders in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
                            content = @Content(schema = @Schema(implementation = ClientOrderResponseDto.class)))
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/all")
    public ResponseEntity<List<ClientOrderResponseDto>> getAllOrders() {
        return ResponseEntity.ok(clientOrderService.getAllOrders());
    }

    @Operation(
            summary = "Cancel client order",
            description = "Cancels a client order and releases stock reservations (not allowed for CONFIRMED orders)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
                    @ApiResponse(responseCode = "400", description = "Cannot cancel CONFIRMED order"),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ClientOrderResponseDto> cancelOrder(
            @Parameter(description = "ID of the order to cancel", required = true)
            @PathVariable Long id) {
        ClientOrderResponseDto cancelledOrder = clientOrderService.cancelOrder(id);
        return ResponseEntity.ok(cancelledOrder);
    }

}
