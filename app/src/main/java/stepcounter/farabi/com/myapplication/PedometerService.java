package stepcounter.farabi.com.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Date;

import stepcounter.farabi.com.myapplication.Interfaces.StepListener;
import stepcounter.farabi.com.myapplication.Interfaces.UpdateActivity;
import stepcounter.farabi.com.myapplication.StepCounter.StepDetector;
/**
 * Created by GSC on 31/07/2017.
 */

public class PedometerService  extends Service implements SensorEventListener , StepListener {
    private int numSteps = 0 ;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Date currentdate ;
    private Sensor accel;

    private UpdateActivity mUpdateAcitivity;
    public class PedometerBinder extends Binder {
        PedometerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return PedometerService.this;
        }
    }
    //Service binder
    private final IBinder mBinder = new PedometerBinder();



    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//thread to handle the long process
        new Thread(new Runnable() {
            @Override
            public void run() {
                currentdate= new Date () ;
                sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                simpleStepDetector = new StepDetector();
                simpleStepDetector.registerListener(PedometerService.this);
                sensorManager.registerListener(PedometerService.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
                final Handler handler = new Handler(Looper.getMainLooper());
                final int delay = 1000 * 60 ; //milliseconds
                //check if date changed every minute ;
                handler.postDelayed(new Runnable(){
                    public void run(){
                        if ( daypassed(currentdate , new Date())) {
                            currentdate =  new Date() ;
                            numSteps = 0 ;
                        }

                        //garbage collector because why not  ;

                        handler.postDelayed(this, delay);
                    }
                }, delay);

            }
        }).start();

        return Service.START_STICKY ;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    sensorEvent.timestamp, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        Log.e("Step made " , String.valueOf(numSteps)) ;

        if ( MainActivity.active)
        mUpdateAcitivity.updatestepcount(numSteps);

    }

    //chech if date  changed .

    private static  boolean daypassed ( Date today , Date date   ) {


        if (new Date().after(date)) {
            return true ;
        }
        else   {
            return false ;

        }

    }


     public  int getsteps()  {
          return this.numSteps ;

     }
    public void setCallbacks(UpdateActivity callbacks) {
        mUpdateAcitivity = callbacks;
    }
}
