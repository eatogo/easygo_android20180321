package idv.ron.easygo.order;

import idv.ron.easygo.product.Product;

@SuppressWarnings("serial")
public class OrderProduct extends Product {
    private Integer quantity;

    public OrderProduct(Integer food_id, String food_name, Integer food_price, Integer quantity) {
        super(food_id, food_name, food_price);
        this.quantity = quantity;
    }

    public OrderProduct(Product product, Integer quantity) {
        this(product.getFood_id(), product.getFood_name(), product.getFood_price(), quantity);
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
