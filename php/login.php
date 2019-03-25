<?php			
	require('function.php');

	$param = $_POST['param'];
    $out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $conn = connect_db(); 
        
    $query = "select * from member where email = '".$data->email."'" ;

    if( $result = mysqli_query($conn, $query) )
    {
	    $count = mysqli_num_rows($result);

	    if( $count == 0 )
	    {
	    	echo json_encode( array('packet_id' => $data->packet_id, 'result' => '0' ) );
	    }
	    else
	    {
	    	$row = mysqli_fetch_assoc($result);
	    	if($row["password"] == $data->password)
	    	{
	    		$token = getToken( 30 );	    		

	    		$query = "update token set access_token='$token' where email='$data->email' ";	    		
            	$result = mysqli_query($conn, $query);
	    		echo json_encode( array('packet_id' => $data->packet_id, 'result' => '1', 'token' => $token, 'seq' => $row["seq"], 'name' => $row["name"], 'email' => $row["email"], 'password' => $data->password, 'gender' => $row["gender"], 'univ' => $row["univ"], 'grade' => $row["grade"],  'birth' => $row["birth"],  'phone' => $row["phone"], 'timestamp' => $row["timestamp"] ) );
	    	}
	    	else
	    		echo json_encode( array('packet_id' => $data->packet_id, 'result' => '-101', 'email' => '-1' ) );
	    }
    }
    
    mysqli_close($conn);
?>