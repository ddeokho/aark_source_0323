<?php	
    require('function.php');	
	$param = $_POST['param'];
    $out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $conn = connect_db(); 
        
    $query = "select * from member where name = '".$data->name."' and phone = '".$data->phone."'";

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
	    	echo json_encode( array('packet_id' => $data->packet_id, 'result' => '1', 'email' => $row["email"] ) );	    	
	    }
    }
    
    mysqli_close($conn);
?>