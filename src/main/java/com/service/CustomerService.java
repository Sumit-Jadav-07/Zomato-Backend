package com.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.entity.CustomerEntity;
import com.entity.RestaurantEntity;
import com.repository.CustomerRepository;
import com.repository.RestaurantRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class CustomerService {

  @Autowired
  HttpServletResponse response;

  @Autowired
  BCryptPasswordEncoder encoder;

  @Autowired
  CustomerRepository customerRepo;

  @Autowired
  RestaurantRepository restaurantRepo;

  public ResponseEntity<?> customerLogin(LoginRequest loginRequest) {
    CustomerEntity customer = customerRepo.findByEmail(loginRequest.getEmail());
    if (customer != null) {
      String encryptedPassword = customer.getPassword();
      if (encoder.matches(loginRequest.getPassword(), encryptedPassword)) {
        Cookie cookie = new Cookie("customer", String.valueOf(customer.getCustomerId()));
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(15 * 24 * 60 * 60);
        response.addCookie(cookie);
        List<RestaurantEntity> restaurants = restaurantRepo.findAll();
        LoginResponse login = new LoginResponse("Login Successful", restaurants);
        return ResponseEntity.ok(login);
      } else {
        return ResponseEntity.badRequest().body("Invalid Password");
      }
    }
    return ResponseEntity.ok("Invalid Email");
  }

  // Authenticating customer by email and password
  // public CustomerEntity authenticateCustomer(String email, String password){
  //   CustomerEntity customer = customerRepo.findByEmail(email);
  //   if(customer != null && encoder.matches(password, customer.getPassword())){
  //     return customer;
  //   }
  //   return null;
  // }

  // Checking if email exist or not
  public CustomerEntity authenticateCustomer(String email){
    CustomerEntity customer = customerRepo.findByEmail(email);
    if(customer != null){
      return customer;
    } 
    return null;
  }

  public void saveToken(String email, String token){
    CustomerEntity customer = customerRepo.findByEmail(email);
    customer.setCusToken(token);
    customerRepo.save(customer);
  }

  public String getEmailByToken(String token){
    CustomerEntity customer = customerRepo.findByCusToken(token);
    return (customer != null) ? customer.getEmail() : null;
  }

  public Integer getCustomerIdByEmail(String email){
    CustomerEntity customer = customerRepo.findByEmail(email);
    return (customer != null ) ? customer.getCustomerId() : null;
  }

  public boolean removeToken(String token){
    CustomerEntity customer = customerRepo.findByCusToken(token);
    if(customer != null){
      customer.setCusToken(null);
      customerRepo.save(customer);
      return true;
    }
    return false;
  }

}
