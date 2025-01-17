<?php
require "DataBase.php";
$db = new DataBase();

if (isset($_POST['username']) && isset($_POST['gender'])) {
    if ($db->dbConnect()) {
        if ($db->createGender("gender", $_POST['username'], $_POST['gender'])) {
            echo "Gender Created Successfully";
        } else {
            echo "Failed to Create gender";
        }
    } else {
        echo "Error: Database connection";
    }
} else {
    echo "All fields are required";
}
?>
