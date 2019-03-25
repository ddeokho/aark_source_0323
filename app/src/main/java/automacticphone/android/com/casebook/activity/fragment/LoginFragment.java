package automacticphone.android.com.casebook.activity.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.labo.kaji.fragmentanimations.MoveAnimation;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.HomeActivity;
import automacticphone.android.com.casebook.activity.common.DataManager;
import automacticphone.android.com.casebook.activity.common.Util;
import automacticphone.android.com.casebook.activity.network.HttpConnectTask;
import automacticphone.android.com.casebook.activity.network.HttpTaskCallBack;
import automacticphone.android.com.casebook.activity.network.NetworkManager;
import automacticphone.android.com.casebook.activity.network.RequestHttpURLConnection;

public class LoginFragment extends Fragment {

    private PopupWindow mPopupWindow;
    private EditText idInputEdit;
    private EditText pwInputEdit;
    private HttpTaskCallBack mCallBack = null;
    private EditText findNameEdit;
    private EditText findPhoneEdit;
    private EditText findPwEmailEdit;
    private EditText findPwNameEdit;
    private CheckBox autoLogin;
    private ZLoadingDialog loadingDialog;

    public LoginFragment() {
        // Required empty public constructor
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view)
        {
            switch (view.getId()) {
                case R.id.find_id_btn:
                    OnFindLoginBtnClick();
                    break;
                case R.id.find_pw_btn:
                    OnFindPasswordBtnClick();
                    break;
                case R.id.join_membership_btn:
                    OnJoinMembershipBtnClick();
                    break;
                case R.id.login_btn:
                    OnLoginBtnClick();
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        LoginFragment.BtnOnClickListener onClickListener = new LoginFragment.BtnOnClickListener();
        idInputEdit = (EditText)view.findViewById(R.id.login_page_id_input);
        pwInputEdit = (EditText)view.findViewById(R.id.login_page_pw_input);
        autoLogin = (CheckBox)view.findViewById(R.id.auto_login_checkbox);

        //옵션버튼
        Button btn = (Button)view.findViewById(R.id.find_id_btn);
        btn.setOnClickListener( onClickListener );

        btn = (Button)view.findViewById(R.id.find_pw_btn);
        btn.setOnClickListener( onClickListener );

        btn = (Button)view.findViewById(R.id.join_membership_btn);
        btn.setOnClickListener( onClickListener );

        btn = (Button)view.findViewById(R.id.login_btn);
        btn.setOnClickListener( onClickListener );

        mCallBack = new HttpTaskCallBack()
        {
            @Override
            public void doProcess(JSONObject jsonObj)
            {
                try
                {
                    if(jsonObj.get("packet_id").equals("login"))
                    {
                        if( jsonObj.get("result").equals("1") )
                        {
                            Toast.makeText( getContext(), "로그인 되었습니다." , Toast.LENGTH_SHORT ).show();
                            String token = (String)jsonObj.get("token");
                            HomeActivity.inst().setToken( token );

                            DataManager.inst().ParsingUserData( jsonObj );
                            String email = DataManager.inst().getUserData().getEmail();
                            HomeActivity.inst().setUserEmail( email );
                            HomeActivity.inst().SaveEmail( email );
                            HomeActivity.inst().ShowLoginMenu();
                            HomeActivity.inst().SetAdminPageBtn();
                            HomeActivity.inst().SaveAutoLogin( autoLogin.isChecked() );
                            NetworkManager.inst().RequestWriteList( getContext(), mCallBack, DataManager.inst().getUserData().getSeq() );
                            NetworkManager.inst().RequestWriteCommentCount( getContext(), mCallBack, DataManager.inst().getUserData().getSeq() );
                            if( HomeActivity.inst().getLoadFcmToken() == true )
                                HomeActivity.inst().SetFcmPush(true);
                        }
                        else if( jsonObj.get("result").equals("-101") )
                        {
                            HomeActivity.inst().ShowAlertDialog( "비밀 번호가 맞지 않습니다." );
                            loadingDialog.cancel();
                        }
                        else if( jsonObj.get("result").equals("0") )
                        {
                            HomeActivity.inst().ShowAlertDialog( "가입되지 않은 계정입니다." );
                            loadingDialog.cancel();
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("find_id"))
                    {
                        if( jsonObj.get("result").equals("1") )
                        {
                            String email = (String)jsonObj.get("email");
                            String msg = String.format( "회원님의 아이디는\n%s\n입니다.", email );
                            HomeActivity.inst().ShowAlertDialog( msg );
                        }
                        else if( jsonObj.get("result").equals("0") )
                        {
                            HomeActivity.inst().ShowAlertDialog( "가입되지 않은 계정입니다." );
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("find_pw"))
                    {
                        if( jsonObj.get("result").equals("1") )
                        {
                            HomeActivity.inst().ShowAlertDialog( "이메일로 회원님의\n비밀번호가\n발송 되었습니다." );
                        }
                        else if( jsonObj.get("result").equals("0") )
                        {
                            HomeActivity.inst().ShowAlertDialog( "가입되지 않은 계정입니다." );
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("write_list"))
                    {
                        loadingDialog.cancel();
                        if( jsonObj.get("result").equals("true") )
                        {
                            if(DataManager.inst().ParsingWriteList( jsonObj ) )
                            {

                            }

                            HomeActivity.inst().ChangeFragment( new TableContentsFragment(), "TableContentsFragment");
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
                }
                catch(JSONException e)
                {
                    e.printStackTrace();

                }
            }
        };

        return view;
    }

    void OnFindLoginBtnClick()
    {
        View popupView = getLayoutInflater().inflate(R.layout.dialog_find_id, null);
        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //popupView 에서 (LinearLayout 을 사용) 레이아웃이 둘러싸고 있는 컨텐츠의 크기 만큼 팝업 크기를 지정

        mPopupWindow.setFocusable(true);
        // 외부 영역 선택히 PopUp 종료

        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        findNameEdit = (EditText) popupView.findViewById(R.id.find_id_name_input);
        findPhoneEdit = (EditText) popupView.findViewById(R.id.find_id_phone_input);

        Button ok = (Button) popupView.findViewById(R.id.find_id_ok_btn);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                JSONObject jsonObj = new JSONObject();
                try
                {
                    jsonObj.put("packet_id", "find_id");
                    jsonObj.put("name", findNameEdit.getText() );
                    jsonObj.put("phone", findPhoneEdit.getText() );

                    ContentValues values = new ContentValues();
                    values.put("param", jsonObj.toString() );

                    String url = RequestHttpURLConnection.serverIp + "/find_id.php";
                    HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, getContext() );
                    httpConnectTask.SetCallBack(mCallBack);
                    httpConnectTask.execute();

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                mPopupWindow.dismiss();
            }
        });
    }

    void OnFindPasswordBtnClick()
    {
        View popupView = getLayoutInflater().inflate(R.layout.dialog_find_password, null);
        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //popupView 에서 (LinearLayout 을 사용) 레이아웃이 둘러싸고 있는 컨텐츠의 크기 만큼 팝업 크기를 지정

        mPopupWindow.setFocusable(true);
        // 외부 영역 선택히 PopUp 종료

        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        findPwEmailEdit = (EditText) popupView.findViewById(R.id.find_pw_email_input);
        findPwNameEdit = (EditText) popupView.findViewById(R.id.find_pw_name_input);

        Button ok = (Button) popupView.findViewById(R.id.find_pw_ok_btn);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObj = new JSONObject();
                try
                {
                    jsonObj.put("packet_id", "find_pw");
                    jsonObj.put("email", findPwEmailEdit.getText() );
                    jsonObj.put("name", findPwNameEdit.getText() );

                    ContentValues values = new ContentValues();
                    values.put("param", jsonObj.toString() );

                    String url = RequestHttpURLConnection.serverIp + "/find_pw.php";
                    HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, getContext() );
                    httpConnectTask.SetCallBack(mCallBack);
                    httpConnectTask.execute();

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                mPopupWindow.dismiss();
            }
        });
    }

    void OnJoinMembershipBtnClick()
    {
        HomeActivity.inst().ChangeFragment( new JoinMembershipFragment(), "JoinMembershipFragment");
    }

    void OnLoginBtnClick()
    {
        if( idInputEdit.getText().length() == 0 )
        {
            HomeActivity.inst().ShowAlertDialog( "아이디를 입력해 해주세요." );
            return;
        }

        if( pwInputEdit.getText().length() == 0 )
        {
            HomeActivity.inst().ShowAlertDialog( "비밀번호를 입력해 해주세요." );
            return;
        }

        SendLogin();
    }

    void SendLogin()
    {
        loadingDialog = Util.ShowLoading( getContext(), Z_TYPE.SINGLE_CIRCLE, false  );
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "login");
            jsonObj.put("email", idInputEdit.getText() );
            jsonObj.put("password", pwInputEdit.getText() );

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/login.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, getContext() );
            httpConnectTask.SetCallBack(mCallBack);
            httpConnectTask.execute();

        }
        catch (JSONException e)
        {
            loadingDialog.cancel();
            e.printStackTrace();
        }
    }
}
