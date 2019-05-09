<?php 
    require('function.php');    
    $param = $_POST['param'];
    //$out_put = json_encode( $param, JSON_UNESCAPED_UNICODE );
    $data = json_decode($param);

    $file_path = "uploads/logo/";
    // Count # of uploaded files in array
    $total = count($_FILES);
    $upload_result = true;
    // Loop through each file
    for( $i=0 ; $i < $total ; $i++ ) {

      //Get the temp file path
      $tmpFilePath = $_FILES['upload_'.$i]['tmp_name'];

      //Make sure we have a file path
      if ($tmpFilePath != "")
      {
        //Setup our new file path
        $newFilePath = $file_path . $_FILES['upload_'.$i]['name'];
        
        //Upload the file into the temp dir
        if(move_uploaded_file($tmpFilePath, $newFilePath) == false )
        {
            $upload_result = false;
            break;
        }        
      }
    }

    //view_title , view_title='$view_title'
    


    if( $upload_result )
    {
        $conn = connect_db();        
        $query = "";  
        
        if( $data->seq > -1 )
        {
            $query = "update case_text set member_seq='$data->member_seq', year='$data->year', title='$data->title', content='$data->content', cate_reg='$data->cate_reg', cate_1='$data->cate_1', cate_2='$data->cate_2', cate_3='$data->cate_3', img_1='$data->img_1', img_2='$data->img_2', img_3='$data->img_3' where seq='$data->seq' ";           
        }
        else
        {
            $query = "insert into case_text( member_seq, year, title, content, cate_reg, cate_1, cate_2, cate_3, img_1, img_2, img_3, view_title ) values( '$data->member_seq', '$data->year', '$data->title', '$data->content', '$data->cate_reg', '$data->cate_1', '$data->cate_2', '$data->cate_3', '$data->img_1', '$data->img_2', '$data->img_3', 'view_title');";
        }
            
        if( $result = mysqli_query($conn, $query) )
        {   
            $seq = $data->seq;
            if( $seq == -1 )
                $seq = mysqli_insert_id($conn);

            echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'true', 'seq' => $seq ) );
        }
        else
        {        
            echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false' ) );
        }
            
        mysqli_close($conn);        
    }
    else
        echo json_encode( array('packet_id' => $data->packet_id, 'result' => 'false', 'error' => '-101' ) );    
?>