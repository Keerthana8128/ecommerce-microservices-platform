package com.orderservice.service;

import com.orderservice.dto.OrderRequestDTO;
import com.orderservice.dto.OrderResponseDTO;
import com.orderservice.model.Order;
import com.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequestDTO dto;
    private Order order;

    @BeforeEach
    void setUp() {
        dto = new OrderRequestDTO();
        dto.setProductName("Laptop");
        dto.setQuantity(2);
        dto.setPrice(999.99);

        order = new Order("Laptop", 2, 999.99, "keerthana");
        order.setId(1L);
    }

    @Test
    @DisplayName("create saves order with username and returns DTO")
    void create_returnsDtoWithCreatedBy() {
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });

        OrderResponseDTO result = orderService.create(dto, "keerthana");

        assertThat(result.getProductName()).isEqualTo("Laptop");
        assertThat(result.getQuantity()).isEqualTo(2);
        assertThat(result.getPrice()).isEqualTo(999.99);
        assertThat(result.getCreatedBy()).isEqualTo("keerthana");
        assertThat(result.getId()).isEqualTo(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("update allows owner (user) to update own order")
    void update_userCanUpdateOwnOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponseDTO result = orderService.update(1L, dto, "keerthana", false);

        assertThat(result.getProductName()).isEqualTo("Laptop");
        assertThat(result.getCreatedBy()).isEqualTo("keerthana");
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("update allows admin to update any order")
    void update_adminCanUpdateAnyOrder() {
        order.setCreatedBy("keerthana");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponseDTO result = orderService.update(1L, dto, "admin", true);

        assertThat(result.getProductName()).isEqualTo("Laptop");
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("update throws when user tries to update another user's order")
    void update_userCannotUpdateOthersOrder() {
        order.setCreatedBy("keerthana");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.update(1L, dto, "otheruser", false))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("not allowed to update");

        verify(orderRepository).findById(1L);
        verify(orderRepository, org.mockito.Mockito.never()).save(any());
    }

    @Test
    @DisplayName("update throws when order not found")
    void update_throwsWhenOrderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.update(999L, dto, "keerthana", false))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Order not found");
    }

    @Test
    @DisplayName("delete allows owner to delete own order")
    void delete_userCanDeleteOwnOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.delete(1L, "keerthana", false);

        verify(orderRepository).delete(order);
    }

    @Test
    @DisplayName("delete allows admin to delete any order")
    void delete_adminCanDeleteAnyOrder() {
        order.setCreatedBy("keerthana");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.delete(1L, "admin", true);

        verify(orderRepository).delete(order);
    }

    @Test
    @DisplayName("delete throws when user tries to delete another user's order")
    void delete_userCannotDeleteOthersOrder() {
        order.setCreatedBy("keerthana");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.delete(1L, "otheruser", false))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("not allowed to delete");

        verify(orderRepository, org.mockito.Mockito.never()).delete(any());
    }

    @Test
    @DisplayName("getUserOrdersPage returns only orders for given user")
    void getUserOrdersPage_returnsUserOrders() {
        Pageable pageable = PageRequest.of(0, 5);
        when(orderRepository.findByCreatedBy(eq("keerthana"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(order), pageable, 1));

        var result = orderService.getUserOrdersPage("keerthana", 0, 5, "id");

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCreatedBy()).isEqualTo("keerthana");
    }

    @Test
    @DisplayName("getAllOrdersPage returns all orders")
    void getAllOrdersPage_returnsAllOrders() {
        Order adminOrder = new Order("Phone", 1, 499.99, "admin");
        adminOrder.setId(2L);
        Pageable pageable = PageRequest.of(0, 5);
        when(orderRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(order, adminOrder), pageable, 2));

        var result = orderService.getAllOrdersPage(0, 5, "id");

        assertThat(result.getContent()).hasSize(2);
    }
}
