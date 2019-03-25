<?php	
    require('function.php');	
	$param = $_POST['param'];
    $out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $conn = connect_db();
        
    $query = "update token set access_token='' where email='$data->email' ";          
    if( $result = mysqli_query($conn, $query) )
    {        
        echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'true' ) );	    
    }
    else
        echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );        
    
    mysqli_close($conn);
?>