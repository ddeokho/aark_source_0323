package automacticphone.android.com.casebook.activity.data;

import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class PromoteRegisterSubData
{
    private EditText contentEditText;
    private ImageView arrowImgView;
    private Button posterDeleteBtn;
    private TextView photoTextView;
    private PromotionSubData promotionSubData = null;
    public View.OnClickListener onClickListener;
    private Uri photoUri;

    public EditText getContentEditText() {
        return contentEditText;
    }

    public void setContentEditText(EditText contentEditText) {
        this.contentEditText = contentEditText;
    }

    public ImageView getArrowImgView() {
        return arrowImgView;
    }

    public void setArrowImgView(ImageView arrowImgView) {
        this.arrowImgView = arrowImgView;
    }

    public Button getPosterDeleteBtn() {
        return posterDeleteBtn;
    }

    public void setPosterDeleteBtn(Button posterDeleteBtn) {
        this.posterDeleteBtn = posterDeleteBtn;
    }

    public TextView getPhotoTextView() {
        return photoTextView;
    }

    public void setPhotoTextView(TextView photoTextView) {
        this.photoTextView = photoTextView;
    }

    public PromotionSubData getPromotionSubData() {
        return promotionSubData;
    }

    public void setPromotionSubData(PromotionSubData promotionSubData) {
        this.promotionSubData = promotionSubData;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }

    public boolean ExistData()
    {
        if( promotionSubData.getTitle().length() == 0 )
            return false;

        if( promotionSubData.getContent().length() == 0 && promotionSubData.getImgName().length() == 0 )
            return false;

        return true;
    }

}
