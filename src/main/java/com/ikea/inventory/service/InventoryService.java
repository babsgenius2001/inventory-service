package com.ikea.inventory.service;

import com.ikea.inventory.entity.ProductEntity;
import com.ikea.inventory.exceptions.FileUploadNotFoundException;
import com.ikea.inventory.exceptions.InvalidFileException;
import com.ikea.inventory.exceptions.ProductNotFoundException;
import com.ikea.inventory.utils.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InventoryService {

    FileUploadResponse createInventoryByFileUpload(MultipartFile multipartFile) throws InvalidFileException, FileUploadNotFoundException;

    FileUploadResponse createProductsByFileUpload(MultipartFile multipartFile) throws InvalidFileException, FileUploadNotFoundException;

    List<ProductEntity> getProducts();

    List<ProductEntity> sellProduct(Long productId) throws ProductNotFoundException;

}
