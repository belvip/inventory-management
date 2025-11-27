package com.belvinard.inventory_management.service.impl;

import com.belvinard.inventory_management.dto.request.ClientOrderRequestDto;
import com.belvinard.inventory_management.dto.response.ClientOrderResponseDto;
import com.belvinard.inventory_management.exception.DuplicateResourceException;
import com.belvinard.inventory_management.exception.ResourceNotFoundException;
import com.belvinard.inventory_management.mapper.ClientOrderMapper;
import com.belvinard.inventory_management.model.Client;
import com.belvinard.inventory_management.model.ClientOrder;
import com.belvinard.inventory_management.model.OrderStatus;
import com.belvinard.inventory_management.repository.ClientOrderRepository;
import com.belvinard.inventory_management.repository.ClientRepository;
import com.belvinard.inventory_management.repository.SaleRepository;
import com.belvinard.inventory_management.service.ClientOrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ClientOrderServiceImpl implements ClientOrderService {

    private static final String ORDER_NOT_FOUND_MSG = "Order not found with id: ";
    private static final int MAX_MODIFICATION_DAYS = 7;

    private final ClientOrderRepository clientOrderRepository;
    private final ClientOrderMapper clientOrderMapper;
    private final ClientRepository clientRepository;
    private final SaleRepository saleRepository;

    /* -----------------------------------------
       ALLOWED STATUS TRANSITIONS (Centralized)
    ----------------------------------------- */
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            OrderStatus.PENDING, Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
            OrderStatus.CONFIRMED, Set.of(OrderStatus.COMPLETED, OrderStatus.CANCELLED, OrderStatus.PENDING),
            OrderStatus.CANCELLED, Set.of(OrderStatus.PENDING),
            OrderStatus.COMPLETED, Set.of(OrderStatus.CONFIRMED)
    );

    /* ========================================================================
                            CREATE ORDER
    ======================================================================== */
    @Override
    public ClientOrderResponseDto createOrder(ClientOrderRequestDto request) {

        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client with ID " + request.clientId() + " not found"));

        if (clientOrderRepository.existsByCode(request.code())) {
            throw new DuplicateResourceException("Order code " + request.code() + " already exists");
        }

        ClientOrder order = clientOrderMapper.toEntity(request);
        order.setClient(client);
        order.setOrderDate(request.orderDate() != null ? request.orderDate() : LocalDate.now());
        order.setStateOrder(OrderStatus.PENDING);

        return clientOrderMapper.toResponseDto(clientOrderRepository.save(order));
    }

    /* ========================================================================
                            GET ORDER
    ======================================================================== */
    @Override
    public ClientOrderResponseDto getOrderById(Long id) {
        return clientOrderRepository.findById(id)
                .map(clientOrderMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MSG + id));
    }

    /* ========================================================================
                            UPDATE ORDER (fields only)
    ======================================================================== */
    @Override
    public ClientOrderResponseDto updateOrder(Long id, ClientOrderRequestDto dto) {
        ClientOrder order = clientOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MSG + id));

        if (!order.getClient().getId().equals(dto.clientId())) {
            Client newClient = clientRepository.findById(dto.clientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Client with ID " + dto.clientId() + " not found"));
            order.setClient(newClient);
        }

        order.setCode(dto.code());
        order.setComments(dto.comments());
        order.setOrderDate(dto.orderDate() != null ? dto.orderDate() : order.getOrderDate());

        return clientOrderMapper.toResponseDto(clientOrderRepository.save(order));
    }

    /* ========================================================================
                            GET ORDERS BY CLIENT
    ======================================================================== */
    @Override
    public List<ClientOrderResponseDto> getOrdersByClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));

        return clientOrderRepository.findByClient(client).stream()
                .map(clientOrderMapper::toResponseDto)
                .toList();
    }

    /* ========================================================================
                            DELETE ORDER
    ======================================================================== */
    @Override
    public void deleteOrder(Long id) {
        ClientOrder order = clientOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MSG + id));

        if (Set.of(OrderStatus.COMPLETED, OrderStatus.CANCELLED).contains(order.getStateOrder())) {
            throw new IllegalStateException("Orders with status " + order.getStateOrder() + " cannot be deleted.");
        }

        clientOrderRepository.delete(order);
    }

    /* ========================================================================
                            UPDATE ORDER STATUS
    ======================================================================== */
    @Override
    public ClientOrderResponseDto updateOrderStatus(Long id, OrderStatus newStatus) {

        ClientOrder order = clientOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MSG + id));

        validateStatusChangeRules(order, newStatus);

        order.setStateOrder(newStatus);
        return clientOrderMapper.toResponseDto(clientOrderRepository.save(order));
    }

    @Override
    public List<ClientOrderResponseDto> getOrdersByStatus(OrderStatus status) {
        return clientOrderRepository.findByStateOrder(status).stream()
                .map(clientOrderMapper::toResponseDto)
                .toList();
    }

    /* ========================================================================
                            STATUS CHANGE VALIDATION
    ======================================================================== */
    private void validateStatusChangeRules(ClientOrder order, OrderStatus newStatus) {
        OrderStatus current = order.getStateOrder();

        validateAllowedTransition(current, newStatus);
        validateTimeConstraints(order, current);
        validateOrderLinesForConfirmation(order, current, newStatus);
        validateCompletionRequirements(order, newStatus);
    }

    private void validateAllowedTransition(OrderStatus current, OrderStatus next) {
        if (!ALLOWED_TRANSITIONS.get(current).contains(next)) {
            throw new IllegalStateException("Invalid transition from " + current + " to " + next);
        }
    }

    private void validateTimeConstraints(ClientOrder order, OrderStatus currentStatus) {

        if (!Set.of(OrderStatus.CONFIRMED, OrderStatus.COMPLETED, OrderStatus.CANCELLED)
                .contains(currentStatus)) return;

        LocalDate lastUpdate = Optional.ofNullable(order.getUpdatedDate())
                .orElse(order.getCreatedDate() != null
                        ? order.getCreatedDate()
                        : order.getOrderDate());

        long daysElapsed = ChronoUnit.DAYS.between(lastUpdate, LocalDate.now());

        if (daysElapsed > MAX_MODIFICATION_DAYS) {
            throw new IllegalStateException(
                    "Modification not allowed after " + MAX_MODIFICATION_DAYS +
                            " days for orders with status " + currentStatus
            );
        }
    }

    private void validateOrderLinesForConfirmation(ClientOrder order, OrderStatus current, OrderStatus next) {
        if (current == OrderStatus.PENDING && next == OrderStatus.CONFIRMED &&
                (order.getOrderClientLineList() == null || order.getOrderClientLineList().isEmpty())) {
            throw new IllegalStateException("Cannot confirm order without order lines.");
        }
    }

    private void validateCompletionRequirements(ClientOrder order, OrderStatus next) {
        if (next != OrderStatus.COMPLETED) return;

        if (order.getOrderClientLineList() == null || order.getOrderClientLineList().isEmpty()) {
            throw new IllegalStateException("Cannot complete order without order lines.");
        }

        if (!hasAssociatedSale(order)) {
            throw new IllegalStateException("Cannot complete order without an associated sale.");
        }
    }

    private boolean hasAssociatedSale(ClientOrder order) {
        return saleRepository.findAll().stream()
                .anyMatch(sale -> sale.getClientOrder() != null && 
                         sale.getClientOrder().getId().equals(order.getId()));
    }

    /* ========================================================================
                            CANCEL ORDER
    ======================================================================== */
    @Override
    @Transactional
    public ClientOrderResponseDto cancelOrder(Long id) {
        ClientOrder order = clientOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MSG + id));

        if (order.getStateOrder() == OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot cancel a CONFIRMED order");
        }

        if (order.getStateOrder() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already CANCELLED");
        }

        if (order.getOrderClientLineList() != null) {
            order.getOrderClientLineList().forEach(line -> {
                if (line.getArticle() != null) line.releaseReservation();
            });
        }

        order.setStateOrder(OrderStatus.CANCELLED);
        return clientOrderMapper.toResponseDto(clientOrderRepository.save(order));
    }

    /* ========================================================================
                            GET ALL ORDERS
    ======================================================================== */
    @Override
    public List<ClientOrderResponseDto> getAllOrders() {
        return clientOrderRepository.findAll().stream()
                .map(clientOrderMapper::toResponseDto)
                .toList();
    }

}

