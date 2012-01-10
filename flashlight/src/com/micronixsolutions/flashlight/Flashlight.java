package com.micronixsolutions.flashlight;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class Flashlight implements SurfaceHolder.Callback {
    private static final String TAG = "Flashlight";
    private int camId = -1;
    private boolean openCameraFail = false;
    private Camera cam = null;
    private Camera.Parameters parameters = null;
    private boolean on = false; //State of the flashlight
    
    private SurfaceView preview = null;
    private SurfaceHolder previewHolder = null;
    
    //Open the camera in a thread, and make sure that it supports 'torch' mode
    Thread openCameraThread = new Thread(new Runnable(){
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try{
                Log.d(TAG, "Opening cameraId: " + camId);
                cam = Camera.open(camId);
            } catch (RuntimeException e){
                Log.d(TAG, e.toString());
                openCameraFail = true;
                return; //TODO: Do something more appropriate
            }
            parameters = cam.getParameters();
            List<String> flash_modes = parameters.getSupportedFlashModes();
            Log.d(TAG, "Supported flash modes: " + flash_modes);
            if(flash_modes == null | !flash_modes.contains("torch")){
                openCameraFail = true;
                cam = null;
                return; //TODO: Do something more appropriate..this class is useless to you
            }

            // Flashlight user could have called 'setTorch(true)' while
            // we were initializing the camera...check for the state, and turn it on
            if(on)
                setTorch(true); //turn the flashlight on
        }
    });
    
    /* CONSTRUCTOR
     * 
     */
    public Flashlight(Context context){
        for(int i=0; i < Camera.getNumberOfCameras(); i++){
            //Assume that only rear facing cameras have a flash...pick the first one we find.
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            Log.d(TAG, "Camera info: " + info);
            if(info.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
                camId = i;
                break;
            }
        }
        if(camId == -1){
            //TODO: No camera found, raise exception or something... this class is useless to you
            openCameraFail = true;
            return;
        }
        
        init();
        //Make the surface view, and set the callback
        preview = new SurfaceView(context); //Pass the activity context
        previewHolder = preview.getHolder();
        previewHolder.addCallback(this);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    /** Call this to manually open the camera...if you previously called 'stop'
     * 
     */
    public void init(){
        //Begin opening the camera
        if(cam == null)
            openCameraThread.start();
    }
    
    /** Call this stop the flashlight, and release the camera
     * 
     */
    public void stop(){
        //release the camera
        if(cam != null){
            cam.release();
            cam = null;
        }
    }
    
    public SurfaceView getSurfaceView(){
        return this.preview; //Return the surface view preview, so user can display it.
    }
    
    /* MAGIC HAPPENS HERE
     * 
     */
    public void setTorch(Boolean on){
        Log.d(TAG, "Flashlight setTorch: " + on);
        this.on = on;
        if(on && cam != null){
            Log.d(TAG, "Starting Preview");
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            cam.setParameters(parameters);
            cam.startPreview();
        }
        else if (cam !=null) {
            Log.d(TAG, "Stopping Preview");
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            cam.setParameters(parameters);
            cam.stopPreview();
        }
    }

    /* SURFACE HOLDER CALLBACK INTERFACE
     * 
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        Log.d(TAG, "surfaceCreated");
        
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
        Log.d(TAG, "surfaceChanged:  Cam is: "+ cam);
        try {
            openCameraThread.join(); //Wait until the open camera thread finishes
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } 
        
        if(cam != null)
            try {
                cam.setPreviewDisplay(previewHolder);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        Log.d(TAG, "surfaceDestroyed");
        stop();

        
    }
}
