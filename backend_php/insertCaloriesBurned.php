<?php
require "DataBase.php";

$db = new DataBase();

if (isset($_POST['username'], $_POST['exercise_name'], $_POST['date'])) {
    $username = $_POST['username'];
    $exercise_name = $_POST['exercise_name'];
    $date = $_POST['date'];

    if ($db->dbConnect()) {
        // Step 1: Get duration from exercise_duration_log
        $durationResult = $db->getDuration($username, $date);
        
        if ($durationResult && !empty($durationResult)) {
            // Cast duration to an integer
            $duration_in_seconds = (int)$durationResult[0]['time_seconds'];
            error_log("Fetched duration in seconds: " . $duration_in_seconds);
        
            // Step 2: Get user's weight
            $weightResult = $db->getWeight($username);
            if ($weightResult !== null) { // Check if weight is not null
                // Cast weight to float after converting from string
                $weight = (float)$weightResult; // Directly use the returned value
                error_log("Fetched weight: " . $weight);
        
                // Step 3: Get MET value for the exercise
                $met_value = $db->getExerciseMetValue($exercise_name);
                if ($met_value !== null) {
                    // Cast MET value to float
                    $met_value = (float)$met_value;
                    error_log("Fetched MET value: " . $met_value);
        
                    // Ensure all values are valid and numeric before proceeding
                    if (is_numeric($met_value) && is_numeric($weight) && $duration_in_seconds > 0) {
                        // Calculate duration in minutes
                        $duration_in_minutes = $duration_in_seconds / 60.0;
        
                        // Calculate calories burned using the formula: MET * weight (kg) * duration (hours)
                        $calories_burned = $met_value * $weight * ($duration_in_minutes / 60.0);
                        error_log("Calories burned: " . $calories_burned);
        
                        // Step 4: Insert calories burned into the database
                        if ($db->insertCaloriesBurned("calories_burned", $username, $exercise_name, $calories_burned, $duration_in_seconds, $date)) {
                            // Log the date being inserted for debugging
                            error_log("Inserting Date: " . $date);  // Log the date before insertion
                        
                            // Success response with weight, MET value, and duration
                            echo json_encode([
                                "success" => true,
                                "message" => "Calories Burned Record Created Successfully",
                                "weight" => $weight,
                                "met_value" => $met_value,
                                "duration_in_seconds" => $duration_in_seconds
                            ]);
                        } else {
                            echo json_encode(["success" => false, "message" => "Failed to Create Record"]);
                        }                        
                    } else {
                        echo json_encode(["success" => false, "message" => "Invalid data for MET value, weight, or duration."]);
                    }
                } else {
                    // Error retrieving MET value for the exercise
                    echo json_encode(["success" => false, "message" => "Error: Could not fetch MET value for exercise '$exercise_name'. Please check if the exercise exists."]);
                }
            } else {
                // Error retrieving weight for the user
                echo json_encode(["success" => false, "message" => "Error: Could not fetch weight for user '$username'. Please ensure the user has a weight record."]);
            }
        } else {
            // Error retrieving exercise duration data
            echo json_encode(["success" => false, "message" => "Error: No duration found for user '$username' on the specified date '$date'."]);
        }        
    } else {
        // Database connection failure
        echo json_encode(["success" => false, "message" => "Error: Database connection failed"]);
    }
} else {
    // Missing required POST parameters
    echo json_encode(["success" => false, "message" => "All fields are required"]);
}

// Logging the received POST parameters for debugging
error_log("Username: " . $_POST['username']);
error_log("Exercise Name: " . $_POST['exercise_name']);
error_log("Date: " . $_POST['date']);
$date = $_POST['date'];
error_log("Received Date: " . $date);  // Log the received date for debugging

?>
