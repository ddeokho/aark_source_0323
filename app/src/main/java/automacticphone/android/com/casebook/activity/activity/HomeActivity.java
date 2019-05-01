package automacticphone.android.com.casebook.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.adapter.RegulationMainAdapter;
import automacticphone.android.com.casebook.activity.common.DataManager;
import automacticphone.android.com.casebook.activity.common.Define;
import automacticphone.android.com.casebook.activity.common.PermissionDeniedCallback;
import automacticphone.android.com.casebook.activity.common.PermissionManager;
import automacticphone.android.com.casebook.activity.data.CaseData;
import automacticphone.android.com.casebook.activity.data.PromoteRegisterData;
import automacticphone.android.com.casebook.activity.data.PromotionData;
import automacticphone.android.com.casebook.activity.data.SubTreeCaseData;
import automacticphone.android.com.casebook.activity.data.UserData;
import automacticphone.android.com.casebook.activity.fragment.AdminPageFragment;
import automacticphone.android.com.casebook.activity.fragment.BoardFragment;
import automacticphone.android.com.casebook.activity.fragment.ContentsViewFragment;
import automacticphone.android.com.casebook.activity.fragment.JoinMembershipFragment;
import automacticphone.android.com.casebook.activity.fragment.LoginFragment;
import automacticphone.android.com.casebook.activity.fragment.ModifyProfileFragment;
import automacticphone.android.com.casebook.activity.fragment.MyPageFragment;
import automacticphone.android.com.casebook.activity.fragment.PromoteAddressFragment;
import automacticphone.android.com.casebook.activity.fragment.PromoteContentsFragment;
import automacticphone.android.com.casebook.activity.fragment.PromoteFragment;
import automacticphone.android.com.casebook.activity.fragment.PromoteListFragment;
import automacticphone.android.com.casebook.activity.fragment.PromoteRegisterFragment;
import automacticphone.android.com.casebook.activity.fragment.PromoteRegisterFragment2;
import automacticphone.android.com.casebook.activity.fragment.QuestionsFragment;
import automacticphone.android.com.casebook.activity.fragment.TableContentsFragment;
import automacticphone.android.com.casebook.activity.fragment.UploadContentsFragment;
import automacticphone.android.com.casebook.activity.fragment.annonceFragment;
import automacticphone.android.com.casebook.activity.fragment.reguViewFragment;
import automacticphone.android.com.casebook.activity.network.HttpConnectTask;
import automacticphone.android.com.casebook.activity.network.HttpTaskCallBack;
import automacticphone.android.com.casebook.activity.network.NetworkManager;
import automacticphone.android.com.casebook.activity.network.RequestHttpURLConnection;

public class HomeActivity extends AppCompatActivity
{
    /**
     * 권한 관리자
     */
    private PermissionManager mPermissionManager = null;
    public static  final int PICTURE_REQUEST_CODE = 100;
    public static final int REQUEST_IMAGE_CAPTURE = 672;

    private volatile static HomeActivity _instance;
    private DrawerLayout drawerLayout;
    private ImageView selectBarImg;
    private ArrayList<Button> homeMenuBtnList = new ArrayList<Button>();
    private ArrayList<View> loginMenuList = new ArrayList<View>();
    private ArrayList<View> guestMenuList = new ArrayList<View>();
    private TextView emailText;
    private HttpTaskCallBack mCallBack = null;
    private EditText searchEdit;
    private int caseRequestDataCount = 0;

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    private String searchText = "";
    private boolean pushAlarm;
    public boolean isPushAlarm() {
        return pushAlarm;
    }

    public void setPushAlarm(boolean pushAlarm)
    {
        this.pushAlarm = pushAlarm;
    }

    public void setUserEmail(String userEmail)
    {
        emailText.setText( userEmail );
        this.userEmail = userEmail;
    }

    private String userEmail = "";
    public SharedPreferences settings;
    public String getToken() {
        return token;
    }
    private InputMethodManager imm;
    private PopupWindow mPopupWindow;
    private SubTreeCaseData selectSubTreeCaseData = null;
    public SubTreeCaseData getSelectSubTreeCaseData() {
        return selectSubTreeCaseData;
    }

    public void setSelectSubTreeCaseData(SubTreeCaseData selectSubTreeCaseData) {
        this.selectSubTreeCaseData = selectSubTreeCaseData;
    }

    public void SaveAlarm( boolean pushAlarm )
    {
        setPushAlarm( pushAlarm );
        settings = getSharedPreferences("settings", Activity.MODE_PRIVATE );
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("push", pushAlarm );

        editor.commit();
    }

    public void SaveEmail( String email )
    {
        settings = getSharedPreferences("settings", Activity.MODE_PRIVATE );
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("email", email);

        editor.commit();
    }

    void SaveToken( String token )
    {
        settings = getSharedPreferences("settings", Activity.MODE_PRIVATE );
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("token", token);

        editor.commit();
    }

    public void SaveAutoLogin( boolean auto )
    {
        settings = getSharedPreferences("settings", Activity.MODE_PRIVATE );
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("auto_login", auto);

        editor.commit();
    }

    void loadPushAlarm()
    {
        SharedPreferences sf = getSharedPreferences("settings", 0);
        pushAlarm = sf.getBoolean("push", true );
    }

    public void SetFcmPush( boolean bAlarm )
    {
        if( bAlarm )
        {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(  this,  new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String mToken = instanceIdResult.getToken();
                    Log.e("FCM_Token = > ",mToken);
                    NetworkManager.inst().RegisterFcmToken( HomeActivity.this, mCallBack, mToken );
                }
            });
        }
        else
        {
            NetworkManager.inst().DeleteFcmToken(this, mCallBack );
        }
    }

    String getLoadEmail()
    {
        SharedPreferences sf = getSharedPreferences("settings", 0);
        String email = sf.getString("email", "");

        return email;
    }

    String getLoadToken()
    {
        SharedPreferences sf = getSharedPreferences("settings", 0);
        String token = sf.getString("token", "");

        return token;
    }

    public boolean getLoadFcmToken()
    {
        SharedPreferences sf = getSharedPreferences("settings", 0);
        boolean token = sf.getBoolean("fcm_token", true );

        return token;
    }

    boolean getAutoLogin()
    {
        SharedPreferences sf = getSharedPreferences("settings", 0);
        boolean bAuto  = sf.getBoolean("auto_login", false );

        return bAuto;
    }

    public void setToken(String token)
    {
        SaveToken( token );
        this.token = token;
    }

    private String token = "";
    private boolean fcmToken = true;

    public void setFcmToken(boolean fcmToken)
    {
        settings = getSharedPreferences("settings", Activity.MODE_PRIVATE );
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("fcm_token", fcmToken);
        this.fcmToken = fcmToken;

        editor.commit();
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    private Fragment currentFragment;

    public String getCurrentFragmentTag() {
        return currentFragmentTag;
    }

    private String currentFragmentTag;
    private String prevFragmentTag;

    public  static HomeActivity inst()
    {
        if(_instance ==null)
        {
            synchronized (HomeActivity.class)
            {
                if(_instance == null)
                {
                    _instance = new HomeActivity();
                }
            }
        }
        return _instance;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if( CheckAppFirstExecute() == false )
        {
            ShowPermissionPopup();
        }
        else
        {
            PermissionCheck();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } catch (IllegalStateException ignore) {

        }

        imm = (InputMethodManager)getSystemService( INPUT_METHOD_SERVICE );
        caseRequestDataCount = 0;
        loadPushAlarm();
        //SetFcmPush( pushAlarm );           //FCM 알림 설정
        _instance = this;
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        selectBarImg = (ImageView)findViewById(R.id.home_menu_bar_img);
        emailText = (TextView)findViewById(R.id.home_login_email_text);
        searchEdit = (EditText) findViewById(R.id.home_search_edit);

        SetBtnsListener();
        SetDrawerMenuList();
        mCallBack = new HttpTaskCallBack()
        {
            @Override
            public void doProcess(JSONObject jsonObj)
            {
                try
                {
                    if(jsonObj.get("packet_id").equals("user_data"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            DataManager.inst().ParsingUserData( jsonObj );
                            NetworkManager.inst().RequestWriteList( getApplicationContext(), mCallBack, DataManager.inst().getUserData().getSeq() );
                            NetworkManager.inst().RequestWriteCommentCount( getApplicationContext(), mCallBack, DataManager.inst().getUserData().getSeq() );
                        }
                        else
                        {
                            ShowGuestMenu();
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("write_list"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            if(DataManager.inst().ParsingWriteList( jsonObj ) )
                            {

                            }
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("case_search_list") )
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            DataManager.inst().ClearCaseDataList();
                            if( DataManager.inst().ParsingCaseData( jsonObj ) )
                            {
                                DataManager.inst().setSelectCaseData( null );
                                BoardFragment.selectTab = 0;
                                BoardFragment fragment = new BoardFragment();
                                fragment.setSearchText( searchText );
                                ChangeFragment( fragment, "BoardFragment");
                            }
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("register_fcm_token") )
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            setFcmToken( true );
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("delete_fcm_token") )
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            setFcmToken( false );
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("case_text_seq"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            CaseData data = DataManager.inst().ParsingUpdateCaseData( jsonObj );
                            if(  data != null )
                            {
                                DataManager.inst().setSelectCaseData( data );
                                NetworkManager.inst().RequestContentOwnerData( HomeActivity.this, mCallBack, data.getMember_seq() );
                                NetworkManager.inst().RequestCommentData( HomeActivity.this, mCallBack, data.getSeq(), 0, 5 );
                                NetworkManager.inst().UpdateCaseContentFeq( HomeActivity.this, mCallBack, data.getSeq(), data.getFeq() + 1 );
                            }
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("content_owner_data"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            DataManager.inst().ParsingContentOwnerData( jsonObj );
                        }
                        else
                            DataManager.inst().setContentOwnerData( null );

                        caseRequestDataCount++;
                    }
                    else if(jsonObj.get("packet_id").equals("comment_data"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            DataManager.inst().getCommentDataList().clear();
                            if( DataManager.inst().ParsingCommentData( jsonObj ) )
                            {
                                caseRequestDataCount++;
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
                            caseRequestDataCount++;
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
                    else if(jsonObj.get("packet_id").equals("promotion_search_list"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            DataManager.inst().ClearPromotionDataList();
                            if(DataManager.inst().ParsingPromotionData( jsonObj ) )
                            {
                                PromoteListFragment fragment = new PromoteListFragment();
                                fragment.setSearchText( searchText );
                                ChangeFragment( fragment, "PromoteListFragment" );
                            }
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("logout"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            NetworkManager.inst().DeleteFcmToken(HomeActivity.this, null );
                            Toast.makeText( HomeActivity.this, "로그아웃 되었습니다." , Toast.LENGTH_SHORT ).show();
                            HomeActivity.inst().DeleteProfile();
                            HomeActivity.inst().ShowGuestMenu();
                            DataManager.inst().setUserData( null );
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("promotion_data_seq"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            if( DataManager.inst().ParsingUpdatePromotionData( jsonObj ) )
                            {
                                int seq = Integer.parseInt( jsonObj.get("seq").toString() );
                                PromotionData data = DataManager.inst().getPromotionData( seq );
                                DataManager.inst().setSelectPromotionData(data);
                                RequestPromoteSubData(data);

                                //경고지역 minSDK문제 있을 수 있음
                               /* PromoteContentsFragment fragment = new PromoteContentsFragment();
                                fragment.setPromotionData( data );
                                HomeActivity.inst().ChangeFragment( fragment, "PromoteContentsFragment");*/
                            }
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("promotion_data"))
                    {
                        ChangeFragment(new PromoteListFragment(), "PromoteListFragment");
                        if( jsonObj.get("result").equals("true") ) {
                            DataManager.inst().ClearPromotionDataList();
                            if (DataManager.inst().ParsingPromotionData(jsonObj)) {
                                //ChangeFragment(new PromoteListFragment(), "PromoteListFragment");
                            }
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("promotion_sub_data"))
                    {
                        if(jsonObj.get("result").equals("true"))
                        {
                            if(DataManager.inst().ParsingPromotionSubData(jsonObj))
                            {
                                PromotionData selectPromotionData = DataManager.inst().getSelectPromotionData();
                                NetworkManager.inst().UpdatePromotionFeq(HomeActivity.this, mCallBack, selectPromotionData.getSeq(), selectPromotionData.getFeq()+1);
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
                            PromotionData selectPromotionData = DataManager.inst().getSelectPromotionData();
                            fragment.setPromotionData( selectPromotionData );
                            HomeActivity.inst().ChangeFragment( fragment, "PromoteContentsFragment");
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("case_year_list"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            if(DataManager.inst().ParsingCaseYearList( jsonObj ) )
                            {
                                if( currentFragmentTag.equals( "TableContentsFragment" ) )
                                {
                                    TableContentsFragment fragment = (TableContentsFragment)currentFragment;
                                    fragment.SetYearList( DataManager.inst().getCaseYearList() );
                                }

                                drawerLayout.closeDrawers();
                                ChangeFragment( new TableContentsFragment(), "TableContentsFragment");
                            }
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("case_data_lastest"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            DataManager.inst().ClearCaseDataList();
                            if(DataManager.inst().ParsingCaseData( jsonObj ) )
                            {
                                setSelectSubTreeCaseData( null );
                                ChangeFragment(new BoardFragment(), "BoardFragment");
                            }
                        }
                    }

                    //공지
                    if(jsonObj.get("packet_id").equals("announce_data"))
                    {
                        if(jsonObj.get("result").equals("true"))
                        {

                            if(DataManager.inst().ParsingAnnounceData(jsonObj))
                            {
                                ChangeFragment(new annonceFragment(), "announceFragment");
                            }
                        }
                    }

                    if( caseRequestDataCount == 3 )
                    {
                        ContentsViewFragment fragment = new ContentsViewFragment();
                        HomeActivity.inst().ChangeFragment( fragment, "ContentsViewFragment");
                    }
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        DataManager.inst().setSelectYear( year );
        AddFragment( new TableContentsFragment(), "TableContentsFragment");

        if( IsAutoLogin() )
        {
            String email = getLoadEmail();
            setUserEmail( email );
            ShowLoginMenu();
            RequestUserData( email );
        }
        else
            ShowGuestMenu();

        CreateRegulationMenuItem();

        int kakaoCaseSeq = DataManager.inst().getKakaoBoardSeq();
        int kakaoPromotionSeq = DataManager.inst().getKakaoPromotionSeq();
        if( kakaoCaseSeq > -1 )
        {
            NetworkManager.inst().RequestCaseDataSeq( HomeActivity.this, mCallBack, kakaoCaseSeq );
        }
        else if( kakaoPromotionSeq > -1 )
        {
            NetworkManager.inst().RequestPromotionDataSeq( HomeActivity.this, mCallBack, kakaoPromotionSeq );
        }
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view)
        {
            switch (view.getId()) {
                case R.id.home_menu_btn:
                {
                    SetDrawerUserInfo();
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
                break;
                case R.id.home_btn:
                    OnHomeBtnClick();
                    break;
                case R.id.search_btn:
                    OnSearchBtnClick();
                    break;
                case R.id.upload_btn:
                    OnUploadBtnClick();
                    break;
                case R.id.promote_btn:
                    OnPromoteBtnClick();
                    break;
                case R.id.my_page_btn:
                    OnMyPageBtnClick();
                    break;
                case R.id.logout_btn:
                    OnLogoutBtnClick();
                    break;
                case R.id.login_btn:
                    OnLoginBtnClick();
                    break;
                case R.id.join_membership_btn:
                    OnJoinMemberShipBtnClick();
                    break;
                case R.id.plan_case_btn:
                    break;

                case R.id.regu_btn:
                    ReguPopupBtnClick();
                    break;

                case R.id.youtube_btn:
                    OnYoutubeBtnClick();
                    break;

                    //공지사항 버튼
                case R.id.announce_btn:
                    OnAnnounceBtnClick();
                    break;

                case R.id.Inquiry_btn:
                    OnInquiryBtnClick();
                    break;
                case R.id.modify_profile_btn:
                    OnModifyProfileBtnClick();
                    break;
                case R.id.admin_page_btn:
                    OnAdminPageBtnClick();
                    break;
                case R.id.home_search_btn:
                    OnTopSearchBtnClick();
                    break;
            }
        }
    }

    public void SetDrawerUserInfo()
    {
        if( DataManager.inst().getUserData() == null )
            return;

        int seq = DataManager.inst().getUserData().getSeq();
        int writeCount = DataManager.inst().getMyWriteCount( seq );
        TextView textView = drawerLayout.findViewById( R.id.author_num_text );
        textView.setText( String.valueOf( writeCount ) );

        int commentCount = DataManager.inst().getWriteCommentCount();
        textView = drawerLayout.findViewById( R.id.comments_num_text );
        textView.setText( String.valueOf( commentCount  ) );
    }

    private void SetBtnsListener()
    {
        BtnOnClickListener onClickListener = new BtnOnClickListener();
        //옵션버튼
        Button btn = (Button)findViewById(R.id.home_menu_btn);
        btn.setOnClickListener( onClickListener );

        btn = (Button)findViewById(R.id.home_btn);
        btn.setOnClickListener( onClickListener );
        homeMenuBtnList.add( btn );

        btn = (Button)findViewById(R.id.search_btn);
        btn.setOnClickListener( onClickListener );
        homeMenuBtnList.add( btn );

        btn = (Button)findViewById(R.id.upload_btn);
        btn.setOnClickListener( onClickListener );
        homeMenuBtnList.add( btn );

        btn = (Button)findViewById(R.id.promote_btn);
        btn.setOnClickListener( onClickListener );
        homeMenuBtnList.add( btn );

        btn = (Button)findViewById(R.id.my_page_btn);
        btn.setOnClickListener( onClickListener );
        homeMenuBtnList.add( btn );

        //드로우 메뉴 버튼들.
        btn = (Button)findViewById(R.id.login_btn);
        btn.setOnClickListener( onClickListener );

        btn = (Button)findViewById(R.id.logout_btn);
        btn.setOnClickListener( onClickListener );

        btn = (Button)findViewById(R.id.join_membership_btn);
        btn.setOnClickListener( onClickListener );

        btn = (Button)findViewById(R.id.plan_case_btn);
        btn.setOnClickListener( onClickListener );

        btn = (Button)findViewById(R.id.regu_btn);
        btn.setOnClickListener( onClickListener);

        btn = (Button)findViewById(R.id.youtube_btn);
        btn.setOnClickListener( onClickListener);

        //공지사항버튼
        btn = (Button)findViewById(R.id.announce_btn) ;
        btn.setOnClickListener(onClickListener);

        btn = (Button)findViewById(R.id.Inquiry_btn);
        btn.setOnClickListener( onClickListener );

        btn = (Button)findViewById(R.id.modify_profile_btn);
        btn.setOnClickListener( onClickListener );

        btn = (Button)findViewById(R.id.admin_page_btn);
        btn.setOnClickListener( onClickListener );
        SetAdminPageBtn();

        btn = (Button)findViewById(R.id.home_search_btn);
        btn.setOnClickListener( onClickListener );
    }

    public void SetAdminPageBtn()
    {
        UserData userData = DataManager.inst().getUserData();
        Button btn = (Button)findViewById(R.id.admin_page_btn);
        if( userData != null && userData.getGrade() == Define.GRADE_ADMIN )
            btn.setVisibility( View.VISIBLE );
        else
            btn.setVisibility( View.INVISIBLE );
    }

    //화면 중앙뷰에 플래그먼트 추가.
    public void AddFragment(Fragment fragment, String fragmentTag )
    {
        currentFragment = fragment;
        currentFragmentTag = fragmentTag;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add( R.id.home_main_view, fragment, fragmentTag );
        fragmentTransaction.commit();
    }

    public boolean ChangeFragment( Fragment fragment, String fragmentTag )
    {
        prevFragmentTag = currentFragmentTag;
        SetHomeBtnPos( fragmentTag );
        currentFragment = fragment;
        currentFragmentTag = fragmentTag;
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.home_main_view, fragment, fragmentTag);
        fragmentTransaction.commitAllowingStateLoss();

        return true;
    }

    public void SetHomeBtnPos( String fragmentName )
    {
        switch ( fragmentName )
        {
            case "TableContentsFragment":
            case "QuestionsFragment":
            case "JoinMembershipFragment":
            case "LoginFragment":
            case "ModifyProfileFragment":
                selectBarImg.setX( homeMenuBtnList.get(0).getX() );
                break;
            case "BoardFragment":
            case "ContentsViewFragment":
                selectBarImg.setX( homeMenuBtnList.get(1).getX() );
                break;
            case "UploadContentsFragment":
                selectBarImg.setX( homeMenuBtnList.get(2).getX() );
                break;
            case "PromoteFragment":
            case "PromoteListFragment":
            case "PromoteRegisterFragment":
            case "PromoteRegisterFragment2":
            case "PromoteAddressFragment":
            case "PromoteContentsFragment":
                selectBarImg.setX( homeMenuBtnList.get(3).getX() );
                break;
            case "MyPageFragment":
            case "AdminPageFragment":
                selectBarImg.setX( homeMenuBtnList.get(4).getX() );
                break;
        }

    }

    public void ShowAlertDialog( String msg )
    {
        AlertDialog.Builder alert = new AlertDialog.Builder( this );
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });
        alert.setMessage( msg );
        alert.show();
    }

    void SetDrawerMenuList()
    {
        //로그인
        View v = (View)findViewById(R.id.home_login_image);
        loginMenuList.add(v);

        v = (View)findViewById(R.id.home_login_email_text);
        loginMenuList.add(v);

        v = (View)findViewById(R.id.home_login_author_text);
        loginMenuList.add(v);

        v = (View)findViewById(R.id.logout_btn );
        loginMenuList.add( v );

        v = (View)findViewById(R.id.author_num_text);
        loginMenuList.add(v);

        v = (View)findViewById(R.id.comments_count_text);
        loginMenuList.add(v);

        v = (View)findViewById(R.id.comments_num_text);
        loginMenuList.add(v);

        v = (View)findViewById(R.id.modify_profile_btn);
        loginMenuList.add(v);

        //비로그인
        v = (View)findViewById(R.id.home_guest_img);
        guestMenuList.add(v);

        v = (View)findViewById(R.id.home_guest_img2);
        guestMenuList.add(v);

        v = (View)findViewById(R.id.home_guest_text1);
        guestMenuList.add(v);

        v = (View)findViewById(R.id.login_btn);
        guestMenuList.add(v);

        v = (View)findViewById(R.id.join_membership_btn);
        guestMenuList.add(v);

    }
    public void ShowLoginMenu()
    {
        for( int i = 0; i < loginMenuList.size(); ++i )
        {
            loginMenuList.get(i).setVisibility( View.VISIBLE );
        }

        for( int i = 0; i < guestMenuList.size(); ++i )
        {
            guestMenuList.get(i).setVisibility( View.INVISIBLE );
        }
    }

    public void ShowGuestMenu()
    {
        for( int i = 0; i < guestMenuList.size(); ++i )
        {
            guestMenuList.get(i).setVisibility( View.VISIBLE );
        }

        for( int i = 0; i < loginMenuList.size(); ++i )
        {
            loginMenuList.get(i).setVisibility( View.INVISIBLE );
        }
    }

    void CreateRegulationMenuItem()
    {
        RegulationMainAdapter adapter = new RegulationMainAdapter( getBaseContext(), DataManager.inst().getRegulMainDataList() );
        ListView regulationView = (ListView) findViewById(R.id.home_menu_case_listview);
        regulationView.setDivider( null );
        regulationView.setAdapter( adapter);

        regulationView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                int cate_reg = DataManager.inst().getRegulMainDataList().get(position).getSeq();
                DataManager.inst().setSelectCateReg( cate_reg );
                NetworkManager.inst().RequestCaseYearList( HomeActivity.this, mCallBack, cate_reg );
            }
        });
    }

    boolean IsAutoLogin()
    {
        boolean bAutoLogin = getAutoLogin();
        if( bAutoLogin == false )
            return false;

        String token = getLoadToken();
        if( token.length() == 0 )
            return false;

        return true;
    }

    void RequestUserData( String email )
    {
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "user_data");
            jsonObj.put("email", email );
            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/user_data.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, this);
            httpConnectTask.SetCallBack(mCallBack);
            httpConnectTask.execute();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    void OnHomeBtnClick()
    {
        selectBarImg.setX( homeMenuBtnList.get(0).getX() );
        ChangeFragment(new TableContentsFragment(), "TableContentsFragment");
    }

    void OnSearchBtnClick()
    {
        selectBarImg.setX( homeMenuBtnList.get(1).getX() );
        //setSearchMainBtnClicked( true );
        DataManager.inst().ClearCaseDataList();
        NetworkManager.inst().RequestCaseData( HomeActivity.this, mCallBack, "case_data_lastest", 0, 0, 0, 0, 0, Define.GOOD_CASE, 0, Define.MAX_CASE_DATA );
    }

    void OnUploadBtnClick()
    {
        if( DataManager.inst().getUserData() != null )
        {
            if( DataManager.inst().getUserData().getGrade() == Define.GRADE_ADMIN  )//|| DataManager.inst().getUserData().getGrade() == Define.GRADE_GRADUATE 관리자만 못하도록 막음
            {
                ShowAlertDialog( "관리자는 사례글을 작성할 수 없습니다." );
                return;
            }
            DataManager.inst().setSelectCaseData( null );
            selectBarImg.setX( homeMenuBtnList.get(2).getX() );
            ChangeFragment(new UploadContentsFragment(), "UploadContentsFragment");
        }
        else
        {
            ShowAlertDialog( "사례글은 로그인 후 작성 가능합니다." );
        }
    }

    void OnPromoteBtnClick()
    {
        selectBarImg.setX( homeMenuBtnList.get(3).getX() );
        DataManager.inst().ClearCaseDataList();
        NetworkManager.inst().RequestPromotionData( HomeActivity.this, mCallBack, "promotion_data", Define.PROMOTION_STUDENT, 0, Define.MAX_CASE_DATA );
    }

    void OnMyPageBtnClick()
    {
        selectBarImg.setX( homeMenuBtnList.get(4).getX() );
        ChangeFragment(new MyPageFragment(), "MyPageFragment");
    }

    void OnLogoutBtnClick()
    {
        NetworkManager.inst().Logout( HomeActivity.this, mCallBack );
    }

    void OnLoginBtnClick()
    {
        drawerLayout.closeDrawers();
        ChangeFragment(new LoginFragment(), "LoginFragment");
    }

    void OnJoinMemberShipBtnClick()
    {
        drawerLayout.closeDrawers();
        ChangeFragment(new JoinMembershipFragment(), "JoinMembershipFragment");
    }

    //유투브
    void OnYoutubeBtnClick()
    {
        drawerLayout.closeDrawers();
        Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UCbDMBZg-GwskhUNX5by5E3w/featured"));
        startActivity(i);
    }

    //공지사항
    void OnAnnounceBtnClick(){
        DataManager.inst().ClearAnnounceDataList();
        drawerLayout.closeDrawers();
        NetworkManager.inst().RequestAnnounceData(HomeActivity.this, mCallBack, "announce_data",0, 15);
    }

    //규정집 팝업 이동
    void ReguPopupBtnClick()
    {
        drawerLayout.closeDrawers();
        ChangeFragment(new reguViewFragment(), "reguViewFragment");


        /*        View popupView = getLayoutInflater().inflate(R.layout.select_regu_popup, null);
        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //popupView 에서 (LinearLayout 을 사용) 레이아웃이 둘러싸고 있는
        //        mPopupWindow.setFocusable(true); 컨텐츠의 크기 만큼 팝업 크기를 지정

        // 외부 영역 선택히 PopUp 종료

        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        Button KsaeFormulaBtn = (Button) popupView.findViewById(R.id.select_ksae_formula_btn);
        KsaeFormulaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://aarkapp.iptime.org/reg_file/KSAE_Formula.pdf"));
                startActivity(i);

            }
        });

        Button KsaeBajaBtn = (Button) popupView.findViewById(R.id.select_ksae_baja_btn);
        KsaeBajaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://aarkapp.iptime.org/reg_file/KSAE_BAJA.pdf"));
                startActivity(i);

            }
        });

        Button KsaeEvBtn = (Button) popupView.findViewById(R.id.select_ksae_ev_btn);
        KsaeEvBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://aarkapp.iptime.org/reg_file/KSAE_EV.pdf"));
                startActivity(i);
            }
        });

        Button SaeBajaBtn = (Button) popupView.findViewById(R.id.select_sae_baja_btn);
        SaeBajaBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://aarkapp.iptime.org/reg_file/SAE_BAJA.pdf"));
                startActivity(i);
            }
        });


        Button cancelBtn = (Button) popupView.findViewById(R.id.select_regu_cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });*/
    }


    void OnInquiryBtnClick()
    {
        drawerLayout.closeDrawers();
        ChangeFragment(new QuestionsFragment(), "QuestionsFragment");
    }

    void OnModifyProfileBtnClick()
    {
        drawerLayout.closeDrawers();
        ChangeFragment(new ModifyProfileFragment(), "ModifyProfileFragment");
    }

    void OnAdminPageBtnClick()
    {
        drawerLayout.closeDrawers();
        ChangeFragment(new AdminPageFragment(), "AdminPageFragment");
    }

    void OnTopSearchBtnClick()
    {
        searchText = searchEdit.getText().toString().trim();
        if( searchText.length() == 0 )
            return;

        if( currentFragmentTag.equals( "TableContentsFragment" ) || currentFragmentTag.equals( "BoardFragment" ) || currentFragmentTag.equals( "ContentsViewFragment" ) )
        {
            //String searchText = searchEdit.getText().toString();
            JSONObject jsonObj = new JSONObject();
            try
            {
                jsonObj.put("packet_id", "case_search_list");
                jsonObj.put("search_text", searchText );
                jsonObj.put("grade", Define.GOOD_CASE );

                ContentValues values = new ContentValues();
                values.put("param", jsonObj.toString() );

                String url = RequestHttpURLConnection.serverIp + "/case_search_list.php";
                HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, this );
                httpConnectTask.SetCallBack(mCallBack);
                httpConnectTask.execute();

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }
        else if( currentFragmentTag.equals( "PromoteListFragment" ) )
        {
            //String searchText = searchEdit.getText().toString();
            PromoteListFragment fragment = (PromoteListFragment)currentFragment;
            fragment.ProcessSearch( searchText );
        }
        else if( currentFragmentTag.equals( "PromoteContentsFragment" ) )
        {
            //String searchText = searchEdit.getText().toString();
            NetworkManager.inst().RequestPromotionSearchList( HomeActivity.this, mCallBack, "promotion_search_list", Define.PROMOTION_STUDENT, searchText, 0, Define.MAX_CASE_DATA );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( currentFragmentTag.equals( "UploadContentsFragment" ))
        {
            ((UploadContentsFragment)currentFragment).onUploadContentsResult( requestCode, resultCode, data );
        }
        else if( currentFragmentTag.equals( "PromoteRegisterFragment" ))
        {
            ((PromoteRegisterFragment)currentFragment).onUploadContentsResult( requestCode, resultCode, data );
        }
        else if( currentFragmentTag.equals( "PromoteRegisterFragment2" ))
        {
            ((PromoteRegisterFragment2)currentFragment).onUploadContentsResult( requestCode, resultCode, data );
        }
    }

    public String getPath(Uri uri)
    {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }

    public void UpdateTableContentsFragment( )
    {
        if( currentFragmentTag.equals( "TableContentsFragment" ) == false )
            return;

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fragmentManager.beginTransaction().detach(currentFragment).commitNow();
            fragmentManager.beginTransaction().attach(currentFragment).commitNow();
        }
        else
        {
            fragmentManager.beginTransaction().detach(currentFragment).attach(currentFragment).commit();
        }
    }

    public void DeleteProfile()
    {
        SaveAutoLogin( false );
        setToken( "" );
        DataManager.inst().setUserData( null );
    }

    public void ShowPermissionPopup()
    {
        View popupView = getLayoutInflater().inflate(R.layout.permition_info_popup, null);
        mPopupWindow = new PopupWindow(popupView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mPopupWindow.showAtLocation( popupView, Gravity.CENTER, 0, 0);

        Button okBtn = (Button) popupView.findViewById(R.id.permission_popup_ok_btn);
        okBtn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mPopupWindow.dismiss();
                SetPermissionPopup();
                PermissionCheck();
            }
        });

    }

    private void SetPermissionPopup()
    {
        settings = getSharedPreferences("settings", Activity.MODE_PRIVATE );
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("showPermissionPopup", true );

        editor.commit();
    }

    public boolean CheckAppFirstExecute(){
        SharedPreferences pref = getSharedPreferences("settings" , Activity.MODE_PRIVATE);
        boolean showPopup = pref.getBoolean("showPermissionPopup", false);
        return showPopup;
    }

    void PermissionCheck()
    {
        // 권한 관리자 생성
        mPermissionManager = PermissionManager.getInstance(this);

        // 인터넷 연결 상태 체크를 위한 권한
        mPermissionManager.addPermission(Manifest.permission.INTERNET, true);
        mPermissionManager.addPermission(Manifest.permission.ACCESS_NETWORK_STATE, true);

        // 외부 SD 저장장치
        mPermissionManager.addPermission(Manifest.permission.READ_EXTERNAL_STORAGE, true);
        mPermissionManager.addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
        mPermissionManager.addPermission(Manifest.permission.CAMERA, true);

        // 권한 요청 시작 및 콜백 설정
        mPermissionManager.startRequest(new PermissionDeniedCallback() {
            @Override
            public void onDenied(String deniedLabels) {
                String alertMsg = "앱을 사용하려면 아래의 권한이 반드시 필요합니다.\n" + deniedLabels +
                        "\n\n설정>앱>앱스>권한 항목에서 권한을 허용해주세요.";

                new AlertDialog.Builder(HomeActivity.this, R.style.AlertDialogMyapps)
                        .setTitle(null)
                        .setMessage(alertMsg)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        mPermissionManager.onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            if( imm.isAcceptingText() )
            {
                View v = getCurrentFocus();
                if (v instanceof EditText)
                {
                    Rect outRect = new Rect();
                    v.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int)event.getRawX(), (int)event.getRawY()))
                    {
                        v.clearFocus();
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }

        return super.dispatchTouchEvent( event );
    }

    public boolean IsShowExitPopup()
    {
        if( currentFragmentTag.equals( "TableContentsFragment" ) ||
                currentFragmentTag.equals( "BoardFragment" ) ||
                currentFragmentTag.equals( "PromoteListFragment" ) ||
                currentFragmentTag.equals( "MyPageFragment" ) )
        {
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed()
    {
        if( IsShowExitPopup() )
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("");
            builder.setMessage("앱을 종료 하시겠습니까?");
            builder.setPositiveButton("예",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            moveTaskToBack( true );
                            finish();
                            android.os.Process.killProcess( android.os.Process.myPid() );
                        }
                    });
            builder.setNegativeButton("아니오",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();
        }
        else
        {
            MovePrevFragment( "" );
        }
    }

    public void MovePrevFragment( String param )
    {
        switch ( prevFragmentTag )
        {
            case "AdminPageFragment":       { ChangeFragment( new AdminPageFragment(), "AdminPageFragment"); }  break;
            case "BoardFragment":
            {
                BoardFragment boardFragment = new BoardFragment();
                if( searchText.length() > 0 )
                    boardFragment.setSearchText( searchText );

                if( param.length() > 0 )
                {
                    BoardFragment.selectTab = Integer.valueOf( param );
                }

                ChangeFragment( boardFragment, "BoardFragment");
            }
            break;
            case "ContentsViewFragment":    { ChangeFragment( new ContentsViewFragment(), "ContentsViewFragment"); }  break;
            case "JoinMembershipFragment":  { ChangeFragment( new JoinMembershipFragment(), "JoinMembershipFragment"); }  break;
            case "LoginFragment":           { ChangeFragment( new LoginFragment(), "LoginFragment"); }  break;
            case "ModifyProfileFragment":   { ChangeFragment( new ModifyProfileFragment(), "ModifyProfileFragment"); }  break;
            case "MyPageFragment":          { ChangeFragment( new MyPageFragment(), "MyPageFragment"); }  break;
            case "PromoteAddressFragment":  { ChangeFragment( new PromoteAddressFragment(), "PromoteAddressFragment"); }  break;
            case "PromoteContentsFragment":
            {
                PromoteContentsFragment fragment = new PromoteContentsFragment();
                PromotionData data = DataManager.inst().getSelectPromotionData();
                if( data != null )
                    fragment.setPromotionData(data);

                ChangeFragment( fragment, "PromoteContentsFragment");
            }
            break;
            case "PromoteFragment":         { ChangeFragment( new PromoteFragment(), "PromoteFragment"); }  break;
            case "PromoteListFragment":
            {
                PromoteListFragment fragment = PromoteListFragment.newInstance( String.valueOf( PromoteListFragment.selectTab ) );
                ChangeFragment( fragment, "PromoteListFragment");
            }
            break;
            case "PromoteRegisterFragment":
            {
                PromoteRegisterFragment fragment = new PromoteRegisterFragment();
                PromotionData data = DataManager.inst().getSelectPromotionData();
                PromoteRegisterData registerData = DataManager.inst().getPromoteRegisterData();
                if( data != null )
                    fragment.setPromotionData(data);
                else if( registerData != null )
                    fragment.setPromoteRegisterData( registerData );

                ChangeFragment( fragment, "PromoteRegisterFragment");
            }
            break;
            case "PromoteRegisterFragment2":
            {
                ChangeFragment( new PromoteListFragment(), "PromoteListFragment");
            }
            break;
            case "QuestionsFragment":       { ChangeFragment( new QuestionsFragment(), "QuestionsFragment"); }  break;
            case "TableContentsFragment":   { ChangeFragment( new TableContentsFragment(), "TableContentsFragment"); }  break;
            case "UploadContentsFragment":   { ChangeFragment( new UploadContentsFragment(), "UploadContentsFragment"); }  break;
        }
    }


    //네트워크매니저에 있어야하는 부분
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
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, this);
            httpConnectTask.SetCallBack(mCallBack);
            httpConnectTask.execute();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

}
