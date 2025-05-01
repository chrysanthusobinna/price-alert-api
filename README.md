# Price Alert Tracking System

A Spring Boot application that tracks product prices and sends alerts when prices drop below a target price.

## Data Loading

The application automatically loads product data from a JSON file (`src/main/resources/static/products.json`) into an H2 in-memory database when it starts up. This ensures that all products are immediately available when the application starts.

## API Endpoints

### 1. Get All Products
```http
GET http://localhost:8081/api/products
```

### 2. Update Product Price
```http
PUT http://localhost:8081/api/products/price?url=https://chrys-online.com/products/macbook-pro-16&price=2299.99
```
Parameters:
- `url` (required): Product URL
- `price` (required): New price (must be greater than 0)

### 3. Create/Update Price Alert
```http
POST http://localhost:8081/api/alerts?productUrl=https://chrys-online.com/products/macbook-pro-16&targetPrice=2299.99&userEmail=test@chrys-online.com&checkFrequency=CUSTOM&customTime=11:12
```
Parameters:
- `productUrl` (required): Product URL
- `targetPrice` (required): Target price (must be less than current price and >= 1)
- `userEmail` (required): Valid email address
- `checkFrequency` (optional, default: DAILY): One of the following:
  - `DAILY_MORNING_09_00` (09:00)
  - `DAILY_AFTERNOON_15_00` (15:00)
  - `DAILY_EVENING_18_00` (18:00)
  - `DAILY_MIDNIGHT_00_00` (00:00)
  - `CUSTOM` (requires customTime parameter)
- `customTime` (required when checkFrequency=CUSTOM): Time in HH:mm format (e.g., 11:12)

### 4. Get User's Alerts
```http
GET http://localhost:8081/api/alerts/user/test@chrys-online.com
```
Parameters:
- `userEmail` (required): Valid email address

### 5. Delete Price Alert
```http
DELETE http://localhost:8081/api/alerts?productUrl=https://chrys-online.com/products/macbook-pro-16&userEmail=test@chrys-online.com
```
Parameters:
- `productUrl` (required): Product URL
- `userEmail` (required): Valid email address

## Validations

The application includes several validations to ensure data integrity and proper functionality:

### Price Validations
- Target price must be less than current price
- Target price must be greater than or equal to 1
- Product price must be greater than or equal to 1
- Price must be a valid number (non-null and numeric)

### Email Validations
- Email addresses must be in a valid format (e.g., user@chrys-online.com)
- Email validation is applied to both request parameters and path variables

### Frequency Validations
- Check frequency must be one of the following values:
  - `DAILY_MORNING_09_00` (09:00)
  - `DAILY_AFTERNOON_15_00` (15:00)
  - `DAILY_EVENING_18_00` (18:00)
  - `DAILY_MIDNIGHT_00_00` (00:00)
  - `CUSTOM` (requires customTime parameter)


## Frequency Options

The system supports the following check frequencies:

| Frequency | Description | Check Time |
|-----------|-------------|------------|
| DAILY_MORNING_09_00 | Daily check at 9:00 AM | 09:00 |
| DAILY_AFTERNOON_15_00 | Daily check at 3:30 PM | 15:30 |
| DAILY_EVENING_18_00 | Daily check at 6:30 PM | 18:00 |
| DAILY_MIDNIGHT_00_00 | Daily check at midnight | 00:00 |
| CUSTOM | Custom time check | User specified |


## Error Responses

The API returns appropriate error messages for various validation failures:
- Invalid email format
- Invalid check frequency
- Missing custom time for CUSTOM frequency
- Invalid custom time format
- Target price validation failures
- Price range validation failures

## Available Products

The following products are loaded from `src/main/resources/static/products.json`:

| Product | URL | Current Price |
|---------|-----|---------------|
| MacBook Pro 16-inch | https://chrys-online.com/products/macbook-pro-16 | $2499.99 |
| iPhone 15 Pro | https://chrys-online.com/products/iphone-15-pro | $999.99 |
| PlayStation 5 Digital Edition | https://chrys-online.com/products/ps5-digital | $399.99 |
| Samsung 65-inch QLED 4K Smart TV | https://chrys-online.com/products/samsung-qled-tv | $1299.99 |
| Apple AirPods Pro (2nd Generation) | https://chrys-online.com/products/airpods-pro | $249.99 |


> [!NOTE]  
> For all testing, please refer to the [TESTING.md](TEST.md) file.

> [!NOTE]  
> For running the project locally, please refer to the [LOCAL_SETUP.md](LOCAL_SETUP.md) file.

- The system uses an H2 in-memory database
- Data is not persisted between application restarts
- Notifications are currently logged to the console
- Products are loaded from JSON file at application startup
- Price checks are scheduled based on the selected frequency
- Custom time must be in 24-hour format (HH:mm)
- Date fields are returned in ISO-8601 array format [year, month, day, hour, minute, second, nanosecond]
