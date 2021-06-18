/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.controller.PromptControllers;

import java.io.IOException;
import java.io.PipedInputStream;

import eroiko.ani.util.MyDS.ArrayByteToString;
import javafx.scene.control.TextArea;

public class TerminalThread implements Runnable {
    private final PipedInputStream outPusher;
    private Thread outPuller;
    private TextArea terminalText;
    private boolean quitFlag = false;

    public TerminalThread(PipedInputStream outPusher, Thread outPuller, TextArea terminalText, boolean quit){
        this.outPuller = outPuller;
        this.outPusher = outPusher;
        this.terminalText = terminalText;
        this.quitFlag = quit;
        this.quitFlag = false;

        this.outPuller = new Thread(this);
        this.outPuller.setDaemon(true);
        this.outPuller.start();
    }

    @Override
    public synchronized void run(){
        while (Thread.currentThread() == outPuller){
            try {
                wait(50l);
            } catch (InterruptedException ie){
                System.out.println(ie.toString());
                System.err.println(ie.toString());
            }
            try {
                if (this.outPusher.available() != 0){
                    var tmp = new ArrayByteToString();
                    do {
                        int av = this.outPusher.available();
                        if (av == 0) break;
                        byte[] b = new byte[av];
                        this.outPusher.read(b);
                        tmp.add(b);
                    } while (!tmp.endsWith("\n") && !this.quitFlag);
                    this.terminalText.appendText(tmp.toString());
            }
            } catch (IOException ie) {
                System.out.println(ie.toString());
                System.err.println(ie.toString());
            }
        }
    }
}