package com.belvinard.inventory_management.repository;

import com.belvinard.inventory_management.model.ClientOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientOrderRepository extends JpaRepository<ClientOrder, Long> {
    boolean existsByCode(String code);
}
