package automacticphone.android.com.casebook.activity.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.HomeActivity;
import automacticphone.android.com.casebook.activity.adapter.PromoteListAdapter;
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

public class PromoteListFragment extends Fragment
{
    private static final String ARG_PARAM1 = "param1";
    private HttpTaskCallBack mCallBack = null;
    private PromotionData selectPromotionData;
    //private int tabIndex = 0;

    private TabLayout mTabLayout;
    public TabLayout getmTabLayout() {
        return mTabLayout;
    }

    public PromoteListAdapter getAdapter() {
        return adapter;
    }

    private PromoteListAdapter adapter;
    private ArrayList<PromotionData> dataList;
    //private ListView promoteList;
    private GridView promoteList;
    private boolean lastItemVisibleFlag = false;    // 리스트 스크롤이 마지막 셀(맨 바닥)로 이동했는지 체크할 변수
    private boolean mLockListView = false;          // 데이터 불러올때 중복안되게 하기위한 변수
    private ProgressBar progressBar;                // 데이터 로딩중을 표시할 프로그레스바
    public static int selectTab = 0;

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    private String searchText = "";
    public static boolean bRequestData = true;     //true: 게시판 들어올때 최신데이터 요청, false: 게시판 들어올때 최신데이터 요청안함.
    public static int scrollIdx = 0;
    public static int scrollTop = 0;
    public PromoteListFragment() {
        // Required empty public constructor
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view)
        {
            switch (view.getId()) {
                case R.id.fragment_promote_Add_btn:
                    OnAddBtnClick();
                    break;
            }
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PromoteListFragment newInstance(String param1 ) {
        PromoteListFragment fragment = new PromoteListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_promote_list, container, false);
        PromoteListFragment.BtnOnClickListener onClickListener = new PromoteListFragment.BtnOnClickListener();

        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            selectTab = Integer.parseInt( mParam1 );
        }

