<?php
require "DataBase.php";
$db = new DataBase();

$response = array();

if ($db->dbConnect()) {
    if (isset($_POST['username']) && isset($_POST['exname'])) {
        $username = $_POST['username'];
        $exname = $_POST['exname'];
        $exdesc = $_POST['exdesc'];
        $eximg = $_POST['eximg'];
        $exdifficulty = $_POST['exdifficulty'];
        $focusbody = $_POST['focusbody'];

        $result = $db->insertIntoExRecordUser($username, $exname, $exdesc, $eximg, $exdifficulty, $focusbody);
        if ($result === true) {
            $response["success"] = "Exercise inserted successfully";
            echo json_encode($response);
        } else {
            $response["error"] = "Failed to insert exercise: " . $result;
            echo json_encode($response);
        }
    } else {
        $response["error"] = "Username or exercise name not provided.";
        echo json_encode($response);
    }
} else {
    $response["error"] = "Error: Database connection";
    echo json_encode($response);
}
?>
