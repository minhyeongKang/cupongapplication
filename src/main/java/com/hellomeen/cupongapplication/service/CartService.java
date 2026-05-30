package com.hellomeen.cupongapplication.service;

import com.hellomeen.cupongapplication.dto.request.CartItemRequest;
import com.hellomeen.cupongapplication.entity.Cart;
import com.hellomeen.cupongapplication.entity.CartItem;
import com.hellomeen.cupongapplication.entity.Member;
import com.hellomeen.cupongapplication.entity.Product;
import com.hellomeen.cupongapplication.exception.EntityNotFoundException;
import com.hellomeen.cupongapplication.repository.CartItemRepository;
import com.hellomeen.cupongapplication.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MemberService memberService;
    private final ProductService productService;

    @Transactional
    public Cart getCart(Long memberId) {
        return cartRepository.findByMemberId(memberId)
                .orElseGet(() -> createCart(memberId));
    }

    @Transactional
    public void addItem(Long memberId, CartItemRequest request) {
        Cart cart = getOrCreateCart(memberId);
        Product product = productService.findById(request.getProductId());

        cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .ifPresentOrElse(
                        item -> item.updateQuantity(item.getQuantity() + request.getQuantity()),
                        () -> {
                            CartItem cartItem = CartItem.builder()
                                    .cart(cart)
                                    .product(product)
                                    .quantity(request.getQuantity())
                                    .build();
                            cartItemRepository.save(cartItem);
                        }
                );
    }

    @Transactional
    public void updateItemQuantity(Long memberId, Long cartItemId, int quantity) {
        CartItem cartItem = findCartItemByIdAndMember(cartItemId, memberId);

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.updateQuantity(quantity);
        }
    }

    @Transactional
    public void removeItem(Long memberId, Long cartItemId) {
        CartItem cartItem = findCartItemByIdAndMember(cartItemId, memberId);
        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void clearCart(Long memberId) {
        Cart cart = getCart(memberId);
        cart.getCartItems().clear();
    }

    private Cart getOrCreateCart(Long memberId) {
        return cartRepository.findByMemberId(memberId)
                .orElseGet(() -> createCart(memberId));
    }

    private Cart createCart(Long memberId) {
        Member member = memberService.findById(memberId);
        Cart cart = Cart.createCart(member);
        return cartRepository.save(cart);
    }

    private CartItem findCartItemByIdAndMember(Long cartItemId, Long memberId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니 항목을 찾을 수 없습니다. id=" + cartItemId));

        if (!cartItem.getCart().getMember().getId().equals(memberId)) {
            throw new IllegalStateException("본인의 장바구니 항목만 수정할 수 있습니다.");
        }
        return cartItem;
    }
}
