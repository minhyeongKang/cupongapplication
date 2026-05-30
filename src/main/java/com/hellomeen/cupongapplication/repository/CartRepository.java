package com.hellomeen.cupongapplication.repository;

import com.hellomeen.cupongapplication.entity.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @EntityGraph(attributePaths = {"cartItems", "cartItems.product", "cartItems.product.category"})
    Optional<Cart> findByMemberId(Long memberId);
}
