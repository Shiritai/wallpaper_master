package eroiko.ani.model.CLI.conversation;

public interface Consultable {
    /** 
     * 請在各自指令完成內部狀態的紀錄, 獲得使用者回應後可以繼續執行 
     * @param cmd : 獲得的使用者答覆, 由 Consulter 類呼叫
     */
    abstract public void exeAfterRequest(String cmd);
    /**
     * 請實作向使用者詢問的內容
     * @return
     */
    abstract public String getPushingMessage();
}
