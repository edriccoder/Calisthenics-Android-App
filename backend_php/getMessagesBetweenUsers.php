<?php
// getMessagesBetweenUsers.php

require_once "DataBase.php";

$db = new DataBase();

if ($db->dbConnect()) {
    if (isset($_GET['contact'])) {
        session_start();
        $adminUser = $_SESSION['name']; // Assuming admin username is stored in session
        $contact = trim($_GET['contact']);

        $messages = $db->getMessagesBetweenUsers('messages', $adminUser, $contact);
        if ($messages !== null) {
            echo json_encode($messages);
        } else {
            echo json_encode([]);
        }
    } else {
        echo json_encode([]);
    }
} else {
    echo json_encode([]);
}
?>
