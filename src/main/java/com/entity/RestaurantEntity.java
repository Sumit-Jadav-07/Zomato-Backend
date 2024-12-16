package com.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Restaurants")
public class RestaurantEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer restaurantId;
  String restaurantName;
  String email;
  String password;
  String contactNumber;
  String category;
  String description;
  String openingHours;
  String closingHours;
  String address;
  Boolean onlineStatus;
  Boolean activeStatus = true;
  String restaurantImagePath;
  String resToken;

  // @OneToMany(mappedBy = "restaurant")
  // private List<RestaurantAddressEntity> RestaurantAddresses;

  // // @OneToMany(mappedBy = "restaurant")
  // // private List<MenuEntity> Menus;

  // @OneToMany(mappedBy = "restaurant")
  // private List<CartEntity> carts;
  
}

