<?php
    require('function.php');
    include_once('config.php');

    $param = $_POST['param'];
    //$out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);

    $conn = connect_db();

        $query = "update case_text set grade='$data->grade' where seq='$data->seq'";
        $tokens = array();
        if( $result = mysqli_query($conn, $query) )
        {
            echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'true', "seq" => $data->seq, "grade" => $data->grade ) );
            $query = "select * from member where seq = '".$data->member_seq."'";
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
        }
        else
            echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );

        $title = "AARK 사례집";
        $message = "올린 사례가 판별되었어요!";
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