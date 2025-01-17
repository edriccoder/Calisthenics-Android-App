<?php
require "DataBaseConfig.php";

class DataBase
{
    public $connect;
    public $data;
    private $sql;
    protected $servername;
    protected $username;
    protected $password;
    protected $databasename;

    public function __construct()
    {
        $this->connect = null;
        $this->data = null;
        $this->sql = null;
        $dbc = new DataBaseConfig();
        $this->servername = $dbc->servername;
        $this->username = $dbc->username;
        $this->password = $dbc->password;
        $this->databasename = $dbc->databasename;
    }

    function dbConnect()
    {
        $this->connect = mysqli_connect($this->servername, $this->username, $this->password, $this->databasename);
        if (!$this->connect) {
            die("Connection failed: " . mysqli_connect_error()); // Output error if connection fails
        }
        return $this->connect;
    }

    // Function to prepare data for safe use in SQL queries
    function prepareData($data)
    {
        if ($this->connect == null) {
            $this->dbConnect(); // Ensure that connection is established
        }
        return mysqli_real_escape_string($this->connect, stripslashes(htmlspecialchars($data)));
    }

    function logIn($table, $username, $password)
    {
        $username = $this->prepareData($username);
        $password = $this->prepareData($password);
        $this->sql = "SELECT * FROM " . $table . " WHERE username = '" . $username . "' AND archive = 0";
        $result = mysqli_query($this->connect, $this->sql);
        $row = mysqli_fetch_assoc($result);
        if (mysqli_num_rows($result) != 0) {
            $dbusername = $row['username'];
            $dbpassword = $row['password'];
            if ($dbusername == $username && password_verify($password, $dbpassword)) {
                $login = true;
            } else $login = false;
        } else $login = false;

        return $login;
    }

    function signUp($table, $fullname, $email, $username, $password)
    {
        $fullname = $this->prepareData($fullname);
        $username = $this->prepareData($username);
        $password = $this->prepareData($password);
        $email = $this->prepareData($email);
        $password = password_hash($password, PASSWORD_DEFAULT);
        $this->sql =
            "INSERT INTO " . $table . " (fullname, username, password, email) VALUES ('" . $fullname . "','" . $username . "','" . $password . "','" . $email . "')";
        if (mysqli_query($this->connect, $this->sql)) {
            return true;
        } else return false;
    }

    function getNameByUsername($table, $username)
    {
        $username = $this->prepareData($username);
        $this->sql = "SELECT fullname FROM " . $table . " WHERE username = '" . $username . "'";
        $result = mysqli_query($this->connect, $this->sql);

        if (mysqli_num_rows($result) != 0) {
            $row = mysqli_fetch_assoc($result);
            $fullname = $row['fullname'];
            return $fullname;
        } else {
            return false;
        }
    }

    function updatePasswordByEmail($table, $email, $newPassword)
    {
        $email = $this->prepareData($email);
        $newPassword = $this->prepareData($newPassword);

        $hashedPassword = password_hash($newPassword, PASSWORD_DEFAULT);

        $this->sql = "UPDATE " . $table . " SET password = '" . $hashedPassword . "' WHERE email = '" . $email . "'";

        if (mysqli_query($this->connect, $this->sql)) {
            return true;
        } else {
            return false;
        }
    }

    function logoutUser()
    {

        session_start();

        $_SESSION = array();

        session_destroy();

        header("Location: login.php");
        exit;
    }

    function addWeeklyGoal($table, $day, $username)
    {
        // Prepare data to prevent SQL injection
        $day = $this->prepareData($day);
        $username = $this->prepareData($username);
    
        // SQL query to insert or update
        $this->sql = "INSERT INTO " . $table . " (day, username) VALUES ('" . $day . "', '" . $username . "')
                      ON DUPLICATE KEY UPDATE day = '" . $day . "'";
    
        // Execute query
        if (mysqli_query($this->connect, $this->sql)) {
            return true;
        } else {
            return false;
        }
    }

    function createFocusGoal($table, $username, $focusbody)
    {
        // Prepare data to prevent SQL injection
        $username = $this->prepareData($username);
        $focusbody = $this->prepareData($focusbody);
    
        // SQL query to insert or update
        $this->sql = "INSERT INTO " . $table . " (username, focusbody) VALUES ('" . $username . "', '" . $focusbody . "')
                      ON DUPLICATE KEY UPDATE focusbody = '" . $focusbody . "'";
    
        // Execute query
        if (mysqli_query($this->connect, $this->sql)) {
            return true;
        } else {
            return false;
        }
    }


    function createGender($table, $username, $gender)
    {
        // Prepare data to prevent SQL injection
        $username = $this->prepareData($username);
        $gender = $this->prepareData($gender);
    
        // SQL query to insert or update
        $this->sql = "INSERT INTO " . $table . " (username, gender) VALUES ('" . $username . "', '" . $gender . "')
                      ON DUPLICATE KEY UPDATE gender = '" . $gender . "'";
    
        // Execute query
        if (mysqli_query($this->connect, $this->sql)) {
            return true;
        } else {
            return false;
        }
    }

    function createAge($table, $username, $birthday, $age) {
        $username = $this->prepareData($username);
        $birthday = $this->prepareData($birthday);
        $age = $this->prepareData($age);
    
        // SQL query to insert or update the birthday and age
        $this->sql = "INSERT INTO " . $table . " (username, birthday, age) 
                      VALUES ('$username', '$birthday', '$age') 
                      ON DUPLICATE KEY UPDATE birthday = '$birthday', age = '$age'";
    
        if (mysqli_query($this->connect, $this->sql)) {
            return true;
        } else {
            return false;
        }
    }

