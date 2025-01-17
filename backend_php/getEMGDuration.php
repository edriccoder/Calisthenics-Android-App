<?php
// Enable error reporting for debugging (disable in production)
ini_set('display_errors', 1);
error_reporting(E_ALL);

// Include the DataBase class
require "DataBase.php";

// Initialize the response array
$response = array();

// Create a new instance of the DataBase class
$db = new DataBase();

// Attempt to connect to the database
if ($db->dbConnect()) {
    // Check if the required POST parameters are set
    if (isset($_POST['username']) && isset($_POST['date'])) {
        // Retrieve and sanitize POST parameters
        $username = trim($_POST['username']);
        $date = trim($_POST['date']);
        
        // Basic validation (you can expand this as needed)
        if (empty($username) || empty($date)) {
            $response["error"] = "Error: 'username' and 'date' parameters cannot be empty.";
            echo json_encode($response);
            exit();
        }
        
        // Fetch EMG duration data using the getEMGDuration function
        $emgDurations = $db->getEMGDuration($username, $date);
        
        if ($emgDurations !== null) {
            // Prepare and send the successful response
            $response["success"] = true;
            $response["emg_durations"] = $emgDurations;
            echo json_encode($response);
        } else {
            // No EMG duration data found for the given user and date
            $response["error"] = "No EMG duration data found for the specified user and date.";
            echo json_encode($response);
        }
    } else {
        // Missing required POST parameters
        $response["error"] = "Error: Missing 'username' or 'date' POST parameters.";
        echo json_encode($response);
    }
} else {
    // Database connection error
    $response["error"] = "Error: Unable to connect to the database.";
    echo json_encode($response);
}
?>
