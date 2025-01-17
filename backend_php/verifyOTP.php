<?php
require 'DataBase.php';
$db = new DataBase();

if (isset($_POST['email']) && isset($_POST['otp'])) {
    $email = $_POST['email'];
    $otp = $_POST['otp'];
    
    if ($db->dbConnect()) {
        $email = $db->prepareData($email);
        $otp = $db->prepareData($otp);
        $result = $db->verifyOTP($email);

        if ($result) {
            if ($result['otp'] == $otp && $result['expiry'] > time()) {
                echo 'OTP verified';
            } else {
                echo 'Invalid OTP or OTP expired';
            }
        } else {
            echo 'OTP not found';
        }
    } else {
        echo 'Error: Database connection';
    }
} else {
    echo 'Email and OTP are required';
}
?>