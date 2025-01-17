<?php
require "DataBase.php";

header('Content-Type: application/json');

$response = array();

$db = new DataBase();

if ($db->dbConnect()) {
    // Check if required POST parameters are set
    if (isset($_POST['username'], $_POST['exercise_name'], $_POST['sets'], $_POST['reps'])) {
        $username = $_POST['username'];
        $exerciseName = $_POST['exercise_name'];
        $sets = $_POST['sets'];
        $reps = $_POST['reps'];
        $date = isset($_POST['date']) ? $_POST['date'] : date("Y-m-d");
        $table = "exercise_log";
        
        // Log incoming parameters for debugging
        error_log("Username: $username, Exercise: $exerciseName, Sets: $sets, Reps: $reps, Date: $date");
    
        if ($db->executeQuery($table, $username, $exerciseName, $sets, $reps, $date)) {
            $response["success"] = "Exercise log updated successfully";
            echo json_encode($response);
        } else {
            $response["error"] = "Failed to update log";
        }
    } else {
        $response["error"] = "Missing parameters";
    }

} else {
    $response["error"] = "Failed to connect to the database";
}


echo json_encode($response);
exit; // Ensure no further processing is done after JSON response
?>