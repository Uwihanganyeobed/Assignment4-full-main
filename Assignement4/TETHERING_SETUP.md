# Android App Setup with USB Tethering

## Step-by-Step Guide

### Step 1: Find Your Computer's IP Address

**On Windows:**
1. Open Command Prompt (Press `Win + R`, type `cmd`, press Enter)
2. Type: `ipconfig` and press Enter
3. Look for your active network adapter (usually "Ethernet adapter" or "Wireless LAN adapter")
4. Find the **IPv4 Address** - this is your computer's IP
   - Example: `192.168.42.1` or `192.168.43.1` or `192.168.1.100`

**Common IP addresses for tethering:**
- `192.168.42.1` (USB tethering)
- `192.168.43.1` (Wi-Fi hotspot)
- `192.168.1.100` (Local network)

### Step 2: Update Android Code

You need to update the BASE_URL in **TWO files**:

1. **NetworkActivity.java** (around line 41)
2. **ProductFormDialog.java** (around line 47)

**Change from:**
```java
private static final String BASE_URL = "http://10.0.2.2:3000/api/products";
```

**Change to (replace with YOUR IP):**
```java
private static final String BASE_URL = "http://192.168.42.1:3000/api/products";
// Replace 192.168.42.1 with YOUR computer's IP address
```

### Step 3: Enable USB Tethering on Your Phone

1. Connect your phone to computer via USB
2. On your phone: **Settings** → **Network & Internet** → **Hotspot & Tethering**
3. Enable **USB Tethering**
4. Wait a few seconds for connection to establish

### Step 4: Start the Web Server

1. Open Command Prompt or Terminal
2. Navigate to the web folder:
   ```bash
   cd "Assignment4Web"
   ```
3. Start the server:
   ```bash
   npm start
   ```
4. You should see: `Server running on http://localhost:3000`

### Step 5: Test Connection

**Test from your phone's browser first:**
1. Open browser on your phone
2. Go to: `http://YOUR_COMPUTER_IP:3000`
   - Example: `http://192.168.42.1:3000`
3. If you see the web interface, connection works!

### Step 6: Build and Run Android App

1. In Android Studio:
   - **Build** → **Clean Project**
   - **Build** → **Rebuild Project**
2. Connect your phone via USB
3. Click **Run** button (or press Shift+F10)
4. Select your phone as the device
5. Wait for app to install and launch

### Step 7: Test the App

1. Open the app
2. Click "Open Network Activity"
3. You should see products loading in the RecyclerView
4. Click the green **+** button to create a product
5. Select a category from dropdown
6. Fill the form and submit

## Troubleshooting

### "Network error" or No Data Loading

1. **Check IP Address:**
   - Make sure you're using the correct IP from `ipconfig`
   - The IP might change when you reconnect tethering

2. **Check Web Server:**
   - Is the server running? Check terminal for "Server running..."
   - Try accessing `http://YOUR_IP:3000` from phone browser

3. **Check Firewall:**
   - Windows Firewall might be blocking port 3000
   - Go to: **Windows Security** → **Firewall & network protection**
   - Click "Allow an app through firewall"
   - Add Node.js or allow port 3000

4. **Check Tethering:**
   - Disable and re-enable USB tethering
   - Try Wi-Fi hotspot instead of USB tethering
   - Check if phone shows "USB tethering active"

5. **Verify IP Address:**
   - Run `ipconfig` again after enabling tethering
   - The IP might be different

### Still Not Working?

1. **Test with phone browser:**
   - If browser can't access `http://YOUR_IP:3000`, the problem is network/firewall
   - If browser works but app doesn't, check the BASE_URL in code

2. **Check Logcat:**
   - In Android Studio: **View** → **Tool Windows** → **Logcat**
   - Look for error messages when making requests

3. **Try Different IP:**
   - Some networks use different IP ranges
   - Try the IP shown in your phone's network settings

## Quick Configuration

**For Emulator:** Use `10.0.2.2`
**For Physical Device with Tethering:** Use your computer's IP (from `ipconfig`)

## Files to Update

1. `app/src/main/java/com/example/assignement4/NetworkActivity.java` (line ~41)
2. `app/src/main/java/com/example/assignement4/ProductFormDialog.java` (line ~47)

After updating, **rebuild the app** before running!

