package eroiko.ani.model.CLI.command.fundamental;

public class Man extends Command {

    private final String cmdName;

    public Man(String cmdName){
        super(Type.MAN);
        this.cmdName = cmdName;
    }
    
    @Override
    public void execute(){
        if (cmdName == null || cmdName.equals("")){
            out.println("What command manual do you want to see?\nFor example, try 'man man'.");
        }
        else if (cmdName.equals("-a") || cmdName.equals("--all")){ // print all commands
            out.println("----- Below are all the available commands -----");
            for (var t : Type.values()){
                if (t.code > 0){
                    out.println(t.getBriefSynopsis());
                }
            }
        }
        else {
            try {
                out.println(Type.valueOf(cmdName.toUpperCase()).getDoc() + "\n");
            } catch (Exception e){
                out.println("No manual entry for " + cmdName);
            }
        }
    }

}
