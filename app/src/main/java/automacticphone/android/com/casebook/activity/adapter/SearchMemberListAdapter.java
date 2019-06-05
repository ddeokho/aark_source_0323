package automacticphone.android.com.casebook.activity.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.HomeActivity;
import automacticphone.android.com.casebook.activity.common.DataManager;
import automacticphone.android.com.casebook.activity.common.Define;
import automacticphone.android.com.casebook.activity.data.CaseData;
import automacticphone.android.com.casebook.activity.data.UserData;
import automacticphone.android.com.casebook.activity.fragment.AdminPageFragment;
import automacticphone.android.com.casebook.activity.network.DownloadManager;
import automacticphone.android.com.casebook.activity.network.HttpConnectTask;
import automacticphone.android.com.casebook.activity.network.HttpTaskCallBack;
import automacticphone.android.com.casebook.activity.network.RequestHttpURLConnection;

public class SearchMemberListAdapter extends BaseAdapter
{
    private Context context;
    private LayoutInflater inflater;
    private SearchMemberListAdapter.BtnOnClickListener onClickListener = new SearchMemberListAdapter.BtnOnClickListener();

    public ArrayList<UserData> getDataList() {
        return dataList;
    }

    private ArrayList<UserData> dataList = new ArrayList<UserData>();
    private PopupWindow mPopupWindow;
    private HttpTaskCallBack mCallBack = null;
    private UserData selectUserData;
    private int userGrade;

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view)
        {
            switch (view.getId()) {
                case R.id.admin_search_grade_btn:
                    selectUserData = dataList.get( (int)view.getTag() );
                    OnAdminSearchGradeBtnClick();
                    break;
                case R.id.grade_popup_normal_member_btn:
                    UpdateMemberGrade(Define.GRADE_STUDENT);
                    break;
                case R.id.grade_popup_ob_member_btn:
                    UpdateMemberGrade(Define.GRADE_COMUNI);//운영위
                    break;
                case R.id.grade_popup_trader_member_btn:
                    UpdateMemberGrade(Define.GRADE_INSPEC);//검차
                    break;
                case R.id.grade_popup_president_member_btn:
                    UpdateMemberGrade(Define.GRADE_PRESIDENT);
                    break;
                case R.id.grade_popup_admin_member_btn:
                    UpdateMemberGrade(Define.GRADE_ADMIN);
                    break;
                case R.id.grade_popup_cancel_btn:
                    mPopupWindow.dismiss();
                    break;
            }
        }
    }
    public SearchMemberListAdapter(Context context, ArrayList<UserData> arrayList)
    {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        if( arrayList != null )
            this.dataList = arrayList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.list_item_admin_page, parent, false);
        }
        Button btn = convertView.findViewById(R.id.admin_search_grade_btn);
        btn.setTag( position );
        btn.setOnClickListener( onClickListener );
        btn.setFocusable( false );

        UserData userData = dataList.get( position );
        if( userData != null )
        {
            TextView textView = convertView.findViewById(R.id.admin_search_index_text );
            textView.setText( String.valueOf( position ) );

            textView = convertView.findViewById(R.id.admin_search_name_text );
            String text = String.format( "%s(%s)", userData.getName(), userData.getEmail() );
            textView.setText( text );
        }

        mCallBack = new HttpTaskCallBack()
        {
            @Override
            public void doProcess(JSONObject jsonObj)
            {
                try
                {
                    if(jsonObj.get("packet_id").equals("update_member_grade"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            String msg = String.format( "'%s'님이 %s 등급으로 변경 되었습니다.", selectUserData.getName(), getGradeText( userGrade ) );
                            Toast.makeText( context, msg, Toast.LENGTH_SHORT ).show();
                            mPopupWindow.dismiss();
                        }
                    }
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        };
        return convertView;
    }

    public String getGradeText( int grade )
    {
        if( grade == Define.GRADE_STUDENT )
            return "일반학생";
        else if( grade == Define.GRADE_COMUNI )
            return "운영위";
        else if( grade == Define.GRADE_INSPEC )
            return "검차위원";
        else if( grade == Define.GRADE_PRESIDENT )
            return "동아리장";
        else
            return "관리자";
    }

    public void ChangeData( ArrayList<UserData> list )
    {
        dataList = list;
        notifyDataSetChanged();
    }

    void OnAdminSearchGradeBtnClick()
    {
        AdminPageFragment adminPageFragment = (AdminPageFragment) HomeActivity.inst().getCurrentFragment();
        View popupView = adminPageFragment.getLayoutInflater().inflate(R.layout.select_member_grade_popup, null);
        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //popupView 에서 (LinearLayout 을 사용) 레이아웃이 둘러싸고 있는 컨텐츠의 크기 만큼 팝업 크기를 지정

        mPopupWindow.setFocusable(true);
        // 외부 영역 선택히 PopUp 종료

        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        Button btn = (Button) popupView.findViewById(R.id.grade_popup_normal_member_btn);
        btn.setOnClickListener( onClickListener );
        btn = (Button) popupView.findViewById(R.id.grade_popup_ob_member_btn);
        btn.setOnClickListener( onClickListener );
        btn = (Button) popupView.findViewById(R.id.grade_popup_trader_member_btn);
        btn.setOnClickListener( onClickListener );
        btn = (Button) popupView.findViewById(R.id.grade_popup_president_member_btn);
        btn.setOnClickListener( onClickListener );
        btn = (Button) popupView.findViewById(R.id.grade_popup_admin_member_btn);
        btn.setOnClickListener( onClickListener );
        btn = (Button) popupView.findViewById(R.id.grade_popup_cancel_btn);
        btn.setOnClickListener( onClickListener );
    }

    void UpdateMemberGrade( int grade )
    {
        JSONObject jsonObj = new JSONObject();
        try
        {
            userGrade = grade;
            jsonObj.put("packet_id", "update_member_grade");
            jsonObj.put("seq", selectUserData.getSeq() );
            jsonObj.put("grade", grade );

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/update_member_grade.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, context );
            httpConnectTask.SetCallBack(mCallBack);
            httpConnectTask.execute();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
