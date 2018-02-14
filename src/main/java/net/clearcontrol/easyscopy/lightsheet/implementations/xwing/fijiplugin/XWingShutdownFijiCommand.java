package net.clearcontrol.easyscopy.lightsheet.implementations.xwing.fijiplugin;

import net.clearcontrol.easyscopy.lightsheet.implementations.xwing.XWingScope;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */

@Plugin(type = Command.class, menuPath = "XWing>Microscope>Shutdown XWing")
public class XWingShutdownFijiCommand implements Command
{

  @Override public void run()
  {
    XWingScope.cleanup();
  }
}
