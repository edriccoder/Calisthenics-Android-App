<?php
require "DataBaseConfig.php";

// Set content type to JSON for proper response handling in Android
header('Content-Type: application/json');

$dbConfig = new DataBaseConfig();
$conn = new mysqli($dbConfig->servername, $dbConfig->username, $dbConfig->password, $dbConfig->databasename);

if ($conn->connect_error) {
    die(json_encode(array("error" => "Connection failed: " . $conn->connect_error)));
}

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Sanitize and validate the username input
    $username = isset($_POST['username']) ? $_POST['username'] : '';

    if (empty($username)) {
        echo json_encode(array("error" => "Username is required."));
        exit();
    }

    $sql = "SELECT DISTINCT log_date FROM exercise_log WHERE username = ?";
    $stmt = $conn->prepare($sql);

    if ($stmt) {
        $stmt->bind_param("s", $username);

        if ($stmt->execute()) {
            $result = $stmt->get_result();
            $log_dates = array();

            while ($row = $result->fetch_assoc()) {
                $log_dates[] = $row['log_date'];
            }

            echo json_encode($log_dates);
        } else {
            echo json_encode(array("error" => "Database error: " . $stmt->error));
        }

        $stmt->close();
    } else {
        echo json_encode(array("error" => "Failed to prepare SQL statement."));
    }
} else {
    echo json_encode(array("error" => "Invalid request method."));
}

$conn->close();
?>
