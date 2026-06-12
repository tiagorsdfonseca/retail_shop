Feature: POS Checkout Validation

    Scenario: Sucessful checkout decreases Inventory
        Given the Catalog Service reports product "PROD-SHIRT-L" has 20 units in stock
        When a customer buys 3 units of "PROD-SHIRT-L" through the Order Service
        Then the Order Service should approve the transaction with status "COMPLETED"
        And the Catalog Service for "PROD-SHIRT_L" should be updated to 17

    Scenario: Unsucessful checkout due to empty stock
        Given the Catalog Service reports product "PROD-SHIRT-L" has 0 units in stock
        When a customer attempts to buy 3 units of "PROD-SHIRT-L" through the Order Service
        And a customer attempts to buy {int} units of {string} through the Order Service
        Then the Order Service should reject the transaction with the status "OUT OF STOCK"
        And no order records should be finalized in the SQL database
        And an automated restock alert should be generated for the store manager
        And the error details should contain "OUT OF STOCK"

    Scenario: Unsucessful checkout due to insufficient stock
        Given the Catalog Service reports product "PROD-SHIRT-L" has 2 units in stock
        When a customer attempts to buy 3 units of "PROD-SHIRT-L" through the Order Service
        Then the Order Service should reject the transaction with the status "INSUFFICIENT STOCK"
        And no order records should be finalized in the SQL database
        And no automated restock alert should be generated for the store manager
        And the error details should contain "INSUFFICIENT STOCK"

    Scenario: Concurrent checkouts leading to stock depletion
        Given the Catalog Service reports product "PROD-SHIRT-L" has 5 units in stock
        When two customers simultaneously attempt to buy 3 units of "PROD-SHIRT-L" through the Order Service
        Then one transaction should be approved with status "COMPLETED" and the other should be canceled with status "INSUFFICIENT STOCK"
        And the Catalog Service for "PROD-SHIRT-L" should be updated to 2 after the first transaction is completed
        And the Catalog Service for "PROD-SHIRT-L" should remain at 2 after the second transaction is canceled