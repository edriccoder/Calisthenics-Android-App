<?php
require "DataBase.php";
$db = new DataBase();

$response = array();

if ($db->dbConnect()) {
    $exerciseRecords = $db->getAllExerciseRecords("exercise_records");
    if ($exerciseRecords !== false) {
        foreach ($exerciseRecords as &$record) {
            if (!empty($record['eximg'])) {
                $record['eximg'] = 'https://calestechsync.dermocura.net/calestechsync/' . $record['eximg'];
            } else {
                $record['eximg'] = 'https://calestechsync.dermocura.net/calestechsync/eximg/jumpsquat.gif'; 
            }
        }
    
        echo json_encode($exerciseRecords);
    } else {
        $response["error"] = "No exercises found.";
        echo json_encode($response);
    }
} else {
    $response["error"] = "Error: Database connection";
    echo json_encode($response);
}
?>
