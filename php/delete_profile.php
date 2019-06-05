<?php		
    require('function.php');
	$param = $_POST['param'];
    $out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $conn = connect_db();   
    
    // 작업 성공여부를 나타내는 플래그
    $success = true; 
    // 트랙잭션을 시작한다.
    $result = mysql_query("SET AUTOCOMMIT=0", $conn);
    $result = mysql_query("BEGIN", $conn);

    $query = "delete from member where email = '".$data->email."'";
    $result = mysqli_query($conn, $query);
    if( $result == false)
        $success = false;
    
   
    $query = "delete from email_certification where email = '".$data->email."'";
    $result = mysqli_query($conn, $query);
    if( $result == false )
        $success = false;

    $query = "delete from token where email = '".$data->email."'";
    $result = mysqli_query($conn, $query);
    if( $result == false )
        $success = false;

    $query = "delete from fcm_token where email = '".$data->email."'";
    $result = mysqli_query($conn, $query);
    if( $result == false )
        $success = false;

    // 작업 성공/실패 여부에 따라 COMMIT/ROLLBACK 처리한다.
    if($success == false) 
    {
        $result = mysql_query("ROLLBACK", $conn);        
        echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );
    }
    else 
    {
        $result = mysql_query("COMMIT", $conn);        
        echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'true' ) );
    }

    mysqli_close($conn);
?>