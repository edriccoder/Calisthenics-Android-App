<?php
header('Content-Type: application/json'); // Set header for JSON response

require "DataBase.php"; // Include your DataBase class

$db = new DataBase(); // Instantiate the DataBase class

$response = array(); // Initialize an array to store the response

// Check if 'username' and 'log_date' POST parameters are set
if (isset($_POST['username']) && isset($_POST['log_date'])) {
    $username = $_POST['username'];
    $log_date = $_POST['log_date']; // The date provided by the user in 'yyyy-MM-dd' format

    // Check if the database connection is established
    if ($db->dbConnect()) {
        // Retrieve the daily_seconds using the getDurationDaily method
        $durationResult = $db->getDurationDaily($username, $log_date);

        if ($durationResult !== false && !empty($durationResult)) {
            // Assuming getDurationDaily returns an array of associative arrays
            // and we're interested in the 'daily_seconds' field
            $exerciseTime = (int)$durationResult[0]['daily_seconds']; // Cast to integer

            // Success response with exerciseTime
            $response['status'] = "success";
            $response['message'] = "Exercise time retrieved successfully";
            $response['exerciseTime'] = $exerciseTime; // Time in seconds

            // Optional: Log the retrieved exercise time for debugging
            error_log("Exercise Time for user '$username' on '$log_date': $exerciseTime seconds");
        } else {
            // No exercise time found for the given user and date
            $response['status'] = "error";
            $response['message'] = "No exercise record found for the user on the specified date.";
        }
    } else {
        // Database connection failed
        $response['status'] = "error";
        $response['message'] = "Error: Database connection failed.";
    }
} else {
    // Missing required POST parameters
    $response['status'] = "error";
    $response['message'] = "Username and log date are required.";
}

// Return the response as JSON
echo json_encode($response);
?>
