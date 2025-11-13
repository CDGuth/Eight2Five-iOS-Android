# GitHub Copilot Instructions

This file provides guidance for GitHub Copilot on how to effectively use the documentation within this project to generate accurate and relevant code suggestions.

## General Guidance & Documentation Interaction Protocol

Your primary role is to act as an intelligent interface to the project's documentation located in the `.github/docs/` directory. This instruction file is **not** the documentation itself; it is a guide on how to find and use the documentation.

### Core Rule: Consult the Source of Truth

Always prioritize the content within the documentation files over any summary in this file. The documentation files are the "source of truth."

### Step-by-Step Protocol for Using Documentation

1.  **Identify the Relevant Technology:** Based on the user's request, determine which framework or SDK is needed (e.g., Expo, KBeacon Android, KBeacon iOS).

2.  **Locate the Documentation File:** Use the "File Location" specified in the relevant section below to identify the correct documentation file.

3.  **Attempt a Targeted Search First:**
    *   Formulate a precise search query based on the user's request (e.g., a specific class name like `KBeaconsMgr`, a method like `startScanning()`, or a concept like `push notifications`).
    *   Use your search tools to find occurrences of this query within the relevant documentation file. The "Key Topics" listed for each section can help you identify relevant keywords for your search.

4.  **Analyze Search Results:**
    *   If the search yields specific, relevant sections, use that information directly to fulfill the user's request.

5.  **Fallback to a Full Read if Necessary:**
    *   If the targeted search is inconclusive, does not provide enough context, or fails to find the required information, you **must** read the entire content of the relevant documentation file. This ensures you have the complete context.

6.  **Synthesize and Apply:** Use the information gathered from the documentation to generate code, answer questions, or perform the requested task.

---

## 1. Expo Documentation

**File Location:** `.github/docs/expo/llms-txt-documentation.md`

### Overview

This document contains a comprehensive guide to the Expo framework. It is the primary source of truth for any Expo-related tasks.

### Key Topics for Search

Use these keywords as a starting point for searching within the documentation file:

-   **Project Lifecycle:** `Create a project`, `Development builds`, `EAS Build`, `EAS Submit`, `EAS Update`
-   **Core APIs & SDK Modules:** `expo-router`, `expo-av`, `expo-sensors`, `expo-camera`, `expo-file-system`, `expo-image-picker`, `expo-notifications`, `expo-auth-session`
-   **Configuration:** `app.json`, `app.config.js`, `eas.json`

### How to Find Information

-   **For new features:** Search the doc for relevant modules like `Camera`, `Location`, or `Notifications`.
-   **For routing:** Search for `Expo Router`, `routes`, `layouts`, or `navigation`.
-   **For building/deployment:** Search for `EAS Build`, `EAS Submit`, or `EAS Update`.

---

## 2. KBeaconPro Android SDK Documentation

**File Location:** `.github/docs/KBeaconPro Android SDK/github-kkmhogen-kbeaconprodemo_android-an-android-based-demo-for-connecting-kbeaconpro-devices.md`

### Overview

This document provides instructions for interacting with KBeaconPro devices on Android. It covers scanning, connecting, and configuring beacons.

### Key Topics for Search

Use these keywords as a starting point for searching within the documentation file:

-   **Core Classes:** `KBeaconsMgr`, `KBeacon`, `KBConnPara`
-   **Key Operations:** `startScanning()`, `stopScanning()`, `onBeaconDiscovered()`, `connect()`, `modifyConfig()`
-   **Configuration Objects:** `KBCfgCommon`, `KBCfgAdvIBeacon`, `KBCfgTrigger`, `KBCfgSensorHT`
-   **Triggers:** `KBTriggerType`, `KBTriggerAction`, `BtnSingleClick`, `AccMotion`

### How to Find Information

-   **To find beacons:** Search the doc for `KBeaconsMgr` and `startScanning`.
-   **To connect to a beacon:** Search for `KBeacon` and `connect()`.
-   **To change settings:** Search for `modifyConfig()` and the relevant configuration object (e.g., `KBCfgAdvIBeacon`).
-   **For sensor data:** Search for `Sensor parameters` or `readSensorDataInfo`.

---

## 3. KBeaconPro iOS SDK Documentation

**File Location:** `.github/docs/KBeaconPro iOS SDK/github-kkmhogen-kbeaconprodemo_ios.md`

### Overview

This document provides instructions for interacting with KBeaconPro devices on iOS using Swift.

### Key Topics for Search

Use these keywords as a starting point for searching within the documentation file:

-   **Core Classes:** `KBeaconsMgr`, `KBeacon`, `KBConnPara`
-   **Key Operations:** `startScanning()`, `stopScanning()`, `onBeaconDiscovered()`, `connect(password:timeout:delegate:)`, `modifyConfig(obj:callback:)`
-   **Configuration Objects:** `KBCfgCommon`, `KBCfgAdvIBeacon`, `KBCfgTrigger`, `KBCfgSensorHT`
-   **Triggers:** `KBTriggerType`, `KBTriggerAction`, `BtnSingleClick`, `AccMotion`

### How to Find Information

-   **To find beacons:** Search the doc for `KBeaconsMgr.sharedBeaconManager` and `startScanning()`.
-   **To connect to a beacon:** Search for `KBeacon` and `connect(password:timeout:delegate:)`.
-   **To change settings:** Search for `modifyConfig(obj:callback:)` and the relevant configuration object.
-   **For sensor data:** Search for `Sensor parameters` or `readSensorDataInfo(callback:)`.

