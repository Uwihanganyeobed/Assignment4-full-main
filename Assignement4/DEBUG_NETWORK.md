# Debug Network Connection Issues

## Current Configuration
- **IP Address in code:** `192.168.137.1`
- **Port:** `3000`
- **Full URL:** `http://192.168.137.1:3000/api/products`

## Step-by-Step Debugging

### Step 1: Verify Web Server is Running

1. Open Command Prompt
2. Navigate to web folder:
   ```bash
   cd "Assignment4Web"
   ```
3. Start server:
   ```bash
   npm start
   ```
4. **Expected output:**
   ```
   Connected to SQLite database.
   Categories table ready.
   Products table ready.
   Server running on http://localhost:3000
   ```

### Step 2: Test from Computer Browser

1. Open browser on your computer
2. Go to: `http://localhost:3000`
3. **Should see:** The web interface with green theme
4. **If not:** Server is not running correctly

### Step 3: Find Correct IP Address

1. **Enable USB Tethering** on your phone first
2. Open Command Prompt
3. Run: `ipconfig`
4. Look for the adapter that shows "Media disconnected" = **FALSE**
5. Find the **IPv4 Address** - this is your tethering IP

**Common IPs:**
- `192.168.137.1` (Windows Mobile Hotspot)
- `192.168.42.1` (USB Tethering)
- `192.168.43.1` (Wi-Fi Hotspot)

### Step 4: Test from Phone Browser

1. **Enable USB Tethering** on phone
2. Open browser on phone
3. Try these URLs (one should work):
   - `http://192.168.137.1:3000`
   - `http://192.168.42.1:3000`
   - `http://192.168.43.1:3000`
4. **If you see the web interface:** ✅ Connection works!
5. **If "Can't reach this page":** ❌ Network/firewall issue

### Step 5: Check Firewall

**Windows Firewall might be blocking port 3000:**

1. Go to: **Windows Security** → **Firewall & network protection**
2. Click: **Advanced settings**
3. Click: **Inbound Rules** → **New Rule**
4. Select: **Port** → **Next**
5. Select: **TCP**, enter port: **3000** → **Next**
6. Select: **Allow the connection** → **Next**
7. Check all profiles → **Next**
8. Name: "Node.js Server" → **Finish**

### Step 6: Update Android Code

**If phone browser works with a different IP:**

1. Open: `NetworkActivity.java` (line ~41)
2. Change: `192.168.137.1` to the IP that worked
3. Open: `ProductFormDialog.java` (line ~47)
4. Change: `192.168.137.1` to the IP that worked
5. **Rebuild:** Build → Clean Project → Rebuild Project

### Step 7: Check Android Logcat

1. In Android Studio: **View** → **Tool Windows** → **Logcat**
2. Filter by: `Volley` or `NetworkActivity`
3. Look for error messages when app tries to connect
4. Common errors:
   - `UnknownHostException` = Wrong IP address
   - `ConnectException` = Server not running or firewall blocking
   - `TimeoutException` = Connection timeout

## Quick Test Commands

**On your phone browser, test these URLs:**
```
http://192.168.137.1:3000
http://192.168.42.1:3000
http://192.168.43.1:3000
```

**On your computer, test:**
```
http://localhost:3000
```

## Common Solutions

### "Unknown error" or "Connection failed"
- ✅ Web server not running → Start with `npm start`
- ✅ Wrong IP address → Check `ipconfig` and update code
- ✅ Firewall blocking → Allow port 3000 in firewall
- ✅ Tethering not enabled → Enable USB tethering on phone

### "Network error: java.net.UnknownHostException"
- ✅ Wrong IP address → Update BASE_URL in code
- ✅ IP address changed → Run `ipconfig` again

### "Network error: java.net.ConnectException"
- ✅ Server not running → Start server
- ✅ Firewall blocking → Check firewall settings
- ✅ Wrong port → Verify server is on port 3000

## Still Not Working?

1. **Check if server is accessible:**
   - Computer browser: `http://localhost:3000` ✅
   - Phone browser: `http://YOUR_IP:3000` ❌
   - **Problem:** Network/firewall issue

2. **Check IP address:**
   - Run `ipconfig` after enabling tethering
   - IP might be different each time
   - Update code with correct IP

3. **Try Wi-Fi Hotspot instead:**
   - Use phone's Wi-Fi hotspot
   - Connect computer to phone's hotspot
   - Use computer's Wi-Fi IP address