    function createActivityGoal($table, $username, $activity)
    {
        // Prepare data to prevent SQL injection
        $username = $this->prepareData($username);
        $activity = $this->prepareData($activity);
    
        // SQL query to insert or update
        $this->sql = "INSERT INTO " . $table . " (username, activity) VALUES ('" . $username . "', '" . $activity . "')
                      ON DUPLICATE KEY UPDATE activity = '" . $activity . "'";
    
        // Execute query
        if (mysqli_query($this->connect, $this->sql)) {
            return true;
        } else {
            return false;
        }
    }

    function createBmi($table, $username, $weight, $height)
    {
        // Prepare data to prevent SQL injection
        $username = $this->prepareData($username);
        $weight = $this->prepareData($weight);
        $height = $this->prepareData($height);
    
        // SQL query to insert or update
        $this->sql = "INSERT INTO " . $table . " (username, weight, height) VALUES ('" . $username . "', '" . $weight . "', '" . $height . "')
                      ON DUPLICATE KEY UPDATE weight = '" . $weight . "', height = '" . $height . "'";
    
        // Execute query
        if (mysqli_query($this->connect, $this->sql)) {
            return true;
        } else {
            return false;
        }
    }
  
    function getAllExerciseRecords($table)
    {
        $this->sql = "SELECT exname, exdesc, eximg, exdifficulty, focusbody, BuildMuscle FROM " . $table;
        $result = mysqli_query($this->connect, $this->sql);

        if (mysqli_num_rows($result) != 0) {
            $exerciseRecords = array(); 
            while ($row = mysqli_fetch_assoc($result)) {
                $exerciseRecords[] = $row;
            }
            return $exerciseRecords;
        } else {
            return false;
        }
    }

    function getWeeklyGoalDayByUsername($table, $username)
    {
        $username = $this->prepareData($username);
        $this->sql = "SELECT day FROM " . $table . " WHERE username = '" . $username . "'";
        $result = mysqli_query($this->connect, $this->sql);

        if (mysqli_num_rows($result) != 0) {
            $row = mysqli_fetch_assoc($result); // Get the first row
            return intval($row['day']); // Return the single digit as an integer
        } else {
            return false; // No entry found
        }
    }


