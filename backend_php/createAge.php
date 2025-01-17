<?php
require "DataBase.php";
$db = new DataBase();

if (isset($_POST['username']) && isset($_POST['birthday']) && isset($_POST['age'])) {
    if ($db->dbConnect()) {
        if ($db->createAge("age", $_POST['username'], $_POST['birthday'], $_POST['age'])) {
            echo "Age and Birthday Created/Updated Successfully";
        } else {
            echo "Failed to Create/Update Age and Birthday";
        }
    } else {
        echo "Error: Database connection";
    }
} else {
    echo "All fields are required";
}
?>
