# Mobile Computing Assignment 4 - Complete Setup Guide

This project consists of two parts:
1. **Web Application** (Assignment4 Web folder) - Backend with database and API
2. **Android Application** (Assignement4 folder) - Mobile app with Volley integration

## Quick Start

### Step 1: Setup Web Application

1. **Navigate to web folder**
   ```bash
   cd "Assignment4 Web"
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start the server**
   ```bash
   npm start
   ```

4. **Verify it's running**
   - Open browser: http://localhost:3000
   - You should see the home page with statistics

### Step 2: Setup Android Application

1. **Open in Android Studio**
   - Open the `Assignement4` folder in Android Studio
   - Wait for Gradle sync to complete

2. **Configure Server URL for Your Device**

   **For Android Emulator:**
   - Default URL `http://10.0.2.2:3000` should work
   - No changes needed

   **For Physical Device (Tethering):**
   - Find your computer's IP address:
     - **Windows**: Open Command Prompt, run `ipconfig`
     - Look for "IPv4 Address" under your network adapter
     - Example: `192.168.43.1` or `192.168.1.100`
   - Update these files:
     - `NetworkActivity.java` (line ~30)
     - `ProductFormFragment.java` (line ~20)
   - Change: `http://10.0.2.2:3000/api/products`
   - To: `http://YOUR_IP:3000/api/products`
   - Example: `http://192.168.43.1:3000/api/products`

3. **Build and Run**
   - Connect device/emulator
   - Click "Run" button in Android Studio
   - Or build APK: `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`

## Testing the Complete Flow

1. **Start Web Server** (from Assignment4 Web folder)
   ```bash
   npm start
   ```

2. **Open Web Interface**
   - Go to http://localhost:3000
   - Navigate to Categories or Products
   - Create some test data

3. **Run Android App**
   - Open app on device/emulator
   - Click "Open Network Activity"
   - You should see existing products in RecyclerView

4. **Create Product from Android**
   - Fill in the form:
     - Name: "Test Product"
     - Description: "Created from Android"
     - Price: 29.99
     - Category ID: 1
   - Click "Create Product"
   - Product should appear immediately in RecyclerView
   - Check web interface - product should be there too!

## Project Structure

```
Assignment4 full/
├── Assignment4 Web/          # Web Application
│   ├── server.js            # Express server with API
│   ├── package.json         # Node.js dependencies
│   ├── database.db          # SQLite database (created automatically)
│   └── public/              # Web interface HTML files
│       ├── index.html
│       ├── categories.html
│       └── products.html
│
└── Assignement4/            # Android Application
    ├── app/
    │   ├── src/main/
    │   │   ├── java/com/example/assignement4/
    │   │   │   ├── MainActivity.java
    │   │   │   ├── NetworkActivity.java
    │   │   │   ├── ProductFormFragment.java
    │   │   │   ├── ProductAdapter.java
    │   │   │   └── Product.java
    │   │   └── res/layout/
    │   │       ├── activity_main.xml
    │   │       ├── activity_network.xml
    │   │       ├── fragment_product_form.xml
    │   │       └── item_product.xml
    │   └── build.gradle.kts
    └── gradle/
```

## Requirements Checklist

✅ **Web Application**
- [x] Local database (SQLite)
- [x] Two linked tables (Categories and Products)
- [x] Full CRUD operations
- [x] Web interface for managing data
- [x] JSON API endpoints for Android

✅ **Android Application**
- [x] NetworkActivity accessible from MainActivity
- [x] RecyclerView to display products
- [x] Fragment with form for creating products
- [x] Volley POST request to create records
- [x] Volley GET request to fetch and display records
- [x] Real-time updates in RecyclerView
- [x] INTERNET permission in manifest

✅ **Integration**
- [x] Android app communicates with web server
- [x] New records appear in both web app and Android app
- [x] Works with localhost (tethering for physical devices)

## API Endpoints

### Categories
- `GET /api/categories` - Get all categories
- `GET /api/categories/:id` - Get category by ID
- `POST /api/categories` - Create category
- `PUT /api/categories/:id` - Update category
- `DELETE /api/categories/:id` - Delete category

### Products
- `GET /api/products` - Get all products (with category info)
- `GET /api/products/:id` - Get product by ID
- `POST /api/products` - Create product
- `PUT /api/products/:id` - Update product
- `DELETE /api/products/:id` - Delete product

## Building APK

### Debug APK
1. In Android Studio: `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
2. APK location: `app/build/outputs/apk/debug/app-debug.apk`

### Release APK (App Bundle)
1. `Build` → `Generate Signed Bundle / APK`
2. Select "Android App Bundle" or "APK"
3. Follow signing wizard
4. Output: `app/build/outputs/bundle/release/` or `app/build/outputs/apk/release/`

## Troubleshooting

### Web Server Issues
- **Port already in use**: Change PORT in `server.js` (default: 3000)
- **Database errors**: Delete `database.db` and restart server
- **Module not found**: Run `npm install` again

### Android Connection Issues
- **"Network error" or timeout**:
  - Verify web server is running
  - Check server URL in code matches your setup
  - For physical device: Ensure USB tethering is active
  - Check firewall allows port 3000
  - Try accessing server URL from device browser first

- **"Connection refused"**:
  - Server might not be running
  - Wrong IP address
  - Firewall blocking connection

### Android Build Issues
- **Gradle sync failed**: 
  - Check internet connection
  - Try: `File` → `Invalidate Caches / Restart`
  - Delete `.gradle` folder and rebuild

- **Class not found errors**:
  - Clean project: `Build` → `Clean Project`
  - Rebuild: `Build` → `Rebuild Project`

## Network Configuration for Tethering

When using a physical device with USB tethering:

1. **Enable USB Tethering** on your phone
2. **Find Computer IP**:
   - Windows: `ipconfig` → Look for IPv4 Address
   - Usually: `192.168.42.1` or `192.168.43.1` or similar
3. **Update Android Code**:
   - Change `BASE_URL` in `NetworkActivity.java` and `ProductFormFragment.java`
   - Replace `10.0.2.2` with your computer's IP
4. **Test Connection**:
   - Open browser on phone
   - Go to `http://YOUR_IP:3000`
   - Should see web interface

## Notes

- The web server runs on `0.0.0.0` to accept connections from any network interface
- Database is created automatically on first run
- Sample data is inserted if tables are empty
- All API responses are in JSON format
- Android app uses JsonObjectRequest for POST and JsonArrayRequest for GET

## Support

If you encounter issues:
1. Check server logs in terminal
2. Check Android Logcat for error messages
3. Verify all dependencies are installed
4. Ensure network connectivity between device and computer

