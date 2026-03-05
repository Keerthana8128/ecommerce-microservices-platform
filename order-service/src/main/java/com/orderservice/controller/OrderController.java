package com.orderservice.controller;

import com.orderservice.dto.OrderRequestDTO;
import com.orderservice.dto.OrderResponseDTO;
import com.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Orders", description = "Create, update, delete, list orders (JWT required)")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    // ✅ USER creates their order
    @PostMapping
    public OrderResponseDTO create(@RequestBody OrderRequestDTO dto, Authentication auth) {
        return orderService.create(dto, auth.getName());
    }

    // ✅ USER updates only their order, ADMIN any
    @PutMapping("/{id}")
    public OrderResponseDTO update(@PathVariable Long id, @RequestBody OrderRequestDTO dto, Authentication auth) {
        return orderService.update(id, dto, auth.getName(), isAdmin(auth));
    }

    // ✅ USER deletes only their order, ADMIN any
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, Authentication auth) {
        orderService.delete(id, auth.getName(), isAdmin(auth));
        return "Order deleted successfully";
    }

    // ✅ USER sees only their orders
    @GetMapping("/page")
    public Page<OrderResponseDTO> userOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            Authentication auth
    ) {
        return orderService.getUserOrdersPage(auth.getName(), page, size, sortBy);
    }

    // ✅ ADMIN sees all orders
    @GetMapping("/admin/page")
    public Page<OrderResponseDTO> adminOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return orderService.getAllOrdersPage(page, size, sortBy);
    }
}
