package idv.ron.easygo.order;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class Order implements Serializable {
    private Integer order_id;
    private String order_user;
    private long order_reserve_date;
    private List<OrderProduct> orderProductList;

    public Order() {
    }

    public Order(Integer order_id, String order_user, long order_reserve_date, List<OrderProduct> orderProductList) {
        this.order_id = order_id;
        this.order_user = order_user;
        this.order_reserve_date = order_reserve_date;
        this.orderProductList = orderProductList;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public String getOrder_user() {
        return order_user;
    }

    public void setOrder_user(String order_user) {
        this.order_user = order_user;
    }

    public long getOrder_reserve_date() {
        return order_reserve_date;
    }

    public void setOrder_reserve_date(long order_reserve_date) {
        this.order_reserve_date = order_reserve_date;
    }

    public List<OrderProduct> getOrderProductList() {
        return orderProductList;
    }

    public void setOrderProductList(List<OrderProduct> orderProductList) {
        this.orderProductList = orderProductList;
    }

}
