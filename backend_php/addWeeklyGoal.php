<?php
require "DataBase.php";
$db = new DataBase();
if (isset($_POST['day']) && isset($_POST['username'])) {
    if ($db->dbConnect()) {
        if ($db->addWeeklyGoal("weekly_goal", $_POST['day'], $_POST['username'])) {
            echo "Weekly goal added successfully";
        } else {
            echo "Failed to add weekly goal";
        }
    } else {
        echo "Error: Database connection";
    }
} else {
    echo "Username and day are required";
}
?>

