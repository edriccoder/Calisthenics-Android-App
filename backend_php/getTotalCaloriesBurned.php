<?php
require "DataBase.php";

header('Content-Type: application/json'); // Set header for JSON response

$db = new DataBase();
$response = array();

/**
 * Function to get total calories burned for a user on a specific date.
 *
 * @param DataBase $db An instance of the DataBase class.
 * @param string $username The username to query.
 * @param string $date The date to query (format: YYYY-MM-DD).
 * @return float|null Returns the total calories burned or null on failure.
 */
function get_total_calories_burned($db, $username, $date) {
    // Prepare the SQL query to sum calories burned for the given username and specific date
    $query = "SELECT SUM(`calories_burned`) AS total_calories 
              FROM `calories_burned` 
              WHERE `username` = ? 
              AND `date` = ?"; // Use the date passed from the POST request

    // Prepare the statement
    $stmt = $db->connect->prepare($query);
    
    if (!$stmt) {
        error_log("Prepare failed: " . $db->connect->error);
        return null;
    }

    // Bind parameters (username and date)
    if (!$stmt->bind_param("ss", $username, $date)) { // Two "s" for two strings
        error_log("Binding parameters failed: " . $stmt->error);
        return null;
    }
    
    // Execute the statement
    if (!$stmt->execute()) {
        error_log("Execute failed: " . $stmt->error);
        return null;
    }

    // Get the result
    $result = $stmt->get_result();
    
    if ($result) {
        $row = $result->fetch_assoc();
        if ($row && isset($row['total_calories'])) {
            return $row['total_calories'] ? (float)$row['total_calories'] : 0; // Return total calories or 0 if null
        } else {
            error_log("No rows returned.");
            return 0;
        }
    } else {
        error_log("Failed to retrieve result set.");
        return null;
    }
}

// Check database connection
if ($db->dbConnect()) {
    // Check for required POST parameters
    if (isset($_POST['username']) && isset($_POST['date'])) {
        $username = trim($_POST['username']);
        $date = trim($_POST['date']); // Get the date from POST request

        // Basic input validation
        if (empty($username)) {
            $response["success"] = false;
            $response["message"] = "Username cannot be empty.";
            echo json_encode($response);
            exit;
        }

        // Get total calories burned for the user on the specific date
        $totalCaloriesBurned = get_total_calories_burned($db, $username, $date);

        // Prepare the response
        if ($totalCaloriesBurned !== null) {
            $response["success"] = true;
            $response["total_calories"] = $totalCaloriesBurned;
            echo json_encode($response);
        } else {
            $response["success"] = false;
            $response["message"] = "Error retrieving total calories burned.";
            echo json_encode($response);
        }
    } else {
        $response["success"] = false;
        $response["message"] = "Username and date are required.";
        echo json_encode($response);
    }
} else {
    $response["success"] = false;
    $response["message"] = "Error: Unable to connect to the database.";
    echo json_encode($response);
}

?>
