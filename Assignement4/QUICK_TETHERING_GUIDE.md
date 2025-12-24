# Quick Tethering Setup Guide

## Problem: UI shows but no data loads
This means the app can't connect to your web server. You need to update the IP address in the code.

## Quick Fix (3 Steps):

### Step 1: Find Your Computer's IP

**Windows:**
1. Press `Win + R`
2. Type `cmd` and press Enter
3. Type `ipconfig` and press Enter
4. Look for **IPv4 Address** under your active network
   - Common values: `192.168.42.1`, `192.168.43.1`, or `192.168.1.100`

### Step 2: Update These 2 Files

**File 1: NetworkActivity.java**
- Location: `app/src/main/java/com/example/assignement4/NetworkActivity.java`
- Line ~41: Change `10.0.2.2` to your IP

**File 2: ProductFormDialog.java**
- Location: `app/src/main/java/com/example/assignement4/ProductFormDialog.java`
- Line ~47: Change `10.0.2.2` to your IP

**Example:**
```java
// OLD (for emulator):
private static final String BASE_URL = "http://10.0.2.2:3000/api/products";

// NEW (for tethering - replace with YOUR IP):
private static final String BASE_URL = "http://192.168.42.1:3000/api/products";
```

### Step 3: Test & Rebuild

1. **Test connection from phone browser:**
   - Open browser on phone
   - Go to: `http://YOUR_IP:3000`
   - If you see the web page, connection works!

2. **Rebuild app:**
   - Android Studio: **Build** → **Clean Project**
   - **Build** → **Rebuild Project**
   - Run the app again

## Common Issues:

**"Network error" or timeout:**
- Check firewall allows port 3000
- Make sure web server is running (`npm start` in Assignment4Web folder)
- Verify IP address is correct (run `ipconfig` again)

**Still not working?**
- Try Wi-Fi hotspot instead of USB tethering
- Check if phone browser can access `http://YOUR_IP:3000`
- The IP might change - check `ipconfig` again after reconnecting

