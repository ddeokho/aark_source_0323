package automacticphone.android.com.casebook.activity.fragment;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.TextTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.KakaoParameterException;
import com.kakao.util.helper.log.Logger;
import com.ssomai.android.scalablelayout.ScalableLayout;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.HomeActivity;
import automacticphone.android.com.casebook.activity.adapter.ContentViewPagerAdapter;
import automacticphone.android.com.casebook.activity.adapter.ContentsCommentAdapter;
import automacticphone.android.com.casebook.activity.common.DataManager;
import automacticphone.android.com.casebook.activity.common.Define;
import automacticphone.android.com.casebook.activity.common.Util;
import automacticphone.android.com.casebook.activity.data.CaseData;
import automacticphone.android.com.casebook.activity.data.CommentData;
import automacticphone.android.com.casebook.activity.data.UserData;
import automacticphone.android.com.casebook.activity.network.HttpConnectTask;
import automacticphone.android.com.casebook.activity.network.HttpTaskCallBack;
import automacticphone.android.com.casebook.activity.network.NetworkManager;
import automacticphone.android.com.casebook.activity.network.RequestHttpURLConnection;

public class ContentsViewFragment extends Fragment
{
    private CaseData caseData;
    private EditText commentEdit;
    private HttpTaskCallBack mCallBack = null;
    private int startCommentIndex;
    private LinearLayout commentLayout;
    private ContentsViewFragment.BtnOnClickListener onClickListener;
    private ViewGroup mContainer;
    private View deleteView;
    private ArrayList<View> commentItemList = new ArrayList<View>();
    private Button goodBtn;
    private Button badBtn;
    private ZLoadingDialog loadingDialog;
    private static  Toast sToast;
    private String prevToastMsg = "";

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view)
        {
            switch (view.getId()) {
                case R.id.content_view_comment_btn:
                    OnCommentBtnClick();
                    break;
                case R.id.list_button_see_more:
                    OnSeeMoreBtnClick();
                    break;
                case R.id.list_item_comment_delete:
                    OnCommentDeleteBtnClick( view );
                    break;
                case R.id.content_view_good_btn:
                    ShowGoodCasePopup();
                    break;
                case R.id.content_view_bad_btn:
                    ShowBadCasePopup();
                    break;
                case R.id.content_view_edit_btn:
                    OnCaseEditBtnClick();
                    break;
                case R.id.content_view_delete_btn:
                    ShowDeletePopup();
                    break;
                case R.id.content_view_share_btn:
                    OnContentShareBtnClick();
                    break;
            }
        }
    }

    private void ShowGoodCasePopup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("");
        builder.setMessage("좋은 사례로 판별하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        OnCaseGoodBtnClick();
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    private void ShowBadCasePopup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("");
        builder.setMessage("안좋은 사례로 판별하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        OnCaseBadBtnClick();
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    public ContentsViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        caseData = DataManager.inst().getSelectCaseData();
        mContainer = container;
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contents_view, container, false);
        TextView textView = view.findViewById( R.id.contents_view_title);
        textView.setText( caseData.getTitle() );

        String dateText = "";
        try
        {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse( caseData.getTimestamp() );
            dateText = new SimpleDateFormat("yy.MM.dd").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        textView = view.findViewById( R.id.contents_view_date);
        textView.setText( dateText );

        textView = view.findViewById( R.id.contents_view_body_text);
        textView.setText( caseData.getContent() );

        textView = view.findViewById( R.id.contents_view_name);
        UserData userData = DataManager.inst().getContentOwnerData();
        if( userData != null )
            textView.setText( Util.getEmailById( userData.getEmail() ) );
        else
            textView.setText( "탈퇴한 회원" );

        ArrayList<String> imageList = new ArrayList<String>();
        if( caseData.getImg_1().length() > 0 )
            imageList.add( caseData.getImg_1() );

        if(caseData.getImg_2().length() > 0)
            imageList.add( caseData.getImg_2() );

        if(caseData.getImg_3().length() > 0)
            imageList.add( caseData.getImg_3() );

        ViewPager viewPager = view.findViewById( R.id.contents_photo_viewpager );
        ContentViewPagerAdapter adapter = new ContentViewPagerAdapter( getContext(), getActivity(), imageList );
        viewPager.setAdapter( adapter );

        if( imageList.size() > 1 )
        {
            TabLayout tabLayout = (TabLayout)view.findViewById(R.id.contents_tab_layout);
            tabLayout.setupWithViewPager( viewPager, true);
        }

        onClickListener = new ContentsViewFragment.BtnOnClickListener();
        commentLayout = view.findViewById( R.id.content_view_linerlayout);
        Button btn = view.findViewById(R.id.content_view_share_btn);
        btn.setOnClickListener( onClickListener );
        for( int i = 0; i < DataManager.inst().getCommentDataList().size(); ++i )
        {
            CommentData commentData = DataManager.inst().getCommentDataList().get(i);
            inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate( R.layout.list_item_comment, container, false );
            btn = v.findViewById(R.id.list_item_comment_delete );
            btn.setTag( commentData.getSeq() );
            btn.setOnClickListener( onClickListener );
            if( IsShowCommentDeleteBtn( commentData.getMemberSeq() ) == false )
            {
                btn.setVisibility(View.INVISIBLE);
            }

            SetCommentInfo( v, commentData );
            commentLayout.addView( v );
            commentItemList.add( v );
        }

        if( IsShowSeeMoreBtn() )
        {
            inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate( R.layout.list_button, container, false );
            commentLayout.addView( v );

            btn = v.findViewById( R.id.list_button_see_more );
            btn.setOnClickListener( onClickListener );
            commentItemList.add( v );
        }

        btn = (Button)view.findViewById(R.id.content_view_comment_btn);
        btn.setOnClickListener( onClickListener );
        commentEdit = view.findViewById( R.id.content_view_comment_edit);
        mCallBack = new HttpTaskCallBack()
        {
            @Override
            public void doProcess(JSONObject jsonObj)
            {
                try
                {
                    if(jsonObj.get("packet_id").equals("upload_comment"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            int totalCommentCount = Integer.valueOf( jsonObj.get("comment_count").toString() );
                            DataManager.inst().setTotalCommentCount( totalCommentCount );
                            SetToast( "댓글이 등록되었습니다.", Toast.LENGTH_SHORT);
                            commentEdit.setText("");
                            startCommentIndex = 0;
                            DeleteCommentItemAll();
                            DataManager.inst().getCommentDataList().clear();
                            NetworkManager.inst().RequestCommentData( getContext(), mCallBack, caseData.getSeq(), startCommentIndex, Define.MAX_COMMENT_COUNT );
                            NetworkManager.inst().RequestWriteList( getContext(), mCallBack, DataManager.inst().getUserData().getSeq() );
                            NetworkManager.inst().RequestWriteCommentCount( getContext(), mCallBack, DataManager.inst().getUserData().getSeq() );
                        }
                        else
                            loadingDialog.cancel();
                    }
                    else if(jsonObj.get("packet_id").equals("write_list"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                           DataManager.inst().ParsingWriteList( jsonObj );
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("write_comment_count"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            int commentCount = Integer.parseInt( jsonObj.get("comment_count").toString() );
                            DataManager.inst().setWriteCommentCount( commentCount );
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("comment_data"))
                    {
                        loadingDialog.cancel();
                        if( jsonObj.get("result").equals("true") )
                        {
                            if( DataManager.inst().ParsingCommentData( jsonObj ) )
                            {
                                ScalableLayout layout = commentLayout.findViewById( R.id.list_button_layout );
                                commentLayout.removeView( layout );
                                UpdateCommentItem( startCommentIndex );
                            }
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("delete_comment"))
                    {
                        loadingDialog.cancel();
                        if( jsonObj.get("result").equals("true") )
                        {
                            SetToast( "댓글이 삭제 되었습니다.", Toast.LENGTH_SHORT);
                            int seq = Integer.parseInt( jsonObj.get("seq").toString() );
                            if( DataManager.inst().DeleteCommentData( seq ))
                            {
                                commentLayout.removeView( deleteView );
                                NetworkManager.inst().RequestWriteCommentCount( getContext(), mCallBack, DataManager.inst().getUserData().getSeq() );
                                NetworkManager.inst().RequestWriteList( getContext(), mCallBack, DataManager.inst().getUserData().getSeq() );
                            }
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("case_grade"))
                    {
                        loadingDialog.cancel();
                        if( jsonObj.get("result").equals("true") )
                        {
                            int seq = Integer.parseInt( jsonObj.get("seq").toString() );
                            int grade = Integer.parseInt( jsonObj.get("grade").toString() );
                            DataManager.inst().UpdateCaseDataGrade( seq, grade );
                            SetToast( "판별 되었습니다.", Toast.LENGTH_SHORT);
                            BoardFragment fragment = new BoardFragment();
                            BoardFragment.selectTab = 2;
                            HomeActivity.inst().ChangeFragment( fragment, "BoardFragment" );
                        }
                        else
                            SetToast( "판별에 실패하였습니다. 다시 시도해 주세요..", Toast.LENGTH_SHORT);
                    }
                    else if(jsonObj.get("packet_id").equals("delete_case_text"))
                    {
                        loadingDialog.cancel();
                        if( jsonObj.get("result").equals("true") )
                        {
                            SetToast( "사례글이 삭제 되었습니다.", Toast.LENGTH_SHORT);
                            int seq = Integer.parseInt( jsonObj.get("seq").toString() );
                            DataManager.inst().DeleteCaseData( seq );
                            NetworkManager.inst().RequestWriteList( getContext(), mCallBack, DataManager.inst().getUserData().getSeq() );
                            HomeActivity.inst().MovePrevFragment( "2" );
                        }
                    }
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        };

        SetButtons( view );
        return view;
    }

    boolean IsShowSeeMoreBtn()
    {
        if( DataManager.inst().getCommentDataList().size() < DataManager.inst().getTotalCommentCount() )
            return true;

        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        DataManager.inst().getCommentDataList().clear();
    }

    void DeleteCommentItemAll()
    {
        for( int i = 0; i < commentItemList.size(); ++i )
        {
            commentLayout.removeView( commentItemList.get(i) );
        }

        commentItemList.clear();
    }

    void SetButtons( View v )
    {
        goodBtn = v.findViewById(R.id.content_view_good_btn);
        goodBtn.setOnClickListener( onClickListener );
        goodBtn.setVisibility( View.INVISIBLE);
        badBtn = v.findViewById(R.id.content_view_bad_btn);
        badBtn.setOnClickListener( onClickListener );
        badBtn.setVisibility(View.INVISIBLE);

        Button editBtn = v.findViewById(R.id.content_view_edit_btn);
        editBtn.setOnClickListener( onClickListener );
        editBtn.setVisibility(View.INVISIBLE);
        Button deleteBtn = v.findViewById(R.id.content_view_delete_btn);
        deleteBtn.setVisibility(View.INVISIBLE);
        deleteBtn.setOnClickListener( onClickListener );

        if( caseData.getGrade() == Define.QUESTION_CASE )
        {
            int userSeq = DataManager.inst().getUserData().getSeq();
            if( DataManager.inst().getUserData() != null )
            {
                if( DataManager.inst().getUserData().getGrade() == Define.GRADE_ADMIN  )
                {
                    goodBtn.setVisibility( View.VISIBLE );
                    badBtn.setVisibility( View.VISIBLE );
                    editBtn.setVisibility( View.VISIBLE );
                    deleteBtn.setVisibility( View.VISIBLE );
                }
                else if( DataManager.inst().getUserData().getGrade() == Define.GRADE_GRADUATE )
                {
                    goodBtn.setVisibility( View.VISIBLE );
                    badBtn.setVisibility( View.VISIBLE );
                }
                else if( DataManager.inst().getContentOwnerData() != null )
                {
                    if( DataManager.inst().getContentOwnerData().getSeq() == userSeq )
                    {
                        editBtn.setVisibility( View.VISIBLE );
                        deleteBtn.setVisibility( View.VISIBLE );
                    }
                }
            }
        }
    }

    void UpdateCommentItem( int start )
    {
        LayoutInflater inflater;
        for( int i = 0; i < DataManager.inst().getCommentDataList().size(); ++i )
        {
            if( i < start )
                continue;

            CommentData commentData = DataManager.inst().getCommentDataList().get(i);
            inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate( R.layout.list_item_comment, mContainer, false );
            Button btn = v.findViewById(R.id.list_item_comment_delete );
            btn.setTag( commentData.getSeq() );
            btn.setOnClickListener( onClickListener );
            if( IsShowCommentDeleteBtn( commentData.getMemberSeq() ) == false )
            {
                btn.setVisibility(View.INVISIBLE);
            }

            SetCommentInfo( v, commentData );
            commentLayout.addView( v );
            commentItemList.add( v );
        }

        if( IsShowSeeMoreBtn() )
        {
            inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate( R.layout.list_button, mContainer, false );
            commentLayout.addView( v );
            commentItemList.add( v );

            Button btn = v.findViewById( R.id.list_button_see_more );
            btn.setOnClickListener( onClickListener );
        }
    }

    public void SetCommentInfo(View v, CommentData data )
    {
        TextView textView = v.findViewById(R.id.list_item_comment_id );
        String id = Util.getEmailById( data.getMemberID() );
        if( id.equals( "null"))
            textView.setText( "탈퇴한 회원" );
        else
            textView.setText( id );

        String dateText = "";
        try
        {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse( data.getTimestamp() );
            dateText = new SimpleDateFormat("yy.MM.dd").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        textView = v.findViewById(R.id.list_item_comment_date );
        textView.setText( dateText );

        textView = v.findViewById(R.id.list_item_comment_text );
        textView.setText( data.getContent() );
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    void OnCommentBtnClick()
    {
        if(DataManager.inst().getUserData() == null )
        {
            HomeActivity.inst().ShowAlertDialog( "로그인 해주세요." );
            return;
        }

        String content = commentEdit.getText().toString();
        content = content.trim();

        if( content.length() == 0 )
        {
            HomeActivity.inst().ShowAlertDialog( "댓글을 입력해 주세요." );
            return;
        }

        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "upload_comment");
            jsonObj.put("member_seq", DataManager.inst().getUserData().getSeq() );
            jsonObj.put("example_seq", caseData.getSeq() );
            jsonObj.put("content", commentEdit.getText().toString() );

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/upload_comment.php";
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

    void OnSeeMoreBtnClick()
    {
        loadingDialog = Util.ShowLoading( getContext(), Z_TYPE.SINGLE_CIRCLE, false  );
        startCommentIndex = DataManager.inst().getCommentDataList().size();
        NetworkManager.inst().RequestCommentData( getContext(), mCallBack, caseData.getSeq(), startCommentIndex, Define.MAX_COMMENT_COUNT );
    }

    void OnCommentDeleteBtnClick( View v )
    {
        loadingDialog = Util.ShowLoading( getContext(), Z_TYPE.SINGLE_CIRCLE, false  );
        JSONObject jsonObj = new JSONObject();
        try
        {
            int commentSeq = Integer.parseInt( v.getTag().toString() );
            deleteView = (ViewGroup)v.getParent();
            jsonObj.put("packet_id", "delete_comment");
            jsonObj.put("seq", commentSeq );

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/delete_comment.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, getContext() );
            httpConnectTask.SetCallBack(mCallBack);
            httpConnectTask.execute();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
            loadingDialog.cancel();
        }
    }

    void OnCaseGoodBtnClick()
    {
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "case_grade");
            jsonObj.put("token", HomeActivity.inst().getToken() );
            jsonObj.put("seq", caseData.getSeq() );
            jsonObj.put("grade", Define.GOOD_CASE );

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/case_grade.php";
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

    void OnCaseBadBtnClick()
    {
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "case_grade");
            jsonObj.put("token", HomeActivity.inst().getToken() );
            jsonObj.put("seq", caseData.getSeq() );
            jsonObj.put("grade", Define.BAD_CASE );

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/case_grade.php";
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

    void OnCaseEditBtnClick()
    {
        HomeActivity.inst().ChangeFragment( new UploadContentsFragment(), "UploadContentsFragment" );
    }

    void OnCaseDeleteBtnClick()
    {
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "delete_case_text");
            jsonObj.put("seq", caseData.getSeq() );

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/delete_case_text.php";
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

    void OnContentShareBtnClick()
    {
        String param = String.format( "case_seq=%d", caseData.getSeq() );
        TextTemplate params = TextTemplate.newBuilder(caseData.getTitle(), LinkObject.newBuilder().setAndroidExecutionParams( param ).build()).setButtonTitle("앱에서 바로 확인").build();

        Map<String, String> serverCallbackArgs = new HashMap<String, String>();
        serverCallbackArgs.put("user_id", "${current_user_id}");
        serverCallbackArgs.put("product_id", "${shared_product_id}");

        KakaoLinkService.getInstance().sendDefault(getContext(), params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
                // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
            }
        });
    }

    public boolean IsShowCommentDeleteBtn( int memberSeq )
    {
        if( DataManager.inst().getUserData() == null )
            return false;

        if( memberSeq == DataManager.inst().getUserData().getSeq() || DataManager.inst().getUserData().getGrade() == Define.GRADE_ADMIN )
            return true;

        return false;
    }

    private void SetToast( String msg, int toastDuration )
    {
        if (sToast == null)
        {
            sToast = Toast.makeText( getContext(), msg, toastDuration );
        }
        else
        {
            if( prevToastMsg.equals( msg ) )
            {
                sToast.cancel();
            }

            sToast = Toast.makeText( getContext(), msg, toastDuration );
        }
        sToast.show();
        prevToastMsg = msg;
    }

    private void ShowDeletePopup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("");
        builder.setMessage("삭제하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        OnCaseDeleteBtnClick();
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }
}
