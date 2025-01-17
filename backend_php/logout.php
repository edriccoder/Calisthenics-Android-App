<?php
require "DataBase.php";
$db = new DataBase();

session_start();
if (!isset($_SESSION['username'])) {

    header("Location: login.php");
    exit;
}

$db->logoutUser();

?>
