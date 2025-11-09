# Client Order Management APIs

## Base Configuration
```json
{
  "base_url": "http://localhost:8282",
  "api_prefix": "/api/v1",
  "authentication": "JWT tokens + OAuth2 (Google/GitHub)",
  "documentation": "Available at /swagger-ui/"
}
```

## Client Order APIs

```json
{
  "client_order_apis": [
    {
      "method": "POST",
      "endpoint": "/api/v1/orders/create",
      "summary": "Create a new client order",
      "description": "Creates a new order for a specific client. The order status will always be set to IN_PREPARATION on creation.",
      "access_roles": ["ADMIN", "MANAGER", "SALES", "USER"],
      "request_body": "ClientOrderRequestDto",
      "responses": {
        "201": "Order created successfully",
        "400": "Validation error or bad request",
        "409": "Order code already exists"
      }
    },
    {
      "method": "GET",
      "endpoint": "/api/v1/orders/{id}",
      "summary": "Get order by ID",
      "description": "Retrieve an order by its unique identifier",
      "access_roles": ["ADMIN", "MANAGER", "SALES"],
      "path_parameters": {
        "id": "Order ID (Long)"
      },
      "responses": {
        "200": "Order found",
        "404": "Order not found"
      }
    },
    {
      "method": "PUT",
      "endpoint": "/api/v1/orders/{id}",
      "summary": "Update an order",
      "description": "Modify an existing order. You can update its code, comments, orderDate, and stateOrder.",
      "access_roles": ["ADMIN", "MANAGER", "SALES", "USER"],
      "path_parameters": {
        "id": "Order ID (Long)"
      },
      "request_body": "ClientOrderRequestDto",
      "responses": {
        "200": "Order updated",
        "404": "Order not found",
        "409": "Duplicate order code"
      }
    },
    {
      "method": "GET",
      "endpoint": "/api/v1/orders/client/{clientId}",
      "summary": "Get orders by client",
      "description": "Retrieve all orders that belong to a specific client",
      "access_roles": ["ADMIN", "MANAGER", "SALES", "USER"],
      "path_parameters": {
        "clientId": "Client ID (Long)"
      },
      "responses": {
        "200": "Orders retrieved",
        "404": "Client not found"
      }
    },
    {
      "method": "DELETE",
      "endpoint": "/api/v1/orders/{id}",
      "summary": "Delete an order",
      "description": "Delete an order by its ID. Orders with status DELIVERED or CANCELED cannot be deleted.",
      "access_roles": ["ADMIN", "MANAGER", "SALES", "USER"],
      "path_parameters": {
        "id": "Order ID (Long)"
      },
      "responses": {
        "204": "Order deleted successfully",
        "404": "Order not found",
        "400": "Cannot delete delivered or canceled orders"
      }
    },
    {
      "method": "PATCH",
      "endpoint": "/api/v1/orders/{id}/status",
      "summary": "Update order status",
      "description": "Change the status of an order following allowed transitions",
      "access_roles": ["ADMIN", "MANAGER", "SALES", "USER"],
      "path_parameters": {
        "id": "Order ID (Long)"
      },
      "query_parameters": {
        "status": "New order status (OrderStatus enum)"
      },
      "responses": {
        "200": "Order status updated successfully",
        "400": "Invalid status transition",
        "404": "Order not found"
      }
    },
    {
      "method": "GET",
      "endpoint": "/api/v1/orders/status/{status}",
      "summary": "Get orders by status",
      "description": "Retrieve all orders with the specified status",
      "access_roles": ["ADMIN", "MANAGER", "SALES", "USER"],
      "path_parameters": {
        "status": "Order status (OrderStatus enum)"
      },
      "responses": {
        "200": "Orders found",
        "404": "No orders found for the given status"
      }
    },
    {
      "method": "GET",
      "endpoint": "/api/v1/orders/all",
      "summary": "Get all orders",
      "description": "Retrieve all orders in the system",
      "access_roles": ["ADMIN", "MANAGER", "SALES", "USER"],
      "responses": {
        "200": "Orders retrieved successfully"
      }
    },
    {
      "method": "PATCH",
      "endpoint": "/api/v1/orders/{id}/cancel",
      "summary": "Cancel client order",
      "description": "Cancels a client order and releases stock reservations (not allowed for CONFIRMED orders)",
      "access_roles": ["ADMIN", "MANAGER"],
      "path_parameters": {
        "id": "Order ID (Long)"
      },
      "responses": {
        "200": "Order cancelled successfully",
        "400": "Cannot cancel CONFIRMED order",
        "404": "Order not found"
      }
    }
  ]
}
```

