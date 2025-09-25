# UV Light Sensor Android App

A Bluetooth Low Energy (BLE) enabled Android application that connects to [UV sensor hardware](https://github.com/SlothCodeSloth/LTR390_BLESensor) to provide real time UV index monitoring and intelligent sunscreen application timing for safe sun exposure.

## 🎯 Overview

The UV Light Sensor app ensures sun safety by providing:
- **Real-time UV monitoring** through a BLE connection to UV Sensor Hardware 
- **Personalized sun exposure calculations** based on skin type, SPF, and altitude
- **Smart sunscreen timing** with countdown alerts and background notifications
- **Interactive UV data visualization** with real time charting
- **Location-aware altitude detection** for enhanced UV exposure accuracy

---

## ✨ Key Features

- 📊 **Real-time UV Index Display**: Live UV measurements
- ⏱️ **Intelligent Timer System**: Personalized countdown based on scientific sun exposure formulas
- 🧴 **Sunscreen Application Tracking**: Smart timing for when sunscreen protection wears off
- 👤 **Personal Profile Management**: Customizable skin type (1-6 scale) based on the Fitzpatrick scale
- 📍 **Location-Based Calculations**: GPS altitude integration for accurate UV Altitude factor
- 📱 **Background Monitoring**: Foreground service maintains timing even when app is minimized
- 🎨 **Theme Support**: Dark/light mode switching with smooth animations
- 📈 **Data Visualization**: Interactive line charts showing UV index trends over time

---

## 🧬 Scientific Foundation

### Sun Exposure Formula
The app uses a scientifically-based formula to calculate safe sun exposure time:

```
Safe Time = (SPF × Skin Factor) / (UV Index × Altitude Factor) × 60 minutes
```

### Fitzpatrick Skin Type Classification
The app implements the **Fitzpatrick scale**, a scientifically recognized system for classifying skin types based on UV sensitivity:

| Type | Classification | Skin Factor | Characteristics |
|------|----------------|-------------|-----------------|
| **Type I** | Very Fair | 0.3 | Always burns, cannot tan |
| **Type II** | Fair | 0.4 | Usually burns, sometimes tans |
| **Type III** | Medium | 0.5 | Sometimes burns, usually tans |
| **Type IV** | Olive | 0.6 | Rarely burns, tans easily |
| **Type V** | Brown | 0.7 | Rarely burns, always tans |
| **Type VI** | Dark Brown | 0.8 | Never burns, always tans |

**Additional Factors:**
- **Altitude Adjustment:** +10% UV intensity per 1000m elevation
- **Safety Limits:** 2-hour minimum, 6-hour maximum exposure times

---

## 🏗️ Tech Stack

| Category | Technology |
|----------|------------|
| **Language** | Java |
| **Architecture** | Fragment-based with Service integration |
| **BLE Communication** | BluetoothGatt, BluetoothLeScanner |
| **Location Services** | Google Play Services Location API |
| **Data Visualization** | MPAndroidChart |
| **Background Processing** | Foreground Service with notifications |
| **Local Storage** | SharedPreferences |
| **UI Components** | Custom animations, Material Design |

---

## 📂 Project Structure

```
app/src/main/java/com.example.bletest3/
├── FragmentPagerAdapter.java
├── HomeFragment.java              # Main UV monitoring and timer logic
├── IntroViewPagerAdapter.java     # Handles the intro screen functions
├── MainActivity.java              # Intro Screen navigation
├── MainActivity2.java             # Fragment navigation and data coordination
├── ProfileFragment.java           # User profile and settings management
├── ScreenItem.java                # Intro Screen Data Model
├── skinType.java                  # Skin type data model
├── skinTypeAdapter.java           # Spinner adapter for skin types
├── skinTypeData.java              # Skin type data provider
└── TimerForegroundService.java    # Background timer service

res/
├── anim/                          # Holds button animations (entering and exiting)
├── drawable/                      # Holds assets related to UI and theming
├── layout/
|   ├── activity_main.xml          # UI for the intro screen
|   ├── activity_main2.xml         # Navigation Container
│   ├── fragment_home.xml          # Main monitoring interface
│   ├── fragment_profile.xml       # Profile setup interface
|   ├── item_skintype.xml          # Skin Type Layout for the Spinenr
│   └── layout_screen              # UI for each intro screen
├── anim/                          # Custom animations
└── menu/                          # Bottom Navigation Menu
```

---

## 🔌 Hardware Requirements

### BLE UV Sensor Device Specifications:
- **Device Name**: Must broadcast as "UVSensor"
- **Service UUID**: `19B10000-E8F2-537E-4F6C-D104768A1214`
- **Characteristic UUID**: `19B10001-E8F2-537E-4F6C-D104768A1214`
- **Data Format**: Integer UV index values (0-10+)
- **Update Frequency**: Configurable (default: 5-second intervals)

### Android Device Requirements:
- Android API level 21+ (Android 5.0 Lollipop)
- Bluetooth Low Energy support
- Location services capability

---

## 🚀 Setup & Installation

### Prerequisites
- Either Android Studio with Android SDK    
  or
- Physical Android device with BLE capability (BLE testing requires real hardware)

### Hardware Setup
1. **Obtain compatible UV sensor hardware** (or build custom BLE UV sensor)
2. **Configure sensor** to broadcast as "UVSensor" with specified UUIDs
3. **Ensure sensor** transmits UV index as integer values

### App Installation
1. **Clone or Download the repository**
   ```bash
   git clone https://github.com/your-username/UV-Sensor-App.git
   cd UV-Sensor-App
   ```

2. **Open in Android Studio**
   - Import project
   - Sync Gradle files

3. **Configure permissions** (automatically handled in AndroidManifest.xml)
   - Bluetooth permissions
   - Location permissions (required for BLE scanning)
   - Foreground service permissions

4. **Build and Install on Emulator or Android Device** 

---

## 📱 Usage Guide

### First Time Setup
1. **Profile Configuration**: Enter your name, select skin type (1-6), and specify sunscreen SPF
2. **Permission Grants**: Allow Bluetooth, location, and notification permissions
3. **Device Pairing**: The app will discover and connect to UV Sensors with a manual button
### Daily Operation
1. **Connect to Sensor**: Tap "Scan" to discover and connect to your UV sensor device
2. **Monitor UV Levels**: View UV index readings and chart data
3. **Apply Sunscreen**: When applying suncreen, tap "Apply Sunscreen" to start the protection timer
4. **Background Monitoring**: Timer continues running even when app is minimized
5. **Reapplication Alerts**: Receive notifications when sunscreen protection expires

### Testing Mode
For development/testing without hardware:
- Use profile name "Testing" to enable simulation mode
- App generates random UV index values and lowered times for testing timer functionality

---

## 🔧 Configuration

### BLE Connection Settings
- **Scan timeout**: 30 seconds
- **Connection retry**: Automatic with user feedback
- **Data refresh**: Every 5 seconds when connected
- **Low power mode**: Optimized for battery efficiency

### Timer Calculations
- **Minimum safe time**: 2 hours (safety override)
- **Maximum calculated time**: 6 hours (practical limit)
- **Real-time adjustment**: Timer adapts to changing UV conditions
- **Background persistence**: Maintains timing when app is backgrounded

### Planned Improvements
- [ ] More UI Animations
- [ ] Cloud data synchronization
- [ ] Saved Historical Tracking
- [ ] Chart Functionality Without Application of Sunscreen
---

## ⚠️ Safety Disclaimer

This app provides estimates based on scientific formulas but should not replace professional medical advice.   
 
---

## 🙏 Acknowledgments

- UV exposure formula based on dermatological research
- MPAndroidChart library for data visualization
- Google Play Services for location integration
- Android BLE documentation and community resources
- Dermatological studies on skin type classification
