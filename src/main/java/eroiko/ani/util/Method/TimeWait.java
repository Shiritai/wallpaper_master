/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.util.Method;

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
            Thread.sleep(miniSecond);
        }
        catch(InterruptedException e) {}
        timer.cancel(); 
    }
}
