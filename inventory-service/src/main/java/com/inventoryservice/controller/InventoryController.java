package com.inventoryservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InventoryController {

    @GetMapping("/api/inventory/check")
    public boolean check(@RequestParam Long productId, @RequestParam int quantity) {
        // temporary: always true
        return true;
    }
}
