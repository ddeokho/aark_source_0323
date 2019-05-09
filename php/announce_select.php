<?php
    require('function.php');    
    $param = $_POST['param'];
    $out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $conn = connect_db();
    
    $start = $data->start;
    $size = $data->size;   
    $totalCount = 0; 
    
    $query = "select * from announce order by TIMESTAMP desc LIMIT $start, $size";
    
    if( $result = mysqli_query($conn, $query) )
    {  
        $count = mysqli_num_rows($result);
        $data_list = [];
            while($row = mysqli_fetch_array($result)) {
                array_push($data_list, array(                    
                    "seq"=>$row['seq'], 
                    "content"=>$row['content'],
                    "grade"=>$row['grade'],
                    "Timestamp" => $row['Timestamp']
                ));
            }
            $my_array = array('packet_id' => $data->packet_id, 'result' => 'true', 'total_count' => $totalCount, 'data_list' => $data_list );
            echo json_encode($my_array, JSON_UNESCAPED_UNICODE);   
       
    }
    else
        echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false') );

    mysqli_close($conn);
?>