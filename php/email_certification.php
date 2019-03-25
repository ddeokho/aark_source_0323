<?php
    include_once('mailer.lib.php');
    require('function.php');

    $param = $_POST['param'];
    $out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $conn = connect_db(); 
        
    // 키값 생성 함수
    function gen_activation_key($email){    
        $generatedKey = sha1(mt_rand(10000,99999).time().$email);
        return $generatedKey;
    }

    $query = "select * from member where email = '".$data->email."'";
    if( $result = mysqli_query($conn, $query) )
    {
        $count = mysqli_num_rows($result);

        if( $count == 0 )
        {
            $key = gen_activation_key( $data->email );    
            $query = "insert into email_certification( email, activation_status, activation_key ) values( '$data->email', 'waiting', '$key');";
            if( $result = mysqli_query($conn, $query) )
            {
                $title = "AARK 사례집 인증 메일 입니다.";
                $body = "
                <html>
                <head>
                </head>
                <body>
                <h3>아래의 링크롤 클릭해서 인증을 완료 해주세요.</h3>
                <a href=http://aarkapp.iptime.org/activation.php?email=$data->email&key=$key>인증 받기</a>
                </body>
                </html>
                ";
                $mail = new PHPMailer;
                $mail->CharSet = 'UTF-8';
                //Enable SMTP debugging. 
                $mail->SMTPDebug = 0;
                //Set PHPMailer to use SMTP.
                $mail->isSMTP();
                //Set SMTP host name                          
                $mail->Host = "smtp.gmail.com";
                //Set this to true if SMTP host requires authentication to send email
                $mail->SMTPAuth = true;
                //Provide username and password     
                $mail->Username = "aarkpush@gmail.com";
                $mail->Password = "sking28524";
                //If SMTP requires TLS encryption then set it
                $mail->SMTPSecure = "ssl";
                //Set TCP port to connect to 
                $mail->Port = 465;
                $mail->From = "aarkpush@gmail.com";  
                $mail->FromName = $data->title;
                $mail->addAddress( $data->email );
                $mail->isHTML(true);
                $mail->Subject = $title;
                $mail->Body = $body;
                $mail->AltBody = "This is the plain text version of the email content";

                if (!$mail->send())
                {
                    echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );   
                }
                else 
                {
                    echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'true' ) );
                }        
            }
            else
            {
                echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );
            }
        }
        else
        {            
            echo json_encode( array('packet_id' => $data->packet_id, 'result' => '-100' ) );         
        }
    }


    
  
   
    mysqli_close($conn);  
?>