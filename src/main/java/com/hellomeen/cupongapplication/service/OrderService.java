package com.hellomeen.cupongapplication.service;

import com.hellomeen.cupongapplication.dto.request.OrderCreateRequest;
import com.hellomeen.cupongapplication.entity.*;
import com.hellomeen.cupongapplication.entity.enums.OrderStatus;
import com.hellomeen.cupongapplication.exception.EntityNotFoundException;
import com.hellomeen.cupongapplication.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberService memberService;
    private final CartService cartService;

    @Transactional
    public Long createFromCart(Long memberId, OrderCreateRequest request) {
        Member member = memberService.findById(memberId);
        Cart cart = cartService.getCart(memberId);

        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("장바구니가 비어있습니다.");
        }

        int totalPrice = cartItems.stream()
                .mapToInt(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        Order order = Order.builder()
                .member(member)
                .status(OrderStatus.PENDING)
                .totalPrice(totalPrice)
                .deliveryAddress(request.getDeliveryAddress())
                .build();

        Order savedOrder = orderRepository.save(order);

        cartItems.forEach(cartItem -> {
            cartItem.getProduct().decreaseStock(cartItem.getQuantity());

            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .orderPrice(cartItem.getProduct().getPrice())
                    .build();
            savedOrder.getOrderItems().add(orderItem);
        });

        cartService.clearCart(memberId);

        return savedOrder.getId();
    }

    public Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다. id=" + orderId));
    }

    public Page<Order> findMyOrders(Long memberId, Pageable pageable) {
        return orderRepository.findByMemberId(memberId, pageable);
    }

    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Page<Order> findShippingOrders(Pageable pageable) {
        return orderRepository.findByStatus(OrderStatus.SHIPPING, pageable);
    }

    public Page<Order> findDeliveredOrders(Pageable pageable) {
        return orderRepository.findByStatus(OrderStatus.DELIVERED, pageable);
    }

    @Transactional
    public void cancel(Long memberId, Long orderId) {
        Order order = findById(orderId);
        if (!order.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("본인의 주문만 취소할 수 있습니다.");
        }
        order.cancel();
    }

    @Transactional
    public void approve(Long orderId) {
        findById(orderId).approve();
    }

    @Transactional
    public void startShipping(Long orderId, String trackingNumber) {
        findById(orderId).startShipping(trackingNumber);
    }

    @Transactional
    public void completeDelivery(Long orderId) {
        findById(orderId).completeDelivery();
    }

    @Transactional
    public void updateStatus(Long orderId, OrderStatus status) {
        findById(orderId).updateStatus(status);
    }
}
