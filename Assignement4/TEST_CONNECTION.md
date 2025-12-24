# Test Connection - Step by Step

## Your Current IP Address
Based on your system, your tethering IP is likely: **192.168.137.1**

## Step 1: Verify Web Server is Running

1. Open Command Prompt
2. Navigate to web folder:
   ```bash
   cd "Assignment4Web"
   ```
3. Start server:
   ```bash
   npm start
   ```
4. You should see: `Server running on http://localhost:3000`

## Step 2: Test from Phone Browser

1. **Enable USB Tethering on your phone:**
   - Settings → Network & Internet → Hotspot & Tethering
   - Enable "USB Tethering"

2. **On your phone, open browser and go to:**
   ```
   http://192.168.137.1:3000
   ```
   
3. **If you see the web interface:**
   ✅ Connection works! Proceed to Step 3
   
4. **If you see "Can't reach this page":**
   - Check firewall settings
   - Try different IP (run `ipconfig` again after enabling tethering)
   - The IP might be different (check `192.168.42.1` or `192.168.43.1`)

## Step 3: Update Android Code

The code has been updated to use `192.168.137.1`. If your phone browser test worked with this IP, you're good!

**If you need to change it:**
- File: `NetworkActivity.java` (line ~41)
- File: `ProductFormDialog.java` (line ~47)

## Step 4: Rebuild and Run

1. In Android Studio:
   - **Build** → **Clean Project**
   - **Build** → **Rebuild Project**

2. Run the app on your phone

3. Open "Network Activity"

4. You should see products loading! 🎉

## Troubleshooting

### Still no data?

1. **Check Logcat in Android Studio:**
   - View → Tool Windows → Logcat
   - Look for error messages when app tries to load data

2. **Verify IP address:**
   - After enabling tethering, run `ipconfig` again
   - The IP might be different
   - Update the code with the correct IP

3. **Check firewall:**
   - Windows might be blocking port 3000
   - Go to: Windows Security → Firewall
   - Allow Node.js or port 3000 through firewall

4. **Try different IP:**
   - Common tethering IPs:
     - `192.168.42.1`
     - `192.168.43.1`
     - `192.168.137.1` (your current one)
   - Update code and test each one

### Quick Test Commands

**On your phone browser, try these URLs:**
- `http://192.168.137.1:3000` (most likely)
- `http://192.168.42.1:3000`
- `http://192.168.43.1:3000`

Whichever one works in the browser, use that IP in your Android code!

