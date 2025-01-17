<?php
require "DataBase.php";

$db = new DataBase();
$response = array();

// Set content type to application/json
header('Content-Type: application/json');

// Define the get_exercise_count function
function get_exercise_count($table, $username, $date) {
    global $db;

    $query = "SELECT exercise_count FROM $table WHERE username = ? AND date = ?";
    
    // Prepare the statement
    $stmt = $db->connect->prepare($query);
    
    // Handle potential errors in the prepared statement
    if (!$stmt) {
        error_log("Prepare failed: " . $db->connect->error);
        return false;
    }

    // Bind parameters
    $stmt->bind_param("ss", $username, $date);
    
    // Execute the statement
    if (!$stmt->execute()) {
        error_log("Execute failed: " . $stmt->error);
        return false;
    }

    // Get the result
    $result = $stmt->get_result();
    
    if ($result && $result->num_rows > 0) {
        $row = $result->fetch_assoc();
        return $row['exercise_count']; // Return the exercise count
    } else {
        // If no records found, return 0
        return 0;
    }
}

// Check database connection
if ($db->dbConnect()) {
    // Check for required POST parameters
    if (isset($_POST['username'])) {
        $username = $_POST['username']; 
        $table = "track_exercise"; 
        // Get today's date
        $date = isset($_POST['date']) ? $_POST['date'] : date("Y-m-d");

        $exerciseCount = get_exercise_count($table, $username, $date); 

        if ($exerciseCount !== false) {
            $response["exercise_count"] = $exerciseCount;
            echo json_encode($response);
        } else {
            $response["error"] = "Failed to retrieve exercise count";
            echo json_encode($response);
        }
    } else {
        $response["error"] = "Username is required";
        echo json_encode($response);
    }
} else {
    $response["error"] = "Error: Unable to connect to the database.";
    echo json_encode($response);
}
?>
