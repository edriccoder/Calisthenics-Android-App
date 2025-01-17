<?php
require "DataBaseConfig.php";

// Create a new instance of DataBaseConfig
$dbConfig = new DataBaseConfig();

// Create a connection using the configuration details from DataBaseConfig
$conn = new mysqli($dbConfig->servername, $dbConfig->username, $dbConfig->password, $dbConfig->databasename);

// Check for connection errors
if ($conn->connect_error) {
    die(json_encode(array("error" => "Connection failed: " . $conn->connect_error)));
}

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $username = $_POST['username'];
    $log_date = $_POST['log_date'];

    // Prepare and bind the SQL query
    $sql = "SELECT log_id, username, exercise_name, sets, reps, log_date FROM exercise_log WHERE username = ? AND log_date = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ss", $username, $log_date);

    // Execute the query and process the result
    if ($stmt->execute()) {
        $result = $stmt->get_result();
        $exercise_logs = array();

        while ($row = $result->fetch_assoc()) {
            $exercise_logs[] = $row;
        }

        // Return the exercise logs as a JSON response
        echo json_encode($exercise_logs);
    } else {
        // Return an error message if the query failed
        echo json_encode(array("error" => "Error retrieving logs."));
    }

    $stmt->close();
}

// Close the database connection
$conn->close();
?>
