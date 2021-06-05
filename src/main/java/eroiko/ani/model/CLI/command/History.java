package eroiko.ani.model.CLI.command;

import java.util.Stack;

public class History extends Command {

    private static Stack<String> commandStack = new Stack<>();
    private static Stack<String> commandStackCatch = new Stack<>();
    
    public History(){}
    
    @Override
    public void execute(){
        int lineCnt = 1;
        for (var c : commandStack){
            System.out.println("No. " + (lineCnt++) + "\t" + c);
        }
    }

    public static void addHistory(String command){
        commandStack.add(command);
    }

    public static String getPreviousCommand(){
        if (commandStack.isEmpty()){
            return null;
        }
        var res = commandStack.pop();
        commandStackCatch.add(res);
        return res;
    }
    
    public static String getNextCommand(){
        if (commandStackCatch.isEmpty()){
            return null;
        }
        var res = commandStackCatch.pop();
        commandStack.add(res);
        return res;
    }

    public static void restoreCommandTraverse(){
        while (!commandStackCatch.isEmpty()){
            commandStack.add(commandStackCatch.pop());
        }
    }
    
}
