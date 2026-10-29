# Digital Banking System — Beginner Project

A full-stack academic prototype based on the uploaded project notes and 12-slide presentation.

## What is implemented

- Navy-blue responsive web interface: login, dashboard, send money, transaction history, notifications.
- Java + Spring Boot REST API.
- H2 database stored locally in `./data/bankingdb`.
- Seeded demo accounts.
- Transfers between the seeded accounts update balances and are stored in the database.
- Basic fraud rules: flag a transfer if it is at least ₹50,000 OR at least 50% of the sender's current balance.
- Transfer and security notifications stored in the database.
- Pending-verification status for flagged transfers; no debit/credit is made for those transfers.

## Simulated / not implemented

- Login is a demo-only username/password check. It is **not production authentication**; passwords are stored as plain text.
- Fraud detection is rule-based, not machine learning.
- OTP delivery and OTP verification are not implemented. Flagged transfers remain `PENDING_VERIFICATION`.
- Notifications are stored and displayed in the UI; no real SMS/email/push messages are sent.
- Apache Kafka, Redis, SAGA orchestration and Razorpay are **not included in this starter build**. They are in the design notes and can be added as later modules.
- This is not connected to a real bank and never moves real money. Do not enter real credentials.

## Demo credentials

| Username | Password | Account number | Starting balance |
|---|---|---|---:|
| gargi | gargi123 | 1000000001 | ₹75,000 |
| bhumika | demo123 | 1000000002 | ₹25,000 |
| pranav | demo123 | 1000000003 | ₹40,000 |

Transfers to another account number must use one of these seeded accounts.

## Requirements

- Java JDK 17 or newer
- Maven 3.8+ (or use Maven support in your IDE)
- VS Code with Extension Pack for Java, or IntelliJ IDEA

## Run in VS Code / terminal

1. Extract the ZIP.
2. Open the extracted `DigitalBankingSystem` folder in VS Code.
3. Check Java is installed:
   ```bash
   java -version
   mvn -version
   ```
4. Start the app from the project root:
   ```bash
   mvn spring-boot:run
   ```
5. Open `http://localhost:8080` in your browser.
6. Sign in with `gargi` / `gargi123`.
7. Try sending ₹500 to account `1000000002`.
8. To test the fraud rule, try a transfer of ₹50,000 or more (provided the balance is sufficient). It should become pending verification without moving money.

The first run downloads dependencies and creates the local database. Keep the terminal running while using the website.

## H2 database console (development only)

Open `http://localhost:8080/h2-console` and use:
- JDBC URL: `jdbc:h2:file:./data/bankingdb`
- Username: `sa`
- Password: leave blank

Do not expose the H2 console or this demo app publicly.

## API quick reference

- `POST /api/login` — body `{"username":"gargi","password":"gargi123"}`
- `GET /api/account/gargi`
- `GET /api/transactions/gargi`
- `GET /api/notifications/gargi`
- `POST /api/transfer/gargi` — body `{"receiverAccount":"1000000002","receiverName":"Bhumika Pawar","amount":"500","description":"Test transfer"}`
- `GET /api/demo-accounts`

## Suggested next development stages

1. Add Spring Security, password hashing, session/JWT authentication and validation.
2. Add OTP creation, expiry, attempt limits and verification endpoint.
3. Add Kafka events and idempotent consumers.
4. Add Redis velocity tracking and API rate limiting.
5. Implement SAGA orchestration with reliable compensation and tests.
6. Integrate Razorpay in test mode and verify webhook signatures.
7. Add automated unit and integration tests.

## Troubleshooting

- `mvn` not found: install Maven or run through an IDE with Maven support.
- Port 8080 already in use: stop the other app using that port, or change `server.port` in `application.properties`.
- Database seems stale: stop the app and delete the local `data` folder to reset demo data. This deletes local prototype data.
