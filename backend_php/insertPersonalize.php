<?php
require "DataBase.php";  // Ensure this file is correctly linked

$db = new DataBase();  // Create an instance of DataBase

if ($db->dbConnect()) {
    // Check if all required POST fields are set
    if (
        isset($_POST['username']) && isset($_POST['exercise_day']) &&
        isset($_POST['exercise_name']) && isset($_POST['exdesc']) &&
        isset($_POST['eximg']) && isset($_POST['exdifficulty']) &&
        isset($_POST['focusbody']) && isset($_POST['activity_goal'])
    ) {
        // Get the POST parameters
        $username = $_POST['username'];
        $exercise_day = $_POST['exercise_day'];
        $exercise_name = $_POST['exercise_name'];
        $exdesc = $_POST['exdesc'];
        $eximg = $_POST['eximg'];
        $exdifficulty = $_POST['exdifficulty'];
        $focusbody = $_POST['focusbody'];
        $activity_goal = $_POST['activity_goal'];

        // Call the insert function from the DataBase class
        $result = $db->insertPersonalize($username, $exercise_day, $exercise_name, $exdesc, $eximg, $exdifficulty, $focusbody, $activity_goal);

        // Check the result of the insert operation
        if ($result === true) {
            echo json_encode(["success" => true, "message" => "Exercise added successfully."]);
        } else {
            echo json_encode(["success" => false, "message" => $result]);  // Return the error message
        }
    } else {
        echo json_encode(["success" => false, "message" => "All fields are required."]);
    }
} else {
    $response["error"] = "Error: Database connection";
    echo json_encode($response);
}
?>