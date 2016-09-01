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
import android.view.Menu;
import android.widget.ListView;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            BTCFragment fragment = new BTCFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }
        //初期パラメータ読み込み＆ListViewへセット
        initParameterSet();
        mlistView = (ListView) findViewById(R.id.p_list);
        mlistView.setAdapter(mCustomAdapater);  //java.lang.RuntimeExceptionでエラー
    }
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