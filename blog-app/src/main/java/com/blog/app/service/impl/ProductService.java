package com.blog.app.service.impl;

import com.blog.app.dto.ProductDTO;
import com.blog.app.entity.Product;
import com.blog.app.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final String BASE_URL = "https://fakestoreapi.com/products";

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ProductRepository productRepository;

    public List<ProductDTO> getAllProducts() {
        ProductDTO[] products = restTemplate.getForObject(BASE_URL, ProductDTO[].class);

        // Save to local DB
        List<Product> productEntities = Arrays.stream(products)
            .map(this::convertToEntity)
            .collect(Collectors.toList());

        productRepository.saveAll(productEntities);

        return Arrays.asList(products);
    }

    public ProductDTO getProductById(Long id) {
        return restTemplate.getForObject(BASE_URL + "/" + id, ProductDTO.class);
    }

    public List<ProductDTO> getLimitedSortedProducts(int limit, String sort) {
        String url = BASE_URL + "?limit=" + limit + "&sort=" + sort;
        ResponseEntity<ProductDTO[]> response = restTemplate.getForEntity(url, ProductDTO[].class);
        return Arrays.asList(response.getBody());
    }

    public List<ProductDTO> getProductsByCategory(String category) {
        String url = BASE_URL + "/category/" + category;
        ResponseEntity<ProductDTO[]> response = restTemplate.getForEntity(url, ProductDTO[].class);
        return Arrays.asList(response.getBody());
    }

    public List<String> getAllCategories() {
        String url = BASE_URL + "/categories";
        ResponseEntity<String[]> response = restTemplate.getForEntity(url, String[].class);
        return Arrays.asList(response.getBody());
    }

    private Product convertToEntity(ProductDTO dto) {
    	Product product = new Product();
        product.setId(dto.getId());
        product.setTitle(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setCategory(dto.getCategory());
        product.setImage(dto.getImage());
        product.setPrice(dto.getPrice());
        return product;
    }
}
