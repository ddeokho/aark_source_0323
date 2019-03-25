<?php
    require('function.php');
    $param = $_POST['param'];
    $out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $conn = connect_db();    

    $start = $data->start;
    $size = $data->size;
    $query = "SELECT * FROM case_text ORDER BY TIMESTAMP DESC LIMIT $start, $size";

    if( $result = mysqli_query($conn, $query) )
    {  
        $count = mysqli_num_rows($result);
        $data_list = [];
        
            while($row = mysqli_fetch_array($result)) {
                array_push($data_list, array(
                    "seq"=>$row['seq'], 
                    "member_seq"=>$row['member_seq'],
                    "year" => $row['year'],
                    "title" => $row['title'],
                    "content" => $row['content'],
                    "cate_reg" => $row['cate_reg'],
                    "cate_1" => $row['cate_1'],
                    "cate_2" => $row['cate_2'],
                    "cate_3" => $row['cate_3'],                    
                    "feq" => $row['feq'],
                    "grade" => $row['grade'],
                    "img_1" => $row['img_1'],
                    "img_2" => $row['img_2'],
                    "img_3" => $row['img_3'],
                    "timestamp" => $row['timestamp'],
                ));
            }

            $my_array = array('packet_id' => $data->packet_id, 'result' => 'true', 'data_list' => $data_list );
            echo json_encode($my_array, JSON_UNESCAPED_UNICODE);   
       
    }
    
    mysqli_close($conn);
?>