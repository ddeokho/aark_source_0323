package automacticphone.android.com.casebook.activity.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.LinearLayout;
import android.view.Gravity;
import android.widget.Toast;


import com.labo.kaji.fragmentanimations.MoveAnimation;
import com.ssomai.android.scalablelayout.ScalableLayout;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.HomeActivity;
import automacticphone.android.com.casebook.activity.adapter.SearchCaseListAdapter;
import automacticphone.android.com.casebook.activity.adapter.SearchMemberListAdapter;
import automacticphone.android.com.casebook.activity.common.DataManager;
import automacticphone.android.com.casebook.activity.common.Util;
import automacticphone.android.com.casebook.activity.data.CaseData;
import automacticphone.android.com.casebook.activity.data.UserData;
import automacticphone.android.com.casebook.activity.network.HttpConnectTask;
import automacticphone.android.com.casebook.activity.network.HttpTaskCallBack;
import automacticphone.android.com.casebook.activity.network.NetworkManager;
import automacticphone.android.com.casebook.activity.network.RequestHttpURLConnection;


public class AdminPageFragment extends Fragment {
    private EditText memberSearchEdit;
    private EditText caseTextSearchEdit;
    //푸시알림
    private EditText gradePushEdit;
    private PopupWindow mPopupWindow;


    private ListView memberListView;
    private ListView caseTextListView;
    private HttpTaskCallBack mCallBack = null;
    private SearchMemberListAdapter adapter;
    private SearchCaseListAdapter caseAdapter;
    private ZLoadingDialog loadingDialog;
    private int requestCount = 2;
    private CaseData selectCaseData = null;

    public AdminPageFragment() {
        // Required empty public constructor
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view)
        {
            switch (view.getId()) {
                case R.id.admin_page_member_search_btn:
                    OnMemberSearchBtnClick();
                    break;
                case R.id.admin_page_case_text_search_btn:
                    OnCaseTextSearchBtnClick();
                    break;
                case R.id.admin_page_push_select_btn:
                    OnGrageSearchBtnClick();
                    break;
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment\
        requestCount = 2;
        View view = inflater.inflate(R.layout.fragment_admin_page, container, false);
        memberListView = view.findViewById( R.id.admin_page_member_list );
        caseTextListView = view.findViewById( R.id.admin_page_case_text_list );
        memberSearchEdit = view.findViewById( R.id.admin_page_member_search_edit );
        caseTextSearchEdit = view.findViewById( R.id.admin_page_case_text_edit);

        BtnOnClickListener onClickListener = new AdminPageFragment.BtnOnClickListener();
        Button btn = view.findViewById(R.id.admin_page_member_search_btn );
        btn.setOnClickListener( onClickListener );

        btn = view.findViewById( R.id.admin_page_case_text_search_btn );
        btn.setOnClickListener( onClickListener );

        adapter = new SearchMemberListAdapter( getContext(), null );
        memberListView.setAdapter( adapter );
        memberListView.setOnItemClickListener( new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d( "Search Member Click: ", "=>" + position );
                loadingDialog = Util.ShowLoading( getContext(), Z_TYPE.SINGLE_CIRCLE, false  );
                UserData selectUserData = adapter.getDataList().get(position);
                if( selectUserData != null )
                {
                    DataManager.inst().setTargetAdminUserData( selectUserData );
                    int member_seq = selectUserData.getSeq();
                    NetworkManager.inst().RequestWriteList( getContext(), mCallBack, member_seq );
                }
            }
        });

