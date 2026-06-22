package EComm_App.repository;

import EComm_App.model.CartItem;
import EComm_App.model.Product;
import EComm_App.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    CartItem findByUserAndProduct(User user, Product product);

    void deleteByUserAndProduct(User user, Product product);

//    User user(User user);

    List<CartItem> findByUser(User user);

    void deleteByUser(User user);
}
