package automacticphone.android.com.casebook.activity.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.labo.kaji.fragmentanimations.MoveAnimation;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.HomeActivity;
import automacticphone.android.com.casebook.activity.adapter.TableContentsYearAdapter;
import automacticphone.android.com.casebook.activity.adapter.UpLoadPhotoAdapter;
import automacticphone.android.com.casebook.activity.common.DataManager;
import automacticphone.android.com.casebook.activity.common.Define;
import automacticphone.android.com.casebook.activity.data.CaseData;
import automacticphone.android.com.casebook.activity.data.RegulationMainData;
import automacticphone.android.com.casebook.activity.data.RegulationSubData1;
import automacticphone.android.com.casebook.activity.data.RegulationSubData2;
import automacticphone.android.com.casebook.activity.data.RegulationSubData3;
import automacticphone.android.com.casebook.activity.data.SubTreeCaseData;
import automacticphone.android.com.casebook.activity.holder.HeaderHolder;
import automacticphone.android.com.casebook.activity.holder.IconTreeItemHolder;
import automacticphone.android.com.casebook.activity.holder.ProfileHolder;
import automacticphone.android.com.casebook.activity.holder.SubTreeViewHolder;
import automacticphone.android.com.casebook.activity.network.HttpTaskCallBack;
import automacticphone.android.com.casebook.activity.network.NetworkManager;


public class TableContentsFragment extends Fragment {
    private boolean bAnimation = false;
    private int cate_reg = 0;
    private RegulationMainData regulMainData;
    private TextView caseTitleTextView;
    private TableContentsYearAdapter yearAdapter;
    private HttpTaskCallBack mCallBack = null;

    public TableContentsFragment() {

        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_table_contents, container, false);

        if( DataManager.inst().getRegulMainDataList().size() > 0 )
            cate_reg = DataManager.inst().getSelectCateReg();

        regulMainData = DataManager.inst().getRegulationMainData( cate_reg );
        caseTitleTextView = view.findViewById(R.id.table_contents_case_title);

        ArrayList<Integer> yearList = DataManager.inst().getCaseYearList();
        String caseTitle = "";
        if( yearList.size() == 0 )
            caseTitle = String.format( "%s", regulMainData.getRegul() );
        else
        {
            int selectYear = DataManager.inst().getSelectYear();
            caseTitle = String.format( "%s(%d)", regulMainData.getRegul(), selectYear );
        }
        caseTitleTextView.setText( caseTitle );
        RecyclerView yearRecyclerList = view.findViewById( R.id.table_contents_year_listview );
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        yearRecyclerList.setLayoutManager(layoutManager);
        yearAdapter = new TableContentsYearAdapter(getContext(), yearList );
        yearRecyclerList.setAdapter( yearAdapter );

        SetTreeNodeData( view, savedInstanceState );

        mCallBack = new HttpTaskCallBack()
        {
            @Override
            public void doProcess(JSONObject jsonObj)
            {
                try
                {
                    if(jsonObj.get("packet_id").equals("case_data") )
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            int totalCount = Integer.valueOf( jsonObj.get("total_count").toString() );
                            if( totalCount == 0 )
                                HomeActivity.inst().ShowAlertDialog( "등록된 사례가 없습니다.");
                            else
                            {
                                if( DataManager.inst().ParsingCaseData( jsonObj ) )
                                {
                                    BoardFragment.selectTab = 0;
                                    HomeActivity.inst().ChangeFragment( new BoardFragment(), "BoardFragment" );
                                }
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
        return view;
    }

    public void SetYearList( ArrayList<Integer> yearList )
    {
        yearAdapter.ChangeData( yearList );
    }

    void SetTreeNodeData( View view, Bundle savedInstanceState )
    {
        final ViewGroup containerView = (ViewGroup) view.findViewById(R.id.table_contents_layout);
        ArrayList<RegulationSubData1> subDataList1 = DataManager.inst().getRegulSubMenu1( regulMainData );
        TreeNode root = TreeNode.root();

        for( int i = 0; i < subDataList1.size(); ++i )
        {
            TreeNode titleNode = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_account_circle, subDataList1.get(i).getRegul() ) ).setViewHolder(new ProfileHolder(getActivity()));
            addCaseData(titleNode, subDataList1.get(i) );
            root.addChild(titleNode);
        }

        AndroidTreeView tView = new AndroidTreeView(getActivity(), root);
        tView.setDefaultAnimation(false);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleDivided, true);
        containerView.addView(tView.getView());

        if (savedInstanceState != null) {
            String state = savedInstanceState.getString("tState");
            if (!TextUtils.isEmpty(state)) {
                tView.restoreState(state);
            }
        }
    }

    private void addCaseData(TreeNode mainNode, RegulationSubData1 subData1 )
    {
        ArrayList<RegulationSubData2> subDataList2 = DataManager.inst().getRegulSubMenu2( subData1 );
        TreeNode subTree;

        int selectYear = DataManager.inst().getSelectYear();
        for( int i = 0; i < subDataList2.size(); ++i )
        {
            subTree = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_people, subDataList2.get(i).getRegul() )).setViewHolder(new HeaderHolder(getActivity()));
            ArrayList<TreeNode> childList = new ArrayList<TreeNode>();
            ArrayList<RegulationSubData3> subDataList3 = DataManager.inst().getRegulSubMenu3(  subDataList2.get(i) );
            int index = 1;
            for( int k = 0; k < subDataList3.size(); ++k, ++index )
            {
                SubTreeCaseData subTreeCaseData = new SubTreeCaseData( selectYear, cate_reg, subData1.getSeq(), subDataList2.get(i).getSeq(), subDataList3.get(k).getSeq() );
                String subMenuText2 = String.format( "%d . %s", index, subDataList3.get(k).getRegul() );
                TreeNode node1 = new TreeNode(new SubTreeViewHolder.SubTreeItem(i, subMenuText2, subTreeCaseData )).setViewHolder(new SubTreeViewHolder(getActivity(), 0 ));
                node1.setClickListener( nodeClickListener );
                childList.add( node1 );
            }

            if( childList.size() > 0 )
                subTree.addChildren( childList );

            mainNode.addChild( subTree );
        }
    }

    private TreeNode.TreeNodeClickListener nodeClickListener = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            SubTreeViewHolder.SubTreeItem item = (SubTreeViewHolder.SubTreeItem) value;
            if( item != null )
            {
                Log.d("Last clicked: ",  String.valueOf( item.index  ));
                if( item.subTreeCaseData != null )
                {
                    HomeActivity.inst().setSearchText("");
                    HomeActivity.inst().setSelectSubTreeCaseData( item.subTreeCaseData );
                    DataManager.inst().ClearCaseDataList();
                    NetworkManager.inst().RequestCaseData( getContext(), mCallBack, "case_data", item.subTreeCaseData.getSelectYear(), item.subTreeCaseData.getCate_reg(), item.subTreeCaseData.getCate_1(), item.subTreeCaseData.getCate_2(), item.subTreeCaseData.getCate_3(), Define.GOOD_CASE, 0, Define.MAX_CASE_DATA );
                }
            }
        }
    };
}
