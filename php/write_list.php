<?php
    require('function.php');
    $param = $_POST['param'];
    $out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $conn = connect_db();
    
    $query = "select * from case_text where member_seq = '$data->member_seq' order by TIMESTAMP desc";    
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
                "timestamp" => $row['timestamp']
            ));           
        }
    
        $query2 = "SELECT case_text.seq, case_text.member_seq, case_text.year, case_text.title, case_text.content, case_text.cate_reg, case_text.cate_1, case_text.cate_2, case_text.cate_3, case_text.feq, case_text.grade, case_text.img_1, case_text.img_2, case_text.img_3, case_text.timestamp FROM case_text LEFT JOIN comment ON case_text.seq = comment.example_seq WHERE comment.member_seq= '$data->member_seq' order by TIMESTAMP desc";

            if( $result2 = mysqli_query($conn, $query2) )
            {
              while($row2 = mysqli_fetch_array($result2)) 
              {
                array_push($data_list, array(
                    "seq"=>$row2['seq'], 
                    "member_seq"=>$row2['member_seq'],
                    "year" => $row2['year'],
                    "title" => $row2['title'],
                    "content" => $row2['content'],
                    "cate_reg" => $row2['cate_reg'],
                    "cate_1" => $row2['cate_1'],
                    "cate_2" => $row2['cate_2'],
                    "cate_3" => $row2['cate_3'],                    
                    "feq" => $row2['feq'],
                    "grade" => $row2['grade'],
                    "img_1" => $row2['img_1'],
                    "img_2" => $row2['img_2'],
                    "img_3" => $row2['img_3'],
                    "timestamp" => $row2['timestamp']
                ));
            }
        }
        $my_array = array('packet_id' => $data->packet_id, 'result' => 'true', 'data_list' => $data_list );
        echo json_encode($my_array, JSON_UNESCAPED_UNICODE);   
       
    }
    else
         echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );

    mysqli_close($conn);
?>