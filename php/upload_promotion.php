<?php   
    require('function.php');  
    $param = $_POST['param'];
    //$out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);
    $data_array = json_decode($param, true);

    $file_path = "uploads/logo/";
    // Count # of uploaded files in array

    $upload_state = true;
    $total = count($_FILES);
    // Loop through each file
    for( $i=0 ; $i < $total ; $i++ ) {

      //Get the temp file path
      $tmpFilePath = $_FILES['upload_'.$i]['tmp_name'];

      //Make sure we have a file path
      if ($tmpFilePath != ""){
        //Setup our new file path
        $newFilePath = $file_path . $_FILES['upload_'.$i]['name'];
        
        //Upload the file into the temp dir
        if(move_uploaded_file($tmpFilePath, $newFilePath) == false ) 
        {
          $upload_state = false;
        }        
      }
    }

    if( $upload_state )
    {
        $conn = connect_db();                  
        $query = "";
        
        // 작업 성공여부를 나타내는 플래그
        $success = true; 
        // 트랙잭션을 시작한다.
        $result = mysql_query("SET AUTOCOMMIT=0", $conn);
        $result = mysql_query("BEGIN", $conn);

        //url추가
        if( $data->seq > -1 )
        {
            $query = "update promotion set title='$data->title',name='$data->name',email='$data->email',address='$data->address', latitude='$data->latitude', longitude='$data->longitude', phone='$data->phone', logo_img='$data->logo_img', url='$data->url' where seq='$data->seq' ";
        }   
        else
        {
            $query = "insert into promotion( member_seq, type, title, name, email, address, latitude, longitude, phone, logo_img, url ) values( '$data->member_seq', '$data->type', '$data->title', '$data->name','$data->email', '$data->address', '$data->latitude', '$data->longitude', '$data->phone', '$data->logo_img', '$data->url' );";    
        }
        
        $result = mysqli_query($conn, $query);
        if( $result == false)
            $success = false;

        $insert_id = -1;  
        if( $data->seq > -1 )
            $insert_id = $data->seq;
        else
            $insert_id = mysqli_insert_id($conn);  
        
        foreach ($data_array['promotion_text'] as $key => $item ) 
        {            
            $seq = $item['seq'];
            $title = $item['title'];
            $content = $item['content'];
            $img_name = $item['img_name'];

            if( $seq > -1 )
            {
                $query = "update promotion_text set title='$title',content='$content',img_name='$img_name' where seq='$seq'";
            }
            else
            {
                $query = "insert into promotion_text( ad_title_seq, title, content, img_name ) values( '$insert_id', '$title', '$content', '$img_name');";    
            }
                

            $result = mysqli_query($conn, $query);
            if($result == false )
            {
                $success = false;
                break;
            }
        }

        foreach ($data_array['promotion_sub_delete'] as $key => $item ) 
        {            
            $sub_data_seq = $item['sub_data_seq'];           
            $query = "delete from promotion_text where seq = '$sub_data_seq'"; 

            $result = mysqli_query($conn, $query);
            if($result == false )
            {
                $success = false;
                break;
            }
        }

        $promotion_seq = $data->seq;
        if( $promotion_seq == -1 )
            $promotion_seq = $insert_id;
                           
        // 작업 성공/실패 여부에 따라 COMMIT/ROLLBACK 처리한다.
        if($success == false) 
        {
            $result = mysql_query("ROLLBACK", $conn);        
            echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );
        }
        else 
        {
            $result = mysql_query("COMMIT", $conn);        
            echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'true', 'seq' => $promotion_seq ) );
        }

        mysqli_close($conn);    
    }
    else
        echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false',  'error' => '-101' ) );
    
?>