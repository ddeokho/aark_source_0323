<?php     
    require('function.php');  
    $param = $_POST['param'];
    $out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $conn = connect_db();     
    
    $query = "select * from member where seq = '".$data->seq."'";

    if( $result = mysqli_query($conn, $query) )
    {
        $count = mysqli_num_rows($result);

        if( $count == 0 )
        {
            echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );
        }
        else
        {
            $row = mysqli_fetch_assoc($result);             
            echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'true', 'seq'=> $row['seq'], 'name'=> $row['name'], 'email'=> $row['email'], 'gender'=> $row['gender'],  'univ'=> $row['univ'], 'grade'=> $row['grade'],
            'address'=> $row['address'], 'phone'=> $row['phone'], ) );
        }
    }
    
    mysqli_close($conn);
?>