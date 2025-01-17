<?php
require "DataBase.php";
$db = new DataBase();

if (isset($_POST['username']) && isset($_POST['time_seconds']) && isset($_POST['date'])) {
    $username = $_POST['username'];
    $time_seconds = $_POST['time_seconds'];
    $date = $_POST['date'];

    if ($db->dbConnect()) {
        if ($db->insertDuration("exercise_duration_log", $username, $time_seconds, $date)) {
            echo "Success";
        } else {
            echo "Failed";
        }
    } else {
        echo "Error: Database connection";
    }
} else {
    echo "All fields are required";
}
?>
