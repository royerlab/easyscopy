package net.clearcontrol.easyscopy;

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
 * Deprecated: The functionality in this class might be replaced by
 * ClearCLIJ.converter(pStack).getRandomAccessibleInterval();
 *
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
@Deprecated
public class EasyScopyUtilities
{
  public static RandomAccessibleInterval<UnsignedShortType> stackToImg(OffHeapPlanarStack pStack) {
    return new StackToImgConverter<UnsignedShortType>(pStack).getRandomAccessibleInterval();
  }
}
