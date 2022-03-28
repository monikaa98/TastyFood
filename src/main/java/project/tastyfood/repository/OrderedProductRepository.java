package project.tastyfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.tastyfood.model.entity.OrderedProduct;

@Repository
public interface OrderedProductRepository extends JpaRepository<OrderedProduct, String> {
}
