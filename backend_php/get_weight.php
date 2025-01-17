<?php
require "DataBase.php";

$db = new DataBase();
$response = array();

// Set content type to application/json
header('Content-Type: application/json');

// Define the get_weight function
function get_weight($table, $username) {
    global $db;

    $query = "SELECT weight FROM $table WHERE username = ?";

    // Prepare the statement
    $stmt = $db->connect->prepare($query);

    // Handle potential errors in the prepared statement
    if (!$stmt) {
        error_log("Prepare failed: " . $db->connect->error);
        return false;
    }

    // Bind parameters
    $stmt->bind_param("s", $username);

    // Execute the statement
    if (!$stmt->execute()) {
        error_log("Execute failed: " . $stmt->error);
        return false;
    }

    // Get the result
    $result = $stmt->get_result();

    if ($result && $result->num_rows > 0) {
        $row = $result->fetch_assoc();
        return $row['weight']; // Return the weight
    } else {
        return 0;
    }
}

// Check database connection
if ($db->dbConnect()) {
    // Check for required POST parameters
    if (isset($_POST['username'])) {
        $username = $_POST['username']; 
        $table = "bmi";

        $result = get_weight($table, $username); 

        if ($result !== false) {
            $response["weight"] = $result;
            echo json_encode($response);
        } else {
            $response["error"] = "Failed to retrieve weight";
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
