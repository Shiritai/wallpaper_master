package eroiko.ani.model.CLI.command.special.Shell;

import eroiko.ani.model.CLI.command.fundamental.Type;

public class Cmd extends Shell {
    
    public Cmd(String parameter){
        super((!parameter.equals("cmd") && !parameter.equals("cmd.exe")) ? parameter : "", Type.CMD);
    }
    
    @Override
    public void execute() throws IllegalArgumentException {
        openShell("cmd /C");
        // try {
        //     var process = Runtime.getRuntime().exec("cmd /C " + "\"" + parameter + "\"");
        //     var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        //     var output = new StringBuilder();
        //     String tmpStr;
        //     while ((tmpStr = reader.readLine()) != null){
        //         output.append(tmpStr).append('\n');
        //     }
        //     int exitVal = process.waitFor();
        //     if (exitVal == 0){
        //         out.println(id.getName() + " : execute successfully with exit code : " + exitVal);
        //         out.println(output);
        //     }
        //     else {
        //         out.println(id.getName() + " : execute failed with exit code : " + exitVal);
        //     }
        // } catch (Exception ex){
        //     ex.printStackTrace();
        // }
    }
}
