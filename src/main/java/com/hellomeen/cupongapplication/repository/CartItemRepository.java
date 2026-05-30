package com.hellomeen.cupongapplication.repository;

import com.hellomeen.cupongapplication.entity.CartItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    @EntityGraph(attributePaths = {"cart", "cart.member"})
    Optional<CartItem> findById(Long id);
}
