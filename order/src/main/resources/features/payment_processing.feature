Feature: Process Payments

    Scenario: Sucessful customer checkout via credit card
        Given an order has been created with a total amount of 45.00
        And the payment gateway accepts the transaction with authorization "AUTH-12345"
        When the payment is processed for this order
        Then the Order Service should mark thr order as "PAID"
        And the transaction reference "AUTH-12345" must be recorded in the SQL database

    Scenario: Payment declined due to insufficient funds
        Given an order has been created with a total amount of 120.00
        And the payment gateway rejects the transaction with code "INSUFFICIENT_FUNDS"
        When teh payment is processed for this order
        Then the Order Service should reject the payment
        And the order status should be updated to "PAYMENT_FAILED"
        And the customer should be prompted to provide an alternative payment method
    
    Scenario: Payment gateway times out during processing
        Given an order has been created with a total amount of 89.99
        And the payment gateway fails to respond within 5 seconds
        When the payment is processed for this order
        Then the Order Service should safely roll back the checkout transaction
        And the order status should be set to "PENDING_RETRY"
        And an admninistrative log should be generated to audit the connection drop

    Scenario: Payment decline due to incorrect card code
        Given an order has been created with a total amount of 69.99
        And the payment gateway rejects the transaction with code "INVALID_CVC"
        When the payment is processed for this Order
        Then the Order Shervice should reject the payment
        And the order status should be updated to "PAYMENT_FAILED"
    
    Scenario: Payment gateway returns an unexpected error
        Given an order has been created with a total amount of 59.99
        And the payment gateway responds with an error code "UNKNOWN_ERROR"
        When the payment is processed for this order
        Then the Order Service should mark the order as "PAYMENT_ERROR"
        And the error details should be logged in the SQL database for further investigation