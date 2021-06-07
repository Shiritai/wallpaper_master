package eroiko.ani.model.CLI.command.fundamental;

import eroiko.ani.model.CLI.command.fundamental.Document.*;
// import java.util.function.Supplier;

/** 
 * 支持指令的類型辨識、查詢文件等操作
 * <p> Documentation is horrible...QQ
 */
public enum Type {
    /* Fundamental */
    COMMAND(0, new Document(
        new Synopsis(new Name("command", "This is the base of all commands, you shouldn't see this!"))
    )),

    MAN(3, new Document(
        new Synopsis(
            new Name("man", "an interface to the console reference manuals"), 
            new Name [] {
                new Name("COMMAND_NAME", "The command manual you'd like to search."),
                new Name("[Option]", null),
        }), 
        new Descriptions(
            new String [] {
                "Use the Option below to specify this command.",
            },
            new Description [] {
                new Description(
                    "-a --all".split(" "), 
                    "Print all available commands."
                ),
            }
        )
    )),


    /* Basic */
    CD(65, new Document(
        new Synopsis(new Name("cd", "change directory"), new Name [] {
            new Name("ABSOLUTE_PATH", "change directory to the ABSOLUTE_PATH."),
            new Name("RELATIVE_PATH", "change directory to the RELATIVE_PATH.")
        }), null, "Noted that [..] represents the parent directory and [.] means the directory itself."
    )),

    CAT(66, new Document(
        new Synopsis(new Name("cat", "concatenate files and print on the standard output"), new Name [] {
            new Name("FILE", "Concatenate a FILE to standard output.")
        })
    )),

    HISTORY(67, new Document(
        new Synopsis(new Name("history", "Eroiko History Library"), new Name [] {
            new Name("<NO_PARAMETER>", "Search all your history commands start from activating this console.")
        })
    )),

    LS(68, new Document(
        new Synopsis(new Name("ls", "list directory contents"), new Name [] {
            new Name("[OPTION]", null)
        }), new Descriptions(
            new String [] {
                "List information about the FILEs (the current directory by default).",
                "Sort entries with following order.",
            },
            new Description [] {
                new Description(
                    "<NO_PARAMETER> -s --sorted".split(" "), 
                    "the order determined by the software designer."
                ),
                new Description(
                    "-n --normal".split(" "),
                    "the alphabetical order."
                ),
                new Description(
                    "-p --path".split(" "),
                    "the order determined by java.nio.Path class."
                )
            }
        )
    )),

    MKDIR(69, new Document(
        new Synopsis(new Name("mkdir", "make directories"), new Name [] {
            new Name("DIRECTORY", "Create the DIRECTORY, if they do not already exist.")
        })
    )),

    RM(70, new Document(
        new Synopsis(new Name("rm", "remove files or directories"), new Name [] {
            new Name("[FILE]", "Remove (unlink) the FILE.")
        })
    )),

    SEARCH(71, new Document(
        new Synopsis(new Name("search", "search files and directories"), new Name [] {
            new Name("[NAME_TOKEN]", "search all files and directories under this path with specific NAME_TOKEN.")
        })
    )),

    TOUCH(72, new Document(
        new Synopsis(new Name("touch", "change file timestamps"), new Name [] {
            new Name("FILE CONTENT", "Create the FILE with the content CONTENT")
        })
    )),

    LN(73, new Document(
        new Synopsis(new Name("ln", "make links between files"), new Name [] {
            new Name("TARGET LINK_NAME", "Create a link to TARGET with the name LINK_NAME.")
        })
    )),

    ECHO(74, new Document(
        new Synopsis(new Name("echo", "display a line of text"), new Name [] {
            new Name("[STRING]", "Echo the STRING(s) to standard output.")
        })
    )),

    
    /* Critical */
    CLEAR(75, new Document(
        new Synopsis(new Name("clear", "clear this console text area"), new Name []{
            new Name("<NO_PARAMETER>", "Throw ClearConsoleException and let the designer handle the clear event.")
        })
    )),

    EXIT(76, new Document(
        new Synopsis(new Name("exit", "exit this console"), new Name []{
            new Name("<NO_PARAMETER>", "Throw ExitConsoleException and let the designer handle the exit event.")
        })
    )),

