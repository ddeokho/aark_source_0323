<?php
    require('function.php');
    $param = $_POST['param'];
    $out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $conn = connect_db();
    
    $query = "select * from regulation_main";

    if( $result = mysqli_query($conn, $query) )
    {  
        $count = mysqli_num_rows($result);
        if($count > 0 )
        {
            $data_list = [];
        
            while($row = mysqli_fetch_array($result)) {
                array_push($data_list, array(
                    "seq"=>$row['seq'], 
                    "regul"=>$row['regul'],
                    "content" => $row['content']                    
                ));
            }

            $my_array = array('packet_id' => $data->packet_id, 'result' => 'true', 'regulation_list' => $data_list );
            echo json_encode($my_array, JSON_UNESCAPED_UNICODE);   
        }
        else
        {
            echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );
        }
    }
    
    mysqli_close($conn);
?>