package automacticphone.android.com.casebook.activity.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.labo.kaji.fragmentanimations.MoveAnimation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.HomeActivity;
import automacticphone.android.com.casebook.activity.adapter.BoardListAdapter;
import automacticphone.android.com.casebook.activity.common.DataManager;
import automacticphone.android.com.casebook.activity.common.Define;
import automacticphone.android.com.casebook.activity.common.Util;
import automacticphone.android.com.casebook.activity.data.CaseData;
import automacticphone.android.com.casebook.activity.data.UserData;
import automacticphone.android.com.casebook.activity.network.HttpTaskCallBack;
import automacticphone.android.com.casebook.activity.network.NetworkManager;

public class MyPageFragment extends Fragment {
    private BoardListAdapter adapter;
    private PopupWindow mPopupWindow;
    private HttpTaskCallBack mCallBack = null;
    private CaseData selectCaseData;
    private int requestCount = 3;

    public MyPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requestCount = 3;
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);
        MyPageFragment.BtnOnClickListener onClickListener = new MyPageFragment.BtnOnClickListener();

        //옵션버튼
        Button btn = (Button)view.findViewById(R.id.my_page_setup_btn);
        btn.setOnClickListener( onClickListener );
        if( DataManager.inst().getUserData() == null )
            btn.setVisibility(View.INVISIBLE);
        else
            btn.setVisibility(View.VISIBLE);

        btn = (Button)view.findViewById(R.id.my_page_grade_btn);
        btn.setOnClickListener( onClickListener );
        btn.setVisibility( View.INVISIBLE );

        UserData userData = null;
        int writeCount = 0;
        int commentCount = 0;
        ArrayList<CaseData> list = new ArrayList<CaseData>();
        //관리자가 검색한 유저 데이터.
        if( DataManager.inst().getTargetAdminUserData() != null )
        {
            userData = DataManager.inst().getTargetAdminUserData();
            writeCount = DataManager.inst().getMemberWriteCount( userData.getSeq() );
            commentCount = DataManager.inst().getMemberWriteCommentCount();
            list = DataManager.inst().getMemberWriteList();
        }
        else if( DataManager.inst().getUserData() != null )
        {
            userData = DataManager.inst().getUserData();
            writeCount = DataManager.inst().getMyWriteCount( userData.getSeq() );
            commentCount = DataManager.inst().getWriteCommentCount();
            list = DataManager.inst().getMyWriteList();
        }

        if( userData != null )
        {
            TextView textView = view.findViewById(R.id.my_page_id_text );
            textView.setText( userData.getEmail() );

            textView = view.findViewById(R.id.my_page_write_count );
            textView.setText(  String.format( "작성글 수:%d", writeCount ) );

            textView = view.findViewById(R.id.my_page_comment_count );

            textView.setText( String.format( "댓글 수:%d", commentCount ) );

            UserData myUserData = DataManager.inst().getUserData();
            if( myUserData != null && myUserData.getGrade() == Define.GRADE_ADMIN )
                btn.setVisibility( View.VISIBLE );
        }


        ListView boardListView = view.findViewById( R.id.my_page_list_view );
        adapter = new BoardListAdapter( getContext(), list );
        boardListView.setAdapter( adapter );
        boardListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if( DataManager.inst().getTargetAdminUserData() != null )
                    selectCaseData = DataManager.inst().getMemberWriteList().get(position);
                else
                    selectCaseData = DataManager.inst().getMyWriteList().get(position);
                NetworkManager.inst().RequestContentOwnerData( getContext(), mCallBack, selectCaseData.getMember_seq() );
                NetworkManager.inst().RequestCommentData( getContext(), mCallBack, selectCaseData.getSeq(), 0, 5 );
                NetworkManager.inst().RequestCommentCount( getContext(), mCallBack, selectCaseData.getSeq() );
            }
        });

        mCallBack = new HttpTaskCallBack()
        {
            @Override
            public void doProcess(JSONObject jsonObj)
            {
                try
                {
                    if(jsonObj.get("packet_id").equals("content_owner_data"))
                    {
                        requestCount--;
                        if( jsonObj.get("result").equals("true") )
                        {
                            DataManager.inst().ParsingContentOwnerData( jsonObj );
                        }
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
                    }
                    else if(jsonObj.get("packet_id").equals("contents_comment_count"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            int totalCommentCount = Integer.parseInt( jsonObj.get("comment_count").toString() );
                            DataManager.inst().setTotalCommentCount( totalCommentCount );
                            requestCount--;
                        }
                    }

                    if( requestCount == 0 )
                    {
                        requestCount = 3;
                        ContentsViewFragment fragment = new ContentsViewFragment();
                        DataManager.inst().setSelectCaseData( selectCaseData );
                        HomeActivity.inst().ChangeFragment( fragment, "ContentsViewFragment");
                    }
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        };

        HomeActivity.inst().setMenuBarPos( 4 );
        return view;
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view)
        {
            switch (view.getId()) {
                case R.id.my_page_setup_btn:
                    OnSetupBtnClick();
                    break;
                case R.id.my_page_grade_btn:
                    OnGradeBtnClick();
                    break;
            }
        }
    }

    void OnSetupBtnClick()
    {
        View popupView = getLayoutInflater().inflate(R.layout.set_alram_popup, null);
        mPopupWindow = new PopupWindow(popupView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        //popupView 에서 (LinearLayout 을 사용) 레이아웃이 둘러싸고 있는 컨텐츠의 크기 만큼 팝업 크기를 지정

        mPopupWindow.setFocusable(true);
        // 외부 영역 선택히 PopUp 종료

        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        TextView textView = popupView.findViewById(R.id.alram_popup_personal_text );
        textView.setMovementMethod( new ScrollingMovementMethod() );

        textView = popupView.findViewById(R.id.alram_popup_access_term_text );
        textView.setMovementMethod( new ScrollingMovementMethod() );

        Button closeBtn = (Button) popupView.findViewById(R.id.set_alram_close_btn);
        closeBtn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });

        Switch alarmSwitch = (Switch) popupView.findViewById(R.id.set_alarm_switch );
        alarmSwitch.setChecked( HomeActivity.inst().isPushAlarm() );
        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                Log.v("Switch State=", ""+isChecked);
                HomeActivity.inst().SaveAlarm( isChecked );
                HomeActivity.inst().SetFcmPush( isChecked );
            }

        });
    }

    void OnGradeBtnClick()
    {
        HomeActivity.inst().ChangeFragment(new AdminPageFragment(), "AdminPageFragment");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        DataManager.inst().setTargetAdminUserData( null );
    }
}