    public function getActGoal($table, $username)
    {
        $username = $this->connect->real_escape_string($username);
        $sql = "SELECT activity FROM $table WHERE username = ?";
        $stmt = $this->connect->prepare($sql);
        $stmt->bind_param("s", $username);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result && $result->num_rows != 0) {
            $row = $result->fetch_assoc();
            $level = $row['activity'];
            return $level;
        } else {
            return false;
        }
    }

    public function getActivity($table, $activity, $exname)
    {
        $exname = $this->connect->real_escape_string($exname);
        $sql = "SELECT `$activity` FROM `$table` WHERE `exname` = ?";
        $stmt = $this->connect->prepare($sql);
        $stmt->bind_param("s", $exname);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result && $result->num_rows != 0) {
            $row = $result->fetch_assoc();
            $activity = $row[$activity];
            return $activity;
        } else {
            return false;
        }
    }


    function getFocusGoalByUsername($table, $username)
    {
        $username = $this->prepareData($username);
        $this->sql = "SELECT focusbody FROM " . $table . " WHERE username = '" . $username . "'";
        $result = mysqli_query($this->connect, $this->sql);

        $focusBodies = array();

        if (mysqli_num_rows($result) != 0) {
            while ($row = mysqli_fetch_assoc($result)) {
                $focusBodies[] = $row['focusbody'];
            }
            return $focusBodies;
        } else {
            return false;
        }
    }

    public function getFocusUser($table, $username, $focusbody)
    {
        $sql = "SELECT exname, exdesc, eximg, exdifficulty FROM $table WHERE username = ? AND focusbody = ?";
        $stmt = $this->connect->prepare($sql);
        $stmt->bind_param("ss", $username, $focusbody); 
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result && $result->num_rows != 0) {
            $exerciseRecords = array();
            while ($row = $result->fetch_assoc()) {
                $exerciseRecords[] = $row;
            }
            return $exerciseRecords;
        } else {
            return false;
        }
    }

    public function getGenerateWeek($table, $username)
    {
        $sql = "SELECT day, count FROM $table WHERE username = ? AND status = 'exercise'";
        $stmt = $this->connect->prepare($sql);
        $stmt->bind_param("s", $username); 
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result && $result->num_rows != 0) {
            $exerciseRecords = array();
            while ($row = $result->fetch_assoc()) {
                $exerciseRecords[] = $row;
            }
            return $exerciseRecords;
        } else {
            return false;
        }
    }

    public function getWeeklyPlan($table, $username, $day)
    {
        // Update the query to ensure both exercise_name and exname have the same collation
        $sql = "
            SELECT 
                t1.exercise_name, 
                t1.exdesc, 
                t1.eximg, 
                t1.activity_goal,
                t2.other_focus
            FROM $table t1
            LEFT JOIN exercise_records t2 
                ON t1.exercise_name COLLATE utf8mb4_unicode_ci	 = t2.exname COLLATE 	utf8mb4_unicode_ci	
            WHERE t1.username = ? AND t1.exercise_day = ?
        ";
        
        // Prepare and execute the statement
        $stmt = $this->connect->prepare($sql);
        $stmt->bind_param("ss", $username, $day); // Both parameters as strings
        $stmt->execute();
        $result = $stmt->get_result();

        // Check if the result has rows
        if ($result && $result->num_rows != 0) {
            $exerciseRecords = array();
            while ($row = $result->fetch_assoc()) {
                // Append fetched data to the array
                $exerciseRecords[] = $row;
            }
            return $exerciseRecords; // Return the result set
        } else {
            return false; // No results found
        }
    }



    public function insertPersonalize($username, $exercise_day, $exercise_name, $exdesc, $eximg, $exdifficulty, $focusbody, $activity_goal) {
        $sql = "INSERT INTO weekly_exercise_plan (username, exercise_day, exercise_name, exdesc, eximg, exdifficulty, focusbody, activity_goal)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        if ($stmt = $this->connect->prepare($sql)) {
            $stmt->bind_param("ssssssss", $username, $exercise_day, $exercise_name, $exdesc, $eximg, $exdifficulty, $focusbody, $activity_goal);
            if ($stmt->execute()) {
                return true;
            } else {
                return "Error executing query: " . $stmt->error;
            }
        } else {
            return "Error preparing query: " . $this->connect->error;
        }
    }


    public function getExerciseRecordsByLevel($table, $difficulty, $activitygoal)
    {
        // Dynamically add the column name for the activity goal to the query
        $sql = "SELECT exname, exdesc, eximg, exdifficulty, focusbody, $activitygoal FROM $table WHERE exdifficulty = ?";
        
        $stmt = $this->connect->prepare($sql);
        $stmt->bind_param("s", $difficulty);  // Only bind difficulty as parameter
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result && $result->num_rows != 0) {
            $exerciseRecords = array();
            while ($row = $result->fetch_assoc()) {
                $exerciseRecords[] = $row;
            }
            return $exerciseRecords;
        } else {
            return false;
        }
    }
    
    public function insert_generateWeekly($username, $day, $status, $count)
    {
        $insertSql = "INSERT INTO generate_weekly (username, day, status, count) VALUES (?, ?, ?, ?)";
        $stmt = $this->connect->prepare($insertSql);

        // Bind the parameters (username, day, status, count) to the SQL statement
        $stmt->bind_param("ssss", $username, $day, $status, $count);

        // Execute the statement and handle any errors
        if (!$stmt->execute()) {
            return "Failed to insert for day: $day. Error: " . $stmt->error;
        }

        return true;
    }

    public function insertIntoExRecordUser($username, $exname, $exdesc, $eximg, $exdifficulty, $focusbody)
    {
        $insertSql = "INSERT INTO exrecord_user (username, exname, exdesc, eximg, exdifficulty, focusbody) VALUES (?, ?, ?, ?, ?, ?)";
        $stmt = $this->connect->prepare($insertSql);
        $stmt->bind_param("ssssss", $username, $exname, $exdesc, $eximg, $exdifficulty, $focusbody);

        if ($stmt->execute()) {
            return true;
        } else {
            return "Failed to insert exercise: " . $stmt->error;
        }
    }

    public function getFocusBodyByUsername($table, $username)
    {
        $username = $this->connect->real_escape_string($username);
        $sql = "SELECT DISTINCT focusbody FROM $table WHERE username = ?";
        $stmt = $this->connect->prepare($sql);
        $stmt->bind_param("s", $username);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result && $result->num_rows != 0) {
            $focusBodies = array();
            while ($row = $result->fetch_assoc()) {
                $focusBodies[] = $row['focusbody'];
            }
            return $focusBodies;
        } else {
            return false;
        }
    }
    

    public function getLevelByUsername($table, $username)
    {
        $username = $this->connect->real_escape_string($username);
        $sql = "SELECT levels FROM $table WHERE username = ?";
        $stmt = $this->connect->prepare($sql);
        $stmt->bind_param("s", $username);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result && $result->num_rows != 0) {
            $row = $result->fetch_assoc();
            $level = $row['levels'];
            return $level;
        } else {
            return false;
        }
    }

    function createLevel($table, $username, $Levels)
    {
        // Prepare data to prevent SQL injection
        $username = $this->prepareData($username);
        $Levels = $this->prepareData($Levels);
    
        // SQL query to insert or update
        $this->sql = "INSERT INTO " . $table . " (username, Levels) VALUES ('" . $username . "', '" . $Levels . "')
                      ON DUPLICATE KEY UPDATE Levels = VALUES(Levels)";
    
        // Execute query
        if (mysqli_query($this->connect, $this->sql)) {
            return true;
        } else {
            return false;
        }
    }


    public function getWarmUp($table, $focusbody) {
        $sql = "SELECT exname, exdesc, eximg, `Loss Weight` FROM $table WHERE focusbody = ?";
        $stmt = $this->connect->prepare($sql);

        $stmt->bind_param("s", $focusbody);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result && $result->num_rows != 0) {
            $exerciseRecords = array();
            while ($row = $result->fetch_assoc()) {
                $exerciseRecords[] = $row;
            }
            $stmt->close();
            return $exerciseRecords;
        } else {
            $stmt->close();
            return false;
        }
    }

    function trackingExercise ($table, $username, $exname, $eximg)
    {

        $username = $this->prepareData($username);
        $exname = $this->prepareData($exname);
        $eximg = $this->prepareData($eximg);

        $this->sql = "INSERT INTO " . $table . " (username, exname, eximg) VALUES ('" . $username . "','" . $exname . "','" . $eximg . "')";

        if (mysqli_query($this->connect, $this->sql)) {
            return true;
        } else {
            return false;
        }
    }

    public function getTrackingExercise($table, $username) {
        $sql = "SELECT exname, eximg FROM $table WHERE username = ?";
        $stmt = $this->connect->prepare($sql);

        $stmt->bind_param("s", $username);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result && $result->num_rows != 0) {
            $exerciseRecords = array();
            while ($row = $result->fetch_assoc()) {
                $exerciseRecords[] = $row;
            }
            $stmt->close();
            return $exerciseRecords;
        } else {
            $stmt->close();
            return false;
        }
    }

    public function getFocus($table, $focusbody)
    {
        $sql = "SELECT exname, exdesc, eximg, exdifficulty FROM $table WHERE focusbody = ?";
        $stmt = $this->connect->prepare($sql);
        $stmt->bind_param("s", $focusbody); 
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result && $result->num_rows != 0) {
            $exerciseRecords = array();
            while ($row = $result->fetch_assoc()) {
                $exerciseRecords[] = $row;
            }
            return $exerciseRecords;
        } else {
            return false;
        }
    }


    public function deleteExercise($username, $exname) {
        $query = "DELETE FROM exrecord_user WHERE username = ? AND exname = ?";
        $stmt = $this->connect->prepare($query); 
        if ($stmt === false) {
            return "Failed to prepare statement";
        }
        $stmt->bind_param("ss", $username, $exname);
        if ($stmt->execute()) {
            return "Exercise Deleted";
        } else {
            return "Failed to delete exercise";
        }
        $stmt->close();
    }

    function insertExercise($username, $exname, $exdesc, $eximg, $exdifficulty, $focusbody) {
        $query = "INSERT INTO exrecord_user (username, exname, exdesc, eximg, exdifficulty, focusbody) VALUES (?, ?, ?, ?, ?, ?)";
        $stmt = $this->connect->prepare($query); 
        if ($stmt === false) {
            return "Failed to prepare statement";
        }
        $stmt->bind_param("ssssss", $username, $exname, $exdesc, $eximg, $exdifficulty, $focusbody);
        if ($stmt->execute()) {
            $stmt->close(); // Close the statement here
            return "Exercise Inserted";
        } else {
            $stmt->close(); // Close the statement here
            return "Failed to insert exercise: " . $stmt->error;
        }
    }
    
    public function sendOTP($email, $otp, $expiry) {
        $query = "INSERT INTO otp_storage (email, otp, expiry) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE otp=?, expiry=?";
        $stmt = $this->connect->prepare($query);
        if ($stmt === false) {
            return false;
        }
        $stmt->bind_param("sssss", $email, $otp, $expiry, $otp, $expiry);
        if ($stmt->execute()) {
            $stmt->close();
            return true;
        } else {
            $stmt->close();
            return false;
        }
    }

    public function verifyOTP($email) {
        $sql = "SELECT otp, expiry FROM otp_storage WHERE email = ?";
        $stmt = $this->connect->prepare($sql);
    
        if ($stmt) {
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $result = $stmt->get_result();
    
            if ($result && $result->num_rows != 0) {
                $row = $result->fetch_assoc();
                $stmt->close();
                return $row;
            } else {
                $stmt->close();
                return false;
            }
        } else {
            return false;
        }
    }
    
    public function insertBodyAssessment($table, $username, $height, $weight, $focus_body, $goal, $level, $weekly_goal) {
        $username = $this->prepareData($username);
        $height = $this->prepareData($height);
        $weight = $this->prepareData($weight);
        $focus_body = $this->prepareData($focus_body);
        $goal = $this->prepareData($goal);
        $level = $this->prepareData($level);
        $weekly_goal = $this->prepareData($weekly_goal);

        $this->sql = "INSERT INTO " . $table . " (user_id, height, weight, focus_body, goal, level, weekly_goal) VALUES ('$username', '$height', '$weight', '$focus_body', '$goal', '$level', '$weekly_goal')";

        if (mysqli_query($this->connect, $this->sql)) {
            return true;
        } else {
            return false;
        }
    }

    public function insertDuration($table, $username, $time_seconds, $date) {
        $username = $this->prepareData($username);
        $time_seconds = $this->prepareData($time_seconds);
        $date = $this->prepareData($date);
    
        // Check if the record with the same username and date already exists
        $checkSql = "SELECT * FROM $table WHERE username = ? AND date = ?";
        $stmt = mysqli_prepare($this->connect, $checkSql);
        mysqli_stmt_bind_param($stmt, 'ss', $username, $date);
        mysqli_stmt_execute($stmt);
        $result = mysqli_stmt_get_result($stmt);
    
        if (mysqli_num_rows($result) > 0) {
            // Record exists, update the time_seconds by adding to the existing value
            $this->sql = "UPDATE $table 
                          SET time_seconds = time_seconds + ?, daily_seconds = time_seconds + ? 
                          WHERE username = ? AND date = ?";
            $stmt = mysqli_prepare($this->connect, $this->sql);
            mysqli_stmt_bind_param($stmt, 'isss', $time_seconds, $time_seconds, $username, $date);
        } else {
            // Record doesn't exist, insert a new row
            $this->sql = "INSERT INTO $table (username, time_seconds, date) 
                          VALUES (?, ?, ?)";
            $stmt = mysqli_prepare($this->connect, $this->sql);
            mysqli_stmt_bind_param($stmt, 'sis', $username, $time_seconds, $date);
        }
    
        // Execute the query
        if (mysqli_stmt_execute($stmt)) {
            return true;
        } else {
            // Optionally log the error or handle it as needed
            return false;
        }
    }
    

    public function insertZeroDuration($table, $username, $time_seconds, $date) {
        $username = $this->prepareData($username);
        $time_seconds = $this->prepareData($time_seconds);
        $date = $this->prepareData($date);
        
        // Check if the record with the same username and date already exists
        $checkSql = "SELECT * FROM $table WHERE username = '$username' AND date = '$date'";
        $result = mysqli_query($this->connect, $checkSql);
    
        if (mysqli_num_rows($result) > 0) {
            // Record exists, update the time_seconds by adding to the existing value
            $this->sql = "UPDATE $table 
                          SET time_seconds = '$time_seconds' 
                          WHERE username = '$username' AND date = '$date'";
        } else {
            // Record doesn't exist, insert a new row
            $this->sql = "INSERT INTO $table (username, time_seconds, date) 
                          VALUES ('$username', '$time_seconds', '$date')";
        }
        
        // Execute the query
        if (mysqli_query($this->connect, $this->sql)) {
            return true;
        } else {
            return false;
        }
    }
    
    

    public function addAssessment($table, $username, $gender, $focusArea, $mainGoal, $difficulty, $weeklyGoal, $weight, $height) {
        $sql = "INSERT INTO $table (username, gender, focusArea, mainGoal, difficulty, weeklyGoal, weight, height) 
                VALUES ('$username', '$gender', '$focusArea', '$mainGoal', '$difficulty', '$weeklyGoal', '$weight', '$height')";

        if ($this->conn->query($sql) === TRUE) {
            return true;
        } else {
            return false;
        }
    }
    public function getFocusBody($table, $focusbody, $exdifficulty, $username) {
        // Fetch the activity from activity_goal based on username
        $activitySql = "SELECT activity FROM activity_goal WHERE username = ?";
        $activityStmt = $this->connect->prepare($activitySql);
        $activityStmt->bind_param("s", $username);
        $activityStmt->execute();
        $activityResult = $activityStmt->get_result();
    
        if ($activityResult && $activityResult->num_rows != 0) {
            $activityRow = $activityResult->fetch_assoc();
            $activity = $activityRow['activity'];
    
            error_log("Activity column: " . $activity);
    
            // Use the activity as a column in the SELECT query with an alias
            $sql = "SELECT exname, exdesc, eximg, `$activity` AS activity_value, other_focus
                    FROM $table 
                    WHERE focusbody = ? AND exdifficulty = ?";
    
            $stmt = $this->connect->prepare($sql);
            $stmt->bind_param("ss", $focusbody, $exdifficulty);
            $stmt->execute();
            $result = $stmt->get_result();
    
            if ($result && $result->num_rows != 0) {
                $exerciseRecords = array();
                while ($row = $result->fetch_assoc()) {
                    // Add the fetched activity to each record
                    $row['activity'] = $activity;
                    error_log("Fetched exercise: " . print_r($row, true)); // Debugging output
                    $exerciseRecords[] = $row;
                }
                return $exerciseRecords;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    

    public function update_exercise_count($table, $username, $date) {
        // Prepare the query for inserting or updating exercise count
        $query = "INSERT INTO $table (username, date, exercise_count) VALUES (?, ?, 1) 
                  ON DUPLICATE KEY UPDATE exercise_count = exercise_count + 1";
    
        // Prepare the statement
        $stmt = $this->connect->prepare($query); 
        if ($stmt === false) {
            error_log("Failed to prepare the INSERT/UPDATE statement: " . $this->conn->error);
            return false;
        }
    
        // Bind the parameters
        $stmt->bind_param("ss", $username, $date);
    
        // Execute the query and return success or failure
        if ($stmt->execute()) {
            $stmt->close(); // Close the statement
            return true;
        } else {
            error_log("Database query error: " . $stmt->error);
            $stmt->close(); // Close the statement
            return false;
        }
    }
    
    public function executeQuery($table, $username, $exerciseName, $sets, $reps, $date) {
        $query = "INSERT INTO $table (username, exercise_name, sets, reps, log_date) 
                  VALUES (?, ?, ?, ?, ?)
                  ON DUPLICATE KEY UPDATE sets=?, reps=?";
        $stmt = $this->connect->prepare($query);
        $stmt->bind_param("sssssii", $username, $exerciseName, $sets, $reps, $date, $sets, $reps);
        
        if ($stmt->execute()) {
            return true;
        } else {
            error_log("SQL Error: " . $this->connect->error);  // Log SQL errors
            return false;
        }
    }

    public function insertCaloriesBurned($table, $username, $exerciseName, $caloriesBurned, $durationInSeconds, $date) {
        $query = "INSERT INTO $table (username, exercise_name, calories_burned, duration_in_seconds, date) 
                  VALUES (?, ?, ?, ?, ?)
                  ON DUPLICATE KEY UPDATE calories_burned=?, duration_in_seconds=?";
        
        $stmt = $this->connect->prepare($query);
        $stmt->bind_param("ssdisdi", $username, $exerciseName, $caloriesBurned, $durationInSeconds, $date, $caloriesBurned, $durationInSeconds);

        
        if ($stmt->execute()) {
            return true;
        } else {
            error_log("SQL Error: " . $this->connect->error);
            return false;
        }
    }    
      

    public function getWeight($username) {
        $query = "SELECT weight FROM bmi WHERE username = ?";
        $stmt = $this->connect->prepare($query);
        if (!$stmt) {
            error_log("Prepare failed: " . $this->connect->error);
            return null;
        }
    
        $stmt->bind_param("s", $username);
        $stmt->execute();
        $result = $stmt->get_result();
        
        if ($row = $result->fetch_assoc()) {
            return $row['weight']; // Return the weight directly
        } else {
            return null;
        }
    }
    

    public function getDuration($username, $date) {
        // Define the query to select time_seconds for a specific user and date
        $query = "SELECT time_seconds FROM exercise_duration_log WHERE username = ? AND date = ?";
        
        // Prepare the statement
        $stmt = $this->connect->prepare($query);
        if (!$stmt) {
            error_log("Prepare failed: " . $this->connect->error);
            return false;
        }
        
        // Bind parameters
        $stmt->bind_param("ss", $username, $date);
        
        // Execute the statement
        if (!$stmt->execute()) {
            error_log("Execute failed: " . $stmt->error);
            return false; // Return false on execution failure
        }

        // Get the result
        $result = $stmt->get_result();
        
        if ($result && $result->num_rows > 0) {
            $duration = [];
            while ($row = $result->fetch_assoc()) {
                $duration[] = $row; // Append each row to the array
            }
            return $duration; // Return the array with exercise data
        } else {
            return []; // Return an empty array if no records found
        }
    }

    public function getDurationDaily($username, $date) {
        // Define the query to select daily_seconds for a specific user and date
        $query = "SELECT daily_seconds FROM exercise_duration_log WHERE username = ? AND date = ?";
        
        // Prepare the statement
        $stmt = $this->connect->prepare($query);
        if (!$stmt) {
            error_log("Prepare failed: " . $this->connect->error);
            return false;
        }
        
        // Bind parameters
        $stmt->bind_param("ss", $username, $date);
        
        // Execute the statement
        if (!$stmt->execute()) {
            error_log("Execute failed: " . $stmt->error);
            return false; // Return false on execution failure
        }
    
        // Get the result
        $result = $stmt->get_result();
        
        if ($result && $result->num_rows > 0) {
            $duration = [];
            while ($row = $result->fetch_assoc()) {
                $duration[] = $row; // Append each row to the array
            }
            return $duration; // Return the array with exercise data
        } else {
            return []; // Return an empty array if no records found
        }
    }
    

    public function getExerciseMetValue($exercise_name) {
        // Define the query to select met_value from exercise_met_values
        $query = "SELECT met_value FROM exercise_met_values WHERE exercise_name = ?";
        
        // Prepare the statement
        $stmt = $this->connect->prepare($query);
        if (!$stmt) {
            error_log("Prepare failed: " . $this->connect->error);
            return null;
        }
        
        // Bind the exercise_name parameter
        $stmt->bind_param("s", $exercise_name);
        
        // Execute the statement
        if (!$stmt->execute()) {
            error_log("Execute failed: " . $stmt->error);
            return null; // Return null on execution failure
        }

        // Get the result
        $result = $stmt->get_result();
        
        // Fetch the met_value data from the result set
        if ($row = $result->fetch_assoc()) {
            return (float)$row['met_value']; // Return the MET value
        } else {
            return null; // Return null if no MET value is found for the exercise name
        }
    }

    public function executeWeightQuery($table, $username, $weight, $log_date) {
        // Prepare the SQL query with placeholders
        $query = "INSERT INTO $table (username, weight, log_date) 
                  VALUES (?, ?, ?) 
                  ON DUPLICATE KEY UPDATE weight = ?";
    
        // Prepare the statement
        if ($stmt = $this->connect->prepare($query)) {
            // Bind parameters: 's' for string, 'd' for double, 's' for string (date)
            $stmt->bind_param("sdss", $username, $weight, $log_date, $weight);
            
            // Execute the statement
            if ($stmt->execute()) {
                $stmt->close();
                return true;
            } else {
                // Log SQL execution errors
                error_log("SQL Execution Error: " . $stmt->error);
                $stmt->close();
                return false;
            }
        } else {
            // Log SQL preparation errors
            error_log("SQL Preparation Error: " . $this->connect->error);
            return false;
        }
    }

    public function insertWeightLogs($table1, $table2, $username, $weight, $log_date) {
        // First query for the first table (table1)
        $query1 = "INSERT INTO $table1 (username, weight, log_date) 
                   VALUES (?, ?, ?) 
                   ON DUPLICATE KEY UPDATE log_date = ?, weight = ?";
        
        // Second query for the second table (table2)
        $query2 = "UPDATE $table2 SET weight = ? WHERE username = ?";
        
        // First query execution
        if ($stmt1 = $this->connect->prepare($query1)) {
            // Bind parameters for the first query: 's' for string, 'd' for double, 's' for string (date)
            $stmt1->bind_param("sdssd", $username, $weight, $log_date, $log_date, $weight);
        
            // Execute the first statement
            if (!$stmt1->execute()) {
                // Log SQL execution errors for the first query
                error_log("SQL Execution Error for query1: " . $stmt1->error);
                $stmt1->close();
                return false;
            }
            $stmt1->close();
        } else {
            // Log SQL preparation errors for the first query
            error_log("SQL Preparation Error for query1: " . $this->connect->error);
            return false;
        }
        
        // Second query execution
        if ($stmt2 = $this->connect->prepare($query2)) {
            // Bind parameters for the second query: 'd' for double (weight) and 's' for string (username)
            $stmt2->bind_param("ds", $weight, $username);
        
            // Execute the second statement
            if (!$stmt2->execute()) {
                // Log SQL execution errors for the second query
                error_log("SQL Execution Error for query2: " . $stmt2->error);
                $stmt2->close();
                return false;
            }
            $stmt2->close();
        } else {
            // Log SQL preparation errors for the second query
            error_log("SQL Preparation Error for query2: " . $this->connect->error);
            return false;
        }
        
        // If both queries succeeded
        return true;
    }
    
    
    public function getWeightLogsLastThreeMonths($table, $username) {
        $query = "SELECT weight, log_date 
                  FROM $table 
                  WHERE username = ? 
                    AND log_date >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH)
                  ORDER BY log_date ASC";
        
        if ($stmt = $this->connect->prepare($query)) {
            $stmt->bind_param("s", $username);
            
            if ($stmt->execute()) {
                $result = $stmt->get_result();
                $weightLogs = [];
                while ($row = $result->fetch_assoc()) {
                    $weightLogs[] = $row;
                }
                $stmt->close();
                return $weightLogs;
            } else {
                error_log("SQL Execution Error: " . $stmt->error);
                $stmt->close();
                return false;
            }
        } else {
            error_log("SQL Preparation Error: " . $this->connect->error);
            return false;
        }
    }   

    public function getWeightLogsByDateRange($table, $username, $start_date, $end_date) {
        // Prepare the SQL query to retrieve weight logs within a specified date range
        $query = "SELECT weight, log_date 
                  FROM $table 
                  WHERE username = ? 
                    AND log_date BETWEEN ? AND ? 
                  ORDER BY log_date ASC";
    
        // Prepare the statement
        if ($stmt = $this->connect->prepare($query)) {
            // Bind the parameters to the query: 's' for string (username) and 's' for the date (start_date and end_date)
            $stmt->bind_param("sss", $username, $start_date, $end_date);
            
            // Execute the query
            if ($stmt->execute()) {
                // Get the result set from the executed query
                $result = $stmt->get_result();
                $weightLogs = [];
    
                // Fetch the result as an associative array and store each row in the weightLogs array
                while ($row = $result->fetch_assoc()) {
                    $weightLogs[] = $row;
                }
    
                // Close the statement after fetching the results
                $stmt->close();
    
                // Return the array of weight logs
                return $weightLogs;
    
            } else {
                // Log the SQL execution error and return false
                error_log("SQL Execution Error: " . $stmt->error);
                $stmt->close();
                return false;
            }
        } else {
            // Log the SQL preparation error and return false
            error_log("SQL Preparation Error: " . $this->connect->error);
            return false;
        }
    }
    

    function getCurrentWeightFromBMI($username) {

        // Prepare the SQL query to retrieve the weight from the bmi table
        $query = "SELECT weight FROM bmi WHERE username = ?";  // Get the latest entry
        
        // Prepare the statement
        if ($stmt = $this->connect->prepare($query)) {
            // Bind the username parameter
            $stmt->bind_param("s", $username);
            
            // Execute the statemnt
            if ($stmt->execute()) {
                $result = $stmt->get_result();
                if ($result && $row = $result->fetch_assoc()) {
                    $stmt->close();
                    return (float)$row['weight'];  // Return the weight as a float
                } else {
                    $stmt->close();
                    return null; // No weight entry found
                }
            } else {
                error_log("SQL Execution Error in getCurrentWeightFromBMI: " . $stmt->error);
                $stmt->close();
                return null;
            }
        } else {
            error_log("SQL Preparation Error in getCurrentWeightFromBMI: " . $this->connect->error);
            return null;
        }
    }

    public function insertEmgDurations($username, $date, $below_easy_seconds, $easy_seconds, $medium_seconds, $hard_seconds) {
    
        // Check if a record exists
        $checkQuery = "SELECT id FROM `user_emg_durations` WHERE username = ? AND date = ?";
        if ($stmtCheck = $this->connect->prepare($checkQuery)) {
            $stmtCheck->bind_param("ss", $username, $date);
            $stmtCheck->execute();
            $stmtCheck->store_result();
            $recordExists = $stmtCheck->num_rows > 0;
            $stmtCheck->close();
        } else {
            echo "SQL Preparation Error for checkQuery: " . $this->connect->error;
            error_log("SQL Preparation Error for checkQuery: " . $this->connect->error);
            return false;
        }
    
        if ($recordExists) {
            // Update existing record
            $updateQuery = "UPDATE `user_emg_durations` SET 
                            below_easy_seconds = ?, 
                            easy_seconds = ?, 
                            medium_seconds = ?, 
                            hard_seconds = ?
                            WHERE username = ? AND date = ?";
            if ($stmtUpdate = $this->connect->prepare($updateQuery)) {
                $stmtUpdate->bind_param("iiiiss", $below_easy_seconds, $easy_seconds, $medium_seconds, $hard_seconds, $username, $date);
                if (!$stmtUpdate->execute()) {
                    echo "SQL Execution Error for updateQuery: " . $stmtUpdate->error;
                    error_log("SQL Execution Error for updateQuery: " . $stmtUpdate->error);
                    $stmtUpdate->close();
                    return false;
                }
                $stmtUpdate->close();
            } else {
                echo "SQL Preparation Error for updateQuery: " . $this->connect->error;
                error_log("SQL Preparation Error for updateQuery: " . $this->connect->error);
                return false;
            }
        } else {
            // Insert new record
            $insertQuery = "INSERT INTO `user_emg_durations` 
                           (username, date, below_easy_seconds, easy_seconds, medium_seconds, hard_seconds) 
                           VALUES (?, ?, ?, ?, ?, ?)";
            if ($stmtInsert = $this->connect->prepare($insertQuery)) {
                $stmtInsert->bind_param("ssiiii", $username, $date, $below_easy_seconds, $easy_seconds, $medium_seconds, $hard_seconds);
                if (!$stmtInsert->execute()) {
                    echo "SQL Execution Error for insertQuery: " . $stmtInsert->error;
                    error_log("SQL Execution Error for insertQuery: " . $stmtInsert->error);
                    $stmtInsert->close();
                    return false;
                }
                $stmtInsert->close();
            } else {
                echo "SQL Preparation Error for insertQuery: " . $this->connect->error;
                error_log("SQL Preparation Error for insertQuery: " . $this->connect->error);
                return false;
            }
        }
    
        return true;
    }

    function getEMGDuration($username, $date) {
        // Prepare the SQL query to retrieve EMG duration data from the user_emg_durations table
        $query = "SELECT below_easy_seconds, easy_seconds, medium_seconds, hard_seconds  
                  FROM user_emg_durations 
                  WHERE username = ? AND date = ? 
                  LIMIT 1";  // Ensures only one record is fetched
    
        // Prepare the SQL statement
        if ($stmt = $this->connect->prepare($query)) {
            // Bind the username and date parameters to the SQL query
            $stmt->bind_param("ss", $username, $date);
            
            // Execute the SQL statement
            if ($stmt->execute()) {
                // Get the result set from the executed statement
                $result = $stmt->get_result();
                
                // Check if a row was returned
                if ($result && $row = $result->fetch_assoc()) {
                    $stmt->close();
                    
                    // Return the EMG duration data as an associative array
                    return array(
                        'below_easy_seconds' => isset($row['below_easy_seconds']) ? (float)$row['below_easy_seconds'] : 0.0,
                        'easy_seconds'       => isset($row['easy_seconds']) ? (float)$row['easy_seconds'] : 0.0,
                        'medium_seconds'     => isset($row['medium_seconds']) ? (float)$row['medium_seconds'] : 0.0,
                        'hard_seconds'       => isset($row['hard_seconds']) ? (float)$row['hard_seconds'] : 0.0
                    );
                } else {
                    // No matching record found
                    $stmt->close();
                    return null;
                }
            } else {
                // Log SQL execution error
                error_log("SQL Execution Error in getEMGDuration: " . $stmt->error);
                $stmt->close();
                return null;
            }
        } else {
            // Log SQL preparation error
            error_log("SQL Preparation Error in getEMGDuration: " . $this->connect->error);
            return null;
        }
    }

    function sendMessage($table, $sender, $receiver, $message) {
        // Prepare the SQL query to insert a message into the specified table
        $query = "INSERT INTO $table (sender, receiver, message) VALUES (?, ?, ?)";

        // Prepare the SQL statement
        if ($stmt = $this->connect->prepare($query)) {
            // Bind the sender, receiver, and message parameters to the SQL query
            $stmt->bind_param("sss", $sender, $receiver, $message);

            // Execute the SQL statement
            if ($stmt->execute()) {
                $stmt->close();
                return true; // Successfully inserted the message
            } else {
                // Log SQL execution error
                error_log("SQL Execution Error in sendMessage: " . $stmt->error);
                $stmt->close();
                return false; // Insertion failed
            }
        } else {
            // Log SQL preparation error
            error_log("SQL Preparation Error in sendMessage: " . $this->connect->error);
            return false; // Statement preparation failed
        }
    }

    function getMessages($table, $user) {
        // Prepare the SQL query to select messages where the user is either the sender or receiver
        $query = "SELECT sender, receiver, message, timestamp 
                FROM $table 
                WHERE sender = ? OR receiver = ? 
                ORDER BY timestamp ASC";

        // Prepare the SQL statement
        if ($stmt = $this->connect->prepare($query)) {
            // Bind the user parameter twice for both sender and receiver fields
            $stmt->bind_param("ss", $user, $user);

            // Execute the SQL statement
            if ($stmt->execute()) {
                // Get the result set from the executed statement
                $result = $stmt->get_result();
                
                // Fetch all rows as an associative array
                $messages = array();
                while ($row = $result->fetch_assoc()) {
                    $messages[] = array(
                        'sender' => $row['sender'],
                        'receiver' => $row['receiver'],
                        'message' => $row['message'],
                        'timestamp' => $row['timestamp']
                    );
                }

                // Close the statement and return the messages
                $stmt->close();
                return $messages;
            } else {
                // Log SQL execution error
                error_log("SQL Execution Error in getMessages: " . $stmt->error);
                $stmt->close();
                return null;
            }
        } else {
            // Log SQL preparation error
            error_log("SQL Preparation Error in getMessages: " . $this->connect->error);
            return null;
        }
    }

    public function getMessagesBetweenUsers($table, $user1, $user2) {
        $query = "SELECT sender, receiver, message, timestamp 
                  FROM $table 
                  WHERE (sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?)
                  ORDER BY timestamp ASC";
    
        if ($stmt = $this->connect->prepare($query)) {
            $stmt->bind_param("ssss", $user1, $user2, $user2, $user1);
            if ($stmt->execute()) {
                $result = $stmt->get_result();
                $messages = array();
                while ($row = $result->fetch_assoc()) {
                    $messages[] = $row;
                }
                $stmt->close();
                return $messages;
            } else {
                error_log("SQL Execution Error in getMessagesBetweenUsers: " . $stmt->error);
                $stmt->close();
                return null;
            }
        } else {
            error_log("SQL Preparation Error in getMessagesBetweenUsers: " . $this->connect->error);
            return null;
        }
    }

    function getFocusByExname($table, $exname)
    {
        // Sanitize the input to prevent SQL injection
        $exname = $this->prepareData($exname);
        
        // Use prepared statements for security
        $stmt = $this->connect->prepare("SELECT focusbody FROM `$table` WHERE exname = ?");
        if (!$stmt) {
            // Log error for debugging
            error_log("Prepare failed: (" . $this->connect->errno . ") " . $this->connect->error);
            return false; // Statement preparation failed
        }
        
        $stmt->bind_param("s", $exname);
        if (!$stmt->execute()) {
            // Log error for debugging
            error_log("Execute failed: (" . $stmt->errno . ") " . $stmt->error);
            $stmt->close();
            return false; // Execution failed
        }
        
        $result = $stmt->get_result();
        if ($result && $result->num_rows > 0) {
            $row = $result->fetch_assoc(); // Get the first row
            $focusBody = trim($row['focusbody']); // Ensure no extra whitespace
            
            // Return as an associative array without validation
            return ["focus_area" => $focusBody];
        } else {
            $stmt->close();
            return false; // No entry found
        }
    }


    
}
?>
