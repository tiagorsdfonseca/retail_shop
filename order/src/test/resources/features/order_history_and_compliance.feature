Feature: Request Order History and Compliance

    Scenario: Returned data includes well-formed order history records and is compliant with data privacy standards
       Given a customer has made 5 previous purchases through the Order Service
       When the customer requests their order history
       Then the Order Service should return a list of 5 order records
       And each order record should include a valid order ID, product details, purchase date, and transaction status
       And the returned data should not include any personally identifiable information (PII) such as payment details or customer contact information

    Scenario: Order history data is properly paginated and sorted by purchase date
         Given a customer has made 20 previous purchases through the Order Service
         When the customer requests their order history with a page size of 5
         Then the Order Service should return the first 5 order records sorted by purchase date in descending order
         And the response should include pagination metadata indicating the total number of records, current page, and total pages