package automacticphone.android.com.casebook.activity.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.labo.kaji.fragmentanimations.MoveAnimation;
import com.ssomai.android.scalablelayout.ScalableLayout;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.json.JSONException;
import org.json.JSONObject;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.HomeActivity;
import automacticphone.android.com.casebook.activity.common.DataManager;
import automacticphone.android.com.casebook.activity.common.Util;
import automacticphone.android.com.casebook.activity.network.HttpConnectTask;
import automacticphone.android.com.casebook.activity.network.HttpTaskCallBack;
import automacticphone.android.com.casebook.activity.network.RequestHttpURLConnection;

public class QuestionsFragment extends Fragment {

    private EditText titleEdit;
    private EditText bodyEdit;
    private HttpTaskCallBack mCallBack = null;
    private ZLoadingDialog loadingDialog;

    public QuestionsFragment() {
        // Required empty public constructor
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view)
        {
            switch (view.getId()) {
                case R.id.question_upload_btn:
                    OnQuestionUploadBtnClick();
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_questions, container, false);
        titleEdit = view.findViewById(R.id.title_input_edit );
        bodyEdit = view.findViewById(R.id.content_input_edit);

        QuestionsFragment.BtnOnClickListener onClickListener = new QuestionsFragment.BtnOnClickListener();
        Button btn = view.findViewById(R.id.question_upload_btn);
        btn.setOnClickListener( onClickListener );

        mCallBack = new HttpTaskCallBack()
        {
            @Override
            public void doProcess(JSONObject jsonObj)
            {
                try
                {
                    if(jsonObj.get("packet_id").equals("question_upload"))
                    {
                        loadingDialog.cancel();
                        if( jsonObj.get("result").equals("true") )
                        {
                            if( HomeActivity.inst().getCurrentFragmentTag().equals( "QuestionsFragment" ) )
                            {
                                Toast.makeText( getContext(), "문의가 접수 되었습니다.", Toast.LENGTH_SHORT ).show();
                                HomeActivity.inst().ChangeFragment( new TableContentsFragment(), "TableContentsFragment" );
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

        return  view;
    }

    private void OnQuestionUploadBtnClick() {
        String title = titleEdit.getText().toString();
        String bodyText = bodyEdit.getText().toString();
        if (title.length() == 0)
        {
            HomeActivity.inst().ShowAlertDialog( "보내는 사람의 이메일을 입력해 주세요." );
            return;
        }

        if( bodyText.length() == 0 )
        {
            HomeActivity.inst().ShowAlertDialog( "문의할 글을 작성하세요." );
            return;
        }

        loadingDialog = Util.ShowLoading( getContext(), Z_TYPE.SINGLE_CIRCLE, false  );
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put("packet_id", "question_upload");
            jsonObj.put("title", title );
            jsonObj.put("body_text", bodyText );

            ContentValues values = new ContentValues();
            values.put("param", jsonObj.toString() );

            String url = RequestHttpURLConnection.serverIp + "/question_upload.php";
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
