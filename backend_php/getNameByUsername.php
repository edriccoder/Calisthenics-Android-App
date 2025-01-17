<?php
require "DataBase.php";
$db = new DataBase();
if (isset($_POST['username'])) {
    if ($db->dbConnect()) {
        $fullname = $db->getNameByUsername("user", $_POST['username']);
        if ($fullname) {
            echo $fullname;
        } else {
            echo "Username not found";
        }
    } else {
        echo "Error: Database connection";
    }
}
?>

<?php
