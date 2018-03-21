package idv.ron.easygo.product;

import java.io.Serializable;


@SuppressWarnings("serial")
public class Product implements Serializable {

    private Integer food_id;

    public Integer getFood_id() {
        return food_id;
    }

    public void setFood_id(Integer food_id) {
        this.food_id = food_id;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public Integer getFood_price() {
        return food_price;
    }

    public void setFood_price(Integer food_price) {
        this.food_price = food_price;
    }

    private String food_name;
    private Integer food_price;

    public Product(Integer food_id, String food_name, Integer food_price) {
        this.food_id = food_id;
        this.food_name = food_name;
        this.food_price = food_price;
    }




    public Product() {

    }

//    private String description;   private String description;



    @Override
    // 要比對欲加入商品與購物車內商品的id是否相同，是則值相同
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        return this.getFood_id() == ((Product) obj).getFood_id();
    }





//    public String getDescription() {
//        return description;
//    }

//    public void setDescription(String description) {
//        this.description = description;
//    }

    @Override
    public String toString() {
        return food_name;
    }

}