<?php
require "DataBase.php";
$db = new DataBase();

header('Content-Type: application/json');

$response = array();
$prefix = 'https://calestechsync.dermocura.net/calestechsync/';

try {
    if ($db->dbConnect()) {
        $username = $_POST['username'] ?? null;
        $exercise_day = $_POST['exercise_day'] ?? null;

        if ($username === null || $exercise_day === null) {
            throw new Exception("Missing parameters: username or exercise_day");
        }

        // Fetch exercises from the weekly plan
        $exercises = $db->getWeeklyPlan('weekly_exercise_plan', $username, $exercise_day);

        if ($exercises !== false) {
            $response['success'] = true;
            foreach ($exercises as &$row) {
                // Ensure image URLs are correctly prefixed
                if (!preg_match('/^https?:\/\//', $row['eximg'])) {
                    $row['eximg'] = $prefix . $row['eximg'];
                }
            }
            $response['data'] = $exercises;
        } else {
            $response['success'] = false;
            $response['message'] = "No exercises found for the given day and username.";
        }
    } else {
        throw new Exception("Database connection failed.");
    }
} catch (Exception $e) {
    error_log($e->getMessage());
    $response['success'] = false;
    $response['message'] = "An error occurred: " . $e->getMessage();
}

// Return a JSON response
echo json_encode($response);
?>
