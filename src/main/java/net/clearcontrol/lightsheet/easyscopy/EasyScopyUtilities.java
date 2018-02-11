package net.clearcontrol.lightsheet.easyscopy;

import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.imglib2.StackToImgConverter;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.integer.UnsignedShortType;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
public class EasyScopyUtilities
{
  public static RandomAccessibleInterval<UnsignedShortType> stackToImg(OffHeapPlanarStack pStack) {
    return new StackToImgConverter<UnsignedShortType>(pStack).getRandomAccessibleInterval();
  }
}
