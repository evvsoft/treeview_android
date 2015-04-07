/*
 * Copyright © 2015 The Evvsoft TreeView Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evvsoft.treeview.demo;

import org.json.JSONArray;
import org.json.JSONException;

import com.evvsoft.treeview.SimpleJsonTreeViewAdapter;
import com.evvsoft.treeview.TreeView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

public class DemoActivity extends Activity {

	private final static String[] from = {"name"};
	private final static String IDENT_THEME = "theme";
	private final static String IDENT_INDICATOR = "indicator";
	private final static String IDENT_JSON = "json";
	private int mTheme = 0;
	private int mIndicator = 0;
	private SimpleJsonTreeViewAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mTheme = savedInstanceState.getInt(IDENT_THEME);
			if (mTheme > 0)
				setTheme(mTheme);
			mIndicator = savedInstanceState.getInt(IDENT_INDICATOR);
		};
		setContentView(R.layout.activity_demo);
		TreeView treeView = (TreeView) findViewById(R.id.treeView1);
		
		try {
			String jsonStr = null;
			if (savedInstanceState != null)
				jsonStr = savedInstanceState.getString(IDENT_JSON);
			if (jsonStr == null)
				jsonStr = "[{id:1,id_parent:null,name:name1},{id:3,id_parent:2,name:name3},{id:2,id_parent:null,name:name2},{id:4,id_parent:3,name:name4},{id:5,id_parent:4,name:name5},{id:6,id_parent:2,name:name6},{id:7,id_parent:6,name:name7},{id:8,id_parent:7,name:name8}]";

			JSONArray demoArray = new JSONArray(jsonStr);
			mAdapter = new SimpleJsonTreeViewAdapter(this, demoArray, from);
			
			View header = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
			TextView tv = (TextView) header.findViewById(android.R.id.text1);
			tv.setText("Demo Header");
			treeView.addHeaderView(header);
			View footer = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
			tv = (TextView) footer.findViewById(android.R.id.text1);
			tv.setText("Demo Footer");
			treeView.addFooterView(footer);
			
			if (mIndicator != 0)
				treeView.setGroupIndicatorResource(mIndicator);
			//treeView.setChoiceMode(TreeView.CHOICE_MODE_SINGLE);
			treeView.setAdapter(mAdapter);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mTheme != 0)
			outState.putInt(IDENT_THEME, mTheme);
		if (mIndicator != 0)
			outState.putInt(IDENT_INDICATOR, mIndicator);
		if (mAdapter != null) {
			String json = mAdapter.toString();
			outState.putString(IDENT_JSON, json);
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SubMenu sub = menu.addSubMenu("Theme");
        sub.add(0, R.style.Theme_TreeView_Light, 0, "Light");
        sub.add(0, R.style.Theme_TreeView_Dark, 0, "Dark");
        sub.add(0, R.drawable.expander_custom_group_holo_light, 0, "Custom group indicator");
        sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	int id = item.getItemId();
        if (id == android.R.id.home || id == 0)
            return false;
        mTheme = 0;
        mIndicator = 0;
        switch (id) {
        case R.style.Theme_TreeView_Light:
        case R.style.Theme_TreeView_Dark:
        	mTheme = id;
        	break;
        case R.drawable.expander_custom_group_holo_light:
        	mIndicator = id;
        	break;
        }
        recreate();
        return true;
    }

}
