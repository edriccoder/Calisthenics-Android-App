<?php
require "DataBase.php";
$db = new DataBase();

if (isset($_POST['email']) && isset($_POST['newPassword'])) {
    if ($db->dbConnect()) {
        if ($db->updatePasswordByEmail("user", $_POST['email'], $_POST['newPassword'])) {
            echo "Password updated successfully";
        } else {
            echo "Failed to update password";
        }
    } else {
        echo "Error: Database connection";
    }
} else {
    echo "Email and new password are required";
}
?>
