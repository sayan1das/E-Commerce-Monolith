package EComm_App.service;

import EComm_App.dto.CartItemRequest;
import EComm_App.model.CartItem;
import EComm_App.model.Product;
import EComm_App.model.User;
import EComm_App.repository.CartItemRepository;
import EComm_App.repository.ProductRepository;
import EComm_App.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;


    public boolean addToCart(String userId, CartItemRequest request) {

        Optional<Product> productOpt = productRepository.findById(request.getProductId());    // Looking for product

        if (productOpt.isEmpty()){
            return false;
        }

        Product product = productOpt.get();

        if (product.getStockQuantity() < request.getQuantity()){
            return false;
        }

        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        if (userOpt.isEmpty()){
            return false;
        }

        User user = userOpt.get();

        CartItem existingCartItem = cartItemRepository.findByUserAndProduct(user , product);
        if (existingCartItem != null){              //Means item already in cart, want to Update the quantity and price

            existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
            existingCartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));
            cartItemRepository.save(existingCartItem);
        }else {
            // create new cart for the user
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            cartItemRepository.save(cartItem);
        }
        return true;

    }

    public boolean deleteItemFromCart(String userId, Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));

        if (productOpt.isPresent() && userOpt.isPresent()){
            cartItemRepository.deleteByUserAndProduct(userOpt.get() , productOpt.get());
            return true;
        }
        return false;

    }

    public List<CartItem> getCart(String userId) {
        return userRepository.findById(Long.valueOf(userId))
                .map(cartItemRepository::findByUser)
                .orElseGet(List::of);
    }

    public void clearCart(String userId) {
        userRepository.findById(Long.valueOf(userId)).ifPresent(cartItemRepository::deleteByUser);
    }
}


/*
public List<CartItem> getCart(String userId) {
        return userRepository.findById(Long.valueOf(userId))
                .map(cartItemRepository::findByUser)
                .orElseGet(List::of);
    }------------------------------ Explanation--------------------------------------------------


Step 1: findById(userId)
        -> Does this USER exist in the database?
        -> YES -> Optional[User]
        -> NO  -> Optional.empty()

Step 2: .map(cartItemRepository::findByUser)
        -> ONLY runs if Step 1 found a user
        -> Calls findByUser(user) -> fetches their cart items
        -> Whatever findByUser returns (even if it's an empty list!) gets wrapped in Optional

Step 3: .orElseGet(List::of)
        -> ONLY triggers if Step 1's Optional was empty (user didn't exist)
        -> Has NOTHING to do with whether the cart itself is empty
 */