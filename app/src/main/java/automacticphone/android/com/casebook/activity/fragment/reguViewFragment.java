package automacticphone.android.com.casebook.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.zyao89.view.zloading.ZLoadingDialog;

import org.json.JSONObject;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.network.HttpTaskCallBack;

public class reguViewFragment extends Fragment {

    private HttpTaskCallBack mCallBack = null;
    private ZLoadingDialog loadingDialog;


    public reguViewFragment(){

    }

    class BtnOnClickListener implements Button.OnClickListener{

        @Override
        public void onClick(View view){
            switch (view.getId()){
                case R.id.select_ksae_formula_btn:
                    ksaeFormulaBtnClick();
                    break;
                case R.id.select_ksae_baja_btn:
                    ksaeBajaBtnClick();
                    break;
                case R.id.select_ksae_ev_btn:
                    ksaeEvBtnClick();
                    break;
                case R.id.select_sae_baja_btn:
                    saeBajaBtnClick();
                    break;
                case R.id.select_green_btn:
                    kasaGreenBtnClick();
                    break;

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reg, container, false);
        reguViewFragment.BtnOnClickListener onClickListener = new reguViewFragment.BtnOnClickListener();

        Button btn = view.findViewById(R.id.select_ksae_formula_btn);
        btn.setOnClickListener( onClickListener );

        btn = view.findViewById(R.id.select_ksae_baja_btn);
        btn.setOnClickListener(onClickListener);

        btn = view.findViewById(R.id.select_ksae_ev_btn);
        btn.setOnClickListener(onClickListener);

        btn = view.findViewById(R.id.select_sae_baja_btn);
        btn.setOnClickListener(onClickListener);

        btn = view.findViewById(R.id.select_green_btn);
        btn.setOnClickListener(onClickListener);

       /* mCallBack = new HttpTaskCallBack()
        {
            @Override
            public void doProcess(JSONObject jsonObj)
            {
            }
        };*/

        return  view;
    }

    void ksaeFormulaBtnClick(){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("http://aarkapp.iptime.org/reg_file/KSAE_Formula.pdf"));
        startActivity(i);
    }

    void ksaeBajaBtnClick(){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("http://aarkapp.iptime.org/reg_file/KSAE_BAJA.pdf"));
        startActivity(i);
    }

    void ksaeEvBtnClick(){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("http://aarkapp.iptime.org/reg_file/KSAE_EV.pdf"));
        startActivity(i);
    }

    void saeBajaBtnClick(){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("http://aarkapp.iptime.org/reg_file/SAE_BAJA.pdf"));
        startActivity(i);
    }

    void kasaGreenBtnClick(){
        //토스트
        Toast.makeText(getContext(), "아직 규정이 발표 되지 않았습니다.",Toast.LENGTH_SHORT).show();

        /*Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("http://aarkapp.iptime.org/reg_file/SAE_BAJA.pdf"));
        startActivity(i);*/

    }
}
