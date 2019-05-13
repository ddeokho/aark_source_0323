package automacticphone.android.com.casebook.activity.fragment;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.HomeActivity;
import automacticphone.android.com.casebook.activity.adapter.BoardListAdapter;
import automacticphone.android.com.casebook.activity.common.DataManager;
import automacticphone.android.com.casebook.activity.common.Define;
import automacticphone.android.com.casebook.activity.data.CaseData;
import automacticphone.android.com.casebook.activity.data.RegulationSubData3;
import automacticphone.android.com.casebook.activity.data.SubTreeCaseData;
import automacticphone.android.com.casebook.activity.network.HttpConnectTask;
import automacticphone.android.com.casebook.activity.network.HttpTaskCallBack;
import automacticphone.android.com.casebook.activity.network.NetworkManager;
import automacticphone.android.com.casebook.activity.network.RequestHttpURLConnection;

public class BoardFragment extends Fragment
{
    private TabLayout mTabLayout;
    private BoardListAdapter adapter;
    private HttpTaskCallBack mCallBack = null;
    private CaseData selectCaseData;
    private int requestCount = 3;
    private boolean lastItemVisibleFlag = false;    // 리스트 스크롤이 마지막 셀(맨 바닥)로 이동했는지 체크할 변수
    private boolean mLockListView = false;          // 데이터 불러올때 중복안되게 하기위한 변수
    private ProgressBar progressBar;                // 데이터 로딩중을 표시할 프로그레스바
    private ArrayList<CaseData> dataList;
    private ListView boardListView;
    public static int selectTab = 0;

