Feature: Admin Promotional Price Calculations

    Scenario: Applying a store-wide holiday discount to items marked as Promotional
        Given a product "PROD-TOY-1" exists with a base price of 20.00
        And its dynamic attributes include "eligible_for_promo" set to true
        When an admin activates a "25%" store-wide holiday markdown
        Then the active retail price for "PROD-TOY-1" should calculate to 15.00

    Scenario: Non-promotional items retain their original price during a markdown
        Given a product "PROD-MILK-1" exists with a base price of 4.00
        And its dynamic attributes include "eligible_for_promo" set to false
        When an admin activates a "25%" store-wide holiday markdown
        Then the active retail price for "PROD-MILK-1" should remain 4.00
    
    Scenario: Promotional price is not appliable when the markdown is deactivated
        Given a product "PROD-TOY-1" exists with a base price of 20.00
        And its dynamic attributes include "eligible_for_promo" set to true
        When an admin deactivates the "25%" store-wide holiday markdown
        Then the active retail price for "PROD-TOY-1" should revert to 20.00

    Scenario: Promotional expired date has passed, price should revert to original
        Given a product "PROD-TOY-1" exists with a base price of 20.00
        And its dynamic attributes include "eligible_for_promo" set to true
        And a "25%" store-wide holiday markdown is active with an expiration date of "2024-12-31"
        When the current date is "2025-01-01"
        Then the active retail price for "PROD-TOY-1" should revert to 20.00

    Scenario: Promotional price is applied only to eligible products
        Given a product "PROD-TOY-1" exists with a base price of 20.00
        And its dynamic attributes include "eligible_for_promo" set to true
        And a product "PROD-MILK-1" exists with a base price of 4.00
        And its dynamic attributes include "eligible_for_promo" set to false
        When an admin activates a "25%" store-wide holiday markdown
        Then the active retail price for "PROD-TOY-1" should calculate to 15.00
        And the active retail price for "PROD-MILK-1" should remain 4.00