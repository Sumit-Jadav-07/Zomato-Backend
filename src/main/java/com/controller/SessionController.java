package com.controller;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.entity.CustomerEntity;
import com.entity.RestaurantEntity;
import com.repository.CustomerRepository;
import com.repository.RestaurantRepository;
import com.service.CustomerService;
import com.service.JWTService;
import com.service.LoginRequest;
import com.service.OtpService;
import com.service.RestaurantService;
import com.service.Services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/public/session")
public class SessionController {

  @Autowired
  CustomerRepository customerRepo;

  @Autowired
  RestaurantRepository restaurantRepo;

  @Autowired
  BCryptPasswordEncoder encoder;

  @Autowired
  OtpService otpservice;

  @Autowired
  JavaMailSender sender;

  @Autowired
  Services service;

  @Autowired
  CustomerService customerService;

  @Autowired
  RestaurantService restaurantService;

  @Autowired
  JWTService jwtService;

  @PostMapping("/customer")
  public String addCustomer(@RequestBody CustomerEntity entity) {
    // entity.setPassword(encoder.encode(entity.getPassword()));
    customerRepo.save(entity);
    return "Success";
  }

  @PostMapping("/restaurant")
  public String addRestaurant(@RequestBody RestaurantEntity entity) {
    restaurantRepo.save(entity);
    return "Success";
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    if (loginRequest.getRole() == null) {
      return ResponseEntity.badRequest().body("Role not specified");
    }

    switch (loginRequest.getRole().toLowerCase()) {
      case "customer":
        CustomerEntity customer = customerService.authenticateCustomer(loginRequest.getEmail());
        if (customer != null) {
          // Generate a random token (not JWT)
          // String token = service.generateToken();// Use a simple random UUID as the token
          String token = jwtService.generateToken(loginRequest.getEmail(), loginRequest.getRole());
          System.out.println("Before");
          System.out.println(token);
          System.out.println("After");
          // customerService.saveToken(loginRequest.getEmail(), token); // Store token in DB or in-memory store like Redis
          List<RestaurantEntity> restaurants = restaurantRepo.findByActiveStatus(true);

          Map<String, Object> response = new HashMap<>();
          response.put("message", "Login Successful as Customer.");
          response.put("token", token);
          response.put("restaurants", restaurants);

          return ResponseEntity.ok()
              .header("Authorization", "Bearer " + token) // Send token in the response header
              .body(response);
        } else {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

      case "restaurant":
        RestaurantEntity restaurant = restaurantService.authenticateRestaurant(loginRequest.getEmail(),
            loginRequest.getPassword());
        if (restaurant != null) {
          // String token = service.generateToken();
          String token = jwtService.generateToken(loginRequest.getEmail(), loginRequest.getRole());
          System.out.println("Before");
          System.out.println(token);
          System.out.println("After");
          // restaurantService.saveToken(loginRequest.getEmail(), token); // Store the token

          Map<String, Object> response = new HashMap<>();
          response.put("message", "Login Successful as Restaurant.");

          return ResponseEntity.ok()
              .header("Authorization", "Bearer " + token)
              .body(response);
        } else {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

      default:
        return ResponseEntity.badRequest().body("Invalid Role Specified");
    }
  }

  @GetMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
    String authHeader = request.getHeader("Authorization");
    if(authHeader != null && authHeader.startsWith("Bearer ")){
      String token = authHeader.substring(7);
      boolean isInvalidated = customerService.removeToken(token) == true || restaurantService.removeToken(token) == true;
      if(isInvalidated){
        return ResponseEntity.ok("Logout successfully");
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
      }
    } else {
      return ResponseEntity.badRequest().body("No token found in request");
    }
  }

  @PostMapping("/sendotp")
  public ResponseEntity<String> sendOtp(@RequestBody LoginRequest loginRequest, HttpSession session) {
    String email = loginRequest.getEmail();
    String role = loginRequest.getRole();
    Object entity = service.findEntityByEmailAndRole(email, role);
    if (entity == null) {
      return ResponseEntity.badRequest().body(role + " email not found");
    }
    String otp = otpservice.getOtp();
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(email);
    message.setSubject("OTP");
    message.setText(otp);
    sender.send(message);
    session.setAttribute("otp", otp);
    session.setAttribute("role", role);
    System.out.println(otp);
    return ResponseEntity.ok("OTP sent successfully");
  }

  @PostMapping("/forgotpassword")
  public ResponseEntity<String> forgotPassword(@RequestBody LoginRequest loginRequest, HttpSession session) {
    String email = loginRequest.getEmail();
    String password = loginRequest.getPassword();
    String otp = loginRequest.getOtp();
    String storedOtp = (String) session.getAttribute("otp");
    String storedRole = (String) session.getAttribute("role");

    if (storedOtp == null || !storedOtp.equals(otp)) {
      return ResponseEntity.badRequest().body("Invalid OTP. Please request a new one");
    }

    Object entity = service.findEntityByEmailAndRole(email, storedRole);

    if (entity == null) {
      return ResponseEntity.badRequest().body(storedRole + " email not found");
    }
    if (entity instanceof CustomerEntity) {
      CustomerEntity customerEntity = (CustomerEntity) entity;
      customerEntity.setPassword(encoder.encode(password));
      customerRepo.save(customerEntity);
    } else if (entity instanceof RestaurantEntity) {
      RestaurantEntity restaurantEntity = (RestaurantEntity) entity;
      restaurantEntity.setPassword(encoder.encode(password));
      restaurantRepo.save(restaurantEntity);
    } else {
      return ResponseEntity.badRequest().body("Invalid user type");
    }
    return ResponseEntity.ok("Password updated successfully");
  }
}
