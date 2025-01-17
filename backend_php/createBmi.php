<?php
require "DataBase.php";
$db = new DataBase();

if (isset($_POST['username']) && isset($_POST['weight']) && isset($_POST['height'])) {
    if ($db->dbConnect()) {
        if ($db->createBmi("bmi", $_POST['username'], $_POST['weight'], $_POST['height'])) {
            echo "BMI Created Successfully";
        } else {
            echo "Failed to Create BMI";
        }
    } else {
        echo "Error: Database connection";
    }
} else {
    echo "All fields are required";
}
?>
