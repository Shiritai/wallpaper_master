package eroiko.ani.controller.PromptControllers;

import java.io.PipedInputStream;

import eroiko.ani.util.MyDS.ArrayByteToString;
import javafx.beans.property.StringProperty;

public class ConsoleThread implements Runnable {

    // private Service<Void> runner;
    private Thread runner;
    private final PipedInputStream in;
    private StringProperty terminal;

    /**
     * 利用 Round-Robin 的性質, 監聽 in 並輸出文字至 terminal (使用 appendText())
     * @param in : the piped inputStream of source
     * @param terminal : the string property to append text
     */
    public ConsoleThread(PipedInputStream in, StringProperty terminal){
        this.in = in;
        this.terminal = terminal;
        runner = new Thread(this);
        runner.setDaemon(true);
        runner.start();
    }
    
    @Override
    public synchronized void run() {
        while (Thread.currentThread() == runner){
            try {
                if (in.available() != 0){
                    var tmp = new ArrayByteToString();
                    do {
                        int av = in.available();
                        if (av == 0) break;
                        var b = new byte [av];
                        in.read(b);
                        tmp.add(b);
                    } while (!tmp.endsWith("\n"));
                    terminal.setValue(terminal.getValue() + tmp.toString());
                }
            } catch (Exception e){ e.printStackTrace(); }
        }
    }
}
