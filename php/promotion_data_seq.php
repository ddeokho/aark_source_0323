<?php
    require('function.php');
    $param = $_POST['param'];
    $out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $conn = connect_db();
    
    $query = "select * from promotion where seq = '$data->seq'";
    if( $result = mysqli_query($conn, $query) )
    {  
        $row = mysqli_fetch_assoc($result);
        echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'true',
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
        "timestamp" => $row['timestamp'],
        "url" => $row['url']        
         ) );
    }
    else
         echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );

    mysqli_close($conn);
?>