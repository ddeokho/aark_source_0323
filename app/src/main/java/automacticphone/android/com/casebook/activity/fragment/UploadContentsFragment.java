package automacticphone.android.com.casebook.activity.fragment;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.HomeActivity;
import automacticphone.android.com.casebook.activity.activity.SelectCategoryActivity;
import automacticphone.android.com.casebook.activity.adapter.UpLoadPhotoAdapter;
import automacticphone.android.com.casebook.activity.common.DataManager;
import automacticphone.android.com.casebook.activity.common.Define;
import automacticphone.android.com.casebook.activity.common.Util;
import automacticphone.android.com.casebook.activity.data.CaseCategoryData;
import automacticphone.android.com.casebook.activity.data.CaseData;
import automacticphone.android.com.casebook.activity.data.UploadPhotoData;
import automacticphone.android.com.casebook.activity.network.DownloadManager;
import automacticphone.android.com.casebook.activity.network.HttpConnectTask;
import automacticphone.android.com.casebook.activity.network.HttpTaskCallBack;
import automacticphone.android.com.casebook.activity.network.NetworkManager;
import automacticphone.android.com.casebook.activity.network.RequestHttpURLConnection;

import static android.app.Activity.RESULT_OK;

public class UploadContentsFragment extends Fragment
{
    private PopupWindow mPopupWindow;
    private Uri photoUri;
    private String imageFilePath;
    private RecyclerView recyclerView;
    private ArrayList<UploadPhotoData> photoList = new ArrayList<UploadPhotoData>();
    private UpLoadPhotoAdapter photoAdapter;
    private EditText titleInputEdit;
    private EditText contentEdit;
    private HttpTaskCallBack mCallBack = null;
    private CaseCategoryData caseCategoryData = null;
    public CaseCategoryData getCaseCategoryData() {
        return caseCategoryData;
    }
    private ZLoadingDialog loadingDialog;

    public void setCaseCategoryData(CaseCategoryData caseCategoryData) {
        this.caseCategoryData = caseCategoryData;
    }

