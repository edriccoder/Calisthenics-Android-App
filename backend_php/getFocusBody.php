<?php
// Enable error reporting but prevent it from being outputted directly
error_reporting(E_ALL);
ini_set('display_errors', 1);

require_once "DataBase.php";

// Start output buffering to prevent any unwanted output
ob_start();

header('Content-Type: application/json');

$response = array();
$db = new DataBase();

try {
    if ($db->dbConnect()) {
        $username = $_POST['username'];
        $focusbody = $_POST['focusbody'];
        $exdifficulty = $_POST['exdifficulty'];
        $table = "exercise_records";

        // Fetch exercises based on the focus body, difficulty, and username
        $exerciseRecords = $db->getFocusBody($table, $focusbody, $exdifficulty, $username);

        if ($exerciseRecords !== false) {
            foreach ($exerciseRecords as &$record) {
                $prefix = 'https://calestechsync.dermocura.net/calestechsync/';
                if (!startsWith($record['eximg'], $prefix)) {
                    $record['eximg'] = $prefix . $record['eximg'];
                }
            }
            $response["exercises"] = $exerciseRecords;
        } else {
            $response["error"] = "No exercises found.";
        }
    } else {
        $response["error"] = "Error: Database connection.";
    }
} catch (Exception $e) {
    $response["error"] = "Unexpected error: " . $e->getMessage();
}

echo json_encode($response);

// Function to check if a string starts with a prefix
function startsWith($string, $prefix) {
    return substr($string, 0, strlen($prefix)) === $prefix;
}
?>
