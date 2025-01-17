<?php
header('Content-Type: application/json'); // Set header for JSON response
require "DataBase.php"; // Include your DataBase class

$response = array(); // Initialize an array to store the response

// Retrieve and sanitize POST data
$username           = isset($_POST['username']) ? trim($_POST['username']) : '';
$date               = isset($_POST['date']) ? trim($_POST['date']) : '';
$below_easy_seconds = isset($_POST['below_easy_seconds']) ? intval($_POST['below_easy_seconds']) : 0;
$easy_seconds       = isset($_POST['easy_seconds']) ? intval($_POST['easy_seconds']) : 0;
$medium_seconds     = isset($_POST['medium_seconds']) ? intval($_POST['medium_seconds']) : 0;
$hard_seconds       = isset($_POST['hard_seconds']) ? intval($_POST['hard_seconds']) : 0;

// Validate input
if (empty($username) || empty($date)) {
    $response['status'] = "error";
    $response['message'] = "Invalid input: Username and date are required.";
    echo json_encode($response);
    exit();
}

// Optionally, validate the date format (YYYY-MM-DD)
if (!preg_match("/^\d{4}-\d{2}-\d{2}$/", $date)) {
    $response['status'] = "error";
    $response['message'] = "Invalid date format. Expected YYYY-MM-DD.";
    echo json_encode($response);
    exit();
}

// Initialize the database connection
$db = new DataBase();
if ($db->dbConnect()) {
    // Insert or update the EMG duration data
    $inserted = $db->insertEmgDurations($username, $date, $below_easy_seconds, $easy_seconds, $medium_seconds, $hard_seconds);
    
    if ($inserted) {
        $response['status'] = "success";
        $response['message'] = "EMG durations inserted/updated successfully.";
    } else {
        $response['status'] = "error";
        $response['message'] = "Failed to insert/update EMG durations.";
    }
} else {
    $response['status'] = "error";
    $response['message'] = "Database connection failed.";
}

echo json_encode($response); // Return the response as JSON
?>
