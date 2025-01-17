<?php
require "DataBase.php";

$db = new DataBase();
$response = array();

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['username'])) {
    $username = $_POST['username'];

    if ($db->dbConnect()) {
        $table = "tracking"; 

        $exerciseRecords = $db->getTrackingExercise($table, $username);

        if ($exerciseRecords !== false) {
            foreach ($exerciseRecords as &$record) {
                if (empty($record['eximg'])) {
                    $record['eximg'] = 'https://calestechsync.dermocura.net/calestechsync/eximg/jumpsquat.gif'; 
                }
            }
            // Return JSON response with exercises as an array
            echo json_encode($exerciseRecords);
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
} else {
    // Invalid request error
    $response["error"] = "Invalid request";
    echo json_encode($response);
}
?>
