<?php
require "DataBase.php";

$db = new DataBase();

if ($db->dbConnect()) {
    if (isset($_POST['username']) && isset($_POST['exname'])) {
        $username = $_POST['username'];
        $exname = $_POST['exname'];

        $activityGoal = $db->getActGoal('activity_goal', $username);
        if ($activityGoal !== false) {
            $activity = $db->getActivity('exercise_records', $activityGoal, $exname);
            if ($activity !== false) {
                echo $activity;
            } else {
                echo "Error: Unable to fetch activity for exercise $exname";
            }
        } else {
            echo "Error: Unable to fetch activity goal for $username";
        }
    } else {
        echo "Error: Username or exercise name not provided via POST";
    }
} else {
    echo "Error: Database connection";
}
?>
