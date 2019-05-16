package automacticphone.android.com.casebook.activity.fragment;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.labo.kaji.fragmentanimations.MoveAnimation;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.HomeActivity;
import automacticphone.android.com.casebook.activity.common.DataManager;
import automacticphone.android.com.casebook.activity.common.Define;
import automacticphone.android.com.casebook.activity.common.Util;
import automacticphone.android.com.casebook.activity.network.HttpConnectTask;
import automacticphone.android.com.casebook.activity.network.HttpTaskCallBack;
import automacticphone.android.com.casebook.activity.network.NetworkManager;
import automacticphone.android.com.casebook.activity.network.RequestHttpURLConnection;

public class JoinMembershipFragment extends Fragment
{
    private EditText emailEdit;
    private EditText passwordEdit1;
    private EditText passwordEdit2;
    private EditText phoneEdit;
    private EditText nameEdit;
    private EditText citizenEdit;
    private EditText birthEdit;

    private CheckBox maleCheckBox;
    private CheckBox femaleCheckBox;
    private CheckBox useCheckBox;
    private CheckBox personalCheckBox;
    private PopupWindow mPopupWindow;

    private HttpTaskCallBack mCallBack = null;
    private ZLoadingDialog loadingDialog;

    public JoinMembershipFragment() {
        // Required empty public constructor
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view)
        {
            switch (view.getId()) {
                case R.id.join_membership_certified_btn:
                    OnCertifiedBtnClick();
                    break;
                case R.id.join_membership_agreement_btn:
                    OnAgreementBtnClick();
                    break;
                case R.id.join_membership_apply_btn:
                    OnApplyBtnClick();
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_join_membership, container, false);
        emailEdit = (EditText)view.findViewById(R.id.join_membership_email_input);
        passwordEdit1 = (EditText)view.findViewById(R.id.join_membership_pw_input);
        passwordEdit2 = (EditText)view.findViewById(R.id.join_membership_pw_input2);
        phoneEdit = (EditText)view.findViewById(R.id.join_membership_phone_input);
        nameEdit = (EditText)view.findViewById(R.id.join_membership_name_input);
        citizenEdit = (EditText)view.findViewById(R.id.join_membership_citizen_input);
        birthEdit = (EditText)view.findViewById(R.id.join_membership_birth_input);
        maleCheckBox = (CheckBox)view.findViewById(R.id.join_membership_male_checkBox);
        femaleCheckBox = (CheckBox)view.findViewById(R.id.join_membership_female_checkBox);
        useCheckBox = (CheckBox)view.findViewById(R.id.join_membership_use_checkBox);
        personalCheckBox = (CheckBox)view.findViewById(R.id.join_membership_personal_checkBox);

        maleCheckBox.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                femaleCheckBox.setChecked( false );
            }
        }) ;

        femaleCheckBox.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                maleCheckBox.setChecked( false );
            }
        }) ;

        JoinMembershipFragment.BtnOnClickListener onClickListener = new JoinMembershipFragment.BtnOnClickListener();
        Button btn = (Button)view.findViewById(R.id.join_membership_certified_btn);
        btn.setOnClickListener(onClickListener);

        btn = (Button)view.findViewById(R.id.join_membership_agreement_btn);
        btn.setOnClickListener(onClickListener);

        btn = (Button)view.findViewById(R.id.join_membership_apply_btn);
        btn.setOnClickListener(onClickListener);

        mCallBack = new HttpTaskCallBack()
        {
            @Override
            public void doProcess(JSONObject jsonObj)
            {
                try
                {
                    if(jsonObj.get("packet_id").equals("email_certification"))
                    {
                        loadingDialog.cancel();
                        if( jsonObj.get("result").equals("true") )
                        {
                            HomeActivity.inst().ShowAlertDialog( "인증 메일이 발송 되었습니다." );
                        }
                        else if( jsonObj.get("result").equals( Define.ERROR_100 ) )
                        {
                            HomeActivity.inst().ShowAlertDialog( "이미 가입된 이메일 입니다." );
                        }
                    }
                    else if( jsonObj.get("packet_id").equals("add_join_membership") )
                    {
                        loadingDialog.cancel();
                        if( jsonObj.get("result").equals("true") )
                        {
                            String token = (String)jsonObj.get("token");
                            HomeActivity.inst().setToken( token );

                            DataManager.inst().ParsingUserData( jsonObj );
                            String email = DataManager.inst().getUserData().getEmail();
                            HomeActivity.inst().setUserEmail( email );
                            HomeActivity.inst().SaveEmail( email );
                            HomeActivity.inst().SaveAutoLogin( true );
                            HomeActivity.inst().ShowLoginMenu();
                            HomeActivity.inst().SetFcmPush( true );
                            NetworkManager.inst().RequestWriteList( getContext(), mCallBack, DataManager.inst().getUserData().getSeq() );
                            NetworkManager.inst().RequestWriteCommentCount( getContext(), mCallBack, DataManager.inst().getUserData().getSeq() );
                        }
                        else if( jsonObj.get("result").equals("-1") )
                        {
                            HomeActivity.inst().ShowAlertDialog( "이메일 인증을 해주세요." );
                        }
                        else if( jsonObj.get("result").equals("-101") )
                        {
                            HomeActivity.inst().ShowAlertDialog( "이미 가입 된 핸드폰 번호입니다." );
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("write_list"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            if (DataManager.inst().ParsingWriteList(jsonObj))
                            {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시", Locale.KOREA);
                                String msg = "전송자: AARK 사례집\n수신동의 일시 : %s\n처리내용 : 수신동의 처리완료\n\n(마이페이지 설정에서 변경가능)";
                                Date date = new Date();
                                msg = String.format( msg, sdf.format( date ) );
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle( "마케팅정보 앱 푸시 알림 동의 안내" );
                                builder.setMessage( msg );
                                builder.setPositiveButton("예",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                dialog.dismiss();     //닫기
                                                HomeActivity.inst().ChangeFragment( new TableContentsFragment(), "TableContentsFragment");
                                                HomeActivity.inst().ShowAlertDialog( "회원 가입이 완료 되었습니다." );
                                            }
                                        });
                                builder.setCancelable( false );
                                builder.show();
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

    private void OnCertifiedBtnClick()
    {
        loadingDialog = Util.ShowLoading( getContext(), Z_TYPE.SINGLE_CIRCLE, false  );
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "email_certification");
            jsonObj.put("email", emailEdit.getText() );
            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/email_certification.php";
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

    private void OnApplyBtnClick()
    {
        if( emailEdit.getText().length() == 0 )
        {
            HomeActivity.inst().ShowAlertDialog( "이메일 인증을 해주세요." );
            return;
        }

        if( passwordEdit1.getText().length() == 0 )
        {
            HomeActivity.inst().ShowAlertDialog( "1차 비밀번호를 넣어 주세요." );
            return;
        }

        if( passwordEdit2.getText().length() == 0 )
        {
            HomeActivity.inst().ShowAlertDialog( "2차 비밀번호를 넣어 주세요." );
            return;
        }

        if(!passwordEdit1.getText().toString().equals(passwordEdit2.getText().toString()) )
        {
            HomeActivity.inst().ShowAlertDialog( "비밀번호가 일치하지 않습니다." );
            return;
        }

/*
        if( passwordEdit1.getText() != passwordEdit2.getText())
        {
            HomeActivity.inst().ShowAlertDialog( "비밀번호가 일치하지 않습니다." );
            return;
        }*/

        if( maleCheckBox.isChecked() == false && femaleCheckBox.isChecked() == false )
        {
            HomeActivity.inst().ShowAlertDialog( "성별을 선택 해 주세요." );
            return;
        }

        if( phoneEdit.getText().length() == 0 )
        {
            HomeActivity.inst().ShowAlertDialog( "핸드폰을 입력 해 주세요." );
            return;
        }

        if( nameEdit.getText().length() == 0 )
        {
            HomeActivity.inst().ShowAlertDialog( "이름을 입력 해 주세요." );
            return;
        }

        if( citizenEdit.getText().length() == 0 )
        {
            HomeActivity.inst().ShowAlertDialog( "학교(동아리)/업체/출신을 입력 해주세요." );
            return;
        }

        if( birthEdit.getText().length() == 0 )
        {
            HomeActivity.inst().ShowAlertDialog( "생년월일을 입력 해 주세요." );
            return;
        }

        if( useCheckBox.isChecked() == false )
        {
            HomeActivity.inst().ShowAlertDialog( "이용 규정 동의를 체크해 주세요." );
            return;
        }

        if( personalCheckBox.isChecked() == false )
        {
            HomeActivity.inst().ShowAlertDialog( "개인 정보 동의를 체크해 주세요." );
            return;
        }

        SendJoinMemberShip();
    }

    private void SendJoinMemberShip()
    {
        loadingDialog = Util.ShowLoading( getContext(), Z_TYPE.SINGLE_CIRCLE, false  );
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "add_join_membership");
            jsonObj.put("name", nameEdit.getText() );
            jsonObj.put("email", emailEdit.getText() );
            jsonObj.put("password", passwordEdit1.getText() );
            if( maleCheckBox.isChecked() )
                jsonObj.put("gender", 0 );
            else
                jsonObj.put("gender", 1 );

            jsonObj.put("univ", citizenEdit.getText() );
            jsonObj.put("grade", Define.GRADE_STUDENT );                       //기본 0번 학생으로 가입
            jsonObj.put("birth", birthEdit.getText() );
            jsonObj.put("phone", phoneEdit.getText() );

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/add_join_membership.php";
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

    private void OnAgreementBtnClick()
    {
        View popupView = getLayoutInflater().inflate(R.layout.access_terms_popup, null);
        mPopupWindow = new PopupWindow(popupView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        //popupView 에서 (LinearLayout 을 사용) 레이아웃이 둘러싸고 있는 컨텐츠의 크기 만큼 팝업 크기를 지정

        mPopupWindow.setFocusable(true);
        // 외부 영역 선택히 PopUp 종료

        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        TextView textView = popupView.findViewById(R.id.access_terms_popup_personal_text );
        textView.setMovementMethod( new ScrollingMovementMethod() );

        textView = popupView.findViewById(R.id.access_terms_popup_access_term_text );
        textView.setMovementMethod( new ScrollingMovementMethod() );

        Button closeBtn = (Button) popupView.findViewById(R.id.access_terms_close_btn);
        closeBtn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
    }
}