## Order Client Line APIs

```json
{
  "order_client_line_apis": [
    {
      "method": "POST",
      "endpoint": "/api/v1/order-lines/create",
      "summary": "Add a new line to an order",
      "description": "Adds an article to a given order (must be IN_PREPARATION). Snapshots article prices and updates order status to VALIDATED if needed.",
      "access_roles": ["ADMIN", "MANAGER"],
      "request_body": "OrderClientLineRequestDto",
      "responses": {
        "201": "Line created successfully",
        "400": "Validation error or business rule violation",
        "404": "Order or Article not found"
      }
    },
    {
      "method": "GET",
      "endpoint": "/api/v1/order-lines/{id}",
      "summary": "Get a single order line by its ID",
      "description": "Retrieve an order line by its unique identifier",
      "access_roles": ["ADMIN", "MANAGER"],
      "path_parameters": {
        "id": "Order line ID (Long)"
      },
      "responses": {
        "200": "Line found",
        "404": "Line not found"
      }
    },
    {
      "method": "GET",
      "endpoint": "/api/v1/order-lines/order/{clientOrderId}",
      "summary": "Get all lines for a given order",
      "description": "Retrieve all order lines for a specific client order",
      "access_roles": ["ADMIN", "MANAGER"],
      "path_parameters": {
        "clientOrderId": "Client Order ID (Long)"
      },
      "responses": {
        "200": "List of lines"
      }
    },
    {
      "method": "PATCH",
      "endpoint": "/api/v1/order-lines/{id}/quantity",
      "summary": "Update the quantity of a line",
      "description": "Update the quantity of an existing order line",
      "access_roles": ["ADMIN", "MANAGER"],
      "path_parameters": {
        "id": "Order line ID (Long)"
      },
      "query_parameters": {
        "newQuantity": "New quantity (BigDecimal, min: 0.01)"
      },
      "responses": {
        "200": "Line updated successfully",
        "400": "Validation or stock error",
        "404": "Line not found"
      }
    },
    {
      "method": "DELETE",
      "endpoint": "/api/v1/order-lines/{id}",
      "summary": "Remove a line from an order",
      "description": "Removes an article from the order and returns the quantity to stock (if order not delivered)",
      "access_roles": ["ADMIN", "MANAGER"],
      "path_parameters": {
        "id": "Order line ID (Long)"
      },
      "responses": {
        "204": "Line removed successfully",
        "400": "Cannot remove line from a delivered order",
        "404": "Line not found"
      }
    },
    {
      "method": "GET",
      "endpoint": "/api/v1/order-lines/order/{clientOrderId}/total",
      "summary": "Calculate total of an order",
      "description": "Calculate the total amount for all lines in a client order",
      "access_roles": ["ADMIN", "MANAGER"],
      "path_parameters": {
        "clientOrderId": "Client Order ID (Long)"
      },
      "responses": {
        "200": "Total calculated",
        "404": "Order not found"
      }
    }
  ]
}
```

## Request/Response DTOs

### ClientOrderRequestDto
```json
{
  "code": "string",
  "orderDate": "date",
  "comments": "string",
  "clientId": "number"
}
```

### ClientOrderResponseDto
```json
{
  "id": "number",
  "code": "string",
  "orderDate": "date",
  "comments": "string",
  "stateOrder": "string",
  "clientId": "number",
  "clientName": "string",
  "orderClientLineList": [
    {
      "id": "number",
      "quantity": "number",
      "unitPrice": "number",
      "articleId": "number",
      "articleDesignation": "string"
    }
  ],
  "createdDate": "string",
  "updatedDate": "string"
}
```

### OrderClientLineRequestDto
```json
{
  "quantity": "number",
  "clientOrderId": "number",
  "articleId": "number"
}
```

### OrderClientLineResponseDto
```json
{
  "id": "number",
  "quantity": "number",
  "unitPrice": "number",
  "totalPrice": "number",
  "clientOrderId": "number",
  "articleId": "number",
  "articleDesignation": "string",
  "articleCode": "string"
}
```

## Order Status Enum
- PENDING
- CONFIRMED  
- COMPLETED
- CANCELLED

## Business Rules
1. Orders are created with status IN_PREPARATION
2. Only orders in IN_PREPARATION can have lines added
3. Orders with status DELIVERED or CANCELED cannot be deleted
4. CONFIRMED orders cannot be cancelled
5. Removing lines returns quantity to stock (if order not delivered)
6. Stock reservations are released when orders are cancelled

## Authentication
All endpoints require JWT authentication with role-based access control as specified in each endpoint.