<?php
// Enable error reporting but prevent it from being outputted directly
error_reporting(E_ALL);
ini_set('display_errors', 0); // Changed to 0 to prevent direct output

require_once "DataBase.php";

// Set header for JSON response
header('Content-Type: application/json');

$response = array();
$db = new DataBase();

try {
    if ($db->dbConnect()) {
        // Ensure 'username' is provided
        if (isset($_POST['username']) && !empty($_POST['username'])) {
            $username = $_POST['username'];
            $table = "focus_goal"; // Replace with your table name containing 'username' and 'focusbody' columns

            // Fetch focus body entries based on the provided username
            $focusBodies = $db->getFocusBodyByUsername($table, $username);

            if ($focusBodies !== false && !empty($focusBodies)) {
                $response["focusBodies"] = $focusBodies;
            } else {
                $response["error"] = "No focus bodies found for the specified username.";
            }
        } else {
            $response["error"] = "Username not provided or is empty.";
        }
    } else {
        $response["error"] = "Error: Database connection.";
    }
} catch (Exception $e) {
    $response["error"] = "Unexpected error: " . $e->getMessage();
}

echo json_encode($response);
?>
