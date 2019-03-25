<?php    
    require('function.php');
    $param = $_POST['param'];
    //$out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $conn = connect_db(); 
       
    $query = "select * from member where phone = '".$data->phone."'";
    if( $result = mysqli_query($conn, $query) )
    {  
        $count = mysqli_num_rows($result);        
        $row = mysqli_fetch_assoc($result);

        if( $count == 0 || $row["seq"] == $data->seq )
        {
            $query = $query = "update member set name='$data->name', password='$data->password',gender='$data->gender', univ='$data->univ', birth='$data->birth', phone='$data->phone' where seq='$data->seq' ";
            
            if( $result = mysqli_query($conn, $query) )
            {       
                echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'true', 'name' => $data->name, 'password' => $data->password, 'gender' => $data->gender, 'univ' => $data->univ, 'birth' => $data->birth, 'phone' => $data->phone ) );
            }
            else
            {
                echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );
            }       
        }
        else
             echo json_encode( array('packet_id' => $data->packet_id, 'result' => '-102' ) );
    }
    else
        echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );
    
    mysqli_close($conn);    
?>