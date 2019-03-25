package automacticphone.android.com.casebook;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import automacticphone.android.com.casebook.activity.HomeActivity;
import automacticphone.android.com.casebook.activity.common.DataManager;
import automacticphone.android.com.casebook.activity.common.PermissionDeniedCallback;
import automacticphone.android.com.casebook.activity.common.PermissionManager;
import automacticphone.android.com.casebook.activity.common.Util;
import automacticphone.android.com.casebook.activity.data.CaseData;
import automacticphone.android.com.casebook.activity.data.PromotionData;
import automacticphone.android.com.casebook.activity.network.DownloadCallBack;
import automacticphone.android.com.casebook.activity.network.DownloadManager;
import automacticphone.android.com.casebook.activity.network.HttpConnectTask;
import automacticphone.android.com.casebook.activity.network.HttpTaskCallBack;
import automacticphone.android.com.casebook.activity.network.NetworkManager;
import automacticphone.android.com.casebook.activity.network.RequestHttpURLConnection;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
{
    private HttpTaskCallBack mCallBack = null;
    private int requestDataCount = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } catch (IllegalStateException ignore) {

        }

        Intent intent = getIntent();
        String data = intent.getDataString();
        if(intent.getAction() == Intent.ACTION_VIEW)
        {
            DataManager.inst().setKakaoBoardSeq( -1 );
            DataManager.inst().setKakaoPromotionSeq( -1 );
            String param = intent.getData().getQueryParameter("case_seq" );
            if( param != null )
            {
                int boardSeq = Integer.parseInt( param );
                DataManager.inst().setKakaoBoardSeq( boardSeq );
            }

             param = intent.getData().getQueryParameter("promotion_seq" );
            if( param != null )
            {
                int promotionSeq = Integer.parseInt( param );
                DataManager.inst().setKakaoPromotionSeq( promotionSeq );
            }
        }

        mCallBack = new HttpTaskCallBack()
        {
            @Override
            public void doProcess(JSONObject jsonObj)
            {
                try
                {
                    if(jsonObj.get("packet_id").equals("regulation_main_data"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            if(DataManager.inst().ParsingRegulationMainData( jsonObj ) )
                                requestDataCount--;

                            int cate_reg = 0;
                            if( DataManager.inst().getRegulMainDataList().size() > 0 )
                                cate_reg = DataManager.inst().getRegulMainDataList().get( 0 ).getSeq();

                            DataManager.inst().setSelectCateReg( cate_reg );
                            NetworkManager.inst().RequestCaseYearList( MainActivity.this, mCallBack, cate_reg );
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("regulation_sub1_data"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            if(DataManager.inst().ParsingRegulationSub1Data( jsonObj ) )
                                requestDataCount--;
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("regulation_sub2_data"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            if(DataManager.inst().ParsingRegulationSub2Data( jsonObj ) )
                                requestDataCount--;
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("regulation_sub3_data"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            if(DataManager.inst().ParsingRegulationSub3Data( jsonObj ) )
                                requestDataCount--;
                        }
                    }
                    else if(jsonObj.get("packet_id").equals("case_year_list"))
                    {
                        if( jsonObj.get("result").equals("true") )
                        {
                            if(DataManager.inst().ParsingCaseYearList( jsonObj ) )
                            {
                                requestDataCount--;
                            }
                        }
                    }

                    if( requestDataCount == 0 )
                    {
                        NextActivity();
                    }
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        };

        getAppKeyHash();
        RequestData();
    }

    private void RequestData()
    {
        RequestRegulationMainData();
        RequestRegulationSub1Data();
        RequestRegulationSub2Data();
        RequestRegulationSub3Data();
    }

    private void NextActivity()
    {
            Intent intent = new Intent( getApplicationContext(), HomeActivity.class);
            startActivity(intent);
    }

    private void RequestRegulationMainData()
    {
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "regulation_main_data");
            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/regulation_main.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, this);
            httpConnectTask.SetCallBack(mCallBack);
            httpConnectTask.execute();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void RequestRegulationSub1Data()
    {
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "regulation_sub1_data");
            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/regulation_sub1.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, this);
            httpConnectTask.SetCallBack(mCallBack);
            httpConnectTask.execute();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void RequestRegulationSub2Data()
    {
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "regulation_sub2_data");
            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/regulation_sub2.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, this);
            httpConnectTask.SetCallBack(mCallBack);
            httpConnectTask.execute();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void RequestRegulationSub3Data()
    {
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "regulation_sub3_data");
            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/regulation_sub3.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, this);
            httpConnectTask.SetCallBack(mCallBack);
            httpConnectTask.execute();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    //다음 API 키해시 등록을 위한 앱 키해시 생성.
   private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.d("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage("앱을 종료 하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        NextActivity();
                    }
                });
        builder.show();
    }
}
