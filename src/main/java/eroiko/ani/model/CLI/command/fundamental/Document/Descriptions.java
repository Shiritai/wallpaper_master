/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.CLI.command.fundamental.Document;

public class Descriptions {
    public final Description [] list;
    public final String [] mainDescription;

    public Descriptions(String [] mainDescription, Description [] list){
        this.mainDescription = mainDescription;
        this.list = list;
    }

    @Override
    public String toString(){
        var tmp = new StringBuilder();
        tmp.append("\n\nDESCRIPTION");
        for (var m : mainDescription){
            tmp.append("\n\t").append(m);
        }
        tmp.append("\n\n");
        for (var d : list){
            tmp.append(d);
        }
        return tmp.toString();
    }
}
