<?php
require "DataBase.php";
$db = new DataBase();

if (isset($_POST['username']) && isset($_POST['activity'])) {
    if ($db->dbConnect()) {
        if ($db->createActivityGoal("activity_goal", $_POST['username'], $_POST['activity'])) {
            echo "Activity Created Successfully";
        } else {
            echo "Failed to Create activity";
        }
    } else {
        echo "Error: Database connection";
    }
} else {
    echo "All fields are required";
}
?>
