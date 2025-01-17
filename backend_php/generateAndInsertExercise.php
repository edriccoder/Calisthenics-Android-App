<?php
require "DataBase.php";
$db = new DataBase();

$response = array();

if ($db->dbConnect()) {
    if (isset($_POST['username'])) {
        $username = $_POST['username'];
        $level = $db->getLevelByUsername('level', $username);
        $daysPerWeek = $db->getWeeklyGoalDayByUsername('weekly_goal', $username);  // Number of exercise days per week
        $goal = $db->getActGoal('activity_goal', $username);  // User's activity goal

        if ($level === false || $daysPerWeek === false || $goal === false) {
            $response["error"] = "User level, weekly goal, or activity goal not found.";
            echo json_encode($response);
            exit;
        }

        $exercises = array();

        // Get exercises based on user level and activity goal
        if ($level === "Beginner") {
            $exercisesForLevel = $db->getExerciseRecordsByLevel('exercise_records', 'Beginner', $goal);
        } elseif ($level === "Intermediate") {
            $exercisesForLevel = $db->getExerciseRecordsByLevel('exercise_records', 'Intermediate', $goal);
        } elseif ($level === "Advance") {
            $exercisesForLevel = $db->getExerciseRecordsByLevel('exercise_records', 'Advance', $goal);
        }

        if (!empty($exercisesForLevel)) {
            $exercisesPerDay = 7;  // Set number of exercises per day
            $totalDays = min($daysPerWeek, 7);  // Ensure user input doesn't exceed 7 days

            for ($day = 1; $day <= $totalDays; $day++) {
                shuffle($exercisesForLevel);
                $dailyExercises = array_slice($exercisesForLevel, 0, $exercisesPerDay);  
            
                foreach ($dailyExercises as $exercise) {
                    // Insert each exercise for the current day into the weekly exercise plan
                    $sql = "INSERT INTO weekly_exercise_plan (username, exercise_day, exercise_name, exdesc, eximg, exdifficulty, focusbody, activity_goal) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    $stmt = $db->connect->prepare($sql);
                    $stmt->bind_param(
                        "sissssss",
                        $username,
                        $day,
                        $exercise['exname'],
                        $exercise['exdesc'],
                        $exercise['eximg'],
                        $exercise['exdifficulty'],
                        $exercise['focusbody'],  
                        $exercise[$goal]  
                    );
                    $stmt->execute();
                }
            }            

            $response["success"] = "Weekly exercise plan generated and saved successfully.";
            echo json_encode($response);
            exit;
        } else {
            $response["error"] = "No exercises found for the given level and goal.";
            echo json_encode($response);
            exit;
        }
    } else {
        $response["error"] = "Username not provided.";
        echo json_encode($response);
        exit;
    }
} else {
    $response["error"] = "Error: Database connection";
    echo json_encode($response);
    exit;
}
?>
