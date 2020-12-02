package com.digiomega.hangrymatesrestaurants.Models;

public class NewOrderModelClass {

    String order_id;
    String deal;
    String status;
    String type;




    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public NewOrderModelClass(){}

    public NewOrderModelClass(String order_id, String deal, String status) {
        this.order_id = order_id;
        this.deal = deal;
        this.status = status;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getDeal() {
        return deal;
    }

    public void setDeal(String deal) {
        this.deal = deal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
