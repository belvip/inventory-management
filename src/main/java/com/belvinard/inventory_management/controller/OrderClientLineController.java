package com.belvinard.inventory_management.controller;

import com.belvinard.inventory_management.dto.request.OrderClientLineRequestDto;
import com.belvinard.inventory_management.dto.response.OrderClientLineResponseDto;
import com.belvinard.inventory_management.service.OrderClientLineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order-lines")
@RequiredArgsConstructor
@Tag(name = "Order Client Lines", description = "Manage lines of a Client Order")
public class OrderClientLineController {

    private final OrderClientLineService orderClientLineService;

    @Operation(
            summary = "Add a new line to an order",
            description = "Adds an article to a given order (must be IN_PREPARATION). Snapshots article prices and updates order status to VALIDATED if needed.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Line created successfully",
                            content = @Content(schema = @Schema(implementation = OrderClientLineResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error or business rule violation"),
                    @ApiResponse(responseCode = "404", description = "Order or Article not found")
            }
    )
    @PostMapping("/create")
    public ResponseEntity<OrderClientLineResponseDto> addLine(
            @Valid @RequestBody OrderClientLineRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderClientLineService.addLineToOrder(dto));
    }

    @Operation(
            summary = "Get a single order line by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Line found",
                            content = @Content(schema = @Schema(implementation = OrderClientLineResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Line not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<OrderClientLineResponseDto> getLineById(
            @Parameter(description = "Order line ID", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(orderClientLineService.getLineById(id));
    }

    @Operation(
            summary = "Get all lines for a given order",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of lines",
                            content = @Content(schema = @Schema(implementation = OrderClientLineResponseDto.class)))
            }
    )
    @GetMapping("/order/{clientOrderId}")
    public ResponseEntity<List<OrderClientLineResponseDto>> getAllLinesForOrder(
            @Parameter(description = "Client Order ID", required = true)
            @PathVariable Long clientOrderId) {
        return ResponseEntity.ok(orderClientLineService.getAllLinesForOrder(clientOrderId));
    }

    @Operation(
            summary = "Update the quantity of a line",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Line updated successfully",
                            content = @Content(schema = @Schema(implementation = OrderClientLineResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Validation or stock error"),
                    @ApiResponse(responseCode = "404", description = "Line not found")
            }
    )
    @PatchMapping("/{id}/quantity")
    public ResponseEntity<OrderClientLineResponseDto> updateLineQuantity(
            @PathVariable Long id,
            @RequestParam @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
            BigDecimal newQuantity) {
        return ResponseEntity.ok(orderClientLineService.updateLineQuantity(id, newQuantity));
    }


    @Operation(
            summary = "Remove a line from an order",
            description = "Removes an article from the order and returns the quantity to stock (if order not delivered).",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Line removed successfully"),
                    @ApiResponse(responseCode = "400", description = "Cannot remove line from a delivered order"),
                    @ApiResponse(responseCode = "404", description = "Line not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeLine(
            @Parameter(description = "Order line ID", required = true)
            @PathVariable Long id) {
        orderClientLineService.removeLineFromOrder(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Calculate total of an order",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Total calculated",
                            content = @Content(schema = @Schema(implementation = BigDecimal.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            }
    )
    @GetMapping("/order/{clientOrderId}/total")
    public ResponseEntity<BigDecimal> calculateTotal(
            @Parameter(description = "Client Order ID", required = true)
            @PathVariable Long clientOrderId) {
        return ResponseEntity.ok(orderClientLineService.calculateOrderTotal(clientOrderId));
    }
}
