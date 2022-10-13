package com.ikea.inventory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikea.inventory.dto.InventoryRequest;
import com.ikea.inventory.dto.ProductRequest;
import com.ikea.inventory.dto.ProductArticle;
import com.ikea.inventory.dto.Products;
import com.ikea.inventory.entity.ArticleEntity;
import com.ikea.inventory.entity.InventoryEntity;
import com.ikea.inventory.entity.ProductArticleEntity;
import com.ikea.inventory.entity.ProductEntity;
import com.ikea.inventory.exceptions.ArticleNotFoundException;
import com.ikea.inventory.exceptions.FileUploadNotFoundException;
import com.ikea.inventory.exceptions.InvalidFileException;
import com.ikea.inventory.exceptions.ProductNotFoundException;
import com.ikea.inventory.repository.ArticleRepository;
import com.ikea.inventory.repository.InventoryRepository;
import com.ikea.inventory.repository.ProductArticleRepository;
import com.ikea.inventory.repository.ProductRepository;
import com.ikea.inventory.utils.FileUploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {
    private static final String INVALID_FILE = "Invalid File - allows only .json/.txt files!";
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private ProductArticleRepository productArticleRepository;
    @Autowired
    private ArticleRepository articleRepository;

    @Override
    public List<ProductEntity> getProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional
    public List<ProductEntity> sellProduct(Long productId) throws ProductNotFoundException {
        ProductEntity productEntity = productRepository.findById(productId).orElse(null);
        if(productEntity != null) {
            List<ArticleEntity> articleEntityList = productEntity.getProductArticles().stream().map(productArticle -> {
                ArticleEntity articleEntity = productArticle.getArticle();
                articleEntity.setStock(articleEntity.getStock() - productArticle.getAmount());
                return articleEntity;
            }).collect(Collectors.toList());

            articleRepository.saveAll(articleEntityList);
            productRepository.delete(productEntity);
            return productRepository.findAll();
        }else{
            throw new ProductNotFoundException("Product not found: " + productId);
        }
    }

    @Override
    public FileUploadResponse createInventoryByFileUpload(MultipartFile multipartFile) throws InvalidFileException, FileUploadNotFoundException {
        FileUploadResponse response = new FileUploadResponse();
        if (multipartFile!=null) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            String extension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
            long size = multipartFile.getSize();
            if (isFileType(extension)) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    InventoryRequest inventory = objectMapper.readValue(multipartFile.getInputStream(), InventoryRequest.class);
                    List<ArticleEntity> articleEntityList = inventory.getArticles().stream().map(article -> {
                        ArticleEntity articleEntity = new ArticleEntity();
                        articleEntity.setArtId(article.getId());
                        articleEntity.setName(article.getName());
                        articleEntity.setStock(Integer.parseInt(article.getStock()));
                        return articleEntity;
                    }).collect(Collectors.toList());

                    articleRepository.saveAll(articleEntityList);

                    articleEntityList.forEach(articleEntity -> {
                        InventoryEntity inventoryEntity = new InventoryEntity();
                        inventoryEntity.setArticle(articleEntity);
                        inventoryRepository.save(inventoryEntity);
                    });

                    response.setResponseMessage("File Uploaded Successfully!");
                    response.setFileName(fileName);
                    response.setDownloadUri("/downloads/" + fileName);
                    response.setSize(size);
                    response.setTimestamp(LocalDateTime.now());
                    return response;
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            } else {
                throw new InvalidFileException(INVALID_FILE);
            }
        } else {
            throw new FileUploadNotFoundException("Kindly upload a file!");
        }
    }

    @Override
    @Transactional
    public FileUploadResponse createProductsByFileUpload(MultipartFile multipartFile) throws InvalidFileException , FileUploadNotFoundException{
        FileUploadResponse response = new FileUploadResponse();
        if (multipartFile != null) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            String extension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
            long size = multipartFile.getSize();
            if (isFileType(extension)) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Products products = objectMapper.readValue(multipartFile.getInputStream(), Products.class);
                    for (ProductRequest product : products.getProducts()) {
                        ProductEntity productEntity = new ProductEntity();
                        productEntity.setName(product.getName());
                        productEntity.setProductArticles(new ArrayList<>());
                        for (ProductArticle productArticle : product.getProductArticles()) {
                            ProductArticleEntity productArticleEntity = new ProductArticleEntity();
                            productArticleEntity.setAmount(Integer.parseInt(productArticle.getAmount()));
                            productArticleEntity.setId(Long.parseLong(productArticle.getId()));
                            productArticleEntity.setArticle(articleRepository.findById(Long.parseLong(productArticle.getId())).orElseThrow(()-> new ArticleNotFoundException("article not found!. Have you checked the inventory?")));
                            productArticleRepository.save(productArticleEntity);
                            productEntity.getProductArticles().add(productArticleEntity);
                        }

                        productRepository.save(productEntity);
                    }

                    response.setResponseMessage("File Uploaded Successfully!");
                    response.setFileName(fileName);
                    response.setDownloadUri("/downloads/" + fileName);
                    response.setSize(size);
                    response.setTimestamp(LocalDateTime.now());
                    return response;
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            } else {
                throw new InvalidFileException(INVALID_FILE);
            }
        } else {
            throw new FileUploadNotFoundException("Kindly upload a file!");
        }
    }

    private boolean isFileType(String fileExtension) {
        String[] allowedFileTypes = {"json", "txt"};
        return Arrays.asList(allowedFileTypes).contains(fileExtension);
    }
}
