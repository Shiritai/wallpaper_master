package eroiko.ani.model.CLI.command.fundamental.Document;

public class Name {
    public final String first;
    public final String second;

    /**
     * The NAME part of the manual document
     * <p> You can also use this as a String pair
     * <p> The first argument should not be null
     * @param first : the command name, usually in lower case
     * @param description : the description of this command 
     */
    public Name(String first, String description){
        this.first = first;
        this.second = description;
        if (first == null){
            throw new IllegalArgumentException("The first argument should not be null!");
        }
    }

    /** 
     * This will treat this class as a name token and print with NAME format :
     * @return {@code first.toUpperCase() + "\n\nNAME\n\t" + first + " - " + second}
     */
    @Override
    public String toString(){
        return first.toUpperCase() + "\n\nNAME\n\t" + first + ((second != null) ? " - " + second : "");
    }

    /** 
     * This will treat this class as a name token and print with NAME format :
     * @return {@code first.toUpperCase() + "\n\nNAME\n\t" + first + " - " + second}
     */
    public String stringPair(){
        return first + ((second != null) ? " -- " + second : "");
    }
}
