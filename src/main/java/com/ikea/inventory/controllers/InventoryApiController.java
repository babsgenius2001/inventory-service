package com.ikea.inventory.controllers;

import com.ikea.inventory.entity.ProductEntity;
import com.ikea.inventory.exceptions.FileUploadNotFoundException;
import com.ikea.inventory.exceptions.InvalidFileException;
import com.ikea.inventory.exceptions.ProductNotFoundException;
import com.ikea.inventory.service.InventoryService;
import com.ikea.inventory.utils.FileUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/api")
public class InventoryApiController {
    private InventoryService inventoryService;

    public InventoryApiController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


    @PostMapping(value = "/inventory/uploadInventoryFile")
    public ResponseEntity<FileUploadResponse> uploadInventory(@RequestParam("file") MultipartFile multipartFile) throws InvalidFileException, FileUploadNotFoundException {
        log.info("Trying to upload an inventory file {}", multipartFile.getName());
        return ResponseEntity.ok(inventoryService.createInventoryByFileUpload(multipartFile));
    }

    @PostMapping(value = "/products/uploadProductFile")
    public ResponseEntity<FileUploadResponse> uploadProducts(@RequestParam("file") MultipartFile multipartFile) throws InvalidFileException, FileUploadNotFoundException {
        log.info("Trying to upload a product file {}", multipartFile.getName());
        return ResponseEntity.ok(inventoryService.createProductsByFileUpload(multipartFile));
    }

    @GetMapping(value = "/products")
    public ResponseEntity<List<ProductEntity>> getProducts() {
        log.info("Getting all available products from the database");
        return ResponseEntity.ok(inventoryService.getProducts());
    }

    @DeleteMapping(value = "/product/sellProduct")
    public ResponseEntity<List<ProductEntity>> sellProduct(@RequestParam("productId") Long productId) throws ProductNotFoundException {
        log.info("Initiating the sale of product with ID {}", productId);
        return ResponseEntity.ok(inventoryService.sellProduct(productId));
    }

}
