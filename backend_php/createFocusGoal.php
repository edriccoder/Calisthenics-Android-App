<?php
require "DataBase.php";
$db = new DataBase();

if (isset($_POST['username']) && isset($_POST['focusbody'])) {
    if ($db->dbConnect()) {
        if ($db->createFocusGoal("focus_goal", $_POST['username'], $_POST['focusbody'])) {
            echo "Focus Goal Created Successfully";
        } else {
            echo "Failed to Create Focus Goal";
        }
    } else {
        echo "Error: Database connection";
    }
} else {
    echo "All fields are required";
}
?>
