<?php
require "DataBase.php";

$db = new DataBase();
$response = array();

if ($db->dbConnect()) {
    // Fetch data
    $focusbody = $_POST['focusbody']; // Trim to remove leading/trailing whitespaces
    $table = "exercise_records"; 

    // Fetch focus data from the database
    $exerciseRecords = $db->getFocus($table, $focusbody);

    if ($exerciseRecords !== false) {
        foreach ($exerciseRecords as &$record) {
            // Check if eximg already contains the prefix
            $prefix = 'https://calestechsync.dermocura.net/calestechsync/';
            if (!startsWith($record['eximg'], $prefix)) {
                $record['eximg'] = $prefix . $record['eximg']; 
            }
        }
        

        // Prepare response
        $response["exercises"] = $exerciseRecords;
        echo json_encode($response);
    } else {
        // No exercises found
        $response["error"] = "No exercises found.";
        echo json_encode($response);
    }
} else {
    // Database connection error
    $response["error"] = "Error: Database connection";
    echo json_encode($response);
}

// Function to check if a string starts with a specific prefix
function startsWith($string, $prefix) {
    return substr($string, 0, strlen($prefix)) === $prefix;
}
?>
