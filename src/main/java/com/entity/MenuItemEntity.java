package com.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "MenuItems")
public class MenuItemEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer itemId;

  @ManyToOne
  @JoinColumn(name = "menuId")
  private MenuEntity menu;

  String itemName;
  String itemDescription;
  String itemPrice;
  Boolean activeStatus = true;
  String itemImagePath;
  
}
