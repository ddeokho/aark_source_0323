 <?php
    include_once('mailer.lib.php');    
    $param = $_POST['param'];    
    $data = json_decode($param);    
        
    $title = sprintf("%s님의 문의사항 입니다.", $data->title );
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
    $mail->addAddress("aarkpush@gmail.com");
    $mail->isHTML(true);
    $mail->Subject = $title;
    $mail->Body = $data->body_text;
    $mail->AltBody = "This is the plain text version of the email content";
    if (!$mail->send())
    {
        echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );        
    }
    else 
    {
        echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'true' ) );
    }
 ?>