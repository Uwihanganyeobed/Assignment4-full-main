# Mobile Computing Assignment 4 - Android App

Android application that communicates with a local web server using Volley for network requests.

## Features

- **NetworkActivity**: New activity accessible from anywhere in the app
- **RecyclerView**: Displays list of products from the database
- **Fragment**: Form for creating new products using Volley POST request
- **Volley Integration**: GET and POST requests to web server API
- **Real-time Updates**: New products appear immediately in RecyclerView after creation

## Setup Instructions

### Prerequisites
- Android Studio
- Web server running on localhost:3000 (see Assignment4 Web folder)

### Building the App

1. **Open Project**
   - Open the `Assignement4` folder in Android Studio

2. **Sync Gradle**
   - Android Studio should automatically sync Gradle dependencies
   - If not, click "Sync Now" when prompted

3. **Configure Server URL**
   - For **Android Emulator**: The default URL `http://10.0.2.2:3000` should work
   - For **Physical Device with Tethering**:
     - Find your computer's IP address:
       - Windows: Run `ipconfig` in Command Prompt, look for "IPv4 Address"
       - Example: `192.168.43.1`
     - Update `BASE_URL` in:
       - `NetworkActivity.java` (line ~30)
       - `ProductFormFragment.java` (line ~20)
     - Change from: `http://10.0.2.2:3000/api/products`
     - Change to: `http://YOUR_COMPUTER_IP:3000/api/products`

4. **Build APK**
   - Go to: `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
   - Or use: `Build` → `Generate Signed Bundle / APK` for release build
   - APK will be in: `app/build/outputs/apk/debug/app-debug.apk`

## Project Structure

```
app/src/main/
├── java/com/example/assignement4/
│   ├── MainActivity.java          # Main entry point
│   ├── NetworkActivity.java       # Activity with RecyclerView and Fragment
│   ├── ProductFormFragment.java   # Fragment with form and Volley POST
│   ├── ProductAdapter.java        # RecyclerView adapter
│   └── Product.java               # Product model class
├── res/
│   ├── layout/
│   │   ├── activity_main.xml       # Main activity layout
│   │   ├── activity_network.xml   # Network activity layout
│   │   ├── fragment_product_form.xml  # Form fragment layout
│   │   └── item_product.xml       # RecyclerView item layout
│   └── ...
└── AndroidManifest.xml            # App manifest with INTERNET permission
```

## How It Works

1. **MainActivity**: Entry point with button to open NetworkActivity
2. **NetworkActivity**: 
   - Contains a Fragment (form) at the top
   - Contains a RecyclerView at the bottom
   - Makes GET request on load to fetch all products
3. **ProductFormFragment**:
   - Form with fields: Name, Description, Price, Category ID
   - Makes POST request when form is submitted
   - Notifies NetworkActivity when product is created
4. **ProductAdapter**: 
   - Displays products in RecyclerView
   - Updates when new products are added

## API Endpoints Used

- `GET /api/products` - Fetch all products (JsonArrayRequest)
- `POST /api/products` - Create new product (JsonObjectRequest)

## Testing

1. **Start Web Server**
   ```bash
   cd "Assignment4 Web"
   npm install
   npm start
   ```

2. **Run Android App**
   - Connect device/emulator
   - Click "Run" in Android Studio
   - Or install the generated APK

3. **Test Flow**
   - Open app → Click "Open Network Activity"
   - Fill form → Click "Create Product"
   - Verify product appears in RecyclerView below
   - Check web interface to verify product was saved

## Troubleshooting

### Connection Errors
- **"Network error"**: 
  - Ensure web server is running
  - Check server URL is correct
  - For physical device: Ensure tethering is active and IP is correct
  - Check firewall allows connections on port 3000

### Build Errors
- **Gradle sync failed**: 
  - Check internet connection
  - Try: `File` → `Invalidate Caches / Restart`

### App Crashes
- Check Logcat for error messages
- Ensure INTERNET permission is in AndroidManifest.xml
- Verify all dependencies are synced

## Dependencies

- Volley: Network requests
- RecyclerView: List display
- Fragment: Form UI component
- Material Components: UI elements

All dependencies are managed through `gradle/libs.versions.toml`.