    private String searchText ="";
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
    public BoardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_board, container, false);
        mTabLayout = (TabLayout) view.findViewById( R.id.fragment_board_tabs );
        requestCount = 2;

        mCallBack = new HttpTaskCallBack()
        {
            @Override
            public void doProcess(JSONObject jsonObj)
            {
                try
                {
                    if(jsonObj.get("packet_id").equals("content_owner_data"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            DataManager.inst().ParsingContentOwnerData( jsonObj );
                        }
                        else
                            DataManager.inst().setContentOwnerData( null );

                        requestCount--;
                    }
                    else if(jsonObj.get("packet_id").equals("comment_data"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            if( DataManager.inst().ParsingCommentData( jsonObj ) )
                            {
                                requestCount--;
                            }
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("update_case_feq"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            int seq = Integer.parseInt( jsonObj.get("seq").toString() );
                            int feq = Integer.parseInt( jsonObj.get("feq").toString() );
                            DataManager.inst().UpdateCaseFeq( seq, feq );
                            ContentsViewFragment fragment = new ContentsViewFragment();
                            DataManager.inst().setSelectCaseData( selectCaseData );
                            HomeActivity.inst().ChangeFragment( fragment, "ContentsViewFragment");
                            requestCount = 3;
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
                    else if(jsonObj.get("packet_id").equals("case_data") )
                    {
                        progressBar.setVisibility(View.GONE);
                        mLockListView = false;
                        if( jsonObj.get("result").equals("true") )
                        {
                            if( DataManager.inst().ParsingCaseData( jsonObj ) )
                            {

                                // 리스트에 다음 데이터를 입력할 동안에 이 메소드가 또 호출되지 않도록 mLockListView 를 true로 설정한다.
                                dataList = DataManager.inst().getCaseDataList();
                                adapter.ChangeData( dataList );
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    if( requestCount == 0 )
                    {
                        NetworkManager.inst().UpdateCaseContentFeq( getContext(), mCallBack, selectCaseData.getSeq(), selectCaseData.getFeq() + 1 );
                    }
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        };

        if( DataManager.inst().getUserData() != null )
        {
            if( DataManager.inst().getUserData().getGrade() == Define.GRADE_ADMIN || DataManager.inst().getUserData().getGrade() == Define.GRADE_GRADUATE
                || DataManager.inst().getUserData().getGrade()==Define.GRADE_INSPEC || DataManager.inst().getUserData().getGrade()==Define.GRADE_COMUNI)//검차, 운영위 추가
            {
                mTabLayout.addTab( mTabLayout.newTab().setText(R.string.board_tab_text1));
                mTabLayout.addTab( mTabLayout.newTab().setText(R.string.board_tab_text2));
                mTabLayout.addTab( mTabLayout.newTab().setText(R.string.board_tab_text3));
            }
            else
            {
                mTabLayout.addTab( mTabLayout.newTab().setText(R.string.board_tab_text1));
                mTabLayout.addTab( mTabLayout.newTab().setText(R.string.board_tab_text2));
            }
        }
        else
        {
            mTabLayout.addTab( mTabLayout.newTab().setText(R.string.board_tab_text1));
            mTabLayout.addTab( mTabLayout.newTab().setText(R.string.board_tab_text2));
        }

        boardListView = view.findViewById( R.id.fragment_board_list_view );
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                boardListView.setSelection( 0 );
                DataManager.inst().ClearCaseDataList();
                selectTab = tab.getPosition();
                switch(tab.getPosition())
                {
                    case 0:
                    {
                       RequestCaseDataList( Define.GOOD_CASE );
                    }
                    break;
                    case 1:
                    {
                        RequestCaseDataList( Define.BAD_CASE );
                    }
                    break;
                    case 2:
                    {
                        RequestCaseDataList( Define.QUESTION_CASE );
                    }
                    break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        } );
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        dataList = DataManager.inst().getCaseDataList();

            SubTreeCaseData subTreeCaseData = HomeActivity.inst().getSelectSubTreeCaseData();
            if( subTreeCaseData != null )
            {
                //TextView textView = view.findViewById(R.id.fragment_board_title );
                RegulationSubData3 subData3 = DataManager.inst().getRegulSubData3( subTreeCaseData.getCate_3() );
                if( subData3 != null )
                {
                    //textView.setText( subData3.getRegul() );
                }
            }

            SelectTab( selectTab );

            adapter = new BoardListAdapter( getContext(), dataList );
            boardListView.setAdapter( adapter );
            boardListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    selectCaseData = dataList.get(position);
                    NetworkManager.inst().RequestContentOwnerData( getContext(), mCallBack, selectCaseData.getMember_seq() );
                    NetworkManager.inst().RequestCommentData( getContext(), mCallBack, selectCaseData.getSeq(), 0, 5 );
                    NetworkManager.inst().RequestCommentCount( getContext(), mCallBack, selectCaseData.getSeq() );
                }
            });

            progressBar = view.findViewById( R.id.fragment_board_progressbar );
            progressBar.setVisibility(View.GONE);

            boardListView.setOnScrollListener(new AbsListView.OnScrollListener()
            {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState)
                { // TODO Auto-generated method stub
                    // 1. OnScrollListener.SCROLL_STATE_IDLE : 스크롤이 이동하지 않을때의 이벤트(즉 스크롤이 멈추었을때).
                    // 2. lastItemVisibleFlag : 리스트뷰의 마지막 셀의 끝에 스크롤이 이동했을때.
                    // 3. mLockListView == false : 데이터 리스트에 다음 데이터를 불러오는 작업이 끝났을때.
                    // 1, 2, 3 모두가 true일때 다음 데이터를 불러온다.
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && mLockListView == false) {
                        // 화면이 바닦에 닿을때 처리
                        // 로딩중을 알리는 프로그레스바를 보인다.
                        progressBar.setVisibility(View.VISIBLE);

                        int grade = mTabLayout.getSelectedTabPosition() + 1;
                        // 다음 데이터를 불러온다.
                        RequestCaseDataList( grade );
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
                {
                    // firstVisibleItem : 화면에 보이는 첫번째 리스트의 아이템 번호.
                    // visibleItemCount : 화면에 보이는 리스트 아이템의 갯수
                    // totalItemCount : 리스트 전체의 총 갯수
                    // 리스트의 갯수가 0개 이상이고, 화면에 보이는 맨 하단까지의 아이템 갯수가 총 갯수보다 크거나 같을때.. 즉 리스트의 끝일때. true
                    lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
                }
            });

        return view;
    }

    private void SelectTab( int idx )
    {
        if( mTabLayout == null )
        return;

        TabLayout.Tab tab = mTabLayout.getTabAt(idx );
        if( tab != null )
            tab.select();
    }

    public void RequestCaseDataList( int grade )
    {
        int start = dataList.size();
        SubTreeCaseData subTreeCaseData = HomeActivity.inst().getSelectSubTreeCaseData();
        if( searchText.length() > 0 )
        {
            NetworkManager.inst().RequestSearchCaseData( getContext(), mCallBack, "case_data", searchText, grade );
        }
        else if( subTreeCaseData != null )
        {
            NetworkManager.inst().RequestCaseData( getContext(), mCallBack, "case_data", subTreeCaseData.getSelectYear(), subTreeCaseData.getCate_reg(), subTreeCaseData.getCate_1(), subTreeCaseData.getCate_2(), subTreeCaseData.getCate_3(), grade, start, Define.MAX_CASE_DATA );
        }
        else
        {
            NetworkManager.inst().RequestCaseData( getContext(), mCallBack, "case_data", 0, 0, 0, 0, 0, grade, start, Define.MAX_CASE_DATA );
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}
