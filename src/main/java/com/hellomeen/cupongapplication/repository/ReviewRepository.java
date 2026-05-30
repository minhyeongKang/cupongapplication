package com.hellomeen.cupongapplication.repository;

import com.hellomeen.cupongapplication.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = {"member"})
    Page<Review> findByProductId(Long productId, Pageable pageable);

    boolean existsByMemberIdAndProductId(Long memberId, Long productId);

    Optional<Review> findByIdAndMemberId(Long id, Long memberId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double findAverageRatingByProductId(Long productId);
}
