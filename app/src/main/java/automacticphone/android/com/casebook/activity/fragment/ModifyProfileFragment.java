package automacticphone.android.com.casebook.activity.fragment;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
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
import automacticphone.android.com.casebook.activity.common.Define;
import automacticphone.android.com.casebook.activity.common.Util;
import automacticphone.android.com.casebook.activity.data.UserData;
import automacticphone.android.com.casebook.activity.network.HttpConnectTask;
import automacticphone.android.com.casebook.activity.network.HttpTaskCallBack;
import automacticphone.android.com.casebook.activity.network.RequestHttpURLConnection;

public class ModifyProfileFragment extends Fragment {

    private EditText passwordEdit1;
    private EditText passwordEdit2;
    private CheckBox maleCheckBox;
    private CheckBox feMaleCheckBox;
    private EditText phoneEdit;
    private EditText nameEdit;
    private EditText univEdit;
    private EditText birthEdit;
    private HttpTaskCallBack mCallBack = null;
    private TextView emailTextView;
    private ZLoadingDialog loadingDialog;

    public ModifyProfileFragment() {
        // Required empty public constructor
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view)
        {
            switch (view.getId()) {
                case R.id.modify_profile_btn:
                    OnProfileClickBtn();
                    break;
                case R.id.modify_profile_withdrawal_btn:
                    ShowWithdrawalPopup();
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_modify_profile, container, false);
        UserData userData = DataManager.inst().getUserData();
        emailTextView = view.findViewById( R.id.modify_profile_email_text );
        emailTextView.setText( userData.getEmail() );

        passwordEdit1 = view.findViewById( R.id.modify_profile_pw_edit);
        passwordEdit1.setText( userData.getPassword() );
        passwordEdit2 = view.findViewById( R.id.modify_profile_pw_edit2);
        passwordEdit2.setText( userData.getPassword() );
        maleCheckBox = view.findViewById( R.id.modify_profile_male_checkBox );
        feMaleCheckBox = view.findViewById( R.id.modify_profile_female_checkBox );
        phoneEdit = view.findViewById( R.id.modify_profile_phone_edit);
        phoneEdit.setText( userData.getPhone() );
        nameEdit = view.findViewById( R.id.modify_profile_name_edit);
        nameEdit.setText( userData.getName() );
        univEdit = view.findViewById( R.id.modify_profile_univ_edit);
        univEdit.setText( userData.getUniv() );
        birthEdit = view.findViewById( R.id.modify_profile_birth_edit);
        birthEdit.setText( userData.getBirth() );

        if( userData.getGender() == 0 )
            maleCheckBox.setChecked( true );
        else
            feMaleCheckBox.setChecked( true );

        maleCheckBox.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                feMaleCheckBox.setChecked( false );
            }
        }) ;

        feMaleCheckBox.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                maleCheckBox.setChecked( false );
            }
        });

        ModifyProfileFragment.BtnOnClickListener onClickListener = new ModifyProfileFragment.BtnOnClickListener();
        Button btn = view.findViewById( R.id.modify_profile_btn );
        btn.setOnClickListener( onClickListener );

        btn = view.findViewById( R.id.modify_profile_withdrawal_btn );
        btn.setOnClickListener( onClickListener );

        mCallBack = new HttpTaskCallBack()
        {
            @Override
            public void doProcess(JSONObject jsonObj)
            {
                try
                {
                    if(jsonObj.get("packet_id").equals("modify_profile"))
                    {
                        loadingDialog.cancel();
                        if( jsonObj.get("result").equals("true") )
                        {
                            DataManager.inst().UpdateUserProfile( jsonObj );
                            Toast.makeText( getContext(), "회원정보가 수정 되었습니다." , Toast.LENGTH_SHORT ).show();
                            HomeActivity.inst().ChangeFragment( new TableContentsFragment(), "TableContentsFragment");
                        }
                        else
                        {
                            if( jsonObj.get("result").equals(Define.ERROR_102 ) )
                            {
                                HomeActivity.inst().ShowAlertDialog( "이미 가입된 핸드폰 번호 입니다." );
                            }
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("delete_profile"))
                    {
                        loadingDialog.cancel();
                        if( jsonObj.get("result").equals("true") )
                        {
                            Toast.makeText( getContext(), "회원탈퇴가 완료 되었습니다." , Toast.LENGTH_SHORT ).show();
                            HomeActivity.inst().DeleteProfile();
                            HomeActivity.inst().ShowGuestMenu();
                            DataManager.inst().setUserData( null );
                            HomeActivity.inst().ChangeFragment( new TableContentsFragment(), "TableContentsFragment");
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

    public boolean CheckProfileInfo()
    {
        if( emailTextView.getText().length() == 0 )
        {
            return false;
        }

        if( passwordEdit1.getText().length() == 0 )
        {
            HomeActivity.inst().ShowAlertDialog("비밀번호를 입력해 주세요.");
            return false;
        }

        if( passwordEdit2.getText().length() == 0 )
        {
            HomeActivity.inst().ShowAlertDialog("비밀번호 확인을 입력해 주세요.");
            return false;
        }

        if( maleCheckBox.isChecked() == false &&  feMaleCheckBox .isChecked() == false )
        {
            HomeActivity.inst().ShowAlertDialog("성별을 선택해 주세요.");
            return false;
        }

        if( phoneEdit.getText().length() == 0 )
        {
            HomeActivity.inst().ShowAlertDialog("핸드폰을 입력해 주세요.");
            return false;
        }

        if( nameEdit.getText().length() == 0 )
        {
            HomeActivity.inst().ShowAlertDialog("이름을 입력해 주세요.");
            return false;
        }

        if( univEdit.getText().length() == 0 )
        {
            HomeActivity.inst().ShowAlertDialog("학교/업체/출신을 입력해 주세요.");
            return false;
        }

        if( birthEdit.getText().length() == 0 )
        {
            HomeActivity.inst().ShowAlertDialog("생년월일을 입력해 주세요.");
            return false;
        }
        String pw = passwordEdit1.getText().toString();
        String pw2 = passwordEdit2.getText().toString();
        if( pw.equals( pw2 ) == false )
        {
            HomeActivity.inst().ShowAlertDialog("비밀번호가 다릅니다.");
            return false;
        }

        return true;
    }

    public void OnProfileClickBtn()
    {
        if( CheckProfileInfo() == false )
            return;

        loadingDialog = Util.ShowLoading( getContext(), Z_TYPE.SINGLE_CIRCLE, false  );
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "modify_profile");
            jsonObj.put("seq", DataManager.inst().getUserData().getSeq() );
            jsonObj.put("name", nameEdit.getText() );
            jsonObj.put("password", passwordEdit1.getText() );
            if( maleCheckBox.isChecked() )
                jsonObj.put("gender", 0 );
            else
                jsonObj.put("gender", 1 );

            jsonObj.put("univ", univEdit.getText() );
            jsonObj.put("birth", birthEdit.getText() );
            jsonObj.put("phone", phoneEdit.getText() );

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/modify_profile.php";
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

    public void OnProfileWithdrawalBtn()
    {
        if( emailTextView.getText().length() == 0 )
            return;

        loadingDialog = Util.ShowLoading( getContext(), Z_TYPE.SINGLE_CIRCLE, false  );
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "delete_profile");
            String email = DataManager.inst().getUserData().getEmail();
            jsonObj.put("email", email );

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/delete_profile.php";
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

    private void ShowWithdrawalPopup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("");
        builder.setMessage("탈퇴하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        OnProfileWithdrawalBtn();
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