    SHUTDOWN(77, new Document(
        new Synopsis(new Name("shutdown", "shutdown this software"), new Name []{
            new Name("<NO_PARAMETER>", "Throw ShutdownSoftwareException and let the designer handle the shutdown event."),
            // new Name("MINI_SECOND", "Throw ShutdownSoftwareException after waiting MINI_SECOND and let the designer handle the shutdown event.")
        })
    )),
    

    /* Special */
    MEOW(129, new Document(
        new Synopsis(new Name("meow", "Just try it!"), new Name [] {new Name("Just try it!", null)})
    )),

    /* Shell */
    SHELL(-1, new Document(
        new Synopsis(new Name("shell", "This is an inner type, you shouldn't see this!"))
    )),

    CMD(131, new Document(
        new Synopsis(new Name("cmd", "call command prompt"), new Name []{
            new Name("LINE_TO_EXECUTE", "Execute LINE_TO_EXECUTE use local command prompt if exist.")
        })
    )),
    
    POWERSHELL(132, new Document(
        new Synopsis(new Name("powershell", "call powershell"), new Name []{
            new Name("LINE_TO_EXECUTE", "Execute LINE_TO_EXECUTE use local powershell if exist.")
        })
    )),
    
    WINDOWS_TERMINAL(133, new Document(
        new Synopsis(new Name("windows terminal", "call windows terminal"), new Name []{
            new Name("LINE_TO_EXECUTE", "Execute LINE_TO_EXECUTE use local windows terminal if exist.")
        })
    )),
    
    BASH(134, new Document(
        new Synopsis(new Name("bash", "call bash"), new Name []{
            new Name("LINE_TO_EXECUTE", "Execute LINE_TO_EXECUTE use local bash if exist.")
        })
    )),

    
    /* External */
    CRAWLER(257, new Document(
        new Synopsis(new Name("crawler", "Use crawler to fetch wallpapers"), new Name []{
            new Name("PAGE_NUMBER ARTWORK_KEYWORD", "Specify PAGE_NUMBER and ARTWORK_KEYWORD."),
            new Name("ARTWORK_KEYWORD", "Specify ARTWORK_KEYWORD.")
        }), new Descriptions(
            new String [] {
                "Use Wallpaper Master Crawler to fetch Wallpapers with specified parameters.",
                "Noted that there are multiple different crawlers in Wallpaper Master."
            },
            new Description [] {
                new Description(
                    "PAGE_NUMBER".split(" "), 
                    "The integer that determine the pages which a single crawler should walk through."
                ),
                new Description(
                    "ARTWORK_KEYWORD".split(" "), 
                    "The keywords according to what you'd like to search."
                ),
            }
        )
    )),
        

    MUSIC(258, new Document(
        new Synopsis(new Name("music", "Open Music Player"), new Name [] {
            new Name("<NO_PARAMETER>", "Open Music with Syamiko."),
            new Name("MUSIC_FILE", "Open Music with AKARI at the path MUSIC_FILE."),
        })
    )),
    
    WALLPAPER(259, new Document(
        new Synopsis(new Name("wallpaper", "Open Wallpaper Viewer"), new Name [] {
            new Name("<NO_PARAMETER>", "Open Wallpaper View with default wallpaper path."),
            new Name("IMAGE_DIRECTORY", "Open Wallpaper View from IMAGE_DIRECTORY."),
            new Name("IMAGE_FILE", "Open Wallpaper View from IMAGE_FILE."),
        })
    )),
    ;
    
    final int code;
    final Document doc;
    // final String name;
    // final String name;
    
    private Type(int code, Document doc){
        this.code = code;
        this.doc = doc;
    }
    
    // private final Supplier<String> manualDoc;
    // private Type(int code, String name, Supplier<String> manualDoc){
    //     this.code = code;
    //     this.name = name;
    //     this.manualDoc = manualDoc;
    // }
    // public final String getDoc(){ return manualDoc.get(); }
    
    public final int getCode(){ return this.code; }
    public final String getName(){ return this.doc.getName(); }
    public final String getDoc(){ return this.doc.toString(); }
    public final String getBriefSynopsis(){ return this.doc.getBriefSynopsis(); }

    // public final String getName(){ return this.name; }

}
