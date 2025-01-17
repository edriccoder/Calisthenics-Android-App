<?php
require "DataBase.php";
$db = new DataBase();

$response = array();

if ($db->dbConnect()) {
    if (isset($_POST['username'])) {
        $username = $_POST['username']; 

        $weeklyGoals = $db->getWeeklyGoalDayByUsername("weekly_goal", $username); 
        $focusBodies = $db->getFocusGoalByUsername("focus_goal", $username); 

        if ($weeklyGoals !== false && $focusBodies !== false && !empty($weeklyGoals)) {
            $data = array();

            $weeklyGoalsCount = count($weeklyGoals);
            $focusBodiesCount = count($focusBodies);

            $cycles = ceil($weeklyGoalsCount / $focusBodiesCount);

            $repeatedFocusBodies = array();
            for ($i = 0; $i < $cycles; $i++) {
                $repeatedFocusBodies = array_merge($repeatedFocusBodies, $focusBodies);
            }

            $repeatedFocusBodies = array_slice($repeatedFocusBodies, 0, $weeklyGoalsCount);

            for ($i = 0; $i < $weeklyGoalsCount; $i++) {
                $data[] = array(
                    "day" => $weeklyGoals[$i],
                    "focusbody" => $repeatedFocusBodies[$i]
                );
            }
            
            echo json_encode($data);
        } else {
            $response["error"] = "No weekly goals found for the specified username.";
            echo json_encode($response);
        }
        
    } else {
        $response["error"] = "Username not provided.";
        echo json_encode($response);
    }
} else {
    $response["error"] = "Error: Database connection";
    echo json_encode($response);
}
?>
