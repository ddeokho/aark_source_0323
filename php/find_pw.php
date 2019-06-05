<?php	
    include_once('mailer.lib.php');	
    require('function.php');
	$param = $_POST['param'];
    $out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $conn = connect_db(); 
        
    $query = "select * from member where email = '".$data->email."' and name = '".$data->name."'";

    if( $result = mysqli_query($conn, $query) )
    {
	    $count = mysqli_num_rows($result);

	    if( $count == 0 )
	    {
	    	echo json_encode( array('packet_id' => $data->packet_id, 'result' => '0' ) );
	    }
	    else
	    {
	    	$row = mysqli_fetch_assoc($result);                	
            $title = "AARK 사례집 비밀번호 안내 입니다.";
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
            $mail->Body = sprintf("회원님의 비밀번호는 %s 입니다.", $row['password']);
            $mail->AltBody = "This is the plain text version of the email content";

            if (!$mail->send())
            {
                echo json_encode( array('packet_id' => $data->packet_id, 'result' => '0' ) );        
            }
            else 
            {
                echo json_encode( array('packet_id' => $data->packet_id, 'result' => '1' ) );
            }
	    }
    }
    
    mysqli_close($conn);
?>