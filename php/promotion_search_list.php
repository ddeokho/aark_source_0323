<?php
    require('function.php');
    $param = $_POST['param'];
    $out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $conn = connect_db();
    
    $start = $data->start;
    $size = $data->size;   
    $query = "select * from promotion where (title like '%$data->search_text%' or name like '%$data->search_text%') and type = '$data->type' order by TIMESTAMP desc LIMIT $start, $size";
    if( $result = mysqli_query($conn, $query) )
    {  
        $count = mysqli_num_rows($result);
        $data_list = [];
        
        while($row = mysqli_fetch_array($result)) {
            array_push($data_list, array(
                "seq"=>$row['seq'], 
                "seq2"=>$row['seq2'], 
                "member_seq"=>$row['member_seq'],
                "type"=>$row['type'],
                "title" => $row['title'],
                "name" => $row['name'],
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
         echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );

    mysqli_close($conn);
?>