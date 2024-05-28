# Ivy Bank Api

This project contains the backend/business logic part of the new Ivy Bank API with the ivy-bank-db module. 
It allows to create customers and their accounts. Credit and debit transactions can be made on these accounts.

# Getting Started

## Docker

A Dockerfile is present with a PostgreSQL database.
The container will expose PostgreSQL port 6432.

Run database locally with Docker in `/ivy-bank-db` submodule
```bash
docker-compose up
```

## Liquibase
Apply Liquibase migrations in `/ivy-bank-db` submodule
```bash
./liquibase/deployment.sh
```

## Spring Boot
Run Spring Boot
```bash
./gradlew bootRun
```


## Endpoints in Ivy Bank API

This API contains following endpoints:

* `POST /customer`

    - allows to create new customer
    - request body example:
    `{
        "firstName": "Mari",
        "lastName": "Maasikas",
        "userType": "PRIVATE"
    }`


* `POST /account`

    - allows to create account for the customer
    - request body example:
    `{
        "customerId": 6,
        "accountName": "Mari Maasikas"
    }`
    - parameter "customerId" is required 


* `GET /account/{accountId}"`

    - allows to find account by account id. Can be used to get account balance.
  

* `POST /transaction`

    - allows to create transactions for a certain account

    - request body example:
      `{
      "accountId": 4,
      "amount": 20.00,
      "currency": "EUR",
      "sender": "Toomas Linnupoeg",
      "receiver": "Mari Maasikas",
      "description": "Lilled",
      "creditDebitIndicator": "CRDT"
      }`

    - parameter "accountId" is required
    - for credit transaction use creditDebitIndicator value "CRDT" and amount must be positive
    - for debit transaction use creditDebitIndicator value "DBIT" and amount must be negative


* `GET /transaction-history`

    - allows to find all account transactions. Results can be limited to certain period by using fromDate amd toDate 
    - query parameters are accountId, fromDate and toDate


    



