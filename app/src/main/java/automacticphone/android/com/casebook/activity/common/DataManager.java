package automacticphone.android.com.casebook.activity.common;


import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import automacticphone.android.com.casebook.activity.data.AnnounceData;
import automacticphone.android.com.casebook.activity.data.CaseData;
import automacticphone.android.com.casebook.activity.data.CommentData;
import automacticphone.android.com.casebook.activity.data.PromoteRegisterData;
import automacticphone.android.com.casebook.activity.data.PromotionData;
import automacticphone.android.com.casebook.activity.data.PromotionSubData;
import automacticphone.android.com.casebook.activity.data.RegulationMainData;
import automacticphone.android.com.casebook.activity.data.RegulationSubData1;
import automacticphone.android.com.casebook.activity.data.RegulationSubData2;
import automacticphone.android.com.casebook.activity.data.RegulationSubData3;
import automacticphone.android.com.casebook.activity.data.UserData;
import automacticphone.android.com.casebook.activity.network.HttpConnectTask;
import automacticphone.android.com.casebook.activity.network.HttpTaskCallBack;
import automacticphone.android.com.casebook.activity.network.RequestHttpURLConnection;

public class DataManager
{
    private volatile static DataManager _instance;

    public ArrayList<RegulationMainData> getRegulMainDataList() {
        return regulMainDataList;
    }
    public ArrayList<RegulationSubData1> getRegulSubDataList1() { return regulSubDataList1; }

    private ArrayList<RegulationMainData> regulMainDataList = new ArrayList<RegulationMainData>();
    private ArrayList<RegulationSubData1> regulSubDataList1 = new ArrayList<RegulationSubData1>();
    private ArrayList<RegulationSubData2> regulSubDataList2 = new ArrayList<RegulationSubData2>();
    private ArrayList<RegulationSubData3> regulSubDataList3 = new ArrayList<RegulationSubData3>();

    public ArrayList<RegulationSubData3> getRegulSubDataList3() {
        return regulSubDataList3;
    }

    public void setRegulSubDataList3(ArrayList<RegulationSubData3> regulSubDataList3) {
        this.regulSubDataList3 = regulSubDataList3;
    }



    public PromotionData getSelectPromotionData() {
        return selectPromotionData;
    }

    public void setSelectPromotionData(PromotionData selectPromotionData) {
        this.selectPromotionData = selectPromotionData;
    }

    private PromotionData selectPromotionData;

    public ArrayList<PromotionData> getPromotionDataList() {
        return promotionDataList;
    }
    private ArrayList<PromotionData> promotionDataList = new ArrayList<PromotionData>();
    public ArrayList<PromotionSubData> getPromotionSubDataList() {
        return promotionSubDataList;
    }

    private ArrayList<PromotionSubData> promotionSubDataList = new ArrayList<PromotionSubData>();
    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    private UserData userData;
    private PromoteRegisterData promoteRegisterData;
    public PromoteRegisterData getPromoteRegisterData() {
        return promoteRegisterData;
    }
    private int selectYear;
    public int getSelectYear() {
        return selectYear;
    }

    public void setSelectYear(int selectYear) {
        this.selectYear = selectYear;
    }

    public ArrayList<CaseData> getCaseDataList() {
        return caseDataList;
    }

    private ArrayList<CaseData> caseDataList = new ArrayList<CaseData>();
    private CaseData selectCaseData;
    public CaseData getSelectCaseData() {
        return selectCaseData;
    }

    private int kakaoBoardSeq = -1;
    public int getKakaoBoardSeq() {
        return kakaoBoardSeq;
    }

    public void setKakaoBoardSeq(int kakaoBoardSeq) {
        this.kakaoBoardSeq = kakaoBoardSeq;
    }

    private int kakaoPromotionSeq = -1;
    public int getKakaoPromotionSeq() {
        return kakaoPromotionSeq;
    }

    public void setKakaoPromotionSeq(int kakaoPromotionSeq) {
        this.kakaoPromotionSeq = kakaoPromotionSeq;
    }
    public void setSelectCaseData(CaseData selectCaseData) {
        this.selectCaseData = selectCaseData;
    }

    public void setPromoteRegisterData(PromoteRegisterData promoteRegisterData) {
        this.promoteRegisterData = promoteRegisterData;
    }

    private ArrayList<CaseData> selectCaseDataList;
    public ArrayList<CaseData> getSelectCaseDataList() {
        return selectCaseDataList;
    }

    private UserData contentOwnerData;
    public UserData getContentOwnerData() {
        return contentOwnerData;
    }
    public void setContentOwnerData(UserData contentOwnerData) {
        this.contentOwnerData = contentOwnerData;
    }


    private ArrayList<CommentData> commentDataList = new ArrayList<CommentData>();
    public ArrayList<CommentData> getCommentDataList() {
        return commentDataList;
    }

    public void setCommentDataList(ArrayList<CommentData> commentDataList) {
        this.commentDataList = commentDataList;
    }

    //공지 리스트
    private ArrayList<AnnounceData> announceDataList = new ArrayList<AnnounceData>();
    public ArrayList<AnnounceData> getAnnounceDataList(){return announceDataList;}

    public void setAnnounceDataList(ArrayList<AnnounceData> announceDataList){
        this.announceDataList = announceDataList;
    }


    private ArrayList<CaseData> myWriteList = new ArrayList<CaseData>();
    public ArrayList<CaseData> getMyWriteList() {
        return myWriteList;
    }

    private UserData targetAdminUserData = null;                                            //관리자 페이지에서 검색해서 선택된 유저데이터.
    public UserData getTargetAdminUserData() {
        return targetAdminUserData;
    }

    public void setTargetAdminUserData(UserData targetAdminUserData) {
        this.targetAdminUserData = targetAdminUserData;
    }

    private ArrayList<CaseData> memberWriteList = new ArrayList<CaseData>();                //관리자 페이지에서 검색 후 선택한 회원이 업로드한 글과 댓글 리스트.
    public ArrayList<CaseData> getMemberWriteList() {
        return memberWriteList;
    }

    private ArrayList<CaseData> searchCaseList = new ArrayList<CaseData>();
    public ArrayList<CaseData> getSearchCaseList() {
        return searchCaseList;
    }

    public void setSearchCaseList(ArrayList<CaseData> searchCaseList) {
        this.searchCaseList = searchCaseList;
    }

    private ArrayList<Integer> caseYearList = new ArrayList<Integer>();
    public ArrayList<Integer> getCaseYearList() {
        return caseYearList;
    }

    public void setCaseYearList(ArrayList<Integer> caseYearList) {
        this.caseYearList = caseYearList;
    }

    private ArrayList<UserData> searchMemberList = new ArrayList<UserData>();
    public ArrayList<UserData> getSearchMemberList() {
        return searchMemberList;
    }

    public void setSearchMemberList(ArrayList<UserData> searchMemberList) {
        this.searchMemberList = searchMemberList;
    }

    private int writeCommentCount;
    public int getWriteCommentCount() {
        return writeCommentCount;
    }

    public void setWriteCommentCount(int writeCommentCount) {
        this.writeCommentCount = writeCommentCount;
    }

    private int memberWriteCommentCount;                                    //관리자사 선택한 유저 댓글 수.
    public int getMemberWriteCommentCount() {
        return memberWriteCommentCount;
    }

    public void setMemberWriteCommentCount(int memberWriteCommentCount) {
        this.memberWriteCommentCount = memberWriteCommentCount;
    }

    public int getMyWriteCount( int member_seq )
    {
        int count = 0;
        for( int i = 0; i < myWriteList.size(); ++i )
        {
            if( member_seq == myWriteList.get(i).getMember_seq() )
                count++;
        }

        return count;
    }

    public int getMemberWriteCount( int member_seq )
    {
        int count = 0;
        for( int i = 0; i < memberWriteList.size(); ++i )
        {
            if( member_seq == memberWriteList.get(i).getMember_seq() )
                count++;
        }

        return count;
    }

    public void setMyWriteList(ArrayList<CaseData> myWriteList) {
        this.myWriteList = myWriteList;
    }

    private int selectCateReg = 0;
    public int getSelectCateReg() {
        return selectCateReg;
    }

    public void setSelectCateReg(int selectCateReg) {
        this.selectCateReg = selectCateReg;
    }
    public int getTotalCommentCount() {
        return totalCommentCount;
    }

    public void setTotalCommentCount(int totalCommentCount) {
        this.totalCommentCount = totalCommentCount;
    }

    private int totalCommentCount;

    public void setSelectCaseDataList(ArrayList<CaseData> selectCaseDataList) {
        this.selectCaseDataList = selectCaseDataList;
    }

    public  static DataManager inst()
    {
        if(_instance ==null)
        {
            synchronized (DataManager.class)
            {
                if(_instance == null)
                {
                    _instance = new DataManager();
                }
            }
        }
        return _instance;
    }

    public boolean ParsingRegulationMainData( JSONObject jsonObj )
{
    regulMainDataList.clear();
    try{
        JSONArray dataArray = jsonObj.getJSONArray("regulation_list");
        for(int i=0; i < dataArray.length(); i++){
            RegulationMainData data = new RegulationMainData();
            JSONObject jObject = dataArray.getJSONObject(i);
            int seq = Integer.valueOf( jObject.getString("seq") );
            String regul = jObject.getString("regul");
            String content = jObject.getString("content");
            data.setSeq( seq );
            data.setRegul( regul );
            data.setContent( content );
            regulMainDataList.add( data );
        }
    }
    catch (JSONException e)
    {
        e.printStackTrace();
        return false;
    }

    return true;
}



