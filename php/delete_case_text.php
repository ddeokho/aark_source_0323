<?php	
    require('function.php');	
	$param = $_POST['param'];
    $out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $conn = connect_db();     
    
    $query = "delete from case_text where seq = '".$data->seq."'";

    if( $result = mysqli_query($conn, $query) )
    {    
        $query2 = "delete from comment where example_seq = '".$data->seq."'";
        if( $result = mysqli_query($conn, $query2) )            
            echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'true', 'seq' => $data->seq ) );	    
        else
            echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false', 'error' => 401 ) );       
    }
    else
        echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false', 'error' => 400 ) );        
    
    mysqli_close($conn);
?>