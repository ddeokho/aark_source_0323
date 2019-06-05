<?php
    require('function.php'); 
    $param = $_POST['param'];
    $out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $conn = connect_db(); 
    
    if( $data->grade == -1 )
        $query = "select * from case_text where title like '%$data->search_text%' or content like '%$data->search_text%'";
    else
        $query = "select * from case_text where (title like '%$data->search_text%' or content like '%$data->search_text%') and grade='".$data->grade."' ";

    if( $result = mysqli_query($conn, $query) )
    {  
        $count = mysqli_num_rows($result);
        $data_list = [];
        
        while($row = mysqli_fetch_array($result)) {
            array_push($data_list, array(
                "seq"=>$row['seq'], 
                "member_seq"=>$row['member_seq'], 
                "year"=>$row['year'],
                "title"=>$row['title'],
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
                "comment_count" => $row['comment_count'],
                "comment_timestamp" => $row['comment_timestamp']
            ));           
        }    
        
        $my_array = array('packet_id' => $data->packet_id, 'result' => 'true', 'data_list' => $data_list );
        echo json_encode($my_array, JSON_UNESCAPED_UNICODE);   
       
    }
    else
         echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );

    mysqli_close($conn);
?>