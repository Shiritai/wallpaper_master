/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.CLI.command.fundamental;

import eroiko.ani.model.CLI.command.fundamental.Document.*;

/** 
 * 支持指令的類型辨識、查詢文件等操作
 * <p> 使用自創 Document classes 完成文件的印出格式
 * <p> Documentation is cool...QQ
 */
public enum Type {
    /* Fundamental */
    /* 所有不可視 enum 皆 <= 0 */
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
                    "-a --all".split(" "), new String [] {
                        "Print all available commands."
                    }
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
            new Name("<NO_PARAMETER>", "List all history commands start from activating this console.")
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
                    "<NO_PARAMETER> -s --sorted".split(" "), new String [] {
                        "The order determined by the software designer."
                    }
                ),
                new Description(
                    "-n --normal".split(" "), new String [] {
                        "The alphabetical order.",
                        "Regardless of whether it's upper or lower case."
                    }
                ),
                new Description(
                    "-p --path".split(" "), new String [] {
                        "The order determined by java.nio.Path class."
                    }
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
        new Synopsis(new Name("rm", "remove a file or a directory"), new Name [] {
            new Name("FILE", "Remove (unlink) the FILE.")
        })
    )),

    SEARCH(71, new Document(
        new Synopsis(new Name("search", "search files and directories"), new Name [] {
            new Name("NAME_TOKEN", "search all files and directories under this path with one specific NAME_TOKEN.")
        })
    )),

    TOUCH(72, new Document(
        new Synopsis(new Name("touch", "change file timestamps"), new Name [] {
            new Name("FILE CONTENT", "Create the FILE with the content CONTENT")
        })
    )),

    LN(73, new Document(
        new Synopsis(new Name("ln", "make links between files"), new Name [] {
            new Name("\"TARGET\" \"LINK_NAME\"", "Create a link to TARGET with the name LINK_NAME.")
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
            new Name("<NO_PARAMETER>", "Throw ClearConsoleException and let the designer handle clear event.")
        })
    )),

    EXIT(76, new Document(
        new Synopsis(new Name("exit", "exit this console"), new Name []{
            new Name("<NO_PARAMETER>", "Throw ExitConsoleException and let the designer handle exit event.")
        })
    )),

    SHUTDOWN(77, new Document(
        new Synopsis(new Name("shutdown", "shutdown this software"), new Name []{
            new Name("<NO_PARAMETER>", "Throw ShutdownSoftwareException and let the designer handle shutdown event."),
            // new Name("MINI_SECOND", "Throw ShutdownSoftwareException after waiting MINI_SECOND and let the designer handle the shutdown event.")
        })
    )),

    INFORMATION(78, new Document(
        new Synopsis(new Name("information", "print the information of this console"), new Name []{
            new Name("<NO_PARAMETER>", "Throw CustomInformationException and let the designer handle this event.")
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

    CMD(130, new Document(
        new Synopsis(new Name("cmd", "call command prompt", "cmd.exe"), new Name []{
            new Name("LINE_TO_EXECUTE", "Execute LINE_TO_EXECUTE use local command prompt if exist.")
        })
    )),
    
    POWERSHELL(131, new Document(
        new Synopsis(new Name("powershell", "call powershell", "powershell.exe", "pwsh"), new Name []{
            new Name("LINE_TO_EXECUTE", "Execute LINE_TO_EXECUTE use local powershell if exist.")
        })
    )),
    
    WT(132, new Document(
        new Synopsis(new Name("wt", "call windows terminal", "wt.exe"), new Name []{
            new Name("LINE_TO_EXECUTE", "Execute LINE_TO_EXECUTE use local windows terminal if exist.")
        })
    )),
    
    BASH(133, new Document(
        new Synopsis(new Name("bash", "call bash"), new Name []{
            new Name("LINE_TO_EXECUTE", "Execute LINE_TO_EXECUTE use local bash if exist.")
        })
    )),

    
    /* External */
    CRAWLER(257, new Document(
        new Synopsis(new Name("crawler", "Use crawler to fetch wallpapers"), new Name []{
            new Name("[OPTION] PAGE_NUMBER ARTWORK_KEYWORD", "Specify PAGE_NUMBER and ARTWORK_KEYWORD."),
            new Name("[OPTION] ARTWORK_KEYWORD", "Specify ARTWORK_KEYWORD.")
        }), new Descriptions(
            new String [] {
                "Use Wallpaper Master Crawler to fetch Wallpapers with specified parameters.",
            },
            new Description [] {
                new Description(
                    "[OPTION] -i".split(" "), new String [] {
                        "Print information while crawling, default is false.",
                    }
                ),
                new Description(
                    "PAGE_NUMBER".split(" "), new String [] {
                        "The integer that determine the pages which a single crawler should walk through.",
                        "Noted that there are multiple different crawlers in Wallpaper Master."
                    }
                ),
                new Description(
                    "ARTWORK_KEYWORD".split(" "), new String [] {
                        "The keywords according to what you'd like to search.",
                    }
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
            new Name("[OPTION] IMAGE_DIRECTORY", "Open Wallpaper View from IMAGE_DIRECTORY."),
            new Name("[OPTION] IMAGE_FILE", "Open Wallpaper View from IMAGE_FILE."),
        }), new Descriptions(
            new String [] {
                "Below are the available OPTIONs.",
            },
            new Description [] {
                new Description(
                    "-p".split(" "), new String [] {
                        "Open Wallpaper Viewer in preview mode, which means you can't add nor delete wallpapers."
                    }
                ),
                new Description(
                    "--this".split(" "), new String [] {
                        "Open Wallpaper Viewer from this folder."
                    }
                ),
                new Description(
                    "--new".split(" "), new String [] {
                        "Open Wallpaper View w.r.t this newest Wallpapers from Crawler or the last opened Wallpaper Viewer."
                    }
                ),
                new Description(
                    "--clean".split(" "), new String [] {
                        "Clean all the temporary files stored after your downloading.", 
                        "Wish you not do this before you choose your wallpapers."
                    }
                ),
            }
        )
    )),

    WM(260, new Document(
        new Synopsis(new Name("wm", "wallpaper master functions"), new Name [] {
            new Name("--about", "Open About window."),
            new Name("--music", "Open Music with Syamiko."),
            new Name("--pref", "Open Preference window."),
            new Name("--terminal", "Switch to Real Terminal."),
            new Name("--complete", "Switch to Main Window."),
        })
    )),

    SETTING(261, new Document(
        new Synopsis(new Name("setting", "activate console setting"))
    ))
    ;
    
    final int code;
    final Document doc;
    
    private Type(int code, Document doc){
        this.code = code;
        this.doc = doc;
    }
    
    public final int getCode(){ return this.code; }
    public final String getName(){ return this.doc.getName(); }
    public final String getDoc(){ return this.doc.toString(); }
    public final String getBriefSynopsis(){ return this.doc.getBriefSynopsis(); }
    
    /* 舊寫法 : 使用 java.util.function.Suppler */
    // private final Supplier<String> manualDoc;
    // private Type(int code, String name, Supplier<String> manualDoc){
    //     this.code = code;
    //     this.name = name;
    //     this.manualDoc = manualDoc;
    // }
    // public final String getDoc(){ return manualDoc.get(); }

    // public final String getName(){ return this.name; }
}