    public UploadContentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_contents, container, false);
        recyclerView = (RecyclerView) view.findViewById( R.id.upload_photo_list_view );
        titleInputEdit = (EditText)view.findViewById(R.id.title_input_edit );
        contentEdit = (EditText)view.findViewById(R.id.content_input_edit );

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        photoAdapter = new UpLoadPhotoAdapter(getActivity().getApplicationContext(), photoList );
        recyclerView.setAdapter(photoAdapter);

        mCallBack = new HttpTaskCallBack()
        {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void doProcess(JSONObject jsonObj)
            {
                try
                {
                    if(jsonObj.get("packet_id").equals("upload_case_text"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            int seq = Integer.parseInt( jsonObj.get("seq").toString() );
                            NetworkManager.inst().RequestCaseDataSeq( getContext(), mCallBack, seq );
                        }
                        else
                        {
                            loadingDialog.cancel();
                            if( jsonObj.get("error").equals( Define.ERROR_101 ) )
                            {
                                Toast.makeText( getContext(), "이미지 업로드에 실패 했습니다." , Toast.LENGTH_SHORT ).show();
                            }
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("case_text_seq"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            CaseData data = DataManager.inst().ParsingUpdateCaseData( jsonObj );
                            if(  data != null )
                            {
                                Toast.makeText( getContext(), "사례글이 업로드 되었습니다." , Toast.LENGTH_SHORT ).show();
                                NetworkManager.inst().RequestWriteList( getContext(), mCallBack, DataManager.inst().getUserData().getSeq() );
                            }

                        }
                        else
                            loadingDialog.cancel();
                    }
                    else if(jsonObj.get("packet_id").equals("write_list"))
                    {
                        loadingDialog.cancel();
                        if( jsonObj.get("result").equals("true") )
                        {
                            if(DataManager.inst().ParsingWriteList( jsonObj ) )
                            {
                                HomeActivity.inst().ChangeFragment( new TableContentsFragment(), "TableContentsFragment");
                            }
                        }
                    }
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        };

        BtnOnClickListener onClickListener = new BtnOnClickListener();
        //옵션버튼
        Button btn = (Button)view.findViewById(R.id.add_photo_btn);
        btn.setOnClickListener( onClickListener );

        //옵션버튼
        btn = (Button)view.findViewById(R.id.select_rule_btn);
        btn.setOnClickListener( onClickListener );

        //업로드 버튼
        btn = (Button)view.findViewById(R.id.upload_btn);
        btn.setOnClickListener( onClickListener );

        if( DataManager.inst().getSelectCaseData() != null )
        {
            SetUploadData( DataManager.inst().getSelectCaseData() );
        }
        return view;
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view)
        {
            switch (view.getId()) {
                case R.id.add_photo_btn:
                    OnAddPhotoBtnClick();
                    break;
                case R.id.select_rule_btn:
                    OnSelectRuleBtnClick();
                    break;
                case R.id.upload_btn:
                    OnUploadBtnClick();
                    break;
            }
        }
    }

    void SetUploadData(CaseData data )
    {
        titleInputEdit.setText( data.getTitle() );
        contentEdit.setText( data.getContent() );
        caseCategoryData = new CaseCategoryData();
        caseCategoryData.setSelectYear( data.getYear() );
        caseCategoryData.setCateReg( data.getCate_reg() );
        caseCategoryData.setCate1( data.getCate_1() );
        caseCategoryData.setCate2( data.getCate_2() );
        caseCategoryData.setCate3( data.getCate_3() );

        photoList.clear();
        if( data.getImg_1().length() > 0 )
        {
            UploadPhotoData photoData = new UploadPhotoData();
            photoData.filePath = data.getImg_1();

            photoList.add( photoData );
        }

        if( data.getImg_2().length() > 0 )
        {
            UploadPhotoData photoData = new UploadPhotoData();
            photoData.filePath = data.getImg_2();

            photoList.add( photoData );
        }

        if( data.getImg_3().length() > 0 )
        {
            UploadPhotoData photoData = new UploadPhotoData();
            photoData.filePath = data.getImg_3();

            photoList.add( photoData );
        }

        if( photoList.size() > 0 )
            photoAdapter.ChangeData( photoList );
    }

    void OnAddPhotoBtnClick()
    {
        View popupView = getLayoutInflater().inflate(R.layout.select_photo_popup, null);
        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //popupView 에서 (LinearLayout 을 사용) 레이아웃이 둘러싸고 있는 컨텐츠의 크기 만큼 팝업 크기를 지정

        mPopupWindow.setFocusable(true);
        // 외부 영역 선택히 PopUp 종료

        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        Button cameraBtn = (Button) popupView.findViewById(R.id.select_photo_camera_btn);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( photoAdapter.getItemCount() == 3 )
                {
                    Toast.makeText( getActivity(), "사진은 최대 3개까지 첨부 가능합니다.", Toast.LENGTH_LONG ).show();
                    return;
                }

                sendTakePhotoIntent();
            }
        });

        Button galleryBtn = (Button) popupView.findViewById(R.id.select_photo_gallery_btn);
        galleryBtn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( photoAdapter.getItemCount() == 3 )
                {
                    Toast.makeText( getActivity(), "사진은 최대 3개까지 첨부 가능합니다.", Toast.LENGTH_LONG ).show();
                    return;
                }

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                //사진을 여러개 선택할수 있도록 한다
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                intent.setType( MediaStore.Images.Media.CONTENT_TYPE);
                HomeActivity.inst().startActivityForResult(Intent.createChooser(intent, "Select Picture"), HomeActivity.PICTURE_REQUEST_CODE);
            }
        });

        Button cancelBtn = (Button) popupView.findViewById(R.id.select_photo_cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
    }

    void OnSelectRuleBtnClick()
    {
        Intent intent = new Intent( getContext(), SelectCategoryActivity.class );
        startActivityForResult( intent, 1 );
    }

    void OnUploadBtnClick()
    {
        int member_seq = DataManager.inst().getUserData().getSeq();
        String title = titleInputEdit.getText().toString();
        if( title.length() == 0 )
        {
            HomeActivity.inst().ShowAlertDialog( "제목을 입력 해 주세요." );
            return;
        }

        String content = contentEdit.getText().toString();
        if(content.length() == 0 )
        {
            HomeActivity.inst().ShowAlertDialog( "내용을 입력 해 주세요." );
            return;
        }

        if( caseCategoryData == null )
        {
            HomeActivity.inst().ShowAlertDialog( "규정 항목을 선택 해 주세요." );
            return;
        }

        String img1 = "", img2 = "", img3 = "";
        if( photoList.size() > 0 )
        {
            if( photoList.get(0).uploadFileUri != null )
                img1 = Util.getFileName( getActivity(), photoList.get(0).uploadFileUri );
            else
                img1 = photoList.get(0).filePath;
        }

        if( photoList.size() > 1 )
        {
            if( photoList.get(1).uploadFileUri != null )
                img2 = Util.getFileName( getActivity(), photoList.get(1).uploadFileUri );
            else
                img2 = photoList.get(1).filePath;
        }

        if( photoList.size() > 2 )
        {
            if(photoList.get(2).uploadFileUri != null)
                img3 = Util.getFileName( getActivity(), photoList.get(2).uploadFileUri );
            else
                img3 = photoList.get(2).filePath;
        }

        JSONObject jsonObj = new JSONObject();
        loadingDialog = Util.ShowLoading( getContext(), Z_TYPE.SINGLE_CIRCLE, false  );
        try
        {
            int seq = -1;
            if( DataManager.inst().getSelectCaseData() != null )
                seq = DataManager.inst().getSelectCaseData().getSeq();

            jsonObj.put("packet_id", "upload_case_text");
            jsonObj.put("seq",  seq);
            jsonObj.put("member_seq",  member_seq);
            jsonObj.put("year",  caseCategoryData.getSelectYear());
            jsonObj.put("title",  title);
            jsonObj.put("content",  content);
            jsonObj.put("cate_reg",  caseCategoryData.getCateReg());
            jsonObj.put("cate_1",  caseCategoryData.getCate1());
            jsonObj.put("cate_2",  caseCategoryData.getCate2());
            jsonObj.put("cate_3",  caseCategoryData.getCate3());
            jsonObj.put("img_1",  img1);
            jsonObj.put("img_2",  img2);
            jsonObj.put("img_3",  img3);

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/upload_case_text.php";
            HttpConnectTask httpConnectTask = null;
            if( photoList.size() > 0 )
            {
                httpConnectTask = new HttpConnectTask(url, values, getContext(), photoList);
            }
            else
            {
                httpConnectTask = new HttpConnectTask(url, values, getContext() );
            }

            httpConnectTask.SetCallBack(mCallBack);
            httpConnectTask.execute();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            loadingDialog.cancel();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".jpg",         /* suffix */
                storageDir          /* directory */
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    public void sendTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity( getActivity().getPackageManager() ) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(getContext(), getActivity().getPackageName(), photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                HomeActivity.inst().startActivityForResult(takePictureIntent, HomeActivity.REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public  void onUploadContentsResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == HomeActivity.PICTURE_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                //멀티 선택을 지원하지 않는 기기에서는 getClipdata()가 없음=> getData()로 접근해야 함.
                if( data.getClipData() == null )
                {
                    if( photoAdapter.getItemCount() == 3 )
                    {
                        Toast.makeText( getActivity(), "사진은 최대 3개까지 첨부 가능합니다.", Toast.LENGTH_LONG ).show();
                        return;
                    }

                    UploadPhotoData photoData = new UploadPhotoData();
                    photoData.uploadFileUri = data.getData();
                    photoAdapter.add( photoData );
                }
                else
                {
                    ClipData clipData = data.getClipData();
                    Log.d( "clipdata", String.valueOf( clipData.getItemCount() ) );
                    int totalCount = photoAdapter.getItemCount() + clipData.getItemCount();
                    if( totalCount > 3 )
                    {
                        Toast.makeText( getActivity(), "사진은 최대 3개까지 첨부 가능합니다.", Toast.LENGTH_LONG ).show();
                        return;
                    }

                    for( int i = 0; i < clipData.getItemCount(); ++i )
                    {
                        UploadPhotoData photoData = new UploadPhotoData();
                        photoData.uploadFileUri = clipData.getItemAt(i).getUri();
                        photoAdapter.add( photoData );
                    }
                }
            }
        }
        else if (requestCode == HomeActivity.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
            ExifInterface exif = null;

            try {
                exif = new ExifInterface(imageFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int exifOrientation;
            int exifDegree;

            if (exif != null) {
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree = exifOrientationToDegrees(exifOrientation);
            } else {
                exifDegree = 0;
            }

            if( photoUri != null )
            {
                UploadPhotoData photoData = new UploadPhotoData();
                photoData.uploadFileUri = photoUri;
                photoData.filePath = imageFilePath;
                photoData.exifDegree = exifDegree;
                photoAdapter.add( photoData );
            }
        }
    }
}