package eroiko.ani.util.myDS;

import java.util.Timer;
import java.util.TimerTask;

class TimeWaiter extends TimerTask{
    @Override
    public void run(){}
}

public class TimeWait {
    public TimeWait(int miniSecond){
        var timer = new Timer();
        timer.schedule(new TimeWaiter(), miniSecond);
        try {
            Thread.sleep(1000);
        }
        catch(InterruptedException e) {}
        timer.cancel(); 
    }
}
