<?php
require "DataBase.php";

$db = new DataBase();

if ($db->dbConnect()) {
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        $focusbody = $_POST['focusbody'];
        $table = "exercise_records"; 
    
        $data = $db->getWarmUp($table, $focusbody);

        if ($data !== false) {
            foreach ($data as &$record) {
                if (!empty($record['eximg'])) {
                    $record['eximg'] = 'https://calestechsync.dermocura.net/calestechsync/' . $record['eximg'];
                } else {
                    $record['eximg'] = 'https://calestechsync.dermocura.net/calestechsync/eximg/jumpsquat.gif'; 
                }
            }
    
            shuffle($data);
            $data = array_slice($data, 0, 4);
    
            echo json_encode($data);
        } else {
            echo json_encode(array("message" => "No data found."));
        }
    } else {
        echo json_encode(array("message" => "Invalid request method."));
    }
} else {
    echo "Error: Database connection";
}
?>
