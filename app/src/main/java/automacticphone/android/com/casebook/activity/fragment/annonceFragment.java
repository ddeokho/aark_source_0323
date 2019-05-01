package automacticphone.android.com.casebook.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


import automacticphone.android.com.casebook.MainActivity;
import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.adapter.AnnounceAdapter;
import automacticphone.android.com.casebook.activity.adapter.BoardListAdapter;
import automacticphone.android.com.casebook.activity.common.DataManager;
import automacticphone.android.com.casebook.activity.common.Define;
import automacticphone.android.com.casebook.activity.data.AnnounceData;
import automacticphone.android.com.casebook.activity.network.HttpTaskCallBack;
import automacticphone.android.com.casebook.activity.network.NetworkManager;


public class annonceFragment extends Fragment {
    private AnnounceAdapter adapter;
    private HttpTaskCallBack mCallBack = null;
    private AnnounceData announceData;
    private boolean lastItemVisibleFlag = false;    // 리스트 스크롤이 마지막 셀(맨 바닥)로 이동했는지 체크할 변수
    private boolean mLockListView = false;          // 데이터 불러올때 중복안되게 하기위한 변수
    private ProgressBar progressBar;                // 데이터 로딩중을 표시할 프로그레스바
    private ArrayList<AnnounceData> dataList;
    private ListView announceListView;


    private String searchText ="";
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public annonceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_announce,container,false);

        announceListView = view.findViewById( R.id.announce_list_view);

        dataList = DataManager.inst().getAnnounceDataList();
        adapter = new AnnounceAdapter( getContext(), dataList );
        announceListView.setAdapter( adapter );

        //progressBar = view.findViewById( R.id.fragment_board_progressbar );
        //progressBar.setVisibility(View.GONE);

        /*announceListView.setOnScrollListener(new AbsListView.OnScrollListener()
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
                    int start = dataList.size();

                    // 다음 데이터를 불러온다.
                    NetworkManager.inst().RequestAnnounceData(getContext(), mCallBack, "announce_data", start, start+15);
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
        });*/

        mCallBack = new HttpTaskCallBack()
        {
            @Override
            public void doProcess(JSONObject jsonObj)
            {

            }
        };
        return view;
    }
}
