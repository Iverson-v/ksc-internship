package com.ksyun.trade.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderForGoodsDTO {
    private String upsteam;
    private int id;
    private BigDecimal priceValue;
    private User user;
    private Region region;
    private List<Config> configs;

    @Data
    public static class User {
        private String username;
        private String email;
        private String phone;
        private String address;

        public User() {
        }

        public User(String username, String email, String phone, String address) {
            this.username = username;
            this.email = email;
            this.phone = phone;
            this.address = address;
        }
    }

    @Data
    public static class Region {
        public String code;
        public String name;

        public Region(){

        }

        public Region(String code,String name){
            this.code=code;
            this.name=name;
        }

        // Constructors, Getters, and Setters
        // ...
    }

    @Data
    public static class Config {
        public String itemNo;
        public String itemName;
        public String unit;
        public int value;
        public Config() {
        }

        public Config(String itemNo, String itemName, String unit, int value) {
            this.itemNo = itemNo;
            this.itemName = itemName;
            this.unit = unit;
            this.value = value;
        }
// Constructors, Getters, and Setters
        // ...
    }
}
