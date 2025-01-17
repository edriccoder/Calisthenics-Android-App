<?php
require "DataBaseConfig.php";

// Get POST data
$username = $_POST['username'];

// Initialize database connection
$database = new DataBaseConfig();
$conn = new mysqli($database->servername, $database->username, $database->password, $database->databasename);

// Check connection
if ($conn->connect_error) {
    die(json_encode(['error' => 'Connection failed: ' . $conn->connect_error]));
}

// Use prepared statement to prevent SQL injection
$sql = $conn->prepare("SELECT weight FROM bmi WHERE username = ? ORDER BY bmi_id ASC");
$sql->bind_param("s", $username);

// Execute the query
$sql->execute();
$result = $sql->get_result();

if ($result->num_rows > 0) {
    $weight_history = array();
    while ($row = $result->fetch_assoc()) {
        $weight_history[] = $row;
    }
    // Get the latest weight
    $current_weight = end($weight_history)['weight'];
    
    // Return the weight history and current weight as JSON
    echo json_encode(['weight_history' => $weight_history, 'current_weight' => $current_weight]);
} else {
    echo json_encode(['error' => 'No weight history found']);
}

// Close connection
$sql->close();
$conn->close();
?>
