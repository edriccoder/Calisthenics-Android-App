<?php
header('Content-Type: application/json'); // Set header for JSON response
require "DataBase.php"; // Include your DataBase class

$response = array(); // Initialize an array to store the response

// Check if the necessary POST parameters are set
if (isset($_POST['username']) && isset($_POST['weight']) && isset($_POST['log_date'])) {
    $username = $_POST['username'];
    $weight = $_POST['weight'];
    $log_date = $_POST['log_date'];

    // Initialize the database connection
    $db = new DataBase();

    if ($db->dbConnect()) {
        // Insert the weight into the two tables
        if ($db->insertWeightLogs("weight_logs", "bmi", $username, $weight, $log_date)) {
            $response['status'] = "success";
            $response['message'] = "Weight log inserted successfully.";
        } else {
            $response['status'] = "error";
            $response['message'] = "Failed to insert weight log.";
        }
    } else {
        $response['status'] = "error";
        $response['message'] = "Database connection failed.";
    }
} else {
    $response['status'] = "error";
    $response['message'] = "Required parameters are missing.";
}

echo json_encode($response); // Return the response as JSON
?>
