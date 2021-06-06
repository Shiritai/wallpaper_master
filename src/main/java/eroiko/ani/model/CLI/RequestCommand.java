package eroiko.ani.model.CLI;

import eroiko.ani.model.CLI.command.basic.fundamental.*;

public class RequestCommand {
    private boolean needReq;
    // private boolean hasCmd;
    // private String cmd;
    // private Type requestType; // 又或者用一個 Command
    private Command queryCommand;

    public RequestCommand(){
        needReq = false;
        // hasCmd = false;
    }

    public boolean checkNeedRequest(){ return needReq; }
    
    public void pushRequest(Command queryCommand, String requestMsg){
        // this.cmd = cmd;
        // hasCmd = true;
        this.queryCommand = queryCommand;
        System.out.println(requestMsg);
        needReq = true;
    }

    // public boolean checkHasResult(){ return hasCmd; }

    public void takeCommand(String cmd){
        queryCommand.exeAfterRequest(cmd);
        needReq = false;
        // hasCmd = false;
        // return cmd;
    }
}
