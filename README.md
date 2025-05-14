# Charity Collection Box Management System

A Spring Boot application for managing collection boxes during fundraising events for charity organizations.

## Features

- Create and manage fundraising events
- Register and assign collection boxes to events
- Handle money in different currencies (EUR, USD, GBP, PLN)
- Real-time currency exchange rates from external API
- Automatic currency conversion when transferring money
- Track collection box status (assigned/empty)
- Generate financial reports
- Interactive API documentation with Swagger UI

## Technologies Used

- Java 21
- Spring Boot 3.4.5
- Spring Data JPA
- H2 In-memory Database
- Spring WebFlux (for currency API calls)
- Frankfurter API for currency exchange rates
- Swagger UI (SpringDoc OpenAPI) for API documentation
- Maven

## Currency Exchange Feature

The application fetches real-time currency exchange rates from the [Frankfurter API](https://www.frankfurter.app/), a free and open-source currency exchange API. The rates are automatically refreshed every hour (configurable).

Supported currencies:
- EUR (Euro)
- USD (US Dollar)
- GBP (British Pound)
- PLN (Polish Złoty)

### Currency Exchange Configuration

You can configure the currency exchange feature in the `application.properties` file:

```properties
# Currency Exchange Configuration
app.currency.useExternalApi=true
app.currency.refreshRateMs=3600000
app.currency.baseCurrency=PLN
```

- `useExternalApi`: Set to `false` to use default hardcoded rates instead of the API
- `refreshRateMs`: Rate refresh interval in milliseconds (default: 1 hour)
- `baseCurrency`: Base currency for exchange rate calculations (default: PLN)

## Building the Application

To build the application, execute the following Maven command:

```bash
mvn clean package
```

## Running the Application

After building, you can run the application using:

```bash
java -jar target/LAT2025-0.0.1-SNAPSHOT.jar
```

Or use Maven Spring Boot plugin:

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

## API Documentation

The application includes Swagger UI for interactive API documentation and testing:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI Specification: `http://localhost:8080/api-docs`

You can use Swagger UI to explore and test all available endpoints without needing a separate API client.

## API Endpoints

### Fundraising Events

1. **Create a new fundraising event**
   - URL: `POST /api/fundraising-events`
   - Request Body:
     ```json
     {
       "name": "Charity One",
       "accountCurrency": "EUR"
     }
     ```

2. **Get all fundraising events**
   - URL: `GET /api/fundraising-events`

3. **Get a fundraising event by ID**
   - URL: `GET /api/fundraising-events/{id}`

4. **Generate financial report**
   - URL: `GET /api/fundraising-events/financial-report`

### Collection Boxes

1. **Register a new collection box**
   - URL: `POST /api/collection-boxes`
   - Request Body:
     ```json
     {
       "identifier": "BOX-001"
     }
     ```

2. **List all collection boxes**
   - URL: `GET /api/collection-boxes`

3. **Get a collection box by ID**
   - URL: `GET /api/collection-boxes/{id}`

4. **Unregister (remove) a collection box**
   - URL: `DELETE /api/collection-boxes/{id}`

5. **Assign a collection box to a fundraising event**
   - URL: `POST /api/collection-boxes/{id}/assign`
   - Request Body:
     ```json
     {
       "fundraisingEventId": 1
     }
     ```

6. **Add money to a collection box**
   - URL: `POST /api/collection-boxes/{id}/add-money`
   - Request Body:
     ```json
     {
       "amount": 100.50,
       "currency": "USD"
     }
     ```

7. **Empty a collection box**
   - URL: `POST /api/collection-boxes/{id}/empty`

## Example Usage

### Create a fundraising event

```bash
curl -X POST http://localhost:8080/api/fundraising-events \
  -H "Content-Type: application/json" \
  -d '{"name":"Charity One","accountCurrency":"EUR"}'
```

### Register a collection box

```bash
curl -X POST http://localhost:8080/api/collection-boxes \
  -H "Content-Type: application/json" \
  -d '{"identifier":"BOX-001"}'
```

### Assign a collection box to a fundraising event

```bash
curl -X POST http://localhost:8080/api/collection-boxes/1/assign \
  -H "Content-Type: application/json" \
  -d '{"fundraisingEventId":1}'
```

### Add money to a collection box

```bash
curl -X POST http://localhost:8080/api/collection-boxes/1/add-money \
  -H "Content-Type: application/json" \
  -d '{"amount":100.50,"currency":"USD"}'
```

### Empty a collection box

```bash
curl -X POST http://localhost:8080/api/collection-boxes/1/empty
```

### Generate a financial report

```bash
curl -X GET http://localhost:8080/api/fundraising-events/financial-report
```

## H2 Database Console

The H2 database console is available at: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:charitydb`
- Username: `sa`
- Password: `password` 