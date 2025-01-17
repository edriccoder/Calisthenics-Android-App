<?php
header('Content-Type: application/json'); // Set the response to JSON
require "DataBase.php"; // Include your database class

$response = array();

if (isset($_POST['username'])) {
    $username = $_POST['username'];

    // Initialize the database connection
    $db = new DataBase();

    if ($db->dbConnect()) {
        // Call the function to get the user's weight
        $weight = $db->getWeight($username);

        if ($weight !== null) {
            // Success response with weight
            $response['status'] = "success";
            $response['weight'] = $weight;
        } else {
            // No weight found
            $response['status'] = "error";
            $response['message'] = "Weight not found for user.";
        }
    } else {
        // Database connection failed
        $response['status'] = "error";
        $response['message'] = "Database connection failed.";
    }
} else {
    // Username not provided
    $response['status'] = "error";
    $response['message'] = "Username is required.";
}

echo json_encode($response); // Return the response in JSON format
?>
