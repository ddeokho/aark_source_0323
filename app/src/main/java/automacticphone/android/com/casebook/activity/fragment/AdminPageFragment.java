package automacticphone.android.com.casebook.activity.fragment;

import android.content.ContentValues;
import android.content.Context;
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

import com.labo.kaji.fragmentanimations.MoveAnimation;
import com.ssomai.android.scalablelayout.ScalableLayout;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.json.JSONException;
import org.json.JSONObject;

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
}
