<?php
    require('function.php');
    $param = $_POST['param'];
    $out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $conn = connect_db();
    
    $start = $data->start;
    $size = $data->size;   
    
    if($data->type == 1){
        $query = "select * from promotion where show_state=0 and type='$data->type' LIMIT $start, $size";//동아리홍보
    }
    else{
        $query = "select * from promotion where show_state=0 and type='$data->type' order by TIMESTAMP desc LIMIT $start, $size";//order by TIMESTAMP desc 추가 업체홍보 변경
    }
    

      
    if( $result = mysqli_query($conn, $query) )
    {  
        $count = mysqli_num_rows($result);
        if($count > 0 )
        {
            $data_list = [];
        
            while($row = mysqli_fetch_array($result)) {
                array_push($data_list, array(
                    "seq"=>$row['seq'], 
                    "seq2"=>$row['seq2'],
                    "member_seq" => $row['member_seq'],
                    "type" => $row['type'],
                    "name" => $row['name'],
                    "title" => $row['title'],
                    "email" => $row['email'],
                    "address" => $row['address'],
                    "latitude" => $row['latitude'],
                    "longitude" => $row['longitude'],
                    "phone" => $row['phone'],
                    "logo_img" => $row['logo_img'],
                    "feq" => $row['feq'],
                    "show_state" => $row['show_state'],
                    "timestamp" => $row['timestamp']                    
                ));
            }

            $my_array = array('packet_id' => $data->packet_id, 'result' => 'true', 'data_array' => $data_list );
            echo json_encode($my_array, JSON_UNESCAPED_UNICODE);   
        }
        else
        {
            echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );
        }
    }
    else
        echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );
    
    mysqli_close($conn);
?>