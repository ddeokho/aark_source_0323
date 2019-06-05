<?php      
    require('function.php'); 
    $param = $_POST['param'];
    //$out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $conn = connect_db(); 
        
    $query = "update promotion set show_state='$data->show_state' where seq='$data->seq'";

    if( $result = mysqli_query($conn, $query) )
    {
        echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'true', 'seq' => $data->seq,  'show_state' => $data->show_state ) );
    }
    else
        echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );
    
    mysqli_close($conn);
?>