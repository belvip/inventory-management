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
import com.belvinard.inventory_management.service.ClientOrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ClientOrderServiceImpl implements ClientOrderService {

    private static final String ORDER_NOT_FOUND_MSG = "Order not found with id: ";
    private static final int MAX_MODIFICATION_DAYS = 30;

    private final ClientOrderRepository clientOrderRepository;
    private final ClientOrderMapper clientOrderMapper;
    private final ClientRepository clientRepository;

    @Override
    public ClientOrderResponseDto createOrder(ClientOrderRequestDto orderRequestDto) {

        Client client = clientRepository.findById(orderRequestDto.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client with ID " + orderRequestDto.clientId() + " not found"));

        if (clientOrderRepository.existsByCode(orderRequestDto.code())) {
            throw new DuplicateResourceException("Order code " + orderRequestDto.code() + " already exists");
        }

        ClientOrder order = clientOrderMapper.toEntity(orderRequestDto);

        order.setClient(client);

        if (orderRequestDto.orderDate() == null) {
            order.setOrderDate(LocalDate.now());
        } else {
            order.setOrderDate(orderRequestDto.orderDate());
        }

        order.setStateOrder(OrderStatus.PENDING);

        ClientOrder saved = clientOrderRepository.save(order);

        return clientOrderMapper.toResponseDto(saved);
    }

    @Override
    public ClientOrderResponseDto getOrderById(Long id) {
        ClientOrder order = clientOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MSG + id));

        return clientOrderMapper.toResponseDto(order);
    }

    @Override
    public ClientOrderResponseDto updateOrder(Long id, ClientOrderRequestDto dto) {
        ClientOrder order = clientOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MSG + id));

        // Update client if clientId has changed
        if (!order.getClient().getId().equals(dto.clientId())) {
            Client newClient = clientRepository.findById(dto.clientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Client with ID " + dto.clientId() + " not found"));
            order.setClient(newClient);
        }

        // ✅ DO NOT update stateOrder here — business logic enforces dedicated endpoint
        order.setCode(dto.code());
        order.setComments(dto.comments());
        order.setOrderDate(dto.orderDate() != null
                ? dto.orderDate()
                : order.getOrderDate());

        ClientOrder updated = clientOrderRepository.save(order);
        return clientOrderMapper.toResponseDto(updated);
    }



    @Override
    public List<ClientOrderResponseDto> getOrdersByClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));

        return clientOrderRepository.findByClient(client).stream()
                .map(clientOrderMapper::toResponseDto)
                .toList();
    }

    @Override
    public void deleteOrder(Long id) {
        ClientOrder order = clientOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MSG + id));

        if (order.getStateOrder() == OrderStatus.COMPLETED || order.getStateOrder() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Orders with status " + order.getStateOrder() + " cannot be deleted.");
        }

        clientOrderRepository.delete(order);
    }

    @Override
    public ClientOrderResponseDto updateOrderStatus(Long id, OrderStatus newStatus) {
        ClientOrder order = clientOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MSG + id));

        OrderStatus currentStatus = order.getStateOrder();

        validateStatusTransition(currentStatus, newStatus);
        validateTimeConstraints(order, currentStatus, newStatus);
        validateOrderHasLinesForConfirmation(order, currentStatus, newStatus);
        validateCompletionRequirements(order, newStatus);

        order.setStateOrder(newStatus);
        ClientOrder updatedOrder = clientOrderRepository.save(order);

        return clientOrderMapper.toResponseDto(updatedOrder);
    }
    
    private void validateOrderHasLinesForConfirmation(ClientOrder order, OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == OrderStatus.PENDING && newStatus == OrderStatus.CONFIRMED && 
            (order.getOrderClientLineList() == null || order.getOrderClientLineList().isEmpty())) {
            throw new IllegalStateException("Cannot confirm order without order lines. Please add at least one item to the order.");
        }
    }


    /**
     * Vérifie si une commande peut encore changer d'état
     * selon son statut actuel et la limite de 30 jours.
     */
    private void validateTimeConstraints(ClientOrder order, OrderStatus currentStatus, OrderStatus newStatus) {

        // Statuts soumis à la règle de 30 jours
        Set<OrderStatus> restrictedStatuses = Set.of(
                OrderStatus.CANCELLED,
                OrderStatus.CONFIRMED,
                OrderStatus.COMPLETED
        );

        // Si le statut actuel n'est pas concerné → aucune restriction
        if (!restrictedStatuses.contains(currentStatus)) {
            return;
        }

        LocalDate statusChangeDate = getStatusChangeDate(order, currentStatus);
        long daysSinceStatusChange = ChronoUnit.DAYS.between(statusChangeDate, LocalDate.now());

        if (daysSinceStatusChange > MAX_MODIFICATION_DAYS) {
            throw new IllegalStateException(
                    "Cannot modify an order with status " + currentStatus +
                            " after " + MAX_MODIFICATION_DAYS + " days."
            );
        }
    }

    private LocalDate getStatusChangeDate(ClientOrder order, OrderStatus currentStatus) {

        // On utilise updatedDate comme date du dernier changement de statut.
        // Fallback sur createdDate puis orderDate.
        return Optional.ofNullable(order.getUpdatedDate())
                .orElse(Optional.ofNullable(order.getCreatedDate())
                        .orElse(order.getOrderDate()));
    }


    private void validateCompletionRequirements(ClientOrder order, OrderStatus newStatus) {

        if (newStatus != OrderStatus.COMPLETED) {
            return;
        }

        if (order.getOrderClientLineList() == null || order.getOrderClientLineList().isEmpty()) {
            throw new IllegalStateException("Cannot complete order without order lines.");
        }

        if (!hasAssociatedSale(order)) {
            throw new IllegalStateException("Cannot complete order without an associated sale.");
        }
    }


    private boolean hasAssociatedSale(ClientOrder order) {
        // Cette méthode devrait vérifier l'existence d'une vente pour cette commande
        // Pour l'instant, on retourne true car la logique complète nécessiterait l'injection du SaleRepository
        return true;
    }


    @Override
    public List<ClientOrderResponseDto> getOrdersByStatus(OrderStatus status) {
        List<ClientOrder> orders = clientOrderRepository.findByStateOrder(status);
        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("No orders found with status: " + status);
        }
        return orders.stream()
                .map(clientOrderMapper::toResponseDto)
                .toList();
    }

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
        
        // Libérer les réservations de stock
        if (order.getOrderClientLineList() != null) {
            order.getOrderClientLineList().forEach(orderLine -> {
                if (orderLine.getArticle() != null) {
                    orderLine.releaseReservation();
                }
            });
        }
        
        order.setStateOrder(OrderStatus.CANCELLED);
        ClientOrder cancelledOrder = clientOrderRepository.save(order);
        
        return clientOrderMapper.toResponseDto(cancelledOrder);
    }

    @Override
    public List<ClientOrderResponseDto> getAllOrders() {
        return clientOrderRepository.findAll().stream()
                .map(clientOrderMapper::toResponseDto)
                .toList();
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        if (current == OrderStatus.PENDING) {
            if (next != OrderStatus.CONFIRMED && next != OrderStatus.CANCELLED) {
                throw new IllegalStateException("Order can only be CONFIRMED or CANCELLED from PENDING status.");
            }
        }

        if (current == OrderStatus.CONFIRMED) {
            if (next != OrderStatus.COMPLETED && next != OrderStatus.CANCELLED && next != OrderStatus.PENDING) {
                throw new IllegalStateException("Order can only be COMPLETED, CANCELLED or returned to PENDING after CONFIRMATION.");
            }
        }

        if (current == OrderStatus.CANCELLED) {
            if (next != OrderStatus.PENDING) {
                throw new IllegalStateException("Cancelled order can only be returned to PENDING status.");
            }
        }

        if (current == OrderStatus.COMPLETED) {
            if (next != OrderStatus.PENDING && next != OrderStatus.CONFIRMED && next != OrderStatus.CANCELLED) {
                throw new IllegalStateException("Completed order can only be returned to PENDING, CONFIRMED or CANCELLED status.");
            }
        }
    }

}

