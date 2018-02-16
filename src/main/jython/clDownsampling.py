
from clearcontrol.stack import OffHeapPlanarStack;
from clearcontrol.stack.imglib2 import ImgToStackConverter;
from clearcontrol.stack.imglib2 import StackToImgConverter;
from fastfuse.tasks import DownsampleXYbyHalfTask;
from ij import IJ;
from ij import ImageJ;
from ij import ImagePlus;
from net.clearcontrol.easyscopy.lightsheet.implementations.clearcl import ClearCLScope;
from net.imglib2 import RandomAccessibleInterval;
from net.imglib2.img.display.imagej import ImageJFunctions;
from net.imglib2.type.numeric.integer import UnsignedShortType;
from net.imglib2.view import Views;

# This line takes the current image which is open in ImageJ; it should be a 16-bit unsigned short image
#@Img lInputImg

# initialize ClearCL context and fastfuse engine
lScope = ClearCLScope.getInstance();

# convert imglib2 image to clearcontrol stack
lStack = ImgToStackConverter(Views.iterable(lInputImg)).getOffHeapPlanarStack();

# downsample the image stack using ClearCL / OpenCL
lResultStack = lScope.executeUnaryFunction(DownsampleXYbyHalfTask, "kernels/downsampling.cl", "downsample_xy_by_half_nearest", "src", "dst", lStack, None);

# convert the result back to imglib2 and show it
lResultImg = StackToImgConverter(lResultStack).getRandomAccessibleInterval();
ImageJFunctions.show(lResultImg);