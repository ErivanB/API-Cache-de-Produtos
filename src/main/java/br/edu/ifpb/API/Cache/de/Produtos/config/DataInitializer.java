package br.edu.ifpb.API.Cache.de.Produtos.config;

import br.edu.ifpb.API.Cache.de.Produtos.model.Product;
import br.edu.ifpb.API.Cache.de.Produtos.service.MemcachedService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final MemcachedService memcachedService;

    @PostConstruct
    public void init() {
        // Adiciona alguns produtos de exemplo
        List<Product> sampleProducts = Arrays.asList(
                new Product("1", "Laptop Gamer", "Notebook para jogos de alta performance",
                        new BigDecimal("2999.99"), 10, "Eletrônicos"),
                new Product("2", "Smartphone", "Celular com câmera de 48MP",
                        new BigDecimal("899.99"), 25, "Eletrônicos"),
                new Product("3", "Headphone Bluetooth", "Fone de ouvido sem fio",
                        new BigDecimal("199.99"), 30, "Áudio")
        );

        sampleProducts.forEach(memcachedService::saveProduct);
        System.out.println("Produtos de exemplo carregados no cache!");
    }
}