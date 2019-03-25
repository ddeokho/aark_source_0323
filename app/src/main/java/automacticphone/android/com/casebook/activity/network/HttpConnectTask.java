package automacticphone.android.com.casebook.activity.network;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import automacticphone.android.com.casebook.activity.HomeActivity;
import automacticphone.android.com.casebook.activity.data.UploadPhotoData;
import automacticphone.android.com.casebook.activity.fragment.JoinMembershipFragment;

public class HttpConnectTask extends AsyncTask< Void, Void, String>
{
    String url;
    ContentValues values;
    Context mContext = null;
    HttpTaskCallBack mCallBack = null;
    ArrayList<UploadPhotoData> attatchFiles = new ArrayList<UploadPhotoData>();

    public HttpConnectTask(String url, ContentValues values, Context c ){
        attatchFiles.clear();
        this.url = url;
        this.values = values;
        this.mContext = c;
    }

    public HttpConnectTask(String url, ContentValues values, Context c, ArrayList<UploadPhotoData> files )
    {
        attatchFiles.clear();
        this.url = url;
        this.values = values;
        this.mContext = c;
        this.attatchFiles = files;
    }

    public void SetCallBack( HttpTaskCallBack callBack ) {
        mCallBack = callBack;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //progress bar를 보여주는 등등의 행위
    }

    @Override
    protected String doInBackground(Void... params) {
        String result = "";
        if( attatchFiles.size() == 0 )
        {
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
        }
        else
        {
            try
            {
                MultipartUpload multipartUpload = new MultipartUpload(url, "UTF-8");
                // 파라미터 키와 값.
                String key;
                String value;

                HashMap<String, String> param = new HashMap<String, String>();
                for (Map.Entry<String, Object> parameter : values.valueSet())
                {
                    key = parameter.getKey();
                    value = parameter.getValue().toString();

                    param.put( key, value);
                }

                HashMap<String, String> files = new HashMap<String, String>();
                int index = 0;
                for (int i = 0; i < attatchFiles.size(); i++)
                {
                    if(attatchFiles.get(i).uploadFileUri == null )
                        continue;

                    String filePath;
                    if( attatchFiles.get(i).filePath.length() > 0 )
                        filePath = attatchFiles.get(i).filePath;
                    else
                        filePath = HomeActivity.inst().getPath( attatchFiles.get(i).uploadFileUri );

                    files.put( "upload_" + index, filePath );
                    index++;
                }

                result = multipartUpload.upload(param, files);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
    }

    @Override
    protected void onPostExecute(String result) {
        // 통신이 완료되면 호출됩니다.
        // 결과에 따른 UI 수정 등은 여기서 합니다.
        try {
                //Toast.makeText( mContext, result, Toast.LENGTH_LONG).show();
                JSONObject jsonObj = new JSONObject( result );
                if(mCallBack != null )
                {
                    mCallBack.doProcess( jsonObj );
                }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
