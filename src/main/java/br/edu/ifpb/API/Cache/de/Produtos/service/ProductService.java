package br.edu.ifpb.API.Cache.de.Produtos.service;

import br.edu.ifpb.API.Cache.de.Produtos.Repository.ProductRepository;
import br.edu.ifpb.API.Cache.de.Produtos.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final MemcachedClient memcachedClient;
    private static final int EXPIRATION_TIME = 3600; // 1 hora em segundos
    private final ProductRepository productRepository;
    // FUNÇÕES DE BUSCA (READ)
    // Estratégia: Cache-Aside - Ler Cache -> Ler DB -> Atualizar Cache

    public Product getProduct(String id) {
        String key = "product:" + id;
        Product product = null;
        try {
            product = memcachedClient.get(key);
            if (product != null) {
                return product;
            }
        }catch (MemcachedException| TimeoutException| InterruptedException e) {
            log.error("Produto", key ," Não Encontrado no cache, continuando para o bando de dados:");
        }
        //Ler do Banco de Dados
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            product = optionalProduct.get();
            try{memcachedClient.set(key, EXPIRATION_TIME, product);
                log.info("Produto Salvo no cach",key);
        }catch (MemcachedException| TimeoutException| InterruptedException e) {
                log.error("Erro ao salvar produto no cache", e.getMessage());
            }
        }
        else{
            log.warn("Produto não encontrado no bando de dados", id);
        }
        return product;
    }
    public Product saveProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        log.info("Produto salvo no Bando de Dados ", savedProduct.getId());

        String key ="product:" + savedProduct.getId();
        try {
            memcachedClient.delete(key);
            //deleta a chave "all_products" se ela for usada
            log.info("Cache invalidado/deletado para o produto: ", key);
        }catch (MemcachedException| TimeoutException| InterruptedException e) {
            log.error("Erro ao salvar produto no cache", e.getMessage());
        }
        return savedProduct;
        }

        public void deleteProduct(String id) {
        productRepository.deleteById(id);
        log.info("Produto deletado no bando de dados");

        //invalidar/deletar a chave no cache
            String key ="product:" + id;
            try{
                memcachedClient.delete(key);
                log.info("Cache invalidado/deletado para o produto: ", key);
            }catch (MemcachedException| TimeoutException| InterruptedException e) {
                log.error("Erro ao salvar produto no cache", e.getMessage());
            }
        }
    public List<Product> getAllProducts() {
        // Retorna a lista diretamente do repositório
        return productRepository.findAll();
    }
        public void clearCache() {
            try {
                memcachedClient.flushAll();
                log.info("Cache invalidado/deletado para o produto");
            }catch (MemcachedException| TimeoutException| InterruptedException e) {
                log.error("Erro ao salvar produto no cache", e.getMessage());
            }
        }

}