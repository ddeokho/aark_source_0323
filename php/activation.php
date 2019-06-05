<?php    
    require('function.php');      
    $email = $_GET['email'];
    $key = $_GET['key'];        
    
    $result = verifyEmailAddress( $email, $key );

    echo $result;

    // 요청온 키값에 따라 검사하기
    function verifyEmailAddress($email, $vkey)
    {
            $conn = connect_db();                      
            $query = "select * from email_certification where email = '".$email."' and activation_key = '".$vkey."'";             

            if( $result = mysqli_query($conn, $query) )
            {
                $count = mysqli_num_rows($result);

                if( $count == 0 )
                {
                    $output='Unable to verify your email address.';
                }
                else
                {
                    $row = mysqli_fetch_assoc($result);
                    if($row["activation_status"] == "active")
                    {
                        $output='Your Email has already been verified.';
                    }
                    else
                    {
                        $query2 = "update email_certification set activation_status='active' where email = '".$email."' and activation_key = '".$vkey."'";                        
                        if( $result2 = mysqli_query($conn, $query2) )
                        {
                            $output='Email Verified';
                        }
                        else
                        {
                            $output='System Faced an error while updating your status.';
                        }
                    }                 
                }
            }
           
            mysql_close();
            return $output;
    }   
    
?>