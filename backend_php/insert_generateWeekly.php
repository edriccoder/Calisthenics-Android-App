<?php
require_once 'DataBase.php';
$db = new DataBase();

header('Content-Type: application/json'); // Always ensure JSON output

if ($db->dbConnect()) {
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        $username = $_POST['username'];
        $weeklyPlan = json_decode($_POST['weeklyPlan'], true); // Decode JSON

        if (!empty($username) && !empty($weeklyPlan)) {
            $exerciseCount = 0; // Initialize cumulative exercise count
            
            // Loop through each day of the week
            foreach (['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'] as $day) {
                $status = isset($weeklyPlan[$day]) ? $weeklyPlan[$day] : 'rest'; // Default to 'rest' if not set
                
                // Only increment the exercise count for non-rest days
                if ($status == 'exercise') {
                    $exerciseCount++;
                }
                
                // For rest days, insert 0 for the count
                // Insert into DB and catch any errors
                $result = $db->insert_generateWeekly($username, $day, $status, ($status == 'rest' ? 0 : $exerciseCount));
                if ($result !== true) {
                    // Send error if insert fails
                    throw new Exception($result);
                }
            }
            echo json_encode(['status' => 'success', 'message' => 'Weekly plan inserted successfully.']);
        } else {
            throw new Exception('Invalid input.');
        }
    } else {
        throw new Exception('Invalid request method.');
    }
} else {
    $response["error"] = "Error: Database connection";
    echo json_encode($response);
}

// Turn off error display (still log errors)
ini_set('display_errors', 0);
ini_set('log_errors', 1);
error_reporting(E_ALL);
?>
