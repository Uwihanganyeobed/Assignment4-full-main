# Assignment 4 Web Application

Web-based application with local SQLite database for Mobile Computing Assignment 4.

## Features

- **Local SQLite Database**: Two linked tables (Categories and Products)
- **Full CRUD Operations**: Create, Read, Update, Delete for both tables
- **Web Interface**: HTML/CSS/JavaScript interface for managing data
- **JSON API**: RESTful API endpoints for Android Volley requests
- **Localhost Only**: Designed to run on localhost:3000

## Setup Instructions

1. **Install Node.js** (if not already installed)
   - Download from https://nodejs.org/

2. **Install Dependencies**
   ```bash
   cd "Assignment4 Web"
   npm install
   ```

3. **Start the Server**
   ```bash
   npm start
   ```

4. **Access the Application**
   - Web Interface: http://localhost:3000
   - API Base URL: http://localhost:3000/api/

## Database Schema

### Categories Table
- `id` (INTEGER PRIMARY KEY)
- `name` (TEXT NOT NULL)
- `description` (TEXT)
- `created_at` (DATETIME)

### Products Table
- `id` (INTEGER PRIMARY KEY)
- `name` (TEXT NOT NULL)
- `description` (TEXT)
- `price` (REAL NOT NULL)
- `category_id` (INTEGER NOT NULL) - Foreign Key to Categories
- `created_at` (DATETIME)

## API Endpoints

### Categories
- `GET /api/categories` - Get all categories
- `GET /api/categories/:id` - Get category by ID
- `POST /api/categories` - Create new category
- `PUT /api/categories/:id` - Update category
- `DELETE /api/categories/:id` - Delete category

### Products
- `GET /api/products` - Get all products (with category info)
- `GET /api/products/:id` - Get product by ID (with category info)
- `POST /api/products` - Create new product
- `PUT /api/products/:id` - Update product
- `DELETE /api/products/:id` - Delete product

## Example API Requests

### Create Category (POST)
```json
POST /api/categories
Content-Type: application/json

{
  "name": "Electronics",
  "description": "Electronic devices"
}
```

### Create Product (POST)
```json
POST /api/products
Content-Type: application/json

{
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "category_id": 1
}
```

### Get All Products (GET)
```
GET /api/products
```

## Notes for Android Development

- The server runs on `0.0.0.0` to accept connections from tethered devices
- When using tethering, find your computer's IP address (e.g., `ipconfig` on Windows)
- Use `http://YOUR_COMPUTER_IP:3000/api/` as the base URL in your Android app
- Example: `http://192.168.43.1:3000/api/products`

## Troubleshooting

- **Port already in use**: Change PORT in server.js to a different port
- **Database errors**: Delete `database.db` file and restart server
- **Connection issues**: Ensure firewall allows connections on port 3000

