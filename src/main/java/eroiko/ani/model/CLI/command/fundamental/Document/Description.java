/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.CLI.command.fundamental.Document;

public class Description {
    public final String [] optionTypes;
    public final String [] description;

    public Description(String [] optionTypes, String [] description){
        this.optionTypes = optionTypes;
        this.description = description;
    }

    @Override
    public String toString(){
        var tmp = new StringBuilder();
        tmp.append('\t').append(String.join(", ", optionTypes)).append('\n');
        for (var d :description){
            tmp.append("\t\t").append(d).append("\n");
        }
        return tmp.append('\n').toString();
    }
}
