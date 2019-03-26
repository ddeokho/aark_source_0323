package automacticphone.android.com.casebook.activity.activity;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;

import automacticphone.android.com.casebook.R;
import automacticphone.android.com.casebook.activity.HomeActivity;
import automacticphone.android.com.casebook.activity.common.DataManager;
import automacticphone.android.com.casebook.activity.data.CaseCategoryData;
import automacticphone.android.com.casebook.activity.data.RegulationMainData;
import automacticphone.android.com.casebook.activity.data.RegulationSubData1;
import automacticphone.android.com.casebook.activity.data.RegulationSubData2;
import automacticphone.android.com.casebook.activity.data.RegulationSubData3;
import automacticphone.android.com.casebook.activity.fragment.UploadContentsFragment;

public class SelectCategoryActivity extends AppCompatActivity {

    private CheckBox cateEtcCheckBox;
    private Spinner selectCategoryMainSpinner;
    private Spinner selectCategorySubSpinner1;
    private Spinner selectCategorySubSpinner2;
    private Spinner selectCategorySubSpinner3;
    private int cateReg;
    private ArrayList<RegulationSubData1> cagegorySub1DataList;
    private ArrayList<RegulationSubData2> cagegorySub2DataList;
    private ArrayList<RegulationSubData3> cagegorySub3DataList;
    private int selectYear = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setContentView(R.layout.activity_select_category);
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } catch (IllegalStateException ignore) {

        }

        cateEtcCheckBox = null; /*(CheckBox) findViewById(R.id.category_popup_etc_checkBox);*/
        selectCategoryMainSpinner = (Spinner)findViewById(R.id.select_category_main_spinner);
        selectCategoryMainSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cateReg = DataManager.inst().getRegulMainDataList().get( position ).getSeq();
                        SetCategorySubData1( position );
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        selectCategorySubSpinner1 = (Spinner)findViewById(R.id.select_category_sub_spinner1);
        selectCategorySubSpinner1.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        SetCategorySubData2( position );
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        selectCategorySubSpinner2 = (Spinner) findViewById(R.id.select_category_sub_spinner2);
        selectCategorySubSpinner2.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        SetCategorySubData3( position );
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        selectCategorySubSpinner3 = (Spinner) findViewById(R.id.select_category_sub_spinner3);
        selectCategorySubSpinner3.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        SetYearPicker();
        SetCategoryMainData();

        Button okBtn = (Button) findViewById(R.id.select_category_ok_btn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                UploadContentsFragment fragment = (UploadContentsFragment)HomeActivity.inst().getCurrentFragment();
                CaseCategoryData data = new CaseCategoryData();
                if( cateEtcCheckBox.isChecked() )
                {
                    int case_reg = DataManager.inst().getRegulMainSeq( "기타" );
                    data.setCateReg( case_reg );
                    RegulationMainData mainData = DataManager.inst().getRegulationMainData(case_reg );
                    cagegorySub1DataList = DataManager.inst().getRegulSubMenu1( mainData );
                    if( cagegorySub1DataList.size() > 0 )
                    {
                        data.setCate1( cagegorySub1DataList.get(0).getSeq() );
                        cagegorySub2DataList = DataManager.inst().getRegulSubMenu2( cagegorySub1DataList.get(0) );
                    }

                    if( cagegorySub2DataList.size() > 0 )
                    {
                        data.setCate2( cagegorySub2DataList.get(0).getSeq() );
                        cagegorySub3DataList = DataManager.inst().getRegulSubMenu3( cagegorySub2DataList.get(0) );
                    }

                    if( cagegorySub3DataList.size() > 0 )
                        data.setCate3( cagegorySub3DataList.get(0).getSeq() );
                }
                else
                {
                    data.setCateReg( cateReg );
                    int idx = (int)selectCategorySubSpinner1.getSelectedItemId();
                    int selectIndex = cagegorySub1DataList.get(idx).getSeq();
                    data.setCate1( selectIndex );

                    idx = (int)selectCategorySubSpinner2.getSelectedItemId();
                    selectIndex = cagegorySub2DataList.get(idx).getSeq();
                    data.setCate2( selectIndex );

                    idx = (int)selectCategorySubSpinner3.getSelectedItemId();
                    selectIndex = cagegorySub3DataList.get(idx).getSeq();
                    data.setCate3( selectIndex );
                }

                data.setSelectYear( selectYear );
                fragment.setCaseCategoryData( data );
                finish();
            }
        });

        Button cancelBtn = (Button)findViewById(R.id.select_category_cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void SetYearPicker()
    {
        Calendar calendar = Calendar.getInstance();
        selectYear = calendar.get(Calendar.YEAR);

        NumberPicker np = (NumberPicker)findViewById( R.id.select_category_year_picker );
        np.setMinValue( selectYear - 100);
        np.setMaxValue( selectYear + 100);
        np.setDescendantFocusability( NumberPicker.FOCUS_BLOCK_DESCENDANTS );
        np.setValue( selectYear );
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.d( "YEAR_PICKER: " , String.valueOf(newVal ));
                selectYear = newVal;
            }
        });
    }

    void SetCategorySubData2( int idx )
    {
        int sub1_seq = cagegorySub1DataList.get(idx).getSeq();
        cagegorySub2DataList = DataManager.inst().getRegulSubMenu2( sub1_seq );
        ArrayList<String> menuList = new ArrayList<String>();
        for( int i = 0; i < cagegorySub2DataList.size(); ++i )
        {
            menuList.add( cagegorySub2DataList.get(i).getRegul() );
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(  SelectCategoryActivity.this, R.layout.custom_simple_dropdown_item_1line, menuList );
        selectCategorySubSpinner2.setAdapter(adapter);
        selectCategorySubSpinner2.setSelection(0);
    }

    void SetCategorySubData3( int idx )
    {
        int sub2_seq = cagegorySub2DataList.get(idx).getSeq();
        cagegorySub3DataList = DataManager.inst().getRegulSubMenu3( sub2_seq );
        ArrayList<String> menuList = new ArrayList<String>();
        for( int i = 0; i < cagegorySub3DataList.size(); ++i )
        {
            menuList.add( cagegorySub3DataList.get(i).getRegul() );
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>( SelectCategoryActivity.this, R.layout.custom_simple_dropdown_item_1line, menuList );
        selectCategorySubSpinner3.setAdapter(adapter);
        selectCategorySubSpinner3.setSelection(0);
    }

    void SetCategorySubData1( int idx )
    {
        cagegorySub1DataList = DataManager.inst().getRegulSubMenu1( idx );

        ArrayList<String> menuList = new ArrayList<String>();
        for( int i = 0; i < cagegorySub1DataList.size(); ++i )
        {
            menuList.add( cagegorySub1DataList.get(i).getRegul() );
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>( SelectCategoryActivity.this, R.layout.custom_simple_dropdown_item_1line, menuList );
        selectCategorySubSpinner1.setAdapter(adapter);
        selectCategorySubSpinner1.setSelection(0);
    }

    void SetCategoryMainData()
    {
        ArrayList<String> cagegoryMain = DataManager.inst().getRegulMainMenu();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>( SelectCategoryActivity.this, R.layout.custom_simple_dropdown_item_1line, cagegoryMain );
        selectCategoryMainSpinner.setAdapter(adapter);
        selectCategoryMainSpinner.setSelection(0);
    }
}
