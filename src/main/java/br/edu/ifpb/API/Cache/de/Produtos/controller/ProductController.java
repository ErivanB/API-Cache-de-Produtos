package br.edu.ifpb.API.Cache.de.Produtos.controller;

import br.edu.ifpb.API.Cache.de.Produtos.model.Product;
import br.edu.ifpb.API.Cache.de.Produtos.service.MemcachedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final MemcachedService memcachedService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = memcachedService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        Product product = memcachedService.getProduct(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        if (product.getId() == null) {
            product.setId(UUID.randomUUID().toString());
        }
        memcachedService.saveProduct(product);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product product) {
        Product existingProduct = memcachedService.getProduct(id);
        if (existingProduct != null) {
            product.setId(id);
            memcachedService.updateProduct(product);
            return ResponseEntity.ok(product);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        Product existingProduct = memcachedService.getProduct(id);
        if (existingProduct != null) {
            memcachedService.deleteProduct(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/cache")
    public ResponseEntity<String> clearProductCache(@PathVariable String id) {
        memcachedService.deleteProduct(id);
        return ResponseEntity.ok("Cache do produto limpo");
    }

    @PostMapping("/cache/clear")
    public ResponseEntity<String> clearAllCache() {
        memcachedService.clearCache();
        return ResponseEntity.ok("Todo o cache foi limpo");
    }
}
