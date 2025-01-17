<?php
require "DataBase.php";

$db = new DataBase();

if ($db->dbConnect()) {
    if (isset($_POST['sender']) && isset($_POST['receiver']) && isset($_POST['message'])) {
        $sender = $_POST['sender'];
        $receiver = $_POST['receiver'];
        $message = $_POST['message'];

        if ($db->sendMessage('messages', $sender, $receiver, $message)) {
            echo "Message sent successfully";
        } else {
            echo "Error: Unable to send message";
        }
    } else {
        echo "Error: Sender, receiver, or message not provided via POST";
    }
} else {
    echo "Error: Database connection failed";
}
?>
