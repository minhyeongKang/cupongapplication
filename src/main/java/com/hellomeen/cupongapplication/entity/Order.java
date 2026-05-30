package com.hellomeen.cupongapplication.entity;

import com.hellomeen.cupongapplication.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private int totalPrice;

    private String deliveryAddress;

    private String trackingNumber;

    public void approve() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("주문 접수 상태의 주문만 승인할 수 있습니다.");
        }
        this.status = OrderStatus.PAID;
    }

    public void startShipping(String trackingNumber) {
        if (this.status != OrderStatus.PAID) {
            throw new IllegalStateException("결제 확인 상태의 주문만 배송 시작할 수 있습니다.");
        }
        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new IllegalArgumentException("운송장 번호를 입력해주세요.");
        }
        this.status = OrderStatus.SHIPPING;
        this.trackingNumber = trackingNumber;
    }

    public void completeDelivery() {
        if (this.status != OrderStatus.SHIPPING) {
            throw new IllegalStateException("배송 중 상태의 주문만 완료 처리할 수 있습니다.");
        }
        this.status = OrderStatus.DELIVERED;
    }

    public void cancel() {
        if (this.status == OrderStatus.SHIPPING || this.status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("배송 중이거나 완료된 주문은 취소할 수 없습니다.");
        }
        this.status = OrderStatus.CANCELLED;
        this.orderItems.forEach(item -> item.getProduct().increaseStock(item.getQuantity()));
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }
}
