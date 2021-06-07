package eroiko.ani.model.CLI.util;

import java.io.PrintStream;
import java.util.Timer;
import java.util.TimerTask;

class TimeWaiter extends TimerTask{
    private final String msg;
    private final PrintStream out;
    public TimeWaiter(PrintStream out, String msg){
        this.msg = msg;
        this.out = out;
    }
    @Override
    public void run(){
        if (msg != null && out != null){
            out.println(msg);
        }
    }
}

public class TimeWait {
    public TimeWait(PrintStream out, String msg, int miniSecond){
        var timer = new Timer();
        timer.schedule(new TimeWaiter(out, msg), miniSecond);
        try {
            Thread.sleep(miniSecond);
        }
        catch(InterruptedException e) {}
        timer.cancel(); 
    }
    public TimeWait(int miniSecond){
        this(null, null, miniSecond); 
    }
}
