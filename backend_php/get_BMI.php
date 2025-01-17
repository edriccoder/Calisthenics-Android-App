<?php
require "DataBase.php";

$db = new DataBase();
$response = array();

header('Content-Type: application/json');

// Define the get_weight function
function get_weight($table, $username) {
    global $db;

    $query = "SELECT weight, height FROM $table WHERE username = ?";

    $stmt = $db->connect->prepare($query);

    if (!$stmt) {
        error_log("Prepare failed: " . $db->connect->error);
        return array("error" => "Prepare failed: " . $db->connect->error);
    }

    $stmt->bind_param("s", $username);

    if (!$stmt->execute()) {
        error_log("Execute failed: " . $stmt->error);
        return array("error" => "Execute failed: " . $stmt->error);
    }

    $result = $stmt->get_result();

    if ($result && $result->num_rows > 0) {
        $data = $result->fetch_assoc();
        return $data;
    } else {
        error_log("No data found for user: " . $username); // Log when no data is found
        return array("error" => "No data found for user: " . $username);
    }
}

if ($db->dbConnect()) {
    if (isset($_POST['username'])) {
        $username = $_POST['username'];
        $table = "bmi";

        $result = get_weight($table, $username);

        // Log and return the final result or error
        error_log("Result for user $username: " . json_encode($result));
        echo json_encode($result);
    } else {
        $response["error"] = "Username is required";
        error_log("Username is required");
        echo json_encode($response);
    }
} else {
    $response["error"] = "Error: Unable to connect to the database.";
    error_log("Error: Unable to connect to the database.");
    echo json_encode($response);
}

?>
