<?php
require "DataBase.php";
$db = new DataBase();

if (isset($_POST['username']) && isset($_POST['levels'])) {
    if ($db->dbConnect()) {
        if ($db->createLevel("level", $_POST['username'], $_POST['levels'])) {
            echo "Level Created Successfully";
        } else {
            echo "Failed to Create Level";
        }
    } else {
        echo "Error: Database connection";
    }
} else {
    echo "All fields are required";
}
?>
