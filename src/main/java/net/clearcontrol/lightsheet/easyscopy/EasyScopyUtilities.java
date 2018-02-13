package net.clearcontrol.lightsheet.easyscopy;

import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.imglib2.StackToImgConverter;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import org.fife.rsta.ac.java.rjc.lang.Modifiers;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
public class EasyScopyUtilities
{
  public static RandomAccessibleInterval<UnsignedShortType> stackToImg(OffHeapPlanarStack pStack) {
    return new StackToImgConverter<UnsignedShortType>(pStack).getRandomAccessibleInterval();
  }

  /**
   * I hate using Java reflections, but I guess it's a good idea here. Cheers, Robert
   *
   * @param pDevice
   * @return List of methods supported by the device
   */
  public static String help(Object pDevice){
    ArrayList<String> lPrimaryResultList = new ArrayList<String>();
    ArrayList<String> lSecondaryResultList = new ArrayList<String>();

    Class lClass = pDevice.getClass();
    if (!lClass.getPackage().getName().contains("clearcontrol")) {
      lPrimaryResultList.add("Warning: " + pDevice.toString() + " is no clearcontrol device");
      //return lPrimaryResultList;
    }
    lPrimaryResultList.add(lClass.getName() + " methods: ");

    Method[] lMethods = lClass.getMethods();
    for (Method lMethod : lMethods) {
      if ( (lMethod.getModifiers() & Modifiers.PUBLIC) > 0) {

        String lHelpEntryText =
            lMethod.getDeclaringClass().getCanonicalName() + "." +
            lMethod.getName();

        lPrimaryResultList.add(lHelpEntryText);
      }
    }
    lPrimaryResultList.sort(Comparator.naturalOrder());

    lPrimaryResultList.addAll(lSecondaryResultList);

    String lResultText = "";
    for (String lEntry : lPrimaryResultList)
    {
      lResultText = lResultText + lEntry + "\n";
    }
    return lResultText;
  }

  // todo: provied help for a certain method. Code snippets below
  /*
  public static String help(Object pDevice, String pMethodName)
  {
    return "";
  }
  */
    /*
    String lHelpEntryText =
        lMethod.getReturnType().getCanonicalName() + " " +
        lMethod.getDeclaringClass().getCanonicalName() + "." +
        lMethod.getName() + "(";
    Parameter[] lParameters = lMethod.getParameters();
    int count = 0;
    for (Parameter lParameter : lParameters) {
      if (count > 0) {
        lHelpEntryText = lHelpEntryText + ",";
      }
      lHelpEntryText = lHelpEntryText + "\n\t" +
                       lParameter.getType().getCanonicalName() + " " +
                       lParameter.getName();
      count++;
    }
    lHelpEntryText = lHelpEntryText + ");";

    if (lMethod.getDeclaringClass() == lClass) {
      lPrimaryResultList.add(lHelpEntryText);
    } else {
      lSecondaryResultList.add(lHelpEntryText);
    }*/
}
