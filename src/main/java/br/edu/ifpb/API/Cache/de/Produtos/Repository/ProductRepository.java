package br.edu.ifpb.API.Cache.de.Produtos.Repository;

import br.edu.ifpb.API.Cache.de.Produtos.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
}
