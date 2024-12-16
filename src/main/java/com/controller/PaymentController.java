package com.controller;

import com.entity.CartEntity;
import com.entity.CustomerEntity;
import com.entity.OrderEntity;
import com.repository.CartRepository;
import com.repository.CustomerRepository;
import com.repository.OrderRepository;
import com.service.ChargeCreditCard;
import com.service.CheckoutRequest;
import com.service.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RestController
@RequestMapping("/api/private/payment")
public class PaymentController {

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private OrderRepository orderRepo;

    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(@RequestBody CheckoutRequest checkoutRequest) {
        OrderEntity order = createOrder(checkoutRequest, null);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Your cart is empty");
        }
        if ("COD".equalsIgnoreCase(checkoutRequest.getPaymentType())) {
            orderRepo.save(order);
            return ResponseEntity.ok("Order created successfully with Cash on delivery");
        } else if ("CreditCard".equalsIgnoreCase(checkoutRequest.getPaymentType())) {

            PaymentResponse paymentResponse = processCreditCardPayment(checkoutRequest);
            if (paymentResponse.isSuccess()) {
                orderRepo.save(order);
                return ResponseEntity.ok("Payment successful and Order created successfully");
            } else {
                System.out.println("Payment failed: " + paymentResponse.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Payment failed: " + paymentResponse.getMessage());
            }
        }
        return ResponseEntity.ok("Invalid payment method");
    }

    private PaymentResponse processCreditCardPayment(CheckoutRequest checkoutRequest) {
        return ChargeCreditCard.run(checkoutRequest);
    }

    private OrderEntity createOrder(CheckoutRequest checkoutRequest, PaymentResponse paymentResponse) {
        // Fetch and validate customer
        Optional<CustomerEntity> customerOp = customerRepo.findById(checkoutRequest.getCustomerId());
        if (customerOp.isEmpty()) {
            System.out.println("Customer not found");
            return null;
        }
        CustomerEntity customer = customerOp.get();

        // Fetch and validate cart
        Optional<CartEntity> cartOp = cartRepo.findById(checkoutRequest.getCartId());
        if (cartOp.isEmpty()) {
            System.out.println("Cart not found or empty");
            return null;
        }
        CartEntity cart = cartOp.get();

        // Proceed with order creation
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy");
        OrderEntity order = new OrderEntity();
        order.setCustomer(customer);
        order.setCart(cart);
        order.setTotalPaid(checkoutRequest.getTotalAmount());
        order.setOrderDate(LocalDateTime.now().format(formatter));
        order.setStatus(1);

        if (paymentResponse != null) {
            order.setAuthCode(paymentResponse.getAuthCode());
            order.setTransactionId(paymentResponse.getTransactionId());
            order.setPaymentType("CreditCard");
        } else {
            order.setPaymentType("COD");
        }

        return order;
    }

}
