package EComm_App.dto;

import lombok.Data;

@Data
public class CartItemRequest {
    private Long productId;
    private Integer quantity;
}
