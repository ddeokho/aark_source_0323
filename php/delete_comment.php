<?php		
    require('function.php');
	$param = $_POST['param'];
    $out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $conn = connect_db(); 
        
    $query = "delete from comment where seq = '".$data->seq."'";

    if( $result = mysqli_query($conn, $query) )
    {//댓글 카운트 아래로
        $query = "update case_text set comment_count='$data->count', comment_timestamp=0 where seq='$data->example_seq' "; //댓글 삭제 시간 삭제

        if( $result = mysqli_query($conn, $query) )
        {        
            echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'true', 'seq' => $data->seq ) );	    
        }
        else
            echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );   
    
    }
    else{
        echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );   
    }     
    
    mysqli_close($conn);
?>