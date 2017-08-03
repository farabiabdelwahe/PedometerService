package stepcounter.farabi.com.myapplication;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.anupcowkur.reservoir.Reservoir;

import java.io.IOException;

/**
 * Created by GSC on 02/08/2017.
 */

public class Pedometer extends Application {

    public static Pedometer instance = null;

    public static Context getInstance() {
        if (null == instance) {
            instance = new Pedometer();
        }
        return instance;
    }
 private int StepCount = 0 ;


        @Override
        public void onCreate() {
            super.onCreate();
            startService(new Intent(this, PedometerService.class));
            try {
                Reservoir.init(this, 2048); //in bytes
            } catch (IOException e) {
                //failure
            }
        }

    public int getStepCount() {
        return StepCount;
    }

    public void setStepCount(int stepCount) {
        StepCount = stepCount;
    }
}
