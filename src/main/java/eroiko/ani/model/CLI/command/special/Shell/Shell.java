/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.CLI.command.special.Shell;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import eroiko.ani.model.CLI.command.fundamental.*;

public abstract class Shell extends Command {

    protected final String parameter;
    
    protected Shell(String parameter, Type type){
        super(type);
        this.parameter = parameter;
    }

    protected Shell(String parameter){
        this(parameter, Type.SHELL);
    }

    protected void openShell(String invokePrefix){
        try {
            var process = Runtime.getRuntime().exec(invokePrefix + " " + "\"" + parameter + "\"");
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
    
}
