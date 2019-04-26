package automacticphone.android.com.casebook.activity.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.HomeActivity;
import automacticphone.android.com.casebook.activity.adapter.PromoteRegisterSubAdapter;
import automacticphone.android.com.casebook.activity.common.DataManager;
import automacticphone.android.com.casebook.activity.common.Define;
import automacticphone.android.com.casebook.activity.common.Util;
import automacticphone.android.com.casebook.activity.data.PromoteRegisterData;
import automacticphone.android.com.casebook.activity.data.PromoteRegisterSubData;
import automacticphone.android.com.casebook.activity.data.PromotionData;
import automacticphone.android.com.casebook.activity.data.PromotionSubData;
import automacticphone.android.com.casebook.activity.data.UploadPhotoData;
import automacticphone.android.com.casebook.activity.network.DownloadManager;
import automacticphone.android.com.casebook.activity.network.HttpConnectTask;
import automacticphone.android.com.casebook.activity.network.HttpTaskCallBack;
import automacticphone.android.com.casebook.activity.network.NetworkManager;
import automacticphone.android.com.casebook.activity.network.RequestHttpURLConnection;

import static android.app.Activity.RESULT_OK;

public class PromoteRegisterFragment2 extends Fragment {

    private HttpTaskCallBack mCallBack = null;
    private PromoteRegisterSubAdapter adapter = null;
    private int promotionType;
    private ZLoadingDialog loadingDialog;
    public PromoteRegisterFragment2() {
        // Required empty public constructor
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view)
        {
            switch (view.getId()) {
                case R.id.promote_register2_upload:
                {
                   OnUploadBtnClick();
                }
                break;
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_promote_register2, container, false);
        ListView listView = view.findViewById( R.id.promote_register_sub_list_view);

        PromoteRegisterFragment2.BtnOnClickListener onClickListener = new PromoteRegisterFragment2.BtnOnClickListener();
        Button btn = view.findViewById(R.id.promote_register2_upload );
        btn.setOnClickListener( onClickListener );

        ArrayList<PromoteRegisterSubData> dataList = new ArrayList<PromoteRegisterSubData>();

        if( DataManager.inst().getSelectPromotionData() != null )
        {
            for( int i = 0; i < DataManager.inst().getPromotionSubDataList().size(); ++i )
            {
                PromotionSubData subData = DataManager.inst().getPromotionSubDataList().get(i);
                if( subData.getSeq() > -1 )
                {
                    PromoteRegisterSubData registerSubData= new PromoteRegisterSubData();
                    registerSubData.setPromotionSubData( subData );
                    dataList.add( registerSubData );
                }
            }
        }
        else
        {
            PromoteRegisterSubData subData = new PromoteRegisterSubData();
            subData.setPromotionSubData( new PromotionSubData() );
            dataList.add( subData );
        }

        adapter = new PromoteRegisterSubAdapter( getContext(), getActivity(), DataManager.inst().getSelectPromotionData(), dataList );
        listView.setAdapter( adapter);

        mCallBack = new HttpTaskCallBack()
        {
            @Override
            public void doProcess(JSONObject jsonObj)
            {
                try
                {
                    if(jsonObj.get("packet_id").equals("upload_promotion"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            NetworkManager.inst().RequestPromotionData( getContext(), mCallBack, "promotion_data", promotionType, 0, Define.MAX_CASE_DATA );
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
                    else if(jsonObj.get("packet_id").equals("promotion_data"))
                    {
                        loadingDialog.cancel();
                        // 리스트에 다음 데이터를 입력할 동안에 이 메소드가 또 호출되지 않도록 mLockListView 를 true로 설정한다.
                        if( jsonObj.get("result").equals("true") )
                        {
                            DataManager.inst().ClearPromotionDataList();
                            if( DataManager.inst().ParsingPromotionData( jsonObj ) )
                            {
                                Toast.makeText( getContext(), "홍보글이 업로드 되었습니다." , Toast.LENGTH_SHORT ).show();
                                DataManager.inst().setSelectPromotionData( null );
                                DataManager.inst().setPromoteRegisterData( null );

                                String selectTab = "0";
                                if( promotionType == Define.PROMOTION_TRADER )
                                    selectTab = "1";

                                PromoteListFragment fragment = PromoteListFragment.newInstance( selectTab );
                                HomeActivity.inst().ChangeFragment( fragment, "PromoteListFragment");
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
        return  view;
    }

    public boolean CheckSubData()
    {
        for( int i = 0; i < adapter.getDataList().size(); ++i )
        {
            PromoteRegisterSubData subData = adapter.getDataList().get( i );
            if( subData.ExistData() == false )
            {
                return false;
            }
        }

        return true;
    }

    void OnUploadBtnClick()
    {
        if( CheckSubData() == false )
        {
            HomeActivity.inst().ShowAlertDialog("소주제/소내용을 입력해 주세요.");
            return;
        }

        loadingDialog = Util.ShowLoading( getContext(), Z_TYPE.SINGLE_CIRCLE, false  );
         JSONObject jsonObj = new JSONObject();
        try
        {
            PromoteRegisterData data = DataManager.inst().getPromoteRegisterData();
            int seq = -1;
            if( DataManager.inst().getSelectPromotionData() != null )
            {
                seq = DataManager.inst().getSelectPromotionData().getSeq();
                promotionType = DataManager.inst().getSelectPromotionData().getType();
            }
            else
            {
                promotionType = Define.PROMOTION_STUDENT;
                if( DataManager.inst().getUserData().getGrade() == Define.GRADE_TRADER )
                    promotionType = Define.PROMOTION_TRADER;
            }

            jsonObj.put("packet_id", "upload_promotion");
            jsonObj.put("seq",  seq );
            jsonObj.put("member_seq",  data.getMemberSeq());
            jsonObj.put("type",  promotionType);
            jsonObj.put("title",  data.getTitle());
            jsonObj.put("name",  data.getName());
            jsonObj.put("email",  data.getEmail());
            //url추가
            jsonObj.put("url", data.getUrl());
            //
            jsonObj.put("address",  data.getAddress() );
            jsonObj.put("latitude",  data.getLot());
            jsonObj.put("longitude",  data.getLnt());
            jsonObj.put("phone",  data.getPhone());
            jsonObj.put("logo_img",  data.getLogoImg());

            ArrayList<UploadPhotoData> logo = new ArrayList<UploadPhotoData>();
            if( data.getLogoUri() != null && data.getLogoImg().length() > 0 )
            {
                    UploadPhotoData uploadData = new UploadPhotoData();
                    uploadData.uploadFileUri = data.getLogoUri();
                    logo.add( uploadData );
            }

            JSONArray jArray = new JSONArray();
            PromoteRegisterSubData subData;
            for( int i = 0; i < adapter.getDataList().size(); ++i )
            {
                subData = adapter.getDataList().get( i );

                JSONObject obj = new JSONObject();
                obj.put("seq", subData.getPromotionSubData().getSeq() );
                obj.put("title", subData.getPromotionSubData().getTitle() );
                obj.put("content", subData.getPromotionSubData().getContent() );
                obj.put( "img_name", subData.getPromotionSubData().getImgName() );
                jArray.put(obj);

                if( subData.getPromotionSubData().getImgName().length() > 0 )
                {
                        UploadPhotoData uploadData = new UploadPhotoData();
                        uploadData.uploadFileUri = subData.getPhotoUri();
                        logo.add( uploadData );
                }
            }

            if( jArray.length() > 0 )
                jsonObj.put( "promotion_text", jArray );

            JSONArray jArrayDelete = new JSONArray();
            for( int k = 0; k < adapter.getDeleteSubDataList().size(); ++k )
            {
                JSONObject obj = new JSONObject();
                obj.put("sub_data_seq", adapter.getDeleteSubDataList().get(k) );
                jArrayDelete.put(obj);
            }

            if( jArrayDelete.length() > 0 )
                jsonObj.put( "promotion_sub_delete", jArrayDelete );

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/upload_promotion.php";
            HttpConnectTask httpConnectTask = null;

            if( logo.size() > 0 )
            {
                httpConnectTask = new HttpConnectTask(url, values, getContext(), logo);
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

    public  void onUploadContentsResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == HomeActivity.PICTURE_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                adapter.SetPhoto( data.getData() );
            }
        }
    }
}
