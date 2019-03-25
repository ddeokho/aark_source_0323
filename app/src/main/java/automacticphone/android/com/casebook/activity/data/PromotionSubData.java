package automacticphone.android.com.casebook.activity.data;

import automacticphone.android.com.casebook.activity.adapter.PromoteSubListAdapter;

public class PromotionSubData
{
    private int seq = -1;
    private int ad_title_seq = -1;
    private String title = "";
    private String content = "";
    private String imgName = "";
    private PromotionData promotionData = null;

    public int getContentType()
    {
        if( content.length() > 0 )
            return PromoteSubListAdapter.TEXT_TYPE;
        else if( imgName.length() > 0 )
            return PromoteSubListAdapter.POSTER_TYPE;
        else
            return PromoteSubListAdapter.ADDRESS_TYPE;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    private int contentType;

    public PromotionData getPromotionData() {
        return promotionData;
    }

    public void setPromotionData(PromotionData promotionData) {
        this.promotionData = promotionData;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getAd_title_seq() {
        return ad_title_seq;
    }

    public void setAd_title_seq(int ad_title_seq) {
        this.ad_title_seq = ad_title_seq;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

}
