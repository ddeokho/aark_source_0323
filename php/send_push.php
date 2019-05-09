<?php 
    $url = "https://fcm.googleapis.com/fcm/send"; 
    $token = "7Ene41Rtijn8pKzRmwoTEVm1Lio="; 
    $serverKey = 'AAAAhFzO1P8:APA91bF8MpVBNImleP2A2KRjxE1CjYegTO8nSo0YYmPR39bSiCbOkzHcKWladcEli3m6N_vdxC8kTu2Lv7PlJ3a0maP610wPJeVu_wlX7qEYucaxrmscHf7LiYcjqOE_5wzUhlgNn7vX'; 
    $title = "Notification title"; 
    $body = "Hello I am from Your php server"; 
    $notification = array('title' =>$title , 'text' => $body, 'sound' => 'default', 'badge' => '1'); 
    $arrayToSend = array('to' => $token, 'notification' => $notification,'priority'=>'high'); 
    $json = json_encode($arrayToSend); 
    $headers = array(); 
    $headers[] = 'Content-Type: application/json'; 
    $headers[] = 'Authorization: key='. $serverKey; 
    $ch = curl_init(); 
    curl_setopt($ch, CURLOPT_URL, $url); 
    curl_setopt($ch, CURLOPT_CUSTOMREQUEST,"POST"); 
    curl_setopt($ch, CURLOPT_POSTFIELDS, $json); 
    curl_setopt($ch, CURLOPT_HTTPHEADER,$headers); 
    //Send the request 
    $response = curl_exec($ch); 
    //Close request 
    if ($response === FALSE) { 
    die('FCM Send Error: ' . curl_error($ch)); 
    } 
    curl_close($ch); 
?> 
