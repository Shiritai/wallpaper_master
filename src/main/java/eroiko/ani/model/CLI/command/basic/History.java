package eroiko.ani.model.CLI.command.basic;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import eroiko.ani.model.CLI.command.fundamental.*;

class Data {
    public String time;
    public String cmd;
    public Data(String cmd, String time){
        this.cmd = cmd;
        this.time = time;
    }
}

/** 以 ArrayList 實作 */
public class History extends Command {

    private static ArrayList<Data> commands = new ArrayList<>();
    private static int currentIndex = 0;
    
    public History(){
        super(Type.HISTORY);
    }
    
    @Override
    public void execute(){
        int lineCnt = 1;
        for (var d : commands){
            out.println("[" + d.time + "]  No. " + (lineCnt++) + "\t" + d.cmd);
        }
    }

    public static void addHistory(String command){
        commands.add(new Data(
            command, DateFormat.getTimeInstance(DateFormat.MEDIUM).format(new Date())
        ));
        ++currentIndex;
    }

    public static String getPreviousCommand(){
        if (commands.isEmpty() || currentIndex == -1){
            return null;
        }
        else if (currentIndex == commands.size()){
            return commands.get(--currentIndex).cmd;
        }
        return commands.get(currentIndex--).cmd;
    }
    
    public static String getNextCommand(){
        if (commands.isEmpty() || currentIndex == commands.size()){
            return null;
        }
        else if (currentIndex == -1){
            return commands.get(++currentIndex).cmd;
        }
        return commands.get(currentIndex++).cmd;
    }

    public static void restoreCommandTraverse(){
        currentIndex = commands.size() - 1;
    }
}
