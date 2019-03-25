package automacticphone.android.com.casebook.activity.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;

import com.labo.kaji.fragmentanimations.MoveAnimation;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.HomeActivity;

public class PromoteFragment extends Fragment {
    private TabLayout mTabLayout;

    public PromoteFragment() {
        // Required empty public constructor
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view)
        {
            switch (view.getId()) {
                case R.id.fragment_promote_Add_btn:
                    OnAddBtnClick();
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_promote, container, false);
        mTabLayout = (TabLayout) view.findViewById( R.id.fragment_promote_tabs );
        mTabLayout.addTab( mTabLayout.newTab().setText(R.string.promote_tab_text1));
        mTabLayout.addTab( mTabLayout.newTab().setText(R.string.promote_tab_text2));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        PromoteFragment.BtnOnClickListener onClickListener = new PromoteFragment.BtnOnClickListener();
        //옵션버튼
        Button btn = (Button)view.findViewById(R.id.fragment_promote_Add_btn);
        btn.setOnClickListener( onClickListener );

        return view;
    }

    void OnAddBtnClick()
    {
        HomeActivity.inst().ChangeFragment( new PromoteRegisterFragment(), "PromoteRegisterFragment");
    }
}
