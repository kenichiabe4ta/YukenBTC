/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.example.android.bluetoothchat;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p/>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class MainActivity extends FragmentActivity {
    private ListView mlistView;
    private CustomAdapter mCustomAdapater;
    private CustomCanvas mCustomCanvas;

    // 連続データ受信用
    private android.os.Handler handler = new android.os.Handler();
    private Runnable timer;
    private boolean start_flg=false;
    private Button START;

    // パラメータ設定用UI view
    private TextView p_id,p_value;
    private Button x10_plus,x1_plus,x1_minus,x10_minus, OKbutton,CANCEL;
    private int temp_value;
    //private TextView mDEBUGtv;
    private TextView s_bt,r_bt;
    private boolean value_chg_f;

    private ParamDetails mParamDetails;
    // パラメータ設定用UI幅変更
    private LinearLayout p_editUI;
    private final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
    private float mWeight = 1.0f;


    // BTCFragment.java#setupChatで生成されるmChatServiceインスタンスと混同
    // MainActivity内でmChatService.write(send);としてmChatService=NULLでエラー
    private BTCFragment mBTCFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // キーボード非表示
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            mBTCFragment = new BTCFragment();
            transaction.replace(R.id.sample_content_fragment, mBTCFragment);
            transaction.commit();
        }

        // fragmentのviewをMainActivityから参照できるようにinflaterで追加
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout fragmentLayout = (LinearLayout)inflater.inflate(R.layout.activity_main_fragment, null);
        LinearLayout mainLinearLayout = (LinearLayout) findViewById(R.id.main_Linearlayout);
        mainLinearLayout.addView(fragmentLayout);

        // 初期パラメータ読み込み＆ListViewへセット
        initParameterSet();
        mlistView = (ListView) findViewById(R.id.p_list);
        mlistView.setAdapter(mCustomAdapater);

        // Canvasのview取得(これがないとmCustomCanvas=nullのままNullPointerException発生
        mCustomCanvas = (CustomCanvas) findViewById(R.id.customview);
        mBTCFragment.setCC(mCustomCanvas);



        // パラメータ設定用UI view
        x10_plus = (Button) findViewById(R.id.x10_plus);
        x1_plus = (Button) findViewById(R.id.x1_plus);
        p_id = (TextView) findViewById(R.id.p_id);
        p_value = (TextView) findViewById(R.id.p_value);
        x1_minus = (Button) findViewById(R.id.x1_minus);
        x10_minus = (Button) findViewById(R.id.x10_minus);
        OKbutton = (Button) findViewById(R.id.ok_bt);
        CANCEL = (Button) findViewById(R.id.cancel_bt);

        START = (Button) findViewById(R.id.start);
        //mDEBUGtv = (TextView) findViewById(R.id.state); //debug用
        s_bt = (Button) findViewById(R.id.s_bt);
        r_bt = (Button) findViewById(R.id.r_bt);

        // パラメータ設定用UI幅変更
        p_editUI = (LinearLayout) findViewById(R.id.p_editUI);
        setParamButton(false,false,false);   // button: +-/ok/cancel
        editUI_open(false);

        // ListViewをタップしたときの処理
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // タップされたパラメータのid,valueをedittextにセット
                ParamDetails pd = (ParamDetails) parent.getAdapter().getItem(position);
                temp_value= pd.getParam_value();         // プリミティブ型はDeepCopy
                p_id.setText(pd.getParam_id());
                p_value.setText(String.valueOf(temp_value));
                mParamDetails = pd;

                // パラメータ設定用UIを表示
                p_value.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                editUI_open(true);
                String msg = "選択パラメータid：" + pd.getParam_id();
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    // パラメータ設定用UI幅変更
    private void editUI_open(boolean open){
        if(open){
            mWeight=1.0f;
            setParamButton(true,false,true);   // button: +-/ok/cancel
        }else{
            mWeight=0.01f;
            setParamButton(false,false,false);   // button: +-/ok/cancel
        }
        //LinearLayout.LayoutParams(int width, int height, float weight)
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,WC,mWeight);
        p_editUI.setLayoutParams(params);
    }
    // タップ制御
    private void setParamButton(boolean edit, boolean ok, boolean cancel){
        x10_plus.setEnabled(edit);
        x1_plus.setEnabled(edit);
        OKbutton.setEnabled(ok);
        CANCEL.setEnabled(cancel);
        x1_minus.setEnabled(edit);
        x10_minus.setEnabled(edit);
    }
    // 初期パラメータ読込み
    private void initParameterSet() {
        AssetManager am = getResources().getAssets();
        InputStream is = null;
        BufferedReader br = null;
        try {
            try {
                is = am.open("init_parameter.txt");
                br = new BufferedReader(new InputStreamReader(is));
                String str;
                List<ParamDetails> pd_List = new ArrayList<>();
                while ((str = br.readLine()) != null) {
                    ParamDetails pd = new ParamDetails();
                    String[] st = str.split(",", 5);
                    //init_parameter.txt データ配置：no,id,name,value,unit
                    pd.setParam_no(st[0]);
                    pd.setParam_id(st[1]);
                    pd.setParam_name(st[2]);
                    pd.setParam_value(parseInt(st[3], 0));    // valueのみintで取り込み
                    pd.setParam_unit(st[4]);
                    pd_List.add(pd);
                }
                mCustomAdapater = new CustomAdapter(this, 0, pd_List);
                Log.d("init_parameter", pd_List.toString());
            } finally {
                if (br != null) br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "初期パラメータの読み込みに失敗しました。", Toast.LENGTH_LONG).show();
        }
    }
    public static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch ( NumberFormatException e ) {
            return defaultValue;
        }
    }
    public static int parseInt(String value) {
        return parseInt(value, 0);
    }
    // パラメータリスト用ボタン
    public void ds1_bt(View v){
        mlistView.setSelection(0);
    }
    public void ds2_bt(View v){
        mlistView.setSelection(10);
    }
    public void ds3_bt(View v){
        mlistView.setSelection(20);
    }
    // ダミーデータ描画
    public void sys_bt(View v){
        mlistView.setSelection(30);
        int[] wave_dt = new int[1024];
        for (int i=0; i<wave_dt.length; i++){ wave_dt[i]=i; }
        mCustomCanvas.setWavedt(wave_dt);
    }
    //↑TextView mTV = (TextView) findViewById(R.id.state);     // NG
    //mDEBUGtv.setText("CH1=dummy data");

    // debug用ボタン
    public void s_bt(View v){    // ダミーデータ送信
        byte[] send = new byte[2];
        send[0]=(byte)'s';          // LED点灯
        mBTCFragment.getmChatService().write(send);
    }
    public void r_bt(View v){    // ダミーデータ送信
        byte[] send = new byte[2];
        send[0]=(byte)'r';          // LED消灯
        mBTCFragment.getmChatService().write(send);
    }

    // パラメータ設定用ボタン
    public void x10_plus(View v){
        if((temp_value+=10)>10000){ temp_value=9999; }
        set_value(temp_value);
    }
    public void x1_plus(View v){
        if((++temp_value)>10000){ temp_value=9999; }
        set_value(temp_value);
    }
    public void x1_minus(View v){
        if((--temp_value)<0)    { temp_value=0; }
        set_value(temp_value);
    }
    public void x10_minus(View v){
        if((temp_value-=10)<0)  { temp_value=0; }
        set_value(temp_value);
    }
    private void set_value(int value){  // 変更データ表示
        p_value.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        p_value.setText(String.valueOf(value));
        setParamButton(true,true,true);   // button: +-/ok/cancel
    }
    public void ok_bt(View v){
        //mlistView.mParamDetails.setParam_value(temp_value);   // エラー
        //mParamDetails.setParam_value(temp_value);   // アクセスNG
        mParamDetails.setParam_value(temp_value);
        editUI_open(false);
        String msg = mParamDetails.getParam_id()+"のデータを変更しました。";
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        // 変更したデータを送信
        // byte[] send = new byte[2];
        // send[0]=(byte)'s';
        // mBTCFragment.getmChatService().write(send);
    }
    public void cancel_bt(View v){
        editUI_open(false);
    }

    // 連続データ受信用ボタン
    public void start(View v){
        if(start_flg){
            start_flg=false;
            START.setText("STOP");
            adStart();
        }else{
            start_flg=true;
            START.setText("START");
            handler.removeCallbacks(timer);
        }
    }
    private void adStart(){
        timer = new Runnable() {
            @Override
            public void run() {
                //繰り返し処理部分
                mCustomCanvas.invalidate();
                s_bt(s_bt);
                handler.postDelayed(timer,getParambyno(24));  //次回処理を２秒後にセット
            }
        };
        //初回実行処理部分
        s_bt(s_bt);
        handler.postDelayed(timer,getParambyno(24));
    }
    private int getParambyno(int no){
        ParamDetails p = (ParamDetails) mlistView.getAdapter().getItem(no);
        return p.getParam_value();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
