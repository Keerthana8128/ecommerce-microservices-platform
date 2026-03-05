package com.inventoryservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

  @GetMapping("/ping")
  public String ping() {
    return "pong";
  }
}