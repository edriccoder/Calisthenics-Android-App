<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

require "DataBaseConfig.php"; // Make sure the file path is correct

// Create an instance of the DataBaseConfig class
$dbConfig = new DataBaseConfig();

// Use the properties from the instance
$conn = new mysqli($dbConfig->servername, $dbConfig->username, $dbConfig->password, $dbConfig->databasename);

// Check the connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} else {
    echo "Connected successfully";
}

// Close the connection
$conn->close();
?>
