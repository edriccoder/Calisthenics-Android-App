<?php
require "DataBase.php";

$db = new DataBase();
$response = array();

if ($db->dbConnect()) {
    if (isset($_POST['username'])) {
        $username = $_POST['username'];
        $table = "track_exercise"; // Your table name

        // Get the date from POST or set it to today's date by default
        $date = isset($_POST['date']) ? $_POST['date'] : date("Y-m-d");

        // Update the exercise count
        if ($db->update_exercise_count($table, $username, $date)) {
            $response["success"] = "Exercise count updated successfully";
        } else {
            $response["error"] = "Failed to update exercise count";
        }
    } else {
        $response["error"] = "Username is required";
    }
} else {
    $response["error"] = "Error: Unable to connect to the database.";
}

// Return the response as JSON
header('Content-Type: application/json');
echo json_encode($response);
?>
