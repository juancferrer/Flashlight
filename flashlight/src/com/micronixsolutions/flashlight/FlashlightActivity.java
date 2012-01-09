package com.micronixsolutions.flashlight;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.micronixsolutions.flashlight.Flashlight;


public class FlashlightActivity extends Activity implements OnCheckedChangeListener {
    public static final String TAG = "FlashlightApp";
    private ToggleButton toggleButton = null;
    private Flashlight flashlight = null;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.main);
        
        //Set the toggle button's on checked listener to this...
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);
        toggleButton.setOnCheckedChangeListener(this);
        
        flashlight = new Flashlight(); //Sweet new flashlight object
        
        //The toggle button could be 'on' if we rotated device, and activity was destroyed/created
        if(toggleButton.isChecked())
            flashlight.setTorch(true);
    }
    
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        //The flashlight holds and instance of a camera...make sure to stop it
        flashlight.stop();
    }
    
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onCheckedChanged: " + isChecked);
        flashlight.setTorch(isChecked);
    }
}