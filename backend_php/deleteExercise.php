<?php
require "DataBase.php";
$db = new DataBase();

if (isset($_POST['exname']) && isset($_POST['username'])) {
    if ($db->dbConnect()) {  
        $exname = $_POST['exname'];
        $username = $_POST['username'];
        $result = $db->deleteExercise($username, $exname);
        echo $result;
    } else {
        echo "Error: Database connection";
    }
} else {
    echo "All fields are required";
}
?>
