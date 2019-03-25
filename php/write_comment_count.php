<?php
    require('function.php');
    $param = $_POST['param'];
    $out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $conn = connect_db();
    
    $query = "select * from comment where member_seq = '$data->member_seq'";
    if( $result = mysqli_query($conn, $query) )
    {  
        $count = mysqli_num_rows($result);              
        echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'true', 'comment_count' => $count ) );
       
    }
    else
        echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );

    mysqli_close($conn);
?>