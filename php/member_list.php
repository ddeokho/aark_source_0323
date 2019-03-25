<?php        
    require('function.php');
    session_start();  
    $param = $_POST['param'];
    $out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);

    $conn = connect_db();     
        $query = "select * from member where name like '%$data->search_text%' or email like '%$data->search_text%'";
        if( $result = mysqli_query($conn, $query) )
        {  
            $count = mysqli_num_rows($result);
            $data_list = [];
            
            while($row = mysqli_fetch_array($result)) {
                array_push($data_list, array(
                    "seq"=>$row['seq'], 
                    "name"=>$row['name'],
                    "email" => $row['email'],                
                    "gender" => $row['gender'],
                    "univ" => $row['univ'],
                    "grade" => $row['grade'],
                    "address" => $row['address'],
                    "phone" => $row['phone']                
                ));           
            }    
            
            $my_array = array('packet_id' => $data->packet_id, 'result' => 'true', 'data_list' => $data_list );
            echo json_encode($my_array, JSON_UNESCAPED_UNICODE);   
           
        }
        else
             echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );

        mysqli_close($conn);
?>