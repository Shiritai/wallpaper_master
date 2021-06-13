/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.CLI.command.fundamental.Document;

public class Name {
    public final String first;
    public final String second;
    public final String [] nickName; // 為之後 Alias 開路

    /**
     * The NAME part of the manual document
     * <p> Can also be used as a String pair
     * <p> The first argument should not be null
     * @param first : the command name, usually in lower case
     * @param description : the description of this command 
     */
    public Name(String first, String description, String... nickName){
        this.first = first;
        this.second = description;
        this.nickName = nickName;
        if (first == null){
            throw new IllegalArgumentException("The first argument should not be null!");
        }
    }

    /** 
     * This will treat this class as a name token with the NAME format :
     * @return {@code first.toUpperCase() + "\n\nNAME\n\t" + first + " - " + second}
     */
    @Override
    public String toString(){
        return first.toUpperCase() + "\n\nNAME\n\t" + first + ((second != null) ? " - " + second : "");
    }

    /** 
     * This will treat this class as a String pair :
     * @return {@code first + ((second != null) ? " -- " + second : ""}
     */
    public String stringPair(){
        return first + ((second != null) ? " -- " + second : "");
    }
}