        caseAdapter = new SearchCaseListAdapter( getContext(), null );
        caseTextListView.setAdapter( caseAdapter );
        caseTextListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d( "Search Case Click: ", "=>" + position );
                loadingDialog = Util.ShowLoading( getContext(), Z_TYPE.SINGLE_CIRCLE, false  );
                selectCaseData =  caseAdapter.getDataList().get( position );
                NetworkManager.inst().RequestContentOwnerData( getContext(), mCallBack, selectCaseData.getMember_seq() );
                NetworkManager.inst().RequestCommentData( getContext(), mCallBack, selectCaseData.getSeq(), 0, 5 );
            }
        });

        //푸시알림 선택 버튼
        gradePushEdit = view.findViewById(R.id.admin_page_push_btn);
        btn = view.findViewById(R.id.admin_page_push_select_btn);
        btn.setOnClickListener(onClickListener);



        mCallBack = new HttpTaskCallBack()
        {
            @Override
            public void doProcess(JSONObject jsonObj)
            {
                try
                {
                    if(jsonObj.get("packet_id").equals("member_list"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            if( DataManager.inst().ParsingMemberList( jsonObj ) )
                            {
                                adapter.ChangeData( DataManager.inst().getSearchMemberList() );
                            }
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("case_search_list"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            if( DataManager.inst().ParsingSearchCaseList( jsonObj ) )
                        {
                            caseAdapter.ChangeData( DataManager.inst().getSearchCaseList() );
                        }
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("write_list"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            if( DataManager.inst().ParsingMemberWriteList( jsonObj ) )
                            {

                                NetworkManager.inst().RequestWriteCommentCount( getContext(), mCallBack, DataManager.inst().getTargetAdminUserData().getSeq() );
                            }
                        }
                        else
                            loadingDialog.cancel();
                    }
                    else if(jsonObj.get("packet_id").equals("write_comment_count"))
                    {
                        loadingDialog.cancel();
                        if( jsonObj.get("result").equals("true") )
                        {
                            int commentCount = Integer.parseInt( jsonObj.get("comment_count").toString() );
                            DataManager.inst().setMemberWriteCommentCount( commentCount );
                            HomeActivity.inst().ChangeFragment( new MyPageFragment(), "MyPageFragment");
                        }
                    }

                    //푸시알림
                    if(jsonObj.get("packet_id").equals("all_User_Push")){
                        loadingDialog.cancel();
                        if(jsonObj.get("result").equals("true")){
                            mPopupWindow.dismiss();
                            Toast.makeText(getContext(), "전체 유저에게 알림이 발송 되었습니다.",Toast.LENGTH_SHORT).show();
                        }
                    }else if(jsonObj.get("packet_id").equals("in_User_Push")){
                        loadingDialog.cancel();
                        if(jsonObj.get("result").equals("true")){
                            mPopupWindow.dismiss();
                            Toast.makeText(getContext(), "검차위원에게 알림이 발송 되었습니다.",Toast.LENGTH_SHORT).show();
                        }
                    }else if(jsonObj.get("packet_id").equals("in_co_User_Push")){
                        loadingDialog.cancel();
                        if(jsonObj.get("result").equals("true")){
                            mPopupWindow.dismiss();
                            Toast.makeText(getContext(), "검차위원+운영위에게 알림이 발송 되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }else if(jsonObj.get("packet_id").equals("in_co_ob_User_Push")){
                        loadingDialog.cancel();
                        if(jsonObj.get("result").equals("true")){
                            mPopupWindow.dismiss();
                            Toast.makeText(getContext(), "검차위원+운영위+오비에게 알림이 발송 되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }


                    if(jsonObj.get("packet_id").equals("content_owner_data"))
                    {
                        requestCount--;
                        if( jsonObj.get("result").equals("true") )
                        {
                            DataManager.inst().ParsingContentOwnerData( jsonObj );
                        }
                        else
                            loadingDialog.cancel();
                    }
                    else if(jsonObj.get("packet_id").equals("comment_data"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            DataManager.inst().getCommentDataList().clear();
                            if( DataManager.inst().ParsingCommentData( jsonObj ) )
                            {
                                requestCount--;
                            }
                        }
                        else
                            loadingDialog.cancel();
                    }

                    if( requestCount == 0 )
                    {
                        loadingDialog.cancel();
                        requestCount = 2;
                        DataManager.inst().setSelectCaseData( selectCaseData );
                        HomeActivity.inst().ChangeFragment( new ContentsViewFragment(), "ContentsViewFragment");
                    }

                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        };
        return view;
    }

    void OnMemberSearchBtnClick()
    {
        String searchText = memberSearchEdit.getText().toString();

        JSONObject jsonObj = new JSONObject();
        try
        {
            String token = HomeActivity.inst().getToken();
            jsonObj.put("packet_id", "member_list");
            jsonObj.put("token", token );
            jsonObj.put("search_text", searchText );

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/member_list.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, getContext() );
            httpConnectTask.SetCallBack(mCallBack);
            httpConnectTask.execute();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    void OnCaseTextSearchBtnClick()
    {
        String searchText = caseTextSearchEdit.getText().toString();

        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "case_search_list");
            jsonObj.put("search_text", searchText );
            jsonObj.put("grade", -1 );

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/case_search_list.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, getContext() );
            httpConnectTask.SetCallBack(mCallBack);
            httpConnectTask.execute();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    //푸시 버튼 클릭 시 -> 그래이드 선택창
    void OnGrageSearchBtnClick(){
        View popupView = getLayoutInflater().inflate(R.layout.select_grade_push_popup, null);
        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //popupView 에서 (LinearLayout 을 사용) 레이아웃이 둘러싸고 있는
        //        mPopupWindow.setFocusable(true); 컨텐츠의 크기 만큼 팝업 크기를 지정

        // 외부 영역 선택히 PopUp 종료

        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        Button selectAllBtn = (Button) popupView.findViewById(R.id.select_all_btn);//전체
        selectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allUserPush();
            }
        });

        Button selectInbtn = (Button) popupView.findViewById(R.id.select_in_btn);//검차
        selectInbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InUserPush();
            }
        });

        Button selectInCobtn = (Button) popupView.findViewById(R.id.select_in_co_btn);//검차+운영
        selectInCobtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                InCoUserPush();
            }
        });

        Button selectInCoGrbtn = (Button) popupView.findViewById(R.id.select_in_co_gr_btn);//검차+ 운영+ 오비
        selectInCoGrbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                InCoObUserPush();
            }
        });


        Button cancelBtn = (Button) popupView.findViewById(R.id.select_push_cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });

    }

    //전체 푸시 알림
    void allUserPush()
    {
        String pushText = gradePushEdit.getText().toString();

        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "all_User_Push");
            jsonObj.put("push_text", pushText );
            jsonObj.put("token", HomeActivity.inst().getToken() );

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/push_case_grade_all.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, getContext() );
            httpConnectTask.SetCallBack(mCallBack);
            httpConnectTask.execute();
            loadingDialog = Util.ShowLoading( getContext(), Z_TYPE.SINGLE_CIRCLE, false  );
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    //전체 검차위원 알림
    void InUserPush()
    {
        String pushText = gradePushEdit.getText().toString();

        JSONObject jsonObj = new JSONObject();
        try
            {
            jsonObj.put("packet_id", "in_User_Push");
            jsonObj.put("push_text", pushText );
            jsonObj.put("token", HomeActivity.inst().getToken() );
            jsonObj.put("grade","5");

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/push_case_grade_in.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, getContext() );
            httpConnectTask.SetCallBack(mCallBack);
            httpConnectTask.execute();
            loadingDialog = Util.ShowLoading( getContext(), Z_TYPE.SINGLE_CIRCLE, false  );
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    //전체 검차+운영회 알림
    void InCoUserPush()
    {
        String pushText = gradePushEdit.getText().toString();

        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "in_co_User_Push");
            jsonObj.put("push_text", pushText );
            jsonObj.put("token", HomeActivity.inst().getToken() );
            jsonObj.put("grade_in","5");
            jsonObj.put("grade_co","4");

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/push_case_grade_in_co.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, getContext() );
            httpConnectTask.SetCallBack(mCallBack);
            httpConnectTask.execute();
            loadingDialog = Util.ShowLoading( getContext(), Z_TYPE.SINGLE_CIRCLE, false  );
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    //전체 검차+운영회+오비 알림
    void InCoObUserPush()
    {
        String pushText = gradePushEdit.getText().toString();

        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "in_co_ob_User_Push");
            jsonObj.put("push_text", pushText );
            jsonObj.put("token", HomeActivity.inst().getToken() );
            jsonObj.put("grade_in","5");
            jsonObj.put("grade_co","4");
            jsonObj.put("grade_ob","1");

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/push_case_grade_in_co_ob.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, getContext() );
            httpConnectTask.SetCallBack(mCallBack);
            httpConnectTask.execute();
            loadingDialog = Util.ShowLoading( getContext(), Z_TYPE.SINGLE_CIRCLE, false  );
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


}
