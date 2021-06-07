package eroiko.ani.model.CLI.command.basic;

import java.util.ArrayList;

import eroiko.ani.model.CLI.command.fundamental.*;

/** 以 ArrayList 實作 */
public class History extends Command {

    private static ArrayList<String> commands = new ArrayList<>();
    private static int currentIndex = 0;
    
    public History(){
        super(Type.HISTORY);
    }
    
    @Override
    public void execute(){
        int lineCnt = 1;
        for (var c : commands){
            out.println("No. " + (lineCnt++) + "\t" + c);
        }
    }

    public static void addHistory(String command){
        commands.add(command);
        ++currentIndex;
    }

    public static String getPreviousCommand(){
        if (commands.isEmpty() || currentIndex == -1){
            return null;
        }
        else if (currentIndex == commands.size()){
            return commands.get(--currentIndex);
        }
        return commands.get(currentIndex--);
    }
    
    public static String getNextCommand(){
        if (commands.isEmpty() || currentIndex == commands.size()){
            return null;
        }
        else if (currentIndex == -1){
            return commands.get(++currentIndex);
        }
        return commands.get(currentIndex++);
    }

    public static void restoreCommandTraverse(){
        currentIndex = commands.size() - 1;
    }
}
