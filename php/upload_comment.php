<?php    
    require('function.php'); 
    $param = $_POST['param'];
    //$out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);

    $conn = connect_db();  
              
    $query = "insert into comment( member_seq, example_seq, content ) values( '$data->member_seq', '$data->example_seq', '$data->content' );";
    
    if( $result = mysqli_query($conn, $query)){
        //댓글 업데이트
        $query = "update case_text set comment_count='$data->count', comment_timestamp=NOW() where seq='$data->example_seq' ";//댓글 추가 시간 업데이트

        if( $result = mysqli_query($conn, $query) )
        {     
            //해당 인원의 전체 댓글 수
            $query = "select * from comment where example_seq = '$data->example_seq'";

            if( $result = mysqli_query($conn, $query) )
            {  
                $count = mysqli_num_rows($result);              
                echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'true', 'comment_count' => $count ) );

                //푸시 체크
                $query = "select * from member where seq = '".$data->writer."'";
                $result = mysqli_query($conn,$query);
                if( mysqli_num_rows($result) > 0 )
                {
                    $row = mysqli_fetch_assoc($result);  
                    $query = "select token from fcm_token where email = '".$row['email']."'";
                    $result = mysqli_query($conn,$query);
                    if(mysqli_num_rows($result) > 0 ){

                        while ($row = mysqli_fetch_assoc($result)) 
                        {               
                            $tokens[] = $row["token"];
                        }
                    }    
                } 
                //
            }
            else
                echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false') );
        }
        else
        {
            echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );
        }
}else{
    echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );
}


    //푸시 추가
    $title = "AARK 사례집";
    $message = "새로운 댓글이 달렸어요.";
    $channel_id = "notice";
    $arr = array( 'title' => $title, 'message' => $message, 'channel_id' => $channel_id );
        //$arr['title'] = $title;
        //$arr['message'] = $message;

    $message_status = Android_Push($tokens, $arr);
        //echo $message_status;
        // 푸시 전송 결과 반환.
        //$obj = json_decode($message_status);

        // 푸시 전송시 성공 수량 반환.
        //$cnt = $obj->{"success"};
        //echo $cnt;

    function Android_Push($tokens, $message) {
        $url = 'https://fcm.googleapis.com/fcm/send';
        $apiKey = GOOGLE_API_KEY;

        $fields = array('registration_ids' => $tokens,'data' => $message);
        $headers = array('Authorization:key='.$apiKey,'Content-Type: application/json');

        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt ($ch, CURLOPT_SSL_VERIFYHOST, 0);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
        $result = curl_exec($ch);
        if ($result === FALSE) {
            die('Curl failed: ' . curl_error($ch));
        }
        curl_close($ch);
        return $result;
    }
    

    mysqli_close($conn);    
?>