package com.orderservice.service;

import com.orderservice.dto.OrderRequestDTO;
import com.orderservice.dto.OrderResponseDTO;
import com.orderservice.model.Order;
import com.orderservice.repository.OrderRepository;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderResponseDTO create(OrderRequestDTO dto, String username) {
        Order order = new Order(dto.getProductName(), dto.getQuantity(), dto.getPrice(), username);
        Order saved = orderRepository.save(order);
        return toDto(saved);
    }

    @Override
    public OrderResponseDTO update(Long id, OrderRequestDTO dto, String username, boolean isAdmin) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // USER can update only their order, ADMIN can update any order
        if (!isAdmin && !order.getCreatedBy().equals(username)) {
            throw new AccessDeniedException("You are not allowed to update this order");
        }

        order.setProductName(dto.getProductName());
        order.setQuantity(dto.getQuantity());
        order.setPrice(dto.getPrice());

        Order saved = orderRepository.save(order);
        return toDto(saved);
    }

    @Override
    public void delete(Long id, String username, boolean isAdmin) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // USER can delete only their order, ADMIN can delete any order
        if (!isAdmin && !order.getCreatedBy().equals(username)) {
            throw new AccessDeniedException("You are not allowed to delete this order");
        }

        orderRepository.delete(order);
    }

    @Override
    public Page<OrderResponseDTO> getUserOrdersPage(String username, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return orderRepository.findByCreatedBy(username, pageable).map(this::toDto);
    }

    @Override
    public Page<OrderResponseDTO> getAllOrdersPage(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return orderRepository.findAll(pageable).map(this::toDto);
    }

    private OrderResponseDTO toDto(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getCreatedBy()
        );
    }
}
