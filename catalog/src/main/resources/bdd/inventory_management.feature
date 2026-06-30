Feature: Retail Inventory Restocking

   # Scenario: Sucessfully restocking products with varying attributes
    #    Given the store manager is logged into the Catalog Admin panel
    #    And a product exists with ID "<product_id>" and current stock is <initial_stock>
    #    When the manager restocks <incoming_stock> units of product "<product_id>"
    #    Then the new total stock for "<product_id>" should be <final_stock>

    Scenario: Sucessfully restocking products with varying attributes
        Given a product exists with ID "PROD-ID-1" and current stock is 3
        When the manager restocks 10 units of product "PROD-ID-1"
        Then the system should respond with "SUCCESS"
        And the new total stock for "PROD-ID-1" should be 13

    Scenario: Rejecting negative stock updates
        Given a product exists with ID "PROD-ERR-1" and current stock is 5
        When the manager restocks -3 units of product "PROD-ERR-1"
        Then the system should respond with "REJECTED"
        And the total stock for "PROD-ERR-1" should remain 5

    Scenario: Handling restock updates for non-existent products
        Given the store manager is logged into the Catalog Admin panel
        When the manager restocks 10 units of product "PROD-UNKNOWN"
        And no product exists with ID "PROD-UNKNOWN"
        Then the system should respond with "PRODUCT UNKNOWN"

    Scenario: Adding stock to a product with zero inventory
        Given a product exists with ID "PROD-ZERO-1" and current stock is 0
        When the manager restocks 15 units of product "PROD-ZERO-1"
        Then the system should respond with "SUCCESS"
        And the new total stock for "PROD-ZERO-1" should be 15