package eroiko.ani.model.CLI.command.fundamental.Document;

public class Document {
    public final Synopsis synopsis;
    public final Descriptions descriptions;
    public final String notes;
    
    public Document(Synopsis synopsis, Descriptions descriptions, String notes){
        this.synopsis = synopsis;
        this.descriptions = descriptions;
        this.notes = notes;
    }

    public Document(Synopsis synopsis, Descriptions descriptions){
        this(synopsis, descriptions, null);
    }

    public Document(Synopsis synopsis){
        this(synopsis, null, null);
    }

    @Override
    public String toString(){
        return synopsis.toString() + 
        ((descriptions != null) ? descriptions.toString() : "") + 
        ((notes != null) ? notes : "");
    }

    public String getBriefSynopsis(){
        return synopsis.name.stringPair();
    }

    public String getName(){
        return synopsis.name.first;
    }
}
