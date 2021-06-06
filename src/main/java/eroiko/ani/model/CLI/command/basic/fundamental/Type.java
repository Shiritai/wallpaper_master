package eroiko.ani.model.CLI.command.basic.fundamental;

public enum Type {
    /* Basic */
    COMMAND(1, "command"),
    CD(1, "cd"),
    CAT(2, "cat"),
    HISTORY(4, "history"),
    LS(5, "ls"),
    MKDIR(6, "mkdir"),
    RM(7, "rm"),
    SEARCH(8, "search"),
    TOUCH(9, "touch"),
    LN(10, "ln"),
    CLEAR(11, "ln"),
    /* Special */
    ARTWORK(101, "artwork"),
    MEOW(102, "meow"),
    MUSIC(103, "music"),
    WALLPAPER(104, "wallpaper");

    final int code;
    final String name;

    private Type(int code, String name){
        this.code = code;
        this.name = name;
    }
    
    public final int getCode(){ return this.code; }
    public final String getName(){ return this.name; }
}
