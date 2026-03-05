package com.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.dto.OrderRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ORDER_JSON = "{\"productName\":\"Laptop\",\"quantity\":1,\"price\":1299.99}";

    @Test
    @DisplayName("POST /orders without auth returns 401")
    void create_withoutAuth_returns401() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ORDER_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "keerthana", roles = "USER")
    @DisplayName("POST /orders as USER creates order with createdBy keerthana")
    void create_asUser_returns201WithCreatedBy() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ORDER_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Laptop"))
                .andExpect(jsonPath("$.quantity").value(1))
                .andExpect(jsonPath("$.price").value(1299.99))
                .andExpect(jsonPath("$.createdBy").value("keerthana"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("POST /orders as ADMIN creates order with createdBy admin")
    void create_asAdmin_returns200WithCreatedBy() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ORDER_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.createdBy").value("admin"));
    }

    @Test
    @WithMockUser(username = "keerthana", roles = "USER")
    @DisplayName("GET /orders/page returns only current user orders")
    void userOrders_returnsUserOrders() throws Exception {
        mockMvc.perform(get("/orders/page").param("page", "0").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable").exists());
    }

    @Test
    @WithMockUser(username = "keerthana", roles = "USER")
    @DisplayName("GET /orders/admin/page as USER returns 403")
    void adminOrders_asUser_returns403() throws Exception {
        mockMvc.perform(get("/orders/admin/page"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("GET /orders/admin/page as ADMIN returns 200")
    void adminOrders_asAdmin_returns200() throws Exception {
        mockMvc.perform(get("/orders/admin/page").param("page", "0").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "keerthana", roles = "USER")
    @DisplayName("PUT /orders/{id} for non-existent order returns 404")
    void update_nonExistent_returns404() throws Exception {
        mockMvc.perform(put("/orders/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ORDER_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Order not found"));
    }

    @Test
    @WithMockUser(username = "keerthana", roles = "USER")
    @DisplayName("DELETE /orders without auth returns 401")
    void delete_withoutAuth_returns401() throws Exception {
        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isUnauthorized());
    }
}
