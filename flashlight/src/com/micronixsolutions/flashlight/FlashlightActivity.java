package com.micronixsolutions.flashlight;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.micronixsolutions.flashlight.Flashlight;


public class FlashlightActivity extends Activity implements OnCheckedChangeListener {
    public static final String TAG = "FlashlightApp";
    private ToggleButton toggleButton = null;
    private CheckBox checkBox = null;
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
        
        //Save a reference to the checkbox
        checkBox = (CheckBox) findViewById(R.id.checkBox1);
        
        flashlight = new Flashlight(this); //Sweet new flashlight object. Make sure to pass this activity as the context
        
        //The flashlight gives us a surfaceview/preview for the camera.
        //If we don't add this to the activity, then the flashlight won't work on samsung devices
        addContentView(flashlight.getSurfaceView(), new ViewGroup.LayoutParams(1,1)); //Make it 1px/1px
        
        //The toggle button could be 'on' if we rotated device, and activity was destroyed/created
        if(toggleButton.isChecked())
            flashlight.setTorch(true);
    }
    
    @Override
    public void onDestroy(){
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        //Always stop the flashlight when the app quits
        flashlight.stop();
    }
    
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        Log.d(TAG, "onStop");
        //Only stop the flashlight if the checkbox isn't checked.
        if(!checkBox.isChecked()){
            toggleButton.setChecked(false); //onCheckedChange will change the flashlight state
            flashlight.stop();
        }
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        Log.d(TAG, "onRestart");
        flashlight.init(); //Re-init the camera
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onCheckedChanged: " + isChecked);
        flashlight.setTorch(isChecked);

    }
}