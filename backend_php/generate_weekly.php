<?php
require "DataBase.php";
$db = new DataBase();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Ensure that the database connection is established
    $db->dbConnect();

    // Get the username from POST request
    $username = $_POST['username'];

    // Fetch the number of exercise days from the database (should be a single digit)
    $exercise_days_count = $db->getWeeklyGoalDayByUsername('weekly_goal', $username); // Ensure this matches your actual table name

    if ($exercise_days_count === false || !is_numeric($exercise_days_count)) {
        echo json_encode(array("message" => "No exercise goal found for this user."));
        exit;
    }

    // Check if exercise_days_count is greater than 7
    if ($exercise_days_count > 7) {
        $exercise_days_count = 7; // Limit to a maximum of 7 days
    }

    // Generate weekly exercise schedule based on the number of exercise days
    $schedule = generateWeeklySchedule($exercise_days_count);

    // Insert the generated schedule into the database
    foreach ($schedule as $day => $data) {
        $status = $data['status'];
        $count = $data['count']; // Get the count for exercise days
        
        $sql = "INSERT INTO generate_weekly (username, day, status, count) VALUES ('$username', '$day', '$status', '$count')";
        if (!mysqli_query($db->connect, $sql)) {
            echo json_encode(array("message" => "Failed to insert schedule data."));
            exit;
        }
    }

    echo json_encode(array("message" => "Weekly plan generated successfully"));
}

// Function to generate the weekly exercise schedule based on exercise days count
function generateWeeklySchedule($exercise_days_count) {
    $days_of_week = array('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday');
    $schedule = array();

    // Initialize all days as rest with count 0
    foreach ($days_of_week as $day) {
        $schedule[$day] = array('status' => 'rest', 'count' => 0);
    }

    // Variable to keep track of the count for exercise days
    $exercise_count = 1;

    if ($exercise_days_count >= 7) {
        // If exercising all 7 days, mark every day as exercise with incrementing count
        foreach ($days_of_week as $day) {
            $schedule[$day] = array('status' => 'exercise', 'count' => $exercise_count++);
        }
    } else {
        // Randomly select days for exercise
        $exercise_days_count = min($exercise_days_count, 6);  // Limit to a max of 6 exercise days
        $selected_days = array_rand($days_of_week, $exercise_days_count);  // Randomize the exercise days

        // Ensure selected_days is an array even if only one day is selected
        $selected_days = (array)$selected_days;

        // Mark the selected days as exercise with incrementing count
        foreach ($selected_days as $day_index) {
            $schedule[$days_of_week[$day_index]] = array('status' => 'exercise', 'count' => $exercise_count++);
        }
    }

    return $schedule;
}
?>
