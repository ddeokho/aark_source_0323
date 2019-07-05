package automacticphone.android.com.casebook.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

import automacticphone.android.com.casebook.R;

public class eventViewFragment extends Fragment {

    public eventViewFragment(){

    }

    class BtnOnClickListener implements Button.OnClickListener{

        @Override
        public void onClick(View view){
            switch (view.getId()){
                case R.id.select_ksae_formula_btn:
                    //ksaeFormulaBtnClick();
                    break;


            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reg, container, false);
        //eventViewFragment.BtnOnClickListener onClickListener = new eventViewFragment().BtnOnClickListener();

        //Button btn = view.findViewById(R.id.select_ksae_formula_btn);
       // btn.setOnClickListener( onClickListener );


        //scrollView = (ScrollView) view.findViewById(R.id.horScrollVIew);
        //scrollView.setHorizontalScrollBarEnabled(true);

       /* mCallBack = new HttpTaskCallBack()
        {
            @Override
            public void doProcess(JSONObject jsonObj)
            {
            }
        };*/

        return  view;
    }

}
