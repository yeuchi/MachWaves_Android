package com.ctyeung.machnumbercalculator;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.PointF;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;

import android.view.Menu;
import com.ctyeung.machnumbercalculator.R;
import com.ctyeung.machnumbercalculator.FragmentTab1;
import com.ctyeung.machnumbercalculator.FragmentTab2;
import com.ctyeung.machnumbercalculator.FragmentTab3;
import com.ctyeung.machnumbercalculator.TabListener;


@SuppressLint("WorldReadableFiles")
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Declare Tab Variable
	    ActionBar.Tab Tab1, Tab2, Tab3;
	    Fragment fragmentTab1 = new FragmentTab1();
	    Fragment fragmentTab2 = new FragmentTab2();
	    Fragment fragmentTab3 = new FragmentTab3();
	    
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		 
        ActionBar actionBar = getActionBar();
 
        // Hide Actionbar Icon
        actionBar.setDisplayShowHomeEnabled(false);
 
        // Hide Actionbar Title
        actionBar.setDisplayShowTitleEnabled(false);
 
        // Create Actionbar Tabs
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
 
        // Set Tab Icon and Titles
       // Tab1 = actionBar.newTab().setIcon(R.drawable.tab1);
        Tab1 = actionBar.newTab().setText("Camera");
        Tab2 = actionBar.newTab().setText("Configuration");
        Tab3 = actionBar.newTab().setText("About");
 
        // Set Tab Listeners
        Tab1.setTabListener(new TabListener(fragmentTab1));
        Tab2.setTabListener(new TabListener(fragmentTab2));
        Tab3.setTabListener(new TabListener(fragmentTab3));
 
        // Add tabs to actionbar
        actionBar.addTab(Tab1);
        actionBar.addTab(Tab2);
        actionBar.addTab(Tab3);
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
