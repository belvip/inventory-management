package com.belvinard.inventory_management.repository;

import com.belvinard.inventory_management.model.Client;
import com.belvinard.inventory_management.model.ClientOrder;
import com.belvinard.inventory_management.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientOrderRepository extends JpaRepository<ClientOrder, Long> {
    boolean existsByCode(String code);
    Optional<ClientOrder> findByCode(String code);
    List<ClientOrder> findByClient(Client client);
    List<ClientOrder> findByClientId(Long clientId);
    List<ClientOrder> findByClientIdAndStateOrder(Long clientId, OrderStatus stateOrder);

    List<ClientOrder> findByStateOrder(OrderStatus status);
}
