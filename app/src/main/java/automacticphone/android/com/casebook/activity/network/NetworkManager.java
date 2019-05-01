package automacticphone.android.com.casebook.activity.network;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import automacticphone.android.com.casebook.activity.common.DataManager;
import automacticphone.android.com.casebook.activity.data.UserData;

public class NetworkManager {

    private volatile static NetworkManager _instance;

    public static NetworkManager inst() {
        if (_instance == null) {
            synchronized (NetworkManager.class) {
                if (_instance == null) {
                    _instance = new NetworkManager();
                }
            }
        }
        return _instance;
    }

    private NetworkManager() {
    }

    public void RequestUserData(String email, Context context, HttpTaskCallBack callBack) {
        try {

            JSONObject userInfo = new JSONObject();
            userInfo.put("packet_id", "request_userdata");
            userInfo.put("email", email);

            ContentValues values = new ContentValues();
            values.put("param", userInfo.toString());

            String url = RequestHttpURLConnection.serverIp + "/userdata.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, context);
            httpConnectTask.SetCallBack(callBack);
            httpConnectTask.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void RequestCommentData(Context context, HttpTaskCallBack callBack, int example_seq, int start, int size) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("packet_id", "comment_data");
            jsonObj.put("example_seq", example_seq);
            jsonObj.put("start", start);
            jsonObj.put("size", size);
            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString());

            String url = RequestHttpURLConnection.serverIp + "/comment_data.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, context);
            httpConnectTask.SetCallBack(callBack);
            httpConnectTask.execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void RequestCommentCount(Context context, HttpTaskCallBack callBack, int example_seq) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("packet_id", "contents_comment_count");
            jsonObj.put("example_seq", example_seq);
            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString());

