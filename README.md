CalesTechSync App

CalesTechSync is an Android application designed to help users achieve their calisthenics goals. This guide will walk you through setting up the app and its backend components.

Prerequisites

Before setting up the app, ensure you have the following installed on your system:

Android Studio (latest version)

XAMPP for running the PHP backend

MySQL (comes with XAMPP)

A physical or virtual Android device for testing

Setup Instructions

Step 1: Set Up the Backend

Locate the backend_php folder provided with the project files.

Copy the entire backend_php folder into the htdocs directory of your XAMPP installation.

On Windows, this is usually located at: C:\xampp\htdocs.

Start XAMPP and ensure both the Apache and MySQL modules are running.

Step 2: Import the Database

Open phpMyAdmin by navigating to http://localhost/phpmyadmin in your web browser.

Click on the Import tab.

Select the database file located in the database folder of the project files.

Example: calestechsync_db.sql.

Click Go to import the database.

Confirm that a new database (e.g., calestechsync_db) has been created with the required tables.

Step 3: Configure the Android App

Open Android Studio.

Clone the project repository or import the project files.

Open the Constants.java file located in the utils or config package (depending on the project structure).

Update the BASE_URL to point to your local server:

public static final String BASE_URL = "http://<your-local-ip>/backend_php/";

Replace <your-local-ip> with your computer's local IP address (e.g., 192.168.1.100).

To find your local IP, run ipconfig on Windows or ifconfig on Mac/Linux.

Step 4: Run the App

Connect your Android device to your computer or set up an emulator in Android Studio.

Build and run the app in Android Studio.

Test the app by interacting with the features such as exercise tracking, weekly plans, and Bluetooth EMG integration.

Features

Personalized Exercise Plans: Generate customized weekly exercise schedules.

Real-Time EMG Monitoring: Connect with Bluetooth devices to monitor muscle activity.

Progress Tracking: Track exercise logs, calories burned, and more.

User-Friendly Interface: Navigate seamlessly through an intuitive UI.

Images

Include screenshots of the app to illustrate its features and guide users visually. Add images to a folder named images in the repository and reference them here:

Home Screen


Exercise Log


Weekly Plan


Troubleshooting

Backend Connection Issues: Ensure XAMPP is running, and the BASE_URL in the app is correctly set.

Database Import Errors: Check the SQL file for syntax errors or compatibility issues with your MySQL version.

Bluetooth Issues: Ensure the Android device has Bluetooth enabled and is paired with the EMG device.

License

This project is licensed under the MIT License. See the LICENSE file for details.

Contributions

We welcome contributions to improve the app. Feel free to fork the repository and submit pull requests.
