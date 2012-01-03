package com.micronixsolutions.flashlight;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.hardware.Camera;

import java.io.IOException;

public class FlashlightActivity extends Activity implements OnCheckedChangeListener {
    public static final String TAG = "FlashlightApp";
    
    private Camera cam = null;
    private SurfaceView preview = null;
    private SurfaceHolder previewHolder = null;
    private Boolean surface_exists = false;
    private ToggleButton toggleButton = null;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.main);

        toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);
        toggleButton.setOnCheckedChangeListener(this);
       
        preview = (SurfaceView) findViewById(R.id.surfaceView1);
        previewHolder = (SurfaceHolder) preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        
    }
    
    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            Log.d(TAG, "surfaceDestroyed");
            surface_exists = false;
            if(cam != null ){
                cam.release();
                Log.d(TAG, "camera released");

            }
        }
        
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            Log.d(TAG, "surfaceCreated");
            surface_exists = true;
        }
        
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // TODO Auto-generated method stub
            Log.d(TAG, "surfaceChanged");
            setTorch(toggleButton.isChecked());
        }
    };

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onCheckedChanged: " + isChecked);
        setTorch(isChecked);
    }
    
    public void setTorch(Boolean on){
        Log.d(TAG, "Setting torch mode to: " + on);
        if(on && surface_exists){
            cam = Camera.open();
            Camera.Parameters parameters = cam.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            cam.setParameters(parameters);
            try {
                cam.setPreviewDisplay(previewHolder);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            cam.startPreview();
        }
        else{
            if(cam != null)
                cam.release();
        }
    }
}