/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.CLI.command.critical;

import java.io.PrintStream;
import java.util.concurrent.Callable;
import eroiko.ani.model.CLI.command.fundamental.Type;
import eroiko.ani.model.CLI.exception.*;

class ShutdownCallableThread implements Callable<Boolean> {
    public static final ShutdownSoftwareException shutdown = new ShutdownSoftwareException(Type.SHUTDOWN.toString());
    public static PrintStream out;
    public Boolean status; // true : no exception; false : throw exception
    public int printNumber;
    
    public static void assignPrintStream(PrintStream printStream){
        out = printStream;
    }
    
    public ShutdownCallableThread(int printNumber){
        this.status = printNumber == 0;
        this.printNumber = printNumber;
    }
    
    @Override
    public Boolean call() throws Exception {
        while (!status){
            out.println(new java.util.Date());
            out.println("The software will shutdown in " + printNumber-- + " second(s)");
            Thread.sleep(1000);
            status = printNumber == 0;
        }
        out.println("Goodbye, hope to see you soon!");
        Thread.sleep(1000);
        return true;
    }
}

public class Shutdown implements ThrowException {
    private final Type type;
    // private final int second;
    // private final PrintStream out;
    // private final ScheduledExecutorService service;
    // private int count;
    
    // public Shutdown(ScheduledExecutorService service, PrintStream out){
    //     this(service, out, 0);
    // }
    
    // public Shutdown(ScheduledExecutorService service, PrintStream out, int second){
    //     this.second = second;
    //     this.out = out;
    //     this.service = service;
    //     type = Type.SHUTDOWN;
    //     count = second;
    // }
    public Shutdown(){
        type = Type.SHUTDOWN;
    }

    @Override
    public void callHost() throws ShutdownSoftwareException {
        throw new ShutdownSoftwareException(type.toString());
        // if (count == 0){
            // throw new ShutdownSoftwareException(type.toString());
        // }
        // else { // 尚未成功，仍須努力
            // ShutdownCallableThread.assignPrintStream(out);
            // service.submit(new ShutdownCallableThread(count));
        // }
    }
}
