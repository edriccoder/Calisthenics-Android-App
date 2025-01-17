<?php
require "DataBase.php";

$db = new DataBase();
$response = array();

if ($db->dbConnect()) {

    $username = $_POST['username'];
    $exname = $_POST['exname']; 
    $exdesc = $_POST['exdesc']; 
    $eximg = $_POST['eximg']; 
    $exdifficulty = $_POST['exdifficulty']; 
    $focusbody = $_POST['focusbody']; 

    // Remove the prefix from eximg if it exists
    $prefix = 'http://192.168.1.28/calestechsync/';
    if (startsWith($eximg, $prefix)) {
        $eximg = str_replace($prefix, '', $eximg);
    }

    $result = $db->insertExercise($username, $exname, $exdesc, $eximg, $exdifficulty, $focusbody);

    if ($result === "Exercise Inserted") {
        $response["success"] = "Exercise inserted successfully.";
        echo json_encode($response);
    } else {

        $response["error"] = $result;
        echo json_encode($response);
    }

} else {

    $response["error"] = "Error: Database connection";
    echo json_encode($response);
}

// Function to check if a string starts with a specific prefix
function startsWith($string, $prefix) {
    return substr($string, 0, strlen($prefix)) === $prefix;
}
?>
