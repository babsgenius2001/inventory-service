package com.ikea.inventory;

import com.ikea.inventory.entity.ProductEntity;
import com.ikea.inventory.exceptions.FileUploadNotFoundException;
import com.ikea.inventory.exceptions.InvalidFileException;
import com.ikea.inventory.exceptions.ProductNotFoundException;
import com.ikea.inventory.repository.InventoryRepository;
import com.ikea.inventory.repository.ProductRepository;
import com.ikea.inventory.service.InventoryService;
import com.ikea.inventory.utils.FileUploadResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
class InventoryTests {
    private static final String PRODUCTS_FILE = "products.json";
    private static final String INVENTORY_FILE = "inventory.json";
    private static final String INVALID_FILE = "inventory.xml";
    private InputStream inputStream;
    @Autowired
    InventoryService inventoryService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Test
    public void contextLoads() {
        //this.applicationContextTest();
    }

    @Order(1)
    @Test
    @DisplayName("Test that new inventory can be created successfully")
    void testThatCreateNewInventoryByFileUploadIsSuccessful() throws IOException, InvalidFileException, FileUploadNotFoundException {
        inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(INVENTORY_FILE);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", INVENTORY_FILE, "multipart/form-data", inputStream);

        FileUploadResponse response = inventoryService.createInventoryByFileUpload(mockMultipartFile);

        assertThat(response.getFileName()).isEqualTo(INVENTORY_FILE);
        assertThat(response.getDownloadUri()).isEqualTo("/downloads/inventory.json");
    }

    @Order(2)
    @Test
    @DisplayName("Test that new products can be created successfully")
    void testThatCreateNewProductsByFileUploadIsSuccessful() throws IOException, InvalidFileException, FileUploadNotFoundException {
        inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(PRODUCTS_FILE);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", PRODUCTS_FILE, "multipart/form-data", inputStream);

        FileUploadResponse response = inventoryService.createProductsByFileUpload(mockMultipartFile);

        assertThat(response.getFileName()).isEqualTo(PRODUCTS_FILE);
        assertThat(response.getDownloadUri()).isEqualTo("/downloads/products.json");
    }

    @Order(3)
    @Test
    @DisplayName("Test that all products can be fetched successfully")
    void testThatAllProductsAreFetchedSuccessfully() {
        List<ProductEntity> fetchedProducts = inventoryService.getProducts();
        assertThat(fetchedProducts).isNotEmpty();
        assertThat(fetchedProducts.stream().map(ProductEntity::getName).collect(Collectors.toList())).contains("Dining Chair");
    }

    @Order(4)
    @Test
    @DisplayName("Test that a product can be removed successfully")
    void testThatAProductsCanBeRemovedFetchedSuccessfully() throws ProductNotFoundException {
        List<ProductEntity> resultAfterProductRemoval = inventoryService.sellProduct(1L);

        assertThat(resultAfterProductRemoval).isNotEmpty();
        assertThat(resultAfterProductRemoval.stream().map(ProductEntity::getId).collect(Collectors.toList())).doesNotContain(1L);
    }

    @Order(5)
    @Test
    @DisplayName("Test that specific file types are acceptable")
    void testThatShouldReturnErrorWhenTryingToUploadUnacceptedFileType() throws IOException {
        inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(INVALID_FILE);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", INVALID_FILE, "multipart/form-data", inputStream);
        InvalidFileException exception = assertThrows(InvalidFileException.class, () -> {
            inventoryService.createInventoryByFileUpload(mockMultipartFile);
        });
        assertThat(exception.getMessage()).contains("Invalid File - allows only .json/.txt files!");
    }

    @Order(6)
    @Test
    @DisplayName("Test that an error occurs when trying to sell a non-existent product")
    void testThatShouldReturnErrorWhenRemovingUnExistingProduct() {
        long productId = 1000L;
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
            inventoryService.sellProduct(productId);
        });
        assertThat(exception.getMessage()).contains("Product not found: " + productId);
    }
}
