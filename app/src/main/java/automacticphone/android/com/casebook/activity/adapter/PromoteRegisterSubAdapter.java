package automacticphone.android.com.casebook.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.HomeActivity;
import automacticphone.android.com.casebook.activity.common.Util;
import automacticphone.android.com.casebook.activity.data.PromoteRegisterSubData;
import automacticphone.android.com.casebook.activity.data.PromotionData;
import automacticphone.android.com.casebook.activity.data.PromotionSubData;
import automacticphone.android.com.casebook.activity.fragment.PromoteRegisterFragment2;

public class PromoteRegisterSubAdapter extends BaseAdapter
{
    private Context context;
    private LayoutInflater inflater;
    private Activity activity;
    public ArrayList<PromoteRegisterSubData> getDataList() {
        return dataList;
    }
    private int selectPhotoBtnPosition;
    private PromotionData selectPromotionData;

    private ArrayList<Integer> deleteSubDataList = new ArrayList<Integer>();
    public ArrayList<Integer> getDeleteSubDataList() {
        return deleteSubDataList;
    }

    private ArrayList<PromoteRegisterSubData> dataList = new ArrayList<PromoteRegisterSubData>();
    public PromoteRegisterSubAdapter(Context context, Activity activity, PromotionData promotionData, ArrayList<PromoteRegisterSubData> arrayList)
    {
        deleteSubDataList.clear();
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dataList = arrayList;
        this.context = context;
        this.activity = activity;
        this.selectPromotionData = promotionData;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view)
        {
            switch (view.getId()) {
                case R.id.promote_sub_photo_btn:
                    {
                        selectPhotoBtnPosition = (Integer)view.getTag();
                        if( dataList.get(selectPhotoBtnPosition).getContentEditText().getText().length() > 0 )
                        {
                            HomeActivity.inst().ShowAlertDialog( "소주제는 텍스트 또는 포스터 둘 중 하나만 가능합니다.!" );
                            return;
                        }

                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                        //사진을 여러개 선택할수 있도록 한다
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        intent.setType("image/*");
                        HomeActivity.inst().startActivityForResult(Intent.createChooser(intent, "Select Picture"), HomeActivity.PICTURE_REQUEST_CODE);
                    }
                    break;
                case R.id.promote_sub_add_btn:
                    {
                        AddItem();
                    }
                    break;
                case R.id.promote_sub_delete_btn:
                    {
                        int position = (Integer)view.getTag();
                        DeleteItem( position );
                    }
                    break;
                case R.id.promote_sub_photo_delete_btn:
                    {
                        int pos = (Integer)view.getTag();
                        dataList.get(pos).getPhotoTextView().setText( activity.getResources().getString( R.string.upload_poster_text ) );
                        dataList.get(pos).getContentEditText().setEnabled( true );
                        dataList.get(pos).getArrowImgView().setVisibility( View.VISIBLE );
                        dataList.get(pos).getPosterDeleteBtn().setVisibility( View.INVISIBLE);
                        dataList.get( pos ).setPhotoUri( null );
                        dataList.get( pos ).getPromotionSubData().setImgName( "" );
                    }
                    break;
            }
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        if(view == null) {
            view = inflater.inflate(R.layout.list_item_promote_sub, parent, false);
        }

        EditText titleEditText = (EditText) view.findViewById(R.id.promote_sub_title);;
        titleEditText.addTextChangedListener(new MyWatcher(titleEditText));
        titleEditText.setTag(position);

        EditText contentEditText = (EditText) view.findViewById(R.id.promote_sub_contents);
        contentEditText.addTextChangedListener(new MyWatcher(contentEditText));
        contentEditText.setTag(position);
        dataList.get(position).setContentEditText( contentEditText );

        TextView photoTextView = (TextView) view.findViewById(R.id.promote_sub_photo_name);

        titleEditText.setText( dataList.get(position).getPromotionSubData().getTitle() );
        contentEditText.setText( dataList.get(position).getPromotionSubData().getContent() );

        if( dataList.get(position).getPromotionSubData().getImgName().length() > 0 )
        {
            photoTextView.setText( dataList.get(position).getPromotionSubData().getImgName() );
        }
        else
            photoTextView.setText( context.getResources().getString( R.string.upload_poster_text) );
        dataList.get(position).setPhotoTextView( photoTextView );

        BtnOnClickListener onClickListener = new BtnOnClickListener();
        Button btn = (Button)view.findViewById(R.id.promote_sub_photo_btn);
        btn.setTag( position );
        btn.setOnClickListener( onClickListener );

        btn = (Button)view.findViewById(R.id.promote_sub_add_btn);
        btn.setTag( position );
        btn.setOnClickListener( onClickListener );

        btn = (Button)view.findViewById(R.id.promote_sub_delete_btn);
        btn.setTag( position );
        btn.setOnClickListener( onClickListener );

        ImageView arrowImgView = (ImageView) view.findViewById(R.id.promote_sub_arrow_img);
        Button posterDeleteBtn = (Button)view.findViewById(R.id.promote_sub_photo_delete_btn);
        posterDeleteBtn.setTag( position );
        posterDeleteBtn.setOnClickListener( onClickListener );
        dataList.get(position).setArrowImgView( arrowImgView );
        dataList.get(position).setPosterDeleteBtn( posterDeleteBtn );
        return view;
    }

    private void AddItem()
    {
        if( dataList.size() == 5 )
        {
            HomeActivity.inst().ShowAlertDialog( "더 이상 늘리지 못합니다." );
            return;
        }

        PromoteRegisterSubData subData = new PromoteRegisterSubData();
        subData.setPromotionSubData( new PromotionSubData() );
        dataList.add( subData );
        notifyDataSetChanged();
    }

    private void DeleteItem( int position )
    {
        int subDataSeq = dataList.get(position).getPromotionSubData().getSeq();
        if( subDataSeq > -1 )
            deleteSubDataList.add( subDataSeq );

        dataList.remove(position);
        notifyDataSetChanged();
    }

    public void SetPhoto( Uri photoUri )
    {
        dataList.get(selectPhotoBtnPosition).getArrowImgView().setVisibility( View.INVISIBLE );
        dataList.get(selectPhotoBtnPosition).getPosterDeleteBtn().setVisibility( View.VISIBLE );
        dataList.get(selectPhotoBtnPosition).getContentEditText().setEnabled( false );
        String fileName = Util.getFileName( activity, photoUri );
        dataList.get( selectPhotoBtnPosition ).setPhotoUri( photoUri );
        dataList.get( selectPhotoBtnPosition ).getPromotionSubData().setImgName( fileName );
        notifyDataSetChanged();
    }

    private class MyWatcher implements TextWatcher {

        private EditText edit;
        public MyWatcher(EditText edit) {
            this.edit = edit;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d("TAG", "Position: " + edit.getTag() +"    "+ "onTextChanged: " + s);
            int position = (Integer) edit.getTag();
            PromotionSubData subData = dataList.get(position).getPromotionSubData();
            if( edit.getId() == R.id.promote_sub_title )
                subData.setTitle( s.toString() );
            else if(edit.getId() == R.id.promote_sub_contents)
                subData.setContent( s.toString() );
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
