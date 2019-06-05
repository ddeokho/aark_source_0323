<?php
    require('function.php');      
    $param = $_POST['param'];
    //$out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $conn = connect_db();
   
    // 키값 생성 함수
    function check_email_active( $conn, $email)
    {          
        $query = "select * from email_certification where email = '".$email."'";        
        if( $result = mysqli_query($conn, $query) )
        {
            $count = mysqli_num_rows($result);

            if( $count == 0 )
            {               
                return 0;
            }
            else
            {
                $row = mysqli_fetch_assoc($result);                
                if($row["activation_status"] == "active")
                {
                    return 1;
                }  
                else
                    return 0;              
            }
        } 

        return -100;
    }
    
    $query = "select * from member where phone = '".$data->phone."'";

    if( $result = mysqli_query($conn, $query) )
    {
        $count = mysqli_num_rows($result);

        if( $count == 0 )
        {
            if( check_email_active( $conn, $data->email ) == 1 )
            {          
                $query = "insert into member( name, email, password, gender, univ, grade, birth, phone ) values( '$data->name', '$data->email', '$data->password', '$data->gender', '$data->univ', '$data->grade', '$data->birth', '$data->phone' );";

                if( $result = mysqli_query($conn, $query) )
                {
                    $seq = mysqli_insert_id($conn);
                    $token = getToken( 30 );
                    $query = "insert into token( email, access_token ) values( '$data->email', '$token');";    
                    $result = mysqli_query($conn, $query);
                    
                    echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'true', 'token' => $token, 'seq' => $seq, 'name' => $data->name, 'email' => $data->email, 'password' => $data->password, 'gender' => $data->gender, 'univ' => $data->univ, 'grade' => $data->grade,  'birth' => $data->birth,  'phone' => $data->phone, 'timestamp' => "" ) );
                }
                else
                {
                    echo json_encode( array('packet_id' => $data->packet_id, 'result' => '-100' ) );
                }
            }
            else
            {
                echo json_encode( array('packet_id' => $data->packet_id, 'result' => '-1' ) );
            }    
        }
        else
        {            
            echo json_encode( array('packet_id' => $data->packet_id, 'result' => '-101' ) );         
        }
    }

    
    
    mysqli_close($conn);    
?>