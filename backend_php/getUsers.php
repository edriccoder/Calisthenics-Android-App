<?php
require "DataBase.php";

$db = new DataBase();

if ($db->dbConnect()) {
    session_start();
    $adminUser = $_SESSION['name']; // Assuming admin username is stored in session

    // Fetch unique users who have conversed with the admin
    $query = "SELECT DISTINCT sender AS user FROM messages WHERE receiver = ?
              UNION
              SELECT DISTINCT receiver AS user FROM messages WHERE sender = ? AND receiver != ?";
    
    if ($stmt = $db->connect->prepare($query)) {
        $stmt->bind_param("sss", $adminUser, $adminUser, $adminUser);
        if ($stmt->execute()) {
            $result = $stmt->get_result();
            $users = array();
            while ($row = $result->fetch_assoc()) {
                if ($row['user'] != $adminUser) {
                    $users[] = $row['user'];
                }
            }
            echo json_encode($users);
        } else {
            error_log("SQL Execution Error in getUsers: " . $stmt->error);
            echo json_encode([]);
        }
        $stmt->close();
    } else {
        error_log("SQL Preparation Error in getUsers: " . $db->connect->error);
        echo json_encode([]);
    }
} else {
    echo "Error: Database connection failed";
}
?>
