<?php
require "DataBase.php";

$db = new DataBase();

if ($db->dbConnect()) {
    if (isset($_GET['user'])) {
        $user = $_GET['user'];

        // Fetch messages involving the specified user
        $messages = $db->getMessages("messages", $user);
        if ($messages !== null) {
            echo json_encode($messages);
        } else {
            echo "Error: Unable to fetch messages for user $user";
        }
    } else {
        echo "Error: User parameter is required";
    }
} else {
    echo "Error: Database connection failed";
}
?>
