package eroiko.ani.util;

import java.util.Timer;
import java.util.TimerTask;

class TimeWater extends TimerTask{
    @Override
    public void run(){}
}

public class TimeWait {
    public TimeWait(int miniSecond){
        var timer = new Timer();
        timer.schedule(new TimeWater(), miniSecond);
        try {
            Thread.sleep(1000);
        }
        catch(InterruptedException e) {
        }
        timer.cancel(); 
    }
}
