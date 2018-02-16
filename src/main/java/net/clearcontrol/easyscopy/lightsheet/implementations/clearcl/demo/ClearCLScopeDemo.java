package net.clearcontrol.easyscopy.lightsheet.implementations.clearcl.demo;

import clearcontrol.stack.OffHeapPlanarStack;
import clearcontrol.stack.imglib2.ImgToStackConverter;
import clearcontrol.stack.imglib2.StackToImgConverter;
import fastfuse.tasks.DownsampleXYbyHalfTask;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import net.clearcontrol.easyscopy.lightsheet.implementations.clearcl.ClearCLScope;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.view.Views;

import java.io.IOException;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
public class ClearCLScopeDemo
{
  public static void main(String... args) throws IOException
  {
    new ImageJ();
    ImagePlus
        lInputImagePlus =
        IJ.openImage("src/main/resources/flybrain.tif");

    RandomAccessibleInterval<UnsignedShortType>
        lInputImg =
        ImageJFunctions.wrap(lInputImagePlus);

    ImageJFunctions.show(lInputImg);

    ClearCLScope lScope = ClearCLScope.getInstance();

    //Img<UnsignedShortType> lImg = ArrayImgs.unsignedShorts(new long[]{128, 256, 16});

    OffHeapPlanarStack
        lStack =
        new ImgToStackConverter<UnsignedShortType>(Views.iterable(
            lInputImg)).getOffHeapPlanarStack();

    OffHeapPlanarStack
        lResultStack =
        lScope.executeUnaryFunction(DownsampleXYbyHalfTask.class,
                                    "kernels/downsampling.cl",
                                    "downsample_xy_by_half_nearest",
                                    "src",
                                    "dst",
                                    lStack,
                                    null);

    /*
    HashMap<String, Object> lParameterMap = new HashMap<>();
    lParameterMap.put("Nx", 3);
    lParameterMap.put("Ny", 3);
    lParameterMap.put("Nz", 3);
    lParameterMap.put("sx", 2.0f);
    lParameterMap.put("sy", 2.0f);
    lParameterMap.put("sz", 2.0f);

    OffHeapPlanarStack
        lResultStack =
        lScope.executeUnaryFunction(DownsampleXYbyHalfTask.class,
                                    "kernels/blur.cl",
                                    "gaussian_blur_image3d",
                                    "src",
                                    "dst",
                                    lStack,
                                    lParameterMap);
*/

    RandomAccessibleInterval<UnsignedShortType>
        lResultImg =
        new StackToImgConverter<UnsignedShortType>(lResultStack).getRandomAccessibleInterval();

    ImageJFunctions.show(lResultImg);

  }
}
