package com.example.muhammed.advertiapp;

/**
 * Created by mido elgebaly1 on 11/6/2017.
 */

public class UserClass {

    private String id;
    private String name;
    private String image_path;
    private String age;
    private String address;
    private String phone_number;
    private String email;
    private String password;
    private String type;

    public UserClass(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserClass(String id, String name, String image_path, String age, String address, String phone_number, String email,
                     String password, String type) {
        this.id = id;
        this.name = name;
        this.image_path = image_path;
        this.age = age;
        this.address = address;
        this.phone_number = phone_number;
        this.email = email;
        this.password = password;
        this.type = type;
    }

    public UserClass(String id, String name, String image_path, String age, String address, String phone_number, String email, String type) {
        this.id = id;
        this.name = name;
        this.image_path = image_path;
        this.age = age;
        this.address = address;
        this.phone_number = phone_number;
        this.email = email;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
