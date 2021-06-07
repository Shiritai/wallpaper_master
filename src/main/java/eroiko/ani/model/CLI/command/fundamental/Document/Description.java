package eroiko.ani.model.CLI.command.fundamental.Document;

public class Description {
    public final String [] optionTypes;
    public final String description;

    public Description(String [] optionTypes, String description){
        this.optionTypes = optionTypes;
        this.description = description;
    }

    @Override
    public String toString(){
        var tmp = new StringBuilder();
        tmp.append('\t').append(String.join(", ", optionTypes)).append('\n');
        tmp.append("\t\t").append(description).append("\n\n");
        return tmp.toString();
    }
}
