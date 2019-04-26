package automacticphone.android.com.casebook.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.HomeActivity;
import automacticphone.android.com.casebook.activity.activity.DaumAddressView;
import automacticphone.android.com.casebook.activity.common.DataManager;
import automacticphone.android.com.casebook.activity.common.Util;
import automacticphone.android.com.casebook.activity.data.PromoteRegisterData;
import automacticphone.android.com.casebook.activity.data.PromotionData;

import static android.app.Activity.RESULT_OK;

public class PromoteRegisterFragment extends Fragment
{
    private EditText nameEdit;
    private EditText titleEdit;
    private EditText addressEdit;
    private EditText detailAddressEdit;
    private EditText phoneEdit;
    private EditText emailEdit;
    //url 추가
    private EditText urlEdit;
    private TextView logoFileText;
    private Double lot;
    private Double lnt;

    public PromotionData getPromotionData() {
        return promotionData;
    }

    public void setPromotionData(PromotionData promotionData) {
        this.promotionData = promotionData;
    }

    private PromotionData promotionData = null;
    private PromoteRegisterData promoteRegisterData = null;
    public void setPromoteRegisterData(PromoteRegisterData promoteRegisterData) {
        this.promoteRegisterData = promoteRegisterData;
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view)
        {
            switch (view.getId()) {
                case R.id.promote_register_next_btn:
                    OnNextBtnClick();
                    break;
                case R.id.promote_register_logo_btn:
                    OnLogoBtnClick();
                    break;
                case R.id.promote_register_address_btn:
                    OnFindAddressBtnClick();
                    break;
            }
        }
    }
    public PromoteRegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_promote_register, container, false);
        nameEdit = view.findViewById( R.id.promote_register_name );
        titleEdit = view.findViewById( R.id.promote_register_title );
        addressEdit = view.findViewById( R.id.promote_register_address );
        detailAddressEdit = view.findViewById( R.id.promote_register_detail_address);
        phoneEdit = view.findViewById( R.id.promote_register_phone );
        emailEdit = view.findViewById( R.id.promote_register_email );
        urlEdit = view.findViewById(R.id.promote_register_url);
        logoFileText = view.findViewById( R.id.promote_register_logo );

        PromoteRegisterFragment.BtnOnClickListener onClickListener = new PromoteRegisterFragment.BtnOnClickListener();
        Button btn = (Button)view.findViewById(R.id.promote_register_logo_btn);
        btn.setOnClickListener( onClickListener );

        btn = (Button)view.findViewById(R.id.promote_register_next_btn);
        btn.setOnClickListener( onClickListener );

        btn = (Button)view.findViewById(R.id.promote_register_address_btn);
        btn.setOnClickListener( onClickListener );

        if( promotionData != null )
        {
            nameEdit.setText( promotionData.getName());
            titleEdit.setText( promotionData.getTitle() );
            String[] splitText = promotionData.getAddress().split("@");
            if(splitText.length > 0 )
                addressEdit.setText( splitText[0] );

            if(splitText.length > 1 )
                detailAddressEdit.setText( splitText[1] );

            phoneEdit.setText( promotionData.getPhone() );
            emailEdit.setText( promotionData.getEmail() );
            //url 추가
            urlEdit.setText(promotionData.getUrl());
            //
            logoFileText.setText( promotionData.getLogImg() );
            lot = promotionData.getLatitude();
            lnt = promotionData.getLongitude();
            promoteRegisterData = new PromoteRegisterData();
        }
        else
        {
            if( promoteRegisterData == null )
                promoteRegisterData = new PromoteRegisterData();
            else
            {
                nameEdit.setText( promoteRegisterData.getName());
                titleEdit.setText( promoteRegisterData.getTitle() );
                String[] splitText = promoteRegisterData.getAddress().split("@");
                if(splitText.length > 0 )
                    addressEdit.setText( splitText[0] );

                if(splitText.length > 1 )
                    detailAddressEdit.setText( splitText[1] );

                phoneEdit.setText( promoteRegisterData.getPhone() );
                emailEdit.setText( promoteRegisterData.getEmail());
                //url추가
                urlEdit.setText(promoteRegisterData.getUrl());
                //
                logoFileText.setText( promoteRegisterData.getLogoImg() );
                lot = promoteRegisterData.getLnt();
                lnt = promoteRegisterData.getLot();
            }
        }

        return view;
    }

    void OnNextBtnClick()
    {
        int member_seq = DataManager.inst().getUserData().getSeq();
        String title = titleEdit.getText().toString();
        String name = nameEdit.getText().toString();
        String address = addressEdit.getText().toString();
        String detailAddress = detailAddressEdit.getText().toString();
        String phone = phoneEdit.getText().toString();
        String email = emailEdit.getText().toString();
        //url 추가
        String url=urlEdit.getText().toString();
        //
        String logoImg = logoFileText.getText().toString();

        if( name.length() == 0 )
        {
            HomeActivity.inst().ShowAlertDialog( "동아리 / 업체명을 입력해주세요." );
            return;
        }

        if( title.length() == 0 )
        {
            HomeActivity.inst().ShowAlertDialog( "제목을 입력 해주세요." );
            return;
        }

        if( address.length() == 0 )
        {
            HomeActivity.inst().ShowAlertDialog( "주소를 입력 해주세요." );
            return;
        }

        if( detailAddress.length() > 0 )
            address = address + "@" + detailAddress;

        String logoFileName = "";
        if( promoteRegisterData.getLogoUri() != null )
            logoFileName = Util.getFileName( getActivity(), promoteRegisterData.getLogoUri() );
        else
        {
            if( promotionData == null )
            {
                HomeActivity.inst().ShowAlertDialog( "업체 사진을 등록해 주세요." );
                return;
            }
            else if( promotionData.getLogImg().length() > 0 )
                logoFileName = promotionData.getLogImg();
        }

        promoteRegisterData.setMemberSeq( member_seq );
        promoteRegisterData.setTitle( title );
        promoteRegisterData.setName( name );
        promoteRegisterData.setEmail( email );
        //url 추가
        promoteRegisterData.setUrl(url);
        //
        promoteRegisterData.setAddress( address );
        promoteRegisterData.setLot( lot );
        promoteRegisterData.setLnt( lnt );
        promoteRegisterData.setPhone( phone );
        promoteRegisterData.setLogoImg( logoFileName );
        DataManager.inst().setPromoteRegisterData( promoteRegisterData );

        HomeActivity.inst().ChangeFragment( new PromoteRegisterFragment2(), "PromoteRegisterFragment2" );
    }

    void OnLogoBtnClick()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        //사진을 여러개 선택할수 있도록 한다
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        HomeActivity.inst().startActivityForResult(Intent.createChooser(intent, "Select Picture"), HomeActivity.PICTURE_REQUEST_CODE);
    }

    void OnFindAddressBtnClick()
    {
        Intent intent = new Intent( getContext(), DaumAddressView.class);
        startActivity(intent);
    }

    public  void onUploadContentsResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == HomeActivity.PICTURE_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                promoteRegisterData.setLogoUri( data.getData() );
                String fileName = Util.getFileName( getActivity(), data.getData() );
                logoFileText.setText( fileName );
            }
        }
    }

    public void SetAddress( String address )
    {
        addressEdit.setText( address );
    }

    public void SetLocationPoint( String strLot, String strLnt )
    {
        lot = Double.parseDouble(strLot);
        lnt = Double.parseDouble( strLnt );
    }
}
