package br.edu.ifpb.API.Cache.de.Produtos.service;

import br.edu.ifpb.API.Cache.de.Produtos.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemcachedService {

    private final MemcachedClient memcachedClient;
    private static final int EXPIRATION_TIME = 3600; // 1 hora em segundos

    // Salvar produto
    public void saveProduct(Product product) {
        try {
            String key = "product:" + product.getId();
            memcachedClient.set(key, EXPIRATION_TIME, product);
            log.info("Produto salvo no cache: {}", key);
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            log.error("Erro ao salvar produto no cache: {}", e.getMessage());
        }
    }

    // Buscar produto por ID
    public Product getProduct(String id) {
        try {
            String key = "product:" + id;
            Product product = memcachedClient.get(key);
            if (product != null) {
                log.info("Produto encontrado no cache: {}", key);
            }
            return product;
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            log.error("Erro ao buscar produto no cache: {}", e.getMessage());
            return null;
        }
    }

    // Buscar todos os produtos
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try {
            // Esta é uma implementação simplificada
            // Em produção, você teria uma lista de IDs ou usaria patterns
            log.info("Buscando todos os produtos do cache");
            // Nota: Memcached não tem busca por padrão, então mantemos uma lista de IDs
            String allProductsKey = "all_products";
            List<String> productIds = memcachedClient.get(allProductsKey);

            if (productIds != null) {
                for (String productId : productIds) {
                    Product product = getProduct(productId);
                    if (product != null) {
                        products.add(product);
                    }
                }
            }
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            log.error("Erro ao buscar todos os produtos: {}", e.getMessage());
        }
        return products;
    }

    // Atualizar produto
    public void updateProduct(Product product) {
        saveProduct(product); // No Memcached, set substitui se a chave existir
        log.info("Produto atualizado no cache: product:{}", product.getId());
    }

    // Deletar produto
    public void deleteProduct(String id) {
        try {
            String key = "product:" + id;
            memcachedClient.delete(key);

            // Remove da lista de todos os produtos
            updateAllProductsList(id, false);

            log.info("Produto removido do cache: {}", key);
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            log.error("Erro ao deletar produto do cache: {}", e.getMessage());
        }
    }

    // Metodo auxiliar para gerenciar a lista de todos os produtos
    private void updateAllProductsList(String productId, boolean add) {
        try {
            String allProductsKey = "all_products";
            List<String> productIds = memcachedClient.get(allProductsKey);

            if (productIds == null) {
                productIds = new ArrayList<>();
            }

            if (add && !productIds.contains(productId)) {
                productIds.add(productId);
            } else if (!add) {
                productIds.remove(productId);
            }

            memcachedClient.set(allProductsKey, EXPIRATION_TIME, productIds);
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            log.error("Erro ao atualizar lista de produtos: {}", e.getMessage());
        }
    }

    // Limpar o cache
    public void clearCache() {
        try {
            memcachedClient.flushAll();
            log.info("Cache limpo completamente");
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            log.error("Erro ao limpar cache: {}", e.getMessage());
        }
    }
}