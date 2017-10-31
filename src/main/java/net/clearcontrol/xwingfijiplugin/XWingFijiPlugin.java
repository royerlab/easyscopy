package net.clearcontrol.xwingfijiplugin;

import clearcl.*;
import clearcl.backend.ClearCLBackendInterface;
import clearcl.backend.javacl.ClearCLBackendJavaCL;
import net.haesleinhuepf.clearcl.utilities.ClearCLImageImgConverter;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import java.io.IOException;
import xwing.main.XWingMain;

@Plugin(type = Command.class, menuPath = "XWing>Start")
public class XWingFijiPlugin<T extends RealType<T>> implements Command
{
  private ClearCLContext mContext;



  @Override public void run()
  {
    XWingMain.main(new String[]{});
  }


}
