<?php
header('Content-Type: application/json');
require "DataBase.php";

$db = new DataBase();
$response = array();

if ($db->dbConnect()) {
    if (isset($_POST['username'])) {
        $username = $_POST['username'];

        $level = $db->getLevelByUsername('level', $username);
        if ($level !== false) {
            $response['level'] = $level;
        } else {
            $response['error'] = "Unable to fetch activity goal for $username";
        }
    
    } else {
        $response['error'] = "Username not provided via POST";
    }
} else {
    $response['error'] = "Database connection failed";
}

echo json_encode($response);
?>
