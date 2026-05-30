package com.hellomeen.cupongapplication.service;

import com.hellomeen.cupongapplication.dto.request.ReviewRequest;
import com.hellomeen.cupongapplication.entity.Member;
import com.hellomeen.cupongapplication.entity.Product;
import com.hellomeen.cupongapplication.entity.Review;
import com.hellomeen.cupongapplication.exception.EntityNotFoundException;
import com.hellomeen.cupongapplication.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberService memberService;
    private final ProductService productService;

    @Transactional
    public Long create(Long memberId, Long productId, ReviewRequest request) {
        if (reviewRepository.existsByMemberIdAndProductId(memberId, productId)) {
            throw new IllegalStateException("이미 리뷰를 작성한 상품입니다.");
        }

        Member member = memberService.findById(memberId);
        Product product = productService.findById(productId);

        Review review = Review.builder()
                .member(member)
                .product(product)
                .content(request.getContent())
                .rating(request.getRating())
                .build();

        return reviewRepository.save(review).getId();
    }

    public Page<Review> findByProduct(Long productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable);
    }

    public Double getAverageRating(Long productId) {
        Double avg = reviewRepository.findAverageRatingByProductId(productId);
        return avg != null ? avg : 0.0;
    }

    @Transactional
    public void update(Long memberId, Long reviewId, ReviewRequest request) {
        Review review = reviewRepository.findByIdAndMemberId(reviewId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다. id=" + reviewId));

        review.update(request.getContent(), request.getRating());
    }

    @Transactional
    public void delete(Long memberId, Long reviewId) {
        Review review = reviewRepository.findByIdAndMemberId(reviewId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다. id=" + reviewId));

        reviewRepository.delete(review);
    }
}
