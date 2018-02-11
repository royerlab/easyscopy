package net.clearcontrol.lightsheet.easyscopy.implementations.xwing.fijiplugin;

import org.scijava.command.Command;
import org.scijava.plugin.Plugin;
import xwing.main.XWingMain;

@Plugin(type = Command.class, menuPath = "XWing>Microscope>Launch app")
public class XWingFijiCommand implements Command
{
  @Override public void run()
  {
    System.out.println("Hello");

    XWingMain.getInstance();
  }

  public static void main(String... args) {
    new XWingFijiCommand().run();

  }
}
