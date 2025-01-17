<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 1);

require "DataBase.php";
$db = new DataBase();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Ensure that the database connection is established
    $db->dbConnect();

    // Get the raw POST body (assuming JSON is sent from Android)
    $input = file_get_contents('php://input');
    $data = json_decode($input, true); // Decode JSON

    // Get the username from the decoded JSON data
    $username = $data['username'] ?? ''; // Use null coalescing to avoid undefined index error

    if (empty($username)) {
        echo json_encode(array("message" => "Invalid username.")); // Return message if username is empty
        exit;
    }

    // Query to fetch the weekly exercise plan for the given username
    $sql = "SELECT day, status, count FROM generate_weekly WHERE username = '$username'"; // Order by count

    $result = mysqli_query($db->connect, $sql);

    if ($result) {
        $weekly_plan = array();

        // Fetch all rows and store them in the weekly_plan array
        while ($row = mysqli_fetch_assoc($result)) {
            // Add each row to the weekly_plan array
            $weekly_plan[] = array(
                "day" => $row['day'],
                "status" => $row['status'],
                "count" => $row['count'] // Count will be hidden on the Android side but still sent
            );
        }

        // Return the weekly plan as JSON
        echo json_encode(array("weekly_plan" => $weekly_plan)); // Wrap in an array
    } else {
        // If no data is found, return a failure message
        echo json_encode(array("message" => "No weekly plan found for this user."));
    }
}
?>