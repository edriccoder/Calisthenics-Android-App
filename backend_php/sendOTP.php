<?php
require 'DataBase.php';
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\SMTP;
use PHPMailer\PHPMailer\Exception;

require 'vendor/autoload.php';
$db = new DataBase();

if (isset($_POST['email'])) {
    $email = $_POST['email'];

    if ($db->dbConnect()) {
        $otp = rand(100000, 999999);
        $expiry = time() + 60; // 1 minute expiry

        $result = $db->sendOTP($email, $otp, $expiry);
        if ($result === true) {
            $mail = new PHPMailer(true);
            try {
                $mail->isSMTP();
                $mail->Host = 'smtp.gmail.com';
                $mail->SMTPAuth = true;
                $mail->Username = ''; // Your Gmail address
                $mail->Password = ''; // Your Gmail password or app-specific password
                $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
                $mail->Port = 587;

                $mail->setFrom('edric5611@gmail.com', 'CalesTectSync');
                $mail->addAddress($email);

                $mail->isHTML(true);
                $mail->Subject = 'Your OTP Code';
                $mail->Body    = "Your OTP code is $otp. It will expire in 1 minute.";

                $mail->send();
                echo 'OTP sent';
            } catch (Exception $e) {
                echo "Error: {$mail->ErrorInfo}";
            }
        } else {
            echo 'Error: Unable to save OTP';
        }
    } else {
        echo 'Error: Database connection';
    }
} else {
    echo 'Email is required';
}
?>
