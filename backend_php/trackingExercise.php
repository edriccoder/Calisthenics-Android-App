<?php
require "DataBase.php";
$db = new DataBase();

if (isset($_POST['username']) && isset($_POST['exname']) && isset($_POST['eximg'])) {
    if ($db->dbConnect()) {
        if ($db->trackingExercise("tracking", $_POST['username'], $_POST['exname'], $_POST['eximg'])) {
            echo "Tracking exercise created Successfully";
        } else {
            echo "Failed to Create Tracking exercise";
        }
    } else {
        echo "Error: Database connection";
    }
} else {
    echo "All fields are required";
}
?>
