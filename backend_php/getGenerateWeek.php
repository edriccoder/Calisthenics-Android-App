<?php
require_once 'DataBase.php';
$db = new DataBase();
if ($db->dbConnect()) {
    if (isset($_POST['username'])) {
        $username = $_POST['username'];
        $table = 'generate_weekly'; 

        $generateWeek = $db->getGenerateWeek($table, $username);

        if ($generateWeek !== false) {
            echo json_encode($generateWeek); 
        } else {
            echo json_encode([]);
        }
    } else {
        echo "Error: Username not provided"; 
    }
} else {
    echo "Error: Database connection";
}
?>