        mTabLayout = (TabLayout) view.findViewById( R.id.fragment_promote_list_tabs );
        mTabLayout.addTab( mTabLayout.newTab().setText(R.string.promote_tab_text1));
        mTabLayout.addTab( mTabLayout.newTab().setText(R.string.promote_tab_text2));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                promoteList.setSelection(0);
                selectTab = tab.getPosition();
                if(bRequestData == false )
                {
                    promoteList.post(new Runnable() {
                        @Override
                        public void run() {
                            bRequestData = true;
                            promoteList.setSelectionFromTop(scrollIdx, scrollTop);
                        }
                    });
                }
                else
                {
                    DataManager.inst().ClearPromotionDataList();
                    switch(selectTab)
                    {
                        case 0:
                        {
                            if( searchText.length() > 0 )
                                NetworkManager.inst().RequestPromotionSearchList( getContext(), mCallBack, "promotion_data", Define.PROMOTION_STUDENT, searchText, 0, Define.MAX_PROM_DATA );
                            else
                                NetworkManager.inst().RequestPromotionData( getContext(), mCallBack, "promotion_data", Define.PROMOTION_STUDENT, 0, Define.MAX_PROM_DATA );
                        }
                        break;
                        case 1:
                        {
                            if( searchText.length() > 0 )
                                NetworkManager.inst().RequestPromotionSearchList( getContext(), mCallBack, "promotion_data", Define.PROMOTION_TRADER, searchText, 0, Define.MAX_PROM_DATA );
                            else
                                NetworkManager.inst().RequestPromotionData( getContext(), mCallBack, "promotion_data", Define.PROMOTION_TRADER, 0, Define.MAX_PROM_DATA );
                        }
                        break;
                    }

                }
            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        } );

        //옵션버튼
        Button btn = (Button)view.findViewById(R.id.fragment_promote_Add_btn);
        btn.setOnClickListener( onClickListener );
        if( DataManager.inst().getUserData() == null || ( DataManager.inst().getUserData().getGrade() != Define.GRADE_PRESIDENT && DataManager.inst().getUserData().getGrade() != Define.GRADE_TRADER ))
            btn.setVisibility( View.INVISIBLE );

        dataList = DataManager.inst().getPromotionDataList();
        adapter = new PromoteListAdapter( getContext(), dataList );
        promoteList = view.findViewById(R.id.promote_list_view );
        promoteList.setAdapter( adapter);

        promoteList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                saveScrollPos();
                selectPromotionData = adapter.getDataList().get( position );
                DataManager.inst().setSelectPromotionData( selectPromotionData );
                RequestPromoteSubData( selectPromotionData );
            }
        });

        progressBar = view.findViewById( R.id.fragment_promote_progressbar );
        progressBar.setVisibility(View.GONE);

        promoteList.setOnScrollListener(new AbsListView.OnScrollListener()
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

                    int selectTab = mTabLayout.getSelectedTabPosition();
                    int type = ( selectTab == 0 ) ? Define.PROMOTION_STUDENT : Define.PROMOTION_TRADER;
                    int start = dataList.size();
                    // 다음 데이터를 불러온다.
                    if( searchText.length() > 0 )
                        NetworkManager.inst().RequestPromotionSearchList( getContext(), mCallBack, "promotion_data", type, searchText, start, Define.MAX_PROM_DATA );
                    else
                        NetworkManager.inst().RequestPromotionData( getContext(), mCallBack, "promotion_data", type, start, Define.MAX_PROM_DATA );
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

        mCallBack = new HttpTaskCallBack()
        {
            @Override
            public void doProcess(JSONObject jsonObj)
            {
                try
                {
                    if(jsonObj.get("packet_id").equals("promotion_sub_data"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            if(DataManager.inst().ParsingPromotionSubData( jsonObj ) )
                            {
                                NetworkManager.inst().UpdatePromotionFeq( getContext(), mCallBack, selectPromotionData.getSeq(), selectPromotionData.getFeq() + 1 );
                            }
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("update_promotion_feq"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            int seq = Integer.parseInt( jsonObj.get("seq").toString() );
                            int feq = Integer.parseInt( jsonObj.get("feq").toString() );
                            DataManager.inst().UpdatePromotionFeq( seq, feq );

                            PromoteContentsFragment fragment = new PromoteContentsFragment();
                            fragment.setPromotionData( selectPromotionData );
                            HomeActivity.inst().ChangeFragment( fragment, "PromoteContentsFragment");
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("promotion_data"))
                    {
                        // 리스트에 다음 데이터를 입력할 동안에 이 메소드가 또 호출되지 않도록 mLockListView 를 true로 설정한다.
                        progressBar.setVisibility(View.GONE);
                        mLockListView = false;
                        if( jsonObj.get("result").equals("true") )
                        {
                            if( DataManager.inst().ParsingPromotionData( jsonObj ) )
                            {
                                dataList = DataManager.inst().getPromotionDataList();
                                adapter.ChangeData( dataList );
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

        SelectTab( selectTab );

        promoteList.post(new Runnable() {
            @Override
            public void run() {
                if(bRequestData == false )
                {
                    bRequestData = true;
                    promoteList.setSelectionFromTop(scrollIdx, scrollTop);
                }
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

    public ArrayList<String> GetDownloadImageList()
    {
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<PromotionSubData> dataList = DataManager.inst().getPromotionSubDataList();
        for( int i = 0; i < dataList.size(); ++i )
        {
            if( dataList.get(i).getImgName().length() == 0 )
                continue;

            String strDir = DownloadManager.getInstance().getPath();
            String filePath = strDir + "/" + dataList.get(i).getImgName();
            if(Util.ExistFile( filePath ) == false )
            {
                list.add( dataList.get(i).getImgName() );
            }
        }

        return list;
    }

    void OnAddBtnClick()
    {
        DataManager.inst().setSelectPromotionData( null );
        HomeActivity.inst().ChangeFragment( new PromoteRegisterFragment(), "PromoteRegisterFragment");
    }

    void RequestPromoteSubData( PromotionData data )
    {
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "promotion_sub_data");
            jsonObj.put("ad_title_seq", data.getSeq() );
            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/promotion_sub_data.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, getContext());
            httpConnectTask.SetCallBack(mCallBack);
            httpConnectTask.execute();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void ProcessSearch( String searchText )
    {
        int promotionType;
        setSearchText( searchText);
        if( mTabLayout.getSelectedTabPosition() == 0 )
        {
            promotionType = Define.PROMOTION_STUDENT;
        }
        else
        {
            promotionType = Define.PROMOTION_TRADER;
        }

        DataManager.inst().ClearPromotionDataList();
        NetworkManager.inst().RequestPromotionSearchList( getContext(), mCallBack, "promotion_data", promotionType, searchText, 0, Define.MAX_PROM_DATA );
    }

    private void saveScrollPos()
    {
        // save index and top position
        scrollIdx = promoteList.getFirstVisiblePosition();
        View v = promoteList.getChildAt(0);
        scrollTop= (v == null) ? 0 : (v.getTop() - promoteList.getPaddingTop());
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        bRequestData = true;
    }
}
