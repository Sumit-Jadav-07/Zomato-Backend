# Zomato-Only-Backend

Welcome to the Zomato-Only-Backend repository! This project is the backend service for a Zomato-like application, built using modern Java technologies.

## Technologies Used

- **Java**: Core programming language.
- **Spring Boot**: For creating stand-alone, production-grade Spring-based applications.
- **Spring MVC**: For designing the web layer of the application.
- **Hibernate**: For ORM (Object-Relational Mapping) and managing database interactions.
- **JWT Token**: For secure authentication.
- **Spring Security**: For providing authentication and authorization.
- **Email Service**: For sending emails.
- **Authorize.net SANDBOX API**: For payment integration.

## Getting Started

### Prerequisites

- Java 17
- Maven
- MySQL (or any other relational database)
- Postman (for API testing)

### Note

This repository is a personal project and is not intended for cloning or use by others. However, you are welcome to visit and take inspiration from it. If you notice any weaknesses or areas for improvement, please feel free to mention them. Your feedback is appreciated!

## REST API Endpoints

### User Authentication

- **Login**: `POST /api/public/session/login` - Authenticate user based on role and receive a JWT token.
- **Logout**: `GET /api/public/session/logout` - Logout user.
- **Register Customer**: `POST /api/public/session/customers` - Register a new customer.
- **Register Restaurant**: `POST /api/public/session/restaurants` - Register a new restaurant.
- **Forgot Password**: `POST /api/public/session/forgotpassword` - Update password using email.

### Restaurant APIs

- **List Restaurants**: `GET /api/private/session/restaurants` - Retrieve a list of all restaurants.
- **Get Restaurant**: `GET /api/private/session/restaurants/{restaurantId}` - Retrieve restaurant details by ID.
- **Update Restaurant**: `PUT /api/private/session/restaurants/` - Update an existing restaurant's details.
- **Delete Restaurant**: `DELETE /api/private/session/restaurants/{restaurantId}` - Delete a restaurant by ID.

### Customer APIs

- **List Customers**: `GET /api/private/customers` - Retrieve a list of all customers.
- **Get Customer**: `GET /api/private/customers/{customerId}` - Retrieve customer details by ID.
- **Update Customer**: `PUT /api/private/session/customers/` - Update an existing customer's details.
- **Delete Customer**: `DELETE /api/private/session/customers/{customerId}` - Delete a customer by ID.

### Menu APIs

- **Add Menu**: `POST /api/private/menu/add` - Add a new menu.
- **List Menus**: `GET /api/private/menu/list` - Retrieve a list of all menus.
- **Get Menu**: `GET /api/private/menu/{menuId}` - Retrieve menu details by ID.
- **Update Menu**: `PUT /api/private/menu/{menuId}` - Update an existing menu.
- **Delete Menu**: `DELETE /api/private/menu/{menuId}` - Delete a menu by ID.

### Cart APIs

- **Create Cart**: `POST /api/private/cart/create` - Create a new cart.
- **Get Cart**: `GET /api/private/cart/{cartId}` - Retrieve a specific cart by ID.
- **Get All Carts**: `GET /api/private/cart/list` - Retrieve all carts.
- **Update Cart**: `PUT /api/private/cart/{cartId}` - Update an existing cart.
- **Delete Cart**: `DELETE /api/private/cart/{cartId}` - Delete a cart by ID.

### Cart Item APIs

- **Add Item to Cart**: `POST /api/private/cart/item/add` - Add an item to a cart.
- **Delete Cart Item**: `DELETE /api/private/cart/item/{cartItemId}` - Remove an item from the cart.
- **Update Cart Item**: `PUT /api/private/cart/item/{cartItemId}` - Update an item in the cart.
- **Get Cart Items**: `GET /api/private/cart/items/{cartId}` - Retrieve items for a specific cart.

### Checkout API

- **Checkout**: `POST /api/private/cart/checkout/{cartId}` - Complete the checkout process for a cart.

## Payment Integration

- **Authorize.net SANDBOX API**: Integrated for payment processing. The payment service is configured using the Authorize.net sandbox environment for secure transaction handling.

## Security

- **JWT Token**: Used for securing endpoints. Include the token in the `Authorization` header as `Bearer <token>`.
- **Spring Security**: Configured to protect endpoints and handle authentication.

## Email Service

The backend includes an email service for sending notifications and confirmations. Configure your email service provider in the `application.properties` file.

## License

This project is licensed under the MIT License. See the `LICENSE` file for more details.