    public boolean ParsingRegulationSub1Data( JSONObject jsonObj )
    {
        regulSubDataList1.clear();
        try{
            JSONArray dataArray = jsonObj.getJSONArray("data_array");
            for(int i=0; i < dataArray.length(); i++){
                RegulationSubData1 data = new RegulationSubData1();
                JSONObject jObject = dataArray.getJSONObject(i);
                int seq = Integer.valueOf( jObject.getString("seq") );
                int cate_main_seq = Integer.valueOf( jObject.getString("cate_main_seq") );
                String regul = jObject.getString("regul");
                String content = jObject.getString("content");
                data.setSeq( seq );
                data.setCateMainSeq( cate_main_seq );
                data.setRegul( regul );
                data.setContent( content );
                regulSubDataList1.add( data );
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    public boolean ParsingRegulationSub2Data( JSONObject jsonObj )
    {
        regulSubDataList2.clear();
        try{
            JSONArray dataArray = jsonObj.getJSONArray("data_array");
            for(int i=0; i < dataArray.length(); i++){
                RegulationSubData2 data = new RegulationSubData2();
                JSONObject jObject = dataArray.getJSONObject(i);
                int seq = Integer.valueOf( jObject.getString("seq") );
                int cate_sub1_seq = Integer.valueOf( jObject.getString("cate_sub1_seq") );
                String regul = jObject.getString("regul");
                String content = jObject.getString("content");
                data.setSeq( seq );
                data.setCateSub1Seq( cate_sub1_seq );
                data.setRegul( regul );
                data.setContent( content );
                regulSubDataList2.add( data );
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    ///레귤레이션 추가(4/25)
    public RegulationSubData2 getRegulSubData2( int sub2_seq )
    {
        for( int i = 0; i < regulSubDataList2.size(); ++i )
        {
            if( regulSubDataList2.get(i).getSeq() == sub2_seq )
                return regulSubDataList2.get(i);
        }

        return null;
    }
    ////

    public boolean ParsingRegulationSub3Data( JSONObject jsonObj )
    {
        regulSubDataList3.clear();
        try{
            JSONArray dataArray = jsonObj.getJSONArray("data_array");
            for(int i=0; i < dataArray.length(); i++){
                RegulationSubData3 data = new RegulationSubData3();
                JSONObject jObject = dataArray.getJSONObject(i);
                int seq = Integer.valueOf( jObject.getString("seq") );
                int cate_sub2_seq = Integer.valueOf( jObject.getString("cate_sub2_seq") );
                String regul = jObject.getString("regul");
                String content = jObject.getString("content");
                data.setSeq( seq );
                data.setCateSub2Seq( cate_sub2_seq );
                data.setRegul( regul );
                data.setContent( content );
                regulSubDataList3.add( data );
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean ParsingUserData( JSONObject jsonObject )
    {
        try{
            userData = new UserData();
            userData.setSeq( Integer.parseInt( jsonObject.get("seq").toString() ) );
            userData.setName( jsonObject.get("name").toString() );
            userData.setEmail( jsonObject.get("email").toString() );
            userData.setBirth( jsonObject.get("birth").toString() );
            userData.setGender( Integer.parseInt( jsonObject.get("gender").toString() ) );
            userData.setUniv( jsonObject.get("univ").toString() );
            userData.setGrade( Integer.parseInt( jsonObject.get("grade").toString() ) );
            userData.setPhone( jsonObject.get("phone").toString() );
            userData.setPassword( jsonObject.get("password").toString() );

        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean ParsingWriteList( JSONObject jsonObject )
    {
        myWriteList.clear();
        try{
            JSONArray dataArray = jsonObject.getJSONArray("data_list");
            for(int i=0; i < dataArray.length(); i++)
            {
                JSONObject jObject = dataArray.getJSONObject(i);
                int seq = Integer.parseInt( jObject.get("seq").toString() );
                if( ExistWriteData( seq ) )
                    continue;

                CaseData data = new CaseData();
                data.setSeq( seq );
                data.setMember_seq( Integer.parseInt( jObject.get("member_seq").toString() ) );
                int year = Integer.parseInt( jObject.get("year").toString() );
                data.setYear( year );
                data.setTitle( jObject.get("title").toString() );
                data.setContent( jObject.get("content").toString() );
                data.setCate_reg( Integer.parseInt( jObject.get("cate_reg").toString() ) );
                data.setCate_1( Integer.parseInt( jObject.get("cate_1").toString() ) );
                data.setCate_2( Integer.parseInt( jObject.get("cate_2").toString() ) );
                data.setCate_3( Integer.parseInt( jObject.get("cate_3").toString() ) );
                data.setFeq( Integer.parseInt( jObject.get("feq").toString() ) );
                data.setGrade( Integer.parseInt( jObject.get("grade").toString() ) );
                data.setImg_1( jObject.get("img_1").toString() );
                data.setImg_2( jObject.get("img_2").toString() );
                data.setImg_3( jObject.get("img_3").toString() );
                data.setTimestamp( jObject.get("timestamp").toString() );

                data.setComment_count(Integer.parseInt(jObject.get("comment_count").toString()));//댓글수

                myWriteList.add( data );
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean ParsingMemberWriteList( JSONObject jsonObject )
    {
        memberWriteList.clear();
        try{
            JSONArray dataArray = jsonObject.getJSONArray("data_list");
            for(int i=0; i < dataArray.length(); i++)
            {
                JSONObject jObject = dataArray.getJSONObject(i);
                int seq = Integer.parseInt( jObject.get("seq").toString() );
                if( ExistMemberWriteData( seq ) )
                    continue;

                CaseData data = new CaseData();
                data.setSeq( seq );
                data.setMember_seq( Integer.parseInt( jObject.get("member_seq").toString() ) );
                int year = Integer.parseInt( jObject.get("year").toString() );
                data.setYear( year );
                data.setTitle( jObject.get("title").toString() );
                data.setContent( jObject.get("content").toString() );
                data.setCate_reg( Integer.parseInt( jObject.get("cate_reg").toString() ) );
                data.setCate_1( Integer.parseInt( jObject.get("cate_1").toString() ) );
                data.setCate_2( Integer.parseInt( jObject.get("cate_2").toString() ) );
                data.setCate_3( Integer.parseInt( jObject.get("cate_3").toString() ) );
                data.setFeq( Integer.parseInt( jObject.get("feq").toString() ) );
                data.setGrade( Integer.parseInt( jObject.get("grade").toString() ) );
                data.setImg_1( jObject.get("img_1").toString() );
                data.setImg_2( jObject.get("img_2").toString() );
                data.setImg_3( jObject.get("img_3").toString() );
                data.setTimestamp( jObject.get("timestamp").toString() );

                data.setComment_count(Integer.parseInt(jObject.get("comment_count").toString()));//댓글

                memberWriteList.add( data );
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean ParsingContentOwnerData( JSONObject jsonObject )
    {
        try{
            contentOwnerData = new UserData();
            contentOwnerData.setSeq( Integer.parseInt( jsonObject.get("seq").toString() ) );
            contentOwnerData.setName( jsonObject.get("name").toString() );
            contentOwnerData.setEmail( jsonObject.get("email").toString() );
            contentOwnerData.setGender( Integer.parseInt( jsonObject.get("gender").toString() ) );
            contentOwnerData.setUniv( jsonObject.get("univ").toString() );
            contentOwnerData.setGrade( Integer.parseInt( jsonObject.get("grade").toString() ) );
            contentOwnerData.setAddress( jsonObject.get("address").toString() );
            contentOwnerData.setPhone( jsonObject.get("phone").toString() );
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean ParsingCommentData( JSONObject jsonObject )
    {
        try{
            JSONArray dataArray = jsonObject.getJSONArray("data_list");
            for(int i=0; i < dataArray.length(); i++){
                CommentData data = new CommentData();
                JSONObject jObject = dataArray.getJSONObject(i);

                data.setSeq( Integer.parseInt( jObject.get("seq").toString() ) );
                data.setMemberSeq( Integer.parseInt( jObject.get("member_seq").toString() ) );
                data.setExampleSeq( Integer.parseInt( jObject.get("example_seq").toString() ) );
                data.setImgName( jObject.get("img_name").toString() );
                data.setContent( jObject.get("content").toString() );
                data.setTimestamp( jObject.get("timestamp").toString() );
                data.setMemberID( jObject.get("member_id").toString() );

                commentDataList.add( data );
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //공지사항 파싱
    public boolean ParsingAnnounceData(JSONObject jsonObject)
    {
        try{
            JSONArray dataArray = jsonObject.getJSONArray("data_list");
            for(int i=0; i < dataArray.length(); i++){
                AnnounceData data = new AnnounceData();
                JSONObject jObject = dataArray.getJSONObject(i);

                data.setSeq( Integer.parseInt( jObject.get("seq").toString() ) );
                data.setContent(jObject.get("content").toString());
                data.setGrade(jObject.get("grade").toString());
                data.setTimeStamp(jObject.get("Timestamp").toString());

                announceDataList.add( data );
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public boolean ParsingPromotionData( JSONObject jsonObject )
    {
        try{
            JSONArray dataArray = jsonObject.getJSONArray("data_array");
            for(int i=0; i < dataArray.length(); i++){
                PromotionData data = new PromotionData();
                JSONObject jObject = dataArray.getJSONObject(i);

                data.setSeq( Integer.parseInt( jObject.get("seq").toString() ) );
                data.setSeq2( Integer.parseInt( jObject.get("seq2").toString() ) );
                data.setMemberSeq( Integer.parseInt( jObject.get("member_seq").toString() ) );
                data.setType( Integer.parseInt( jObject.get("type").toString() ) );
                data.setName( jObject.get("name").toString() );
                data.setTitle( jObject.get("title").toString() );
                data.setEmail( jObject.get("email").toString() );
                data.setAddress( jObject.get("address").toString() );
                data.setLatitude( Double.parseDouble( jObject.get("latitude").toString() ) );
                data.setLongitude( Double.parseDouble( jObject.get("longitude").toString() ) );
                data.setPhone( jObject.get("phone").toString()  );
                data.setLogImg( jObject.get("logo_img").toString()  );
                data.setFeq( Integer.parseInt( jObject.get("feq").toString() ) );
                data.setShowState( Integer.parseInt( jObject.get("feq").toString() ) );
                data.setTimestamp( jObject.get("timestamp").toString() );
                //url 추가
                data.setUrl(jObject.get("url").toString());
                //

                promotionDataList.add( data );
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public PromotionData getPromotionData( int seq )
    {
       for( int i = 0; i < promotionDataList.size(); ++i )
       {
           if( promotionDataList.get(i).getSeq() == seq )
               return promotionDataList.get(i);
       }

       return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean ParsingUpdatePromotionData(JSONObject jsonObject )
    {
        try{
            int seq = Integer.parseInt( jsonObject.get("seq").toString() );
            PromotionData data = getPromotionData( seq );
            boolean bAddData = false;
            if( data == null )
            {
                bAddData = true;
                data = new PromotionData();
            }

            data.setSeq( seq );
            data.setSeq2( Integer.parseInt( jsonObject.get("seq2").toString() ) );
            data.setMemberSeq( Integer.parseInt( jsonObject.get("member_seq").toString() ) );
            data.setType( Integer.parseInt( jsonObject.get("type").toString() ) );
            data.setName( jsonObject.get("name").toString() );
            data.setTitle( jsonObject.get("title").toString() );
            data.setEmail( jsonObject.get("email").toString() );
            data.setAddress( jsonObject.get("address").toString() );
            data.setLatitude( Double.parseDouble( jsonObject.get("latitude").toString() ) );
            data.setLongitude( Double.parseDouble( jsonObject.get("longitude").toString() ) );
            data.setPhone( jsonObject.get("phone").toString()  );
            data.setLogImg( jsonObject.get("logo_img").toString()  );
            data.setFeq( Integer.parseInt( jsonObject.get("feq").toString() ) );
            data.setShowState( Integer.parseInt( jsonObject.get("feq").toString() ) );
            data.setTimestamp( jsonObject.get("timestamp").toString() );
            //url추가
            data.setUrl(jsonObject.get("url").toString());
            //

            if( bAddData )
            {
                promotionDataList.add( data );
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean ParsingPromotionSubData( JSONObject jsonObject )
    {
        promotionSubDataList.clear();
        try{
            JSONArray dataArray = jsonObject.getJSONArray("data_array");
            for(int i=0; i < dataArray.length(); i++){
                PromotionSubData data = new PromotionSubData();
                JSONObject jObject = dataArray.getJSONObject(i);
                data.setSeq( Integer.parseInt( jObject.get("seq").toString() ) );
                data.setAd_title_seq( Integer.parseInt( jObject.get("ad_title_seq").toString() ) );
                data.setTitle( jObject.get("title").toString() );
                data.setContent( jObject.get("content").toString() );
                data.setImgName( jObject.get("img_name").toString() );

                promotionSubDataList.add( data );
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean ParsingCaseData( JSONObject jsonObject )
    {
        try{
            JSONArray dataArray = jsonObject.getJSONArray("data_list");
            for(int i=0; i < dataArray.length(); i++){
                JSONObject jObject = dataArray.getJSONObject(i);
                int seq = Integer.parseInt( jObject.get("seq").toString() );
                int cate_reg = Integer.parseInt( jObject.get("cate_reg").toString() );
                if( ExistCaseData( seq ) != null )
                    continue;

                CaseData data = new CaseData();
                data.setSeq( seq );
                data.setMember_seq( Integer.parseInt( jObject.get("member_seq").toString() ) );
                data.setYear( Integer.parseInt( jObject.get("year").toString() ) );
                data.setTitle( jObject.get("title").toString() );
                data.setContent( jObject.get("content").toString() );
                data.setCate_reg( cate_reg );
                data.setCate_1( Integer.parseInt( jObject.get("cate_1").toString() ) );
                data.setCate_2( Integer.parseInt( jObject.get("cate_2").toString() ) );
                data.setCate_3( Integer.parseInt( jObject.get("cate_3").toString() ) );
                data.setFeq( Integer.parseInt( jObject.get("feq").toString() ) );
                data.setGrade( Integer.parseInt( jObject.get("grade").toString() ) );
                data.setImg_1( jObject.get("img_1").toString() );
                data.setImg_2( jObject.get("img_2").toString() );
                data.setImg_3( jObject.get("img_3").toString() );
                data.setTimestamp( jObject.get("timestamp").toString() );
                data.setComment_count(Integer.parseInt(jObject.get("comment_count").toString()));//댓글

                caseDataList.add( data );
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CaseData ParsingUpdateCaseData(JSONObject jsonObject )
    {
        CaseData data = null;
        try {
            int seq = Integer.parseInt( jsonObject.get("seq").toString() );
            int cate_reg = Integer.parseInt( jsonObject.get("cate_reg").toString() );
            CaseData caseData = ExistCaseData( seq );
            //기존 데이터가 있는 경우.
            if( caseData != null  )
                data = caseData;
            else
                data = new CaseData();

            data.setSeq( seq );
            data.setMember_seq( Integer.parseInt( jsonObject.get("member_seq").toString() ) );
            int year = Integer.parseInt( jsonObject.get("year").toString() );
            data.setYear( year );
            data.setTitle( jsonObject.get("title").toString() );
            data.setContent( jsonObject.get("content").toString() );
            data.setCate_reg( cate_reg );
            data.setCate_1( Integer.parseInt( jsonObject.get("cate_1").toString() ) );
            data.setCate_2( Integer.parseInt( jsonObject.get("cate_2").toString() ) );
            data.setCate_3( Integer.parseInt( jsonObject.get("cate_3").toString() ) );
            data.setFeq( Integer.parseInt( jsonObject.get("feq").toString() ) );
            data.setGrade( Integer.parseInt( jsonObject.get("grade").toString() ) );
            data.setImg_1( jsonObject.get("img_1").toString() );
            data.setImg_2( jsonObject.get("img_2").toString() );
            data.setImg_3( jsonObject.get("img_3").toString() );
            data.setTimestamp( jsonObject.get("timestamp").toString() );

            //댓글수
            data.setComment_count(Integer.parseInt(jsonObject.get("comment_count").toString()));//댓글

            if( caseData == null )
            {
                caseDataList.add( data );
                // 내림차순 정렬
                DescendingCaseData descending = new DescendingCaseData();
                Collections.sort(caseDataList, descending);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return  data;
    }

    public void UpdateCaseDataGrade( int seq, int grade )
    {
        for( int i = 0; i < myWriteList.size(); ++i )
        {
            if( seq == myWriteList.get(i).getSeq() )
            {
                myWriteList.get(i).setGrade( grade );
                break;
            }
        }

        for( int i = 0; i < caseDataList.size(); ++i )
        {
            if( seq == caseDataList.get(i).getSeq() )
            {
                caseDataList.get(i).setGrade( grade );
                break;
            }
        }
    }

    private boolean ExistWriteData( int seq )
    {
        for( int i = 0; i < myWriteList.size(); ++i )
        {
            if( seq == myWriteList.get(i).getSeq() )
                return true;
        }

        return false;
    }

    private boolean ExistMemberWriteData( int seq )
    {
        for( int i = 0; i < memberWriteList.size(); ++i )
        {
            if( seq == memberWriteList.get(i).getSeq() )
                return true;
        }

        return false;
    }

    public boolean ParsingMemberList( JSONObject jsonObject )
    {
        searchMemberList.clear();
        try{
            JSONArray dataArray = jsonObject.getJSONArray("data_list");
            for(int i=0; i < dataArray.length(); i++){
                JSONObject jObject = dataArray.getJSONObject(i);

                UserData data = new UserData();
                data.setSeq( Integer.parseInt( jObject.get("seq").toString() ) );
                data.setName( jObject.get("name").toString() );
                data.setEmail( jObject.get("email").toString() );
                data.setGender( Integer.parseInt( jObject.get("gender").toString() ) );
                data.setUniv( jObject.get("univ").toString() );
                data.setGrade( Integer.parseInt( jObject.get("grade").toString() ) );
                data.setAddress( jObject.get("address").toString() );
                data.setPhone( jObject.get("phone").toString() );

                searchMemberList.add( data );
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean ParsingSearchCaseList( JSONObject jsonObject )
    {
        searchCaseList.clear();
        try{
            JSONArray dataArray = jsonObject.getJSONArray("data_list");
            for(int i=0; i < dataArray.length(); i++)
            {
                JSONObject jObject = dataArray.getJSONObject(i);
                CaseData data = new CaseData();
                data.setSeq( Integer.parseInt( jObject.get("seq").toString() ) );
                data.setMember_seq( Integer.parseInt( jObject.get("member_seq").toString() ) );
                data.setYear( Integer.parseInt( jObject.get("year").toString() ) );
                data.setTitle( jObject.get("title").toString() );
                data.setContent( jObject.get("content").toString() );
                data.setCate_reg( Integer.parseInt( jObject.get("cate_reg").toString() ) );
                data.setCate_1( Integer.parseInt( jObject.get("cate_1").toString() ) );
                data.setCate_2( Integer.parseInt( jObject.get("cate_2").toString() ) );
                data.setCate_3( Integer.parseInt( jObject.get("cate_3").toString() ) );
                data.setFeq( Integer.parseInt( jObject.get("feq").toString() ) );
                data.setGrade( Integer.parseInt( jObject.get("grade").toString() ) );
                data.setImg_1( jObject.get("img_1").toString() );
                data.setImg_2( jObject.get("img_2").toString() );
                data.setImg_3( jObject.get("img_3").toString() );
                data.setTimestamp( jObject.get("timestamp").toString() );

                data.setComment_count(Integer.parseInt(jObject.get("comment_count").toString()));//댓글

                searchCaseList.add( data );
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void DeletePromotionData( int seq )
    {
        for( int i = 0; i < promotionDataList.size(); ++i )
        {
            if( promotionDataList.get(i).getSeq() == seq )
            {
                promotionDataList.remove(i);
                break;
            }
        }
    }

    public RegulationMainData getRegulationMainData( int cateReg )
    {
        for( int i = 0; i < regulMainDataList.size(); ++i )
        {
            if( cateReg == regulMainDataList.get(i).getSeq() )
                return regulMainDataList.get(i);
        }

        return null;
    }

    public ArrayList<String> getRegulMainMenu()
    {
        ArrayList<String> list = new ArrayList<String>();
        for( int i = 0; i < regulMainDataList.size(); ++i )
        {
            list.add( regulMainDataList.get(i).getRegul() );
        }

        return list;
    }

    public ArrayList<RegulationSubData1> getRegulSubMenu1( int idx )
    {
        RegulationMainData data = regulMainDataList.get( idx );
        if( data == null )
            return null;

        ArrayList<RegulationSubData1> list = new ArrayList<RegulationSubData1>();
        for( int i = 0; i < regulSubDataList1.size(); ++i )
        {
            if( regulSubDataList1.get(i).getCateMainSeq() == data.getSeq() )
                list.add( regulSubDataList1.get(i) );
        }

        return list;
    }

    public ArrayList<RegulationSubData1> getRegulSubMenu1( RegulationMainData mainData )
    {
        if( mainData == null )
            return null;

        ArrayList<RegulationSubData1> list = new ArrayList<RegulationSubData1>();
        for( int i = 0; i < regulSubDataList1.size(); ++i )
        {
            if( regulSubDataList1.get(i).getCateMainSeq() == mainData.getSeq() )
                list.add( regulSubDataList1.get(i) );
        }

        return list;
    }

    public ArrayList<RegulationSubData2> getRegulSubMenu2( int sub1_seq )
    {
        ArrayList<RegulationSubData2> list = new ArrayList<RegulationSubData2>();
        for( int i = 0; i < regulSubDataList2.size(); ++i )
        {
            if( regulSubDataList2.get(i).getCateSub1Seq() == sub1_seq )
                list.add( regulSubDataList2.get(i) );
        }

        return list;
    }

    public ArrayList<RegulationSubData2> getRegulSubMenu2( RegulationSubData1 subData )
    {
        ArrayList<RegulationSubData2> list = new ArrayList<RegulationSubData2>();
        for( int i = 0; i < regulSubDataList2.size(); ++i )
        {
            if( regulSubDataList2.get(i).getCateSub1Seq() == subData.getSeq() )
                list.add( regulSubDataList2.get(i) );
        }

        return list;
    }

    public ArrayList<RegulationSubData3> getRegulSubMenu3( RegulationSubData2 subData )
    {
        ArrayList<RegulationSubData3> list = new ArrayList<RegulationSubData3>();
        for( int i = 0; i < regulSubDataList3.size(); ++i )
        {
            if( regulSubDataList3.get(i).getCateSub2Seq() == subData.getSeq() )
                list.add( regulSubDataList3.get(i) );
        }

        return list;
    }

    public ArrayList<RegulationSubData3> getRegulSubMenu3( int sub2_seq )
    {
        ArrayList<RegulationSubData3> list = new ArrayList<RegulationSubData3>();
        for( int i = 0; i < regulSubDataList3.size(); ++i )
        {
            if( regulSubDataList3.get(i).getCateSub2Seq() == sub2_seq )
                list.add( regulSubDataList3.get(i) );
        }

        return list;
    }

    public RegulationSubData3 getRegulSubData3( int sub3_seq )
    {
        for( int i = 0; i < regulSubDataList3.size(); ++i )
        {
            if( regulSubDataList3.get(i).getSeq() == sub3_seq )
                return regulSubDataList3.get(i);
        }

        return null;
    }

    public boolean DeleteCommentData( int seq )
    {
        for( int i = 0; i < commentDataList.size(); ++i )
        {
            if( seq == commentDataList.get(i).getSeq() )
            {
                commentDataList.remove(i);
                return true;
            }
        }

        return false;
    }

    public void DeleteCaseData( int seq )
    {
        for( int i = 0; i < myWriteList.size(); ++i )
        {
            if( seq == myWriteList.get(i).getSeq() )
            {
                myWriteList.remove(i);
                break;
            }
        }

        for( int i = 0; i < caseDataList.size(); ++i )
        {
            if( seq == caseDataList.get(i).getSeq() )
            {
                caseDataList.remove(i);
                break;
            }
        }
    }

    public void UpdateCaseFeq( int seq, int feq )
    {
        for( int i = 0; i < myWriteList.size(); ++i )
        {
            if( seq == myWriteList.get(i).getSeq() )
            {
                myWriteList.get(i).setFeq( feq );
                break;
            }
        }

        for( int i = 0; i < caseDataList.size(); ++i )
        {
            if( seq == caseDataList.get(i).getSeq() )
            {
                caseDataList.get(i).setFeq( feq );
                break;
            }
        }
    }

    public void UpdatePromotionFeq( int seq, int feq )
    {
        for( int i = 0; i < promotionDataList.size(); ++i )
        {
            if( promotionDataList.get(i).getSeq() == seq )
            {
                promotionDataList.get(i).setFeq( feq );
                break;
            }
        }
    }

    public CaseData ExistCaseData( int seq )
    {
        for( int i = 0; i < caseDataList.size(); ++i )
        {
            if( seq == caseDataList.get(i).getSeq() )
                return caseDataList.get(i);
        }

        return null;
    }

    public boolean UpdateUserProfile( JSONObject jsonObject )
    {
        try {
            userData.setName( jsonObject.get("name").toString() );
            userData.setPassword( jsonObject.get("password").toString() );
            userData.setGender( Integer.parseInt( jsonObject.get("gender").toString() ) );
            userData.setUniv( jsonObject.get("univ").toString() );
            userData.setBirth( jsonObject.get("birth").toString() );
            userData.setPhone( jsonObject.get("phone").toString() );

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public int getRegulMainSeq( String str )
    {
        for( int i = 0; i < regulMainDataList.size(); ++ i)
        {
            if( regulMainDataList.get(i).getRegul().contains( str ) )
                return regulMainDataList.get(i).getSeq();
        }

        return -1;
    }

    public boolean ParsingCaseYearList( JSONObject jsonObject )
    {
        caseYearList.clear();
        try{
            JSONArray dataArray = jsonObject.getJSONArray("data_list");
            for(int i=0; i < dataArray.length(); i++)
            {
                JSONObject jObject = dataArray.getJSONObject(i);
                caseYearList.add( Integer.valueOf( jObject.get("year").toString() ) );
            }
            Collections.sort( caseYearList );
            Collections.reverse( caseYearList );
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void ClearCaseDataList()
    {
        caseDataList.clear();
    }

    public void ClearPromotionDataList()
    {
        promotionDataList.clear();
    }

    public void ClearAnnounceDataList()
    {
        announceDataList.clear();
    }

    // String 내림차순
    class DescendingCaseData implements Comparator<CaseData> {

        @Override
        public int compare(CaseData o1, CaseData o2) {
            return o1.getTimestamp().compareTo(o2.getTimestamp());
        }
    }

    // String 내림차순
    class DescendingPromotionData implements Comparator<PromotionData> {

        @Override
        public int compare(PromotionData o1, PromotionData o2) {
            return o1.getTimestamp().compareTo(o2.getTimestamp());
        }

    }
}
