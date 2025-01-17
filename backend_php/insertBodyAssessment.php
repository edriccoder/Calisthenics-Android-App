<?php
require "DataBase.php";
$db = new DataBase();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (
        isset($_POST['username']) && isset($_POST['height']) && isset($_POST['weight']) &&
        isset($_POST['focus_body']) && isset($_POST['goal']) && isset($_POST['level']) && isset($_POST['weekly_goal'])
    ) {
        $username = $_POST['username'];
        $height = $_POST['height'];
        $weight = $_POST['weight'];
        $focus_body = $_POST['focus_body'];
        $goal = $_POST['goal'];
        $level = $_POST['level'];
        $weekly_goal = $_POST['weekly_goal'];

        if ($db->insertBodyAssessment('body_assessment', $username, $height, $weight, $focus_body, $goal, $level, $weekly_goal)) {
            echo json_encode(["success" => true, "message" => "Body assessment data inserted successfully!"]);
        } else {
            echo json_encode(["success" => false, "message" => "Failed to insert body assessment data."]);
        }
    } else {
        echo json_encode(["success" => false, "message" => "All fields are required."]);
    }
} else {
    echo json_encode(["success" => false, "message" => "Invalid request method."]);
}
?>