            String url = RequestHttpURLConnection.serverIp + "/contents_comment_count.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, context);
            httpConnectTask.SetCallBack(callBack);
            httpConnectTask.execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void RequestContentOwnerData(Context context, HttpTaskCallBack callBack, int seq) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("packet_id", "content_owner_data");
            jsonObj.put("seq", seq);
            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString());

            String url = RequestHttpURLConnection.serverIp + "/content_owner_data.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, context);
            httpConnectTask.SetCallBack(callBack);
            httpConnectTask.execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void RequestWriteList(Context context, HttpTaskCallBack callBack, int member_seq) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("packet_id", "write_list");
            jsonObj.put("member_seq", member_seq);

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString());

            String url = RequestHttpURLConnection.serverIp + "/write_list.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, context);
            httpConnectTask.SetCallBack(callBack);
            httpConnectTask.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void RequestWriteCommentCount(Context context, HttpTaskCallBack callBack, int member_seq)
    {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("packet_id", "write_comment_count");
            jsonObj.put("member_seq", member_seq);

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString());

            String url = RequestHttpURLConnection.serverIp + "/write_comment_count.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, context);
            httpConnectTask.SetCallBack(callBack);
            httpConnectTask.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void UpdateCaseContentFeq( Context context, HttpTaskCallBack callBack, int seq, int feq )
    {
        JSONObject jsonObj = new JSONObject();
        try
        {
            int member_seq = -1;
            if( DataManager.inst().getUserData() != null )
                member_seq = DataManager.inst().getUserData().getSeq();

            jsonObj.put("packet_id", "update_case_feq");
            jsonObj.put("seq", seq);
            jsonObj.put("member_seq", member_seq);
            jsonObj.put("feq", feq);

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/update_case_feq.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, context);
            httpConnectTask.SetCallBack(callBack);
            httpConnectTask.execute();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void UpdatePromotionFeq( Context context, HttpTaskCallBack callBack, int seq, int feq )
    {
        JSONObject jsonObj = new JSONObject();
        try
        {
            int member_seq = -1;
            if( DataManager.inst().getUserData() != null )
                member_seq = DataManager.inst().getUserData().getSeq();

            jsonObj.put("packet_id", "update_promotion_feq");
            jsonObj.put("seq", seq);
            jsonObj.put("member_seq", member_seq);
            jsonObj.put("feq", feq);

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/update_promotion_feq.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, context);
            httpConnectTask.SetCallBack(callBack);
            httpConnectTask.execute();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void RegisterFcmToken( Context context, HttpTaskCallBack callBack, String fcmToken )
    {
        UserData userData = DataManager.inst().getUserData();
        if( userData == null )
            return;

        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "register_fcm_token");
            jsonObj.put("email", userData.getEmail() );
            jsonObj.put("token", fcmToken );

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/register_fcm_token.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, context);
            httpConnectTask.SetCallBack(callBack);
            httpConnectTask.execute();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void DeleteFcmToken( Context context, HttpTaskCallBack callBack )
    {
        UserData userData = DataManager.inst().getUserData();
        if( userData == null )
            return;

        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "delete_fcm_token");
            jsonObj.put("email", userData.getEmail() );

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/delete_fcm_token.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, context);
            httpConnectTask.SetCallBack(callBack);
            httpConnectTask.execute();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void RequestCaseDataSeq( Context context, HttpTaskCallBack callBack, int seq )
    {
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "case_text_seq");
            jsonObj.put("seq",  seq);

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/case_text_seq.php";
            HttpConnectTask httpConnectTask = null;
            httpConnectTask = new HttpConnectTask(url, values, context );

            httpConnectTask.SetCallBack(callBack);
            httpConnectTask.execute();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void RequestPromotionDataSeq( Context context, HttpTaskCallBack callBack, int seq )
    {
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "promotion_data_seq");
            jsonObj.put("seq",  seq);

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/promotion_data_seq.php";
            HttpConnectTask httpConnectTask = null;
            httpConnectTask = new HttpConnectTask(url, values, context );

            httpConnectTask.SetCallBack(callBack);
            httpConnectTask.execute();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

   public void RequestPromotionSearchList( Context context, HttpTaskCallBack callBack, String packetName, int promotionType, String searchText, int start, int size )
    {
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", packetName);
            jsonObj.put("type", promotionType );
            jsonObj.put("search_text", searchText );
            jsonObj.put("start", start );
            jsonObj.put("size", size );
            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/promotion_search_list.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, context);
            httpConnectTask.SetCallBack( callBack );
            httpConnectTask.execute();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void Logout( Context context, HttpTaskCallBack callBack )
    {
        JSONObject jsonObj = new JSONObject();
        try
        {
            String email = DataManager.inst().getUserData().getEmail();
            jsonObj.put("packet_id", "logout");
            jsonObj.put("email", email );
            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/logout.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, context);
            httpConnectTask.SetCallBack( callBack );
            httpConnectTask.execute();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void RequestCaseYearList( Context context, HttpTaskCallBack callBack, int cate_reg )
    {
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "case_year_list");
            jsonObj.put("cate_reg", cate_reg);
            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/case_year_list.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, context);
            httpConnectTask.SetCallBack(callBack);
            httpConnectTask.execute();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void RequestCaseData( Context context, HttpTaskCallBack callBack, String packetName, int selectYear, int cate_reg, int cate_1, int cate_2, int cate_3, int grade, int start, int size )
    {
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", packetName);
            jsonObj.put("year", selectYear);
            jsonObj.put("cate_reg", cate_reg);
            jsonObj.put("cate_1", cate_1);
            jsonObj.put("cate_2", cate_2);
            jsonObj.put("cate_3", cate_3);
            jsonObj.put("grade", grade);
            jsonObj.put("start", start);
            jsonObj.put("size", size);

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/case_data.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, context);
            httpConnectTask.SetCallBack(callBack);
            httpConnectTask.execute();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void RequestPromotionData( Context context, HttpTaskCallBack callBack, String packetName, int type, int start, int size  )
    {
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", packetName);
            jsonObj.put("type", type);
            jsonObj.put("start", start);
            jsonObj.put("size", size);

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/promotion_data.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, context);
            httpConnectTask.SetCallBack(callBack);
            httpConnectTask.execute();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void RequestSearchCaseData( Context context, HttpTaskCallBack callBack, String packetName, String searchText, int grade  )
    {
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", packetName );
            jsonObj.put("search_text", searchText );
            jsonObj.put("grade", grade );

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/case_search_list.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, context );
            httpConnectTask.SetCallBack(callBack);
            httpConnectTask.execute();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    //공지 가져오기
    public void RequestAnnounceData( Context context, HttpTaskCallBack callBack,String packetName, int start, int size )
    {
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", packetName );
            jsonObj.put("start", start);
            jsonObj.put("size", size);

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/announce_select.php";
            HttpConnectTask httpConnectTask = new HttpConnectTask(url, values, context );
            httpConnectTask.SetCallBack(callBack);
            httpConnectTask.execute();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

}
