<?php
require "DataBaseConfig.php";

// Get POST data
$username = $_POST['username'];
$weight = $_POST['weight'];

// Initialize database connection
$database = new DataBaseConfig();
$conn = new mysqli($database->servername, $database->username, $database->password, $database->databasename);

// Check connection
if ($conn->connect_error) {
    die(json_encode(['error' => 'Connection failed: ' . $conn->connect_error]));
}

// Use prepared statement to prevent SQL injection
$sql = $conn->prepare("INSERT INTO bmi (username, weight) VALUES (?, ?)");
$sql->bind_param("ss", $username, $weight); // "ss" stands for two strings

// Execute the query
if ($sql->execute()) {
    echo "Weight updated successfully";
} else {
    echo json_encode(['error' => 'Error updating weight']);
}

// Close connection
$sql->close();
$conn->close();
?>
