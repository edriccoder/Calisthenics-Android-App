<?php
require "DataBase.php";

header('Content-Type: application/json'); // Set header for JSON response

$db = new DataBase();
$response = array();

if ($db->dbConnect()) {
    // Get 'exname' parameter from POST request
    $exname = isset($_POST['exname']) ? trim($_POST['exname']) : '';

    if (empty($exname)) {
        // Missing 'exname' parameter
        $response["error"] = "Exercise name ('exname') is required.";
        echo json_encode($response);
        exit();
    }

    $table = "exercise_records";

    // Fetch focus data from the database
    $exerciseRecord = $db->getFocusByExname($table, $exname);

    if ($exerciseRecord !== false && is_array($exerciseRecord)) {
        // Prepare response with the retrieved exercise
        $response["exercises"] = [$exerciseRecord]; // Wrap in an array to form JSONArray
    } else {
        // No exercises found or an error occurred
        $response["exercises"] = [];
        $response["error"] = "No exercises found for the given name.";
    }

    echo json_encode($response);
} else {
    // Database connection error
    $response["exercises"] = []; // Ensure 'exercises' is always an array
    $response["error"] = "Error: Database connection.";
    echo json_encode($response);
}
?>
