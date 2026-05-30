package com.hellomeen.cupongapplication.entity.enums;

public enum OrderStatus {
    PENDING,    // 결제 대기
    PAID,       // 결제 완료
    SHIPPING,   // 배송 중
    DELIVERED,  // 배송 완료
    CANCELLED   // 주문 취소
}
