package net.clearcontrol.easyscopy.lightsheet.implementations.clearcl.utilities;

import clearcl.ClearCLBuffer;
import clearcl.ClearCLContext;
import clearcl.ClearCLImage;
import clearcl.enums.HostAccessType;
import clearcl.enums.ImageChannelDataType;
import clearcl.enums.ImageChannelOrder;
import clearcl.enums.KernelAccessType;
import coremem.ContiguousMemoryInterface;
import coremem.enums.NativeTypeEnum;
import coremem.offheap.OffHeapMemory;
import coremem.offheap.OffHeapMemoryAccess;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class ClearCLImageImgConverter<T extends RealType<T>>
{
  private ClearCLContext mContext;
  private IterableInterval<T> mIterable = null;
  private ClearCLImage mImage = null;

  public ClearCLImageImgConverter(ClearCLContext pClearCLContext, IterableInterval<T> pIterable) {
    mContext = pClearCLContext;
    mIterable = pIterable;
  }

  public ClearCLImageImgConverter(ClearCLContext pClearCLContext, ClearCLImage pImage) {
    mContext = pClearCLContext;
    mImage = pImage;
  }

  public ClearCLImage getImage() {
    if (mImage == null) {
      mImage = convertImgToClearCLImage(mIterable);
    }
    return mImage;
  }

  public IterableInterval<T> getImg() {
    if (mIterable == null) {
      // Todo: this cast is a bit dangerous
      mIterable = (IterableInterval<T>) Views.iterable(convertClearClImageToImg(mImage));
    }
    return mIterable;
  }


  private Img<FloatType> convertClearClImageToImg(ClearCLImage image) {

    Img<FloatType> img = ArrayImgs.floats(image.getDimensions());

    int bytesPerPixel = 4; //because we are talking about Java floats
    long numberOfPixels = image.getWidth()
                                  * image.getHeight()
                                  * image.getDepth();

    long numberOfBytesToAllocate = bytesPerPixel * numberOfPixels;

    ContiguousMemoryInterface contOut =
        new OffHeapMemory("memmm",
                          null,
                          OffHeapMemoryAccess
                              .allocateMemory(numberOfBytesToAllocate),
                          numberOfBytesToAllocate);

    ClearCLBuffer
        buffer = mContext.createBuffer(NativeTypeEnum.Float, numberOfPixels);
    image.copyTo(buffer, true);
    buffer.writeTo(contOut, true);

    int count = 0;
    Cursor<FloatType> cursor = img.cursor();
    while (cursor.hasNext())
    {
      cursor.next().set(contOut.getFloat(count));
      count+=bytesPerPixel;
    }
    return img;
  }


  private ClearCLImage convertImgToClearCLImage( IterableInterval<T> iterable)
  {
    long[] dimensions = new long[iterable.numDimensions()];
    iterable.dimensions(dimensions);

    ClearCLImage
        lClearClImage =
        mContext.createImage(HostAccessType.ReadWrite,
                             KernelAccessType.ReadWrite,
                             ImageChannelOrder.Intensity,
                             ImageChannelDataType.Float,
                             dimensions);

    long sumDimensions = 1;
    for (int i = 0; i < dimensions.length; i++)
    {
      sumDimensions *= dimensions[i];
    }

    float[] inputArray = new float[(int) sumDimensions];

    int count = 0;
    Cursor<T> cursor = iterable.cursor();
    while (cursor.hasNext()) {
      inputArray[count] = cursor.next().getRealFloat();
      count++;
    }

    lClearClImage.readFrom(inputArray, true);
    return lClearClImage;
  }
}
