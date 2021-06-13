/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.CLI.conversation;

import java.io.PrintStream;

public class Consulter {
    private boolean needReq;
    private Consultable queryCommand;
    private final PrintStream out;
    
    /** 對談器, 接受 Consultable 指令的對談請求, 並將獲得與使用者對談的結果傳回該指令 */
    public Consulter(PrintStream out){
        this.out = out;
        needReq = false;
    }

    /** 檢查當前是否有對談請求 */
    public boolean checkNeedRequest(){ return needReq; }
    
    /** 
     * 從 Consultable 指令接受對談請求
     * @param queryCommand : Consultable 指令
     */
    public void pushRequest(Consultable queryCommand){
        this.queryCommand = queryCommand;
        needReq = true;
        out.println(queryCommand.getPushingMessage());
    }

    /**
     * 將與使用者對談的答覆傳回至請求對談的指令
     * @param cmd : 使用者的答覆
     */
    public void takeCommand(String cmd){
        queryCommand.exeAfterRequest(cmd);
        needReq = false;
        queryCommand = null; // clean memory
    }
}
