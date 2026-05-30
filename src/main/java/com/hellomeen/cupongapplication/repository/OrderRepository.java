package com.hellomeen.cupongapplication.repository;

import com.hellomeen.cupongapplication.entity.Order;
import com.hellomeen.cupongapplication.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"member", "orderItems", "orderItems.product"})
    Page<Order> findByMemberId(Long memberId, Pageable pageable);

    @EntityGraph(attributePaths = {"member", "orderItems", "orderItems.product"})
    Page<Order> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"member", "orderItems", "orderItems.product"})
    Optional<Order> findById(Long id);

    List<Order> findByMemberIdAndStatus(Long memberId, OrderStatus status);

    @EntityGraph(attributePaths = {"member", "orderItems", "orderItems.product"})
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    long countByStatus(OrderStatus status);
}
