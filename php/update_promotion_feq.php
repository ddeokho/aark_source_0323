<?php    
    require('function.php'); 
    $param = $_POST['param'];
    //$out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $conn = connect_db();
       
    // 작업 성공여부를 나타내는 플래그
    $success = true; 
    // 트랙잭션을 시작한다.
    $result = mysql_query("SET AUTOCOMMIT=0", $conn);
    $result = mysql_query("BEGIN", $conn);

    $query = "update promotion set feq='$data->feq' where seq='$data->seq' ";
    $result = mysqli_query($conn, $query);
    if( $result == false)
        $success = false;

    if( $data->member_seq > -1 )
    {
        $query = "insert into statistics_table( ad_table_seq, member_seq, ad_feq ) values( '$data->seq', '$data->member_seq', '$data->feq');";
        $result = mysqli_query($conn, $query);
        if( $query == false)
            $success = false;    
    }
        
    if($success == false)
    {     
        $result = mysql_query("ROLLBACK", $conn);            
        echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );
    }
    else
    {
        $result = mysql_query("COMMIT", $conn);
        echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'true', 'seq' => $data->seq, 'feq' => $data->feq ) );        
    }   
    
    
    mysqli_close($conn);    
?>