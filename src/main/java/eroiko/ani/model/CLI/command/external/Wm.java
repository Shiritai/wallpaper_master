/*
 * Author : Shiritai (楊子慶, or Eroiko on Github) at 2021/06/13.
 * See https://github.com/Shiritai/wallpaper_master for more information.
 * Created using VSCode.
 */
package eroiko.ani.model.CLI.command.external;

import eroiko.ani.MainApp;
import eroiko.ani.controller.MainController;
import eroiko.ani.controller.PrimaryControllers.MusicWithSyamiko;
import eroiko.ani.controller.PromptControllers.ConsoleController;
import eroiko.ani.model.CLI.command.fundamental.Command;
import eroiko.ani.model.CLI.command.fundamental.Type;

public class Wm extends Command {

    private final String toDo;
    
    /** 
     * WM stands for the acronym of Wallpaper Master
     * <p> This command is for Wallpaper Master manipulation
     */
    public Wm(String toDo){
        super(Type.WM);
        this.toDo = toDo;
    }
    
    @Override
    public void execute() throws IllegalArgumentException {
        switch (toDo){
            case "--about" -> MainController.OpenAboutWindow();
            case "--pref" -> MainController.OpenPreferenceWindow();
            case "--music" -> MusicWithSyamiko.openMusicWithSyamiko();
            case "--terminal" -> { ConsoleController.OpenCompleteTerminal(); /* System.setOut(MainApp.stdOut); */ } // reset stdout
            case "--complete" -> { MainApp.OpenMainWindow(); /* System.setOut(MainApp.stdOut); */ } // reset stdout
            default -> throw illegalParaStr("Invalid parameter option.");
        }
    }
    
}
