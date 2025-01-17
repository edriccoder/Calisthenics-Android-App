<?php
require "DataBase.php";
$db = new DataBase();

$response = array(); // Initialize an array to store the response

if (isset($_POST['username']) && isset($_POST['log_date'])) {
    $username = $_POST['username'];
    $log_date = $_POST['log_date']; // The date provided by the user

    // Check if the database connection is established
    if ($db->dbConnect()) {
        // Get the current weight from the BMI table
        $currentWeight = $db->getCurrentWeightFromBMI($username);

        if ($currentWeight !== null) {
            // Insert or update the weight into the 'weight_logs' table
            if ($db->executeWeightQuery('weight_logs', $username, $currentWeight, $log_date)) {
                // Indicate success
                $response['status'] = "success";
                $response['message'] = "Weight updated successfully";

                // Retrieve the weight logs from the last three months
                $weightLogs = $db->getWeightLogsLastThreeMonths('weight_logs', $username);
                
                if ($weightLogs !== false) {
                    // Add the weight logs to the response
                    $response['weight_logs'] = $weightLogs;
                } else {
                    $response['status'] = "error";
                    $response['message'] = "Failed to retrieve weight logs";
                }

            } else {
                $response['status'] = "error";
                $response['message'] = "Failed to update weight";
            }

        } else {
            $response['status'] = "error";
            $response['message'] = "No weight entry found for the user";
        }
    } else {
        $response['status'] = "error";
        $response['message'] = "Error: Database connection failed";
    }
} else {
    $response['status'] = "error";
    $response['message'] = "Username and log date are required";
}

// Return the response as JSON
echo json_encode($response);
