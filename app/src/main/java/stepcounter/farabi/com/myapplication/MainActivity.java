package stepcounter.farabi.com.myapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import stepcounter.farabi.com.myapplication.Interfaces.StepListener;
import stepcounter.farabi.com.myapplication.Interfaces.UpdateActivity;
import stepcounter.farabi.com.myapplication.StepCounter.StepDetector;

public class MainActivity extends AppCompatActivity implements UpdateActivity {
    private TextView textView;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    static boolean active = false;

    private static final String TEXT_NUM_STEPS = "Number of Steps Today: ";
    //number of steps

    TextView TvSteps ;
    Button BtnStart ;
    Button BtnStop ;

    PedometerService mService;
    boolean mBound = false;





    protected void onStart() {
        super.onStart();
        active = true ;
        // Bind to LocalService
        Intent intent = new Intent(this, PedometerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind to LocalService
        final Intent intent = new Intent(getApplicationContext(), PedometerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
       //   startService(intent);



        BtnStart = (Button) findViewById(R.id.btn_start);
        BtnStop = (Button) findViewById(R.id.btn_stop);



        BtnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {





            }
        });


        BtnStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {



                stopService(intent) ;


            }
        });





    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
           unbindService(mConnection);
            mBound = false;
            active = false ;
        }

    }


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {


            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PedometerService.PedometerBinder binder = (PedometerService.PedometerBinder) service;

            Toast.makeText(MainActivity.this, "Service is connected", Toast.LENGTH_LONG).show();
            mService = binder.getService();
            mBound = true;
            TvSteps = (TextView) findViewById(R.id.tv_steps);
            mService.setCallbacks(MainActivity.this);
            int  num = 0 ;
             num = mService.getsteps();
            TvSteps.setText(TEXT_NUM_STEPS + num);

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;


        }
    };


    @Override
    public void updatestepcount(int i) {
        mService.setCallbacks(MainActivity.this);
        int  num = 0 ;
         num = mService.getsteps();


        TvSteps.setText(TEXT_NUM_STEPS + num);

    }
}
