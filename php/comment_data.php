<?php    
    require('function.php');
    $param = $_POST['param'];
    //$out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $conn = connect_db();    

    $query = "select * from comment where example_seq= '".$data->example_seq."' ";

    $start = $data->start;
    $size = $data->size;
    $query = "SELECT * FROM comment where example_seq = '$data->example_seq' ORDER BY TIMESTAMP DESC LIMIT $start, $size";
    
    if( $result = mysqli_query($conn, $query) )
    {  
        $count = mysqli_num_rows($result);        
        $data_list = [];
        
            while($row = mysqli_fetch_array($result)) 
            {
                $query2 = "select * from member where seq = '".$row['member_seq']."'";
                
                if( $result2 = mysqli_query($conn, $query2) )         
                {
                    $member_row = mysqli_fetch_assoc($result2);
                    array_push($data_list, array(
                    "seq"=>$row['seq'], 
                    "member_seq"=>$row['member_seq'],                    
                    "example_seq" => $row['example_seq'],
                    "img_name" => $row['img_name'],
                    "content" => $row['content'],                    
                    "timestamp" => $row['timestamp'],
                    "member_id"=>$member_row['email']
                    ));    
                }                
            }

            $my_array = array('packet_id' => $data->packet_id, 'result' => 'true', 'data_list' => $data_list );
            echo json_encode($my_array, JSON_UNESCAPED_UNICODE);   
       
    }
    
    mysqli_close($conn);
?>