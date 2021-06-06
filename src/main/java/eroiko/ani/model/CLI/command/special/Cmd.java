package eroiko.ani.model.CLI.command.special;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import eroiko.ani.model.CLI.command.fundamental.Command;
import eroiko.ani.model.CLI.command.fundamental.Type;

public class Cmd extends Command {

    private final String parameter;
    
    public Cmd(String parameter){
        super(Type.CMD);
        this.parameter = (!parameter.equals("cmd") && !parameter.equals("cmd.exe")) ? parameter : "";
    }
    
    @Override
    public void execute() throws IllegalArgumentException {
        try {
            var process = Runtime.getRuntime().exec("cmd /C " + "\"" + parameter + "\"");
            var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            var output = new StringBuilder();
            String tmpStr;
            while ((tmpStr = reader.readLine()) != null){
                output.append(tmpStr).append('\n');
            }
            int exitVal = process.waitFor();
            if (exitVal == 0){
                out.println(id.getName() + " : execute successfully with exit code : " + exitVal);
                out.println(output);
            }
            else {
                out.println(id.getName() + " : execute failed with exit code : " + exitVal);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void exeAfterRequest(String cmd) {
        
    }
}
