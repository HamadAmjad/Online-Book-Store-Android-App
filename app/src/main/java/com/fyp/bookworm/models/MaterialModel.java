package com.fyp.bookworm.models;

public class MaterialModel {

    String image, name, city, price, quantity, contactNo, category, id, uid;

    public MaterialModel() {
    }

    public MaterialModel(String image, String name, String price) {
        this.image = image;
        this.name = name;
        this.price = price;
    }

    public MaterialModel(String image, String name, String city, String price, String quantity, String contactNo, String category, String id, String uid) {
        this.image = image;
        this.name = name;
        this.city = city;
        this.price = price;
        this.quantity = quantity;
        this.contactNo = contactNo;
        this.category = category;
        this.id = id;
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
