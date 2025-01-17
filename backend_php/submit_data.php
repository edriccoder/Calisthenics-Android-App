<?php
require "DataBase.php";

$db = new DataBase();

if ($db->connect_error) {
    die("Connection failed: " . $db->connect_error);
}

$username = $_POST["username"];
$gender = $_POST["gender"];
$focusArea = $_POST["focusArea"];
$mainGoal = $_POST["mainGoal"];
$difficulty = $_POST["difficulty"];
$weeklyGoal = $_POST["weeklyGoal"];
$weight = $_POST["weight"];
$height = $_POST["height"];

if ($db->addAssessment("body_assessment", $username, $gender, $focusArea, $mainGoal, $difficulty, $weeklyGoal, $weight, $height)) {
    echo "New record created successfully";
} else {
    echo "Error: Unable to create record";
}