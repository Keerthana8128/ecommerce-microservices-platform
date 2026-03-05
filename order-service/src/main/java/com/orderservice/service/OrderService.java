package com.orderservice.service;

import com.orderservice.dto.OrderRequestDTO;
import com.orderservice.dto.OrderResponseDTO;
import org.springframework.data.domain.Page;

public interface OrderService {
    OrderResponseDTO create(OrderRequestDTO dto, String username);
    OrderResponseDTO update(Long id, OrderRequestDTO dto, String username, boolean isAdmin);
    void delete(Long id, String username, boolean isAdmin);

    Page<OrderResponseDTO> getUserOrdersPage(String username, int page, int size, String sortBy);
    Page<OrderResponseDTO> getAllOrdersPage(int page, int size, String sortBy);
}
