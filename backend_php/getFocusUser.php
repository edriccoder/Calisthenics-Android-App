<?php
require "DataBase.php";

$db = new DataBase();
$response = array();

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['username']) && isset($_POST['focusbody'])) {
    $username = $_POST['username'];
    $focusbody = $_POST['focusbody'];

    if ($db->dbConnect()) {
        $table = "exrecord_user"; 

        $exerciseRecords = $db->getFocusUser($table, $username, $focusbody);

        if ($exerciseRecords !== false) {
            foreach ($exerciseRecords as &$record) {
                if (!empty($record['eximg'])) {
                    $record['eximg'] = 'https://calestechsync.dermocura.net/calestechsync/' . $record['eximg'];
                } else {
                    $record['eximg'] = 'https://calestechsync.dermocura.net/eximg/jumpsquat.gif'; 
                }
            }

            // Send JSON response with "exercises" key
            $response["exercises"] = $exerciseRecords;
            echo json_encode($response);
        } else {
            $response["error"] = "No exercises found.";
            echo json_encode($response);
        }
    } else {
        $response["error"] = "Error: Database connection";
        echo json_encode($response);
    }
} else {
    $response["error"] = "Invalid request";
    echo json_encode($response);
}
?>
