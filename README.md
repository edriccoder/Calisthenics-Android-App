# CalesTechSync App

CalesTechSync is an Android application designed to help users achieve their calisthenics goals. This guide will walk you through setting up the app and its backend components.

## Prerequisites

Before setting up the app, ensure you have the following installed on your system:

- Android Studio (latest version)
- XAMPP for running the PHP backend
- MySQL (comes with XAMPP)
- A physical or virtual Android device for testing

## Setup Instructions

### Step 1: Set Up the Backend

1. Locate the `backend_php` folder provided with the project files.
2. Copy the entire `backend_php` folder into the `htdocs` directory of your XAMPP installation.
   - On Windows, this is usually located at: `C:\xampp\htdocs`.
3. Start XAMPP and ensure both the Apache and MySQL modules are running.

### Step 2: Import the Database

1. Open phpMyAdmin by navigating to `http://localhost/phpmyadmin` in your web browser.
2. Click on the **Import** tab.
3. Select the database file located in the `database` folder of the project files.
   - Example: `calestechsync_db.sql`.
4. Click **Go** to import the database.
5. Confirm that a new database (e.g., `calestechsync_db`) has been created with the required tables.

### Step 3: Configure the Android App

1. Open Android Studio.
2. Clone the project repository or import the project files.
3. Open the `Constants.java` file located in the `utils` or `config` package (depending on the project structure).
4. Update the `BASE_URL` to point to your local server:

```java
public static final String BASE_URL = "http://<your-local-ip>/backend_php/";
```

5. CalesTechSync App Setup and Features

### Step 4: Replace `<your-local-ip>`
Replace `<your-local-ip>` with your computer's local IP address (e.g., `192.168.1.100`).
To find your local IP, run `ipconfig` on Windows or `ifconfig` on Mac/Linux.

### Step 5: Run the App
1. Connect your Android device to your computer or set up an emulator in Android Studio.
2. Build and run the app in Android Studio.
3. Test the app by interacting with features such as:
   - **Exercise tracking**
   - **Weekly plans**
   - **Bluetooth EMG integration**

## Features
- **Personalized Exercise Plans:** Generate customized weekly exercise schedules.
- **Real-Time EMG Monitoring:** Connect with Bluetooth devices to monitor muscle activity.
- **Progress Tracking:** Track exercise logs, calories burned, and more.
- **User-Friendly Interface:** Navigate seamlessly through an intuitive UI.

## Images
Include screenshots of the app to illustrate its features and guide users visually. Add images to a folder named `images` in the repository and reference them here:


![472355858_3160884677399006_3335637664677869190_n](https://github.com/user-attachments/assets/b7302860-5a31-420b-8fec-d5ffb5e2424d)
![473383344_1268232211106527_252475676193052082_n](https://github.com/user-attachments/assets/d8dad63e-8a93-4bcd-afe4-ddf5be7c03a5)
![472788698_1251219222627541_5688613694058276310_n](https://github.com/user-attachments/assets/459f3b1d-f936-41b4-9872-02654b35073e)
![472789496_1345964466419757_8348957864607240307_n](https://github.com/user-attachments/assets/5e56ddf7-af31-4ca3-9e4c-2f35dc05d7c2)
![473332099_606461608462532_4893729876954145822_n](https://github.com/user-attachments/assets/2181faea-a75d-4f7a-8dfd-13cbd8c2cba5)
![471529626_609143442058091_2816009445183579515_n](https://github.com/user-attachments/assets/f6feb70d-d3f6-46bf-bf3d-2a645b30c219)
![474128204_616407074100335_973992035947197467_n](https://github.com/user-attachments/assets/042922c5-c164-40ad-b67f-df9910f9df65)
![473764930_1796930834399319_3074026158882644519_n](https://github.com/user-attachments/assets/c25c2771-42d3-4bb3-9a1a-656df6a160ec)
![473610640_573364072183710_8657516758156664276_n](https://github.com/user-attachments/assets/f4690f0f-fe04-423e-b85b-9e12e0b0ce60)

## Troubleshooting

### Backend Connection Issues:
- Ensure XAMPP is running, and the `BASE_URL` in the app is correctly set.

### Database Import Errors:
- Check the SQL file for syntax errors or compatibility issues with your MySQL version.

### Bluetooth Issues:
- Ensure the Android device has Bluetooth enabled and is paired with the EMG device.




