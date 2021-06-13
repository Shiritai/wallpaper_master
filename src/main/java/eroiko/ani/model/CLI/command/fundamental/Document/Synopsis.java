/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.CLI.command.fundamental.Document;

public class Synopsis {
    public final Name name;
    public final Name [] parameters;

    public Synopsis(Name name, Name [] parameters){
        this.name = name;
        this.parameters = parameters;
    }

    public Synopsis(Name name){
        this(name, null);
    }

    @Override
    public String toString(){
        var tmp = new StringBuilder();
        tmp.append("\n\nSYNOPSIS\n");
        for (var p : parameters){
            if (p.first != null){
                tmp.append("\t").append(name.first).append(' ').append(p.first).append('\n');
                if (p.second != null){
                    tmp.append("\t\t").append(p.second).append('\n');
                }
            }
        }
        return name.toString() + tmp.toString();
    }
}
