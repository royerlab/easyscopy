package clearcontrol.stack.imglib2;

import clearcl.enums.ImageChannelDataType;
import clearcontrol.stack.OffHeapPlanarStack;
import com.sun.org.apache.bcel.internal.generic.FLOAD;
import coremem.enums.NativeTypeEnum;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
public class ImgToStackConverter<T extends RealType<T>>
{
  IterableInterval<T> mInputImg;
  OffHeapPlanarStack mStack = null;

  public ImgToStackConverter(IterableInterval<T> pInputImg) {
    mInputImg = pInputImg;
  }



  public OffHeapPlanarStack getOffHeapPlanarStack() {
    process();

    return mStack;
  }

  private void process() {
    if (mStack == null) {
      long[] lDimensions = new long[mInputImg.numDimensions()];
      mInputImg.dimensions(lDimensions);

      NativeTypeEnum lType = determineType(mInputImg.cursor().next());


      OffHeapPlanarStack lStack = new OffHeapPlanarStack(true, 0, lType, 1, lDimensions);

      Cursor<T> cursor = mInputImg.cursor();

      long lOffSet = 0;
      long lElementSize = lType.getSizeInBytes();
      if (lType == NativeTypeEnum.UnsignedShort)
      {
        while (cursor.hasNext())
        {
          T element = cursor.next();
          lStack.getContiguousMemory()
                .setShort(lOffSet, (short) element.getRealDouble());
          lOffSet+=lElementSize;
        }
      } else if (lType == NativeTypeEnum.UnsignedByte)
      {
        while (cursor.hasNext())
        {
          T element = cursor.next();
          lStack.getContiguousMemory()
                .setByte(lOffSet, (byte) element.getRealDouble());
          lOffSet+=lElementSize;
        }
      } else if (lType == NativeTypeEnum.Float)
      {
        while (cursor.hasNext())
        {
          T element = cursor.next();
          lStack.getContiguousMemory()
                .setFloat(lOffSet, (float) element.getRealDouble());
          lOffSet+=lElementSize;
        }
      }
      // todo: extend list of supported types

      mStack = lStack;
    }
  }

  private NativeTypeEnum determineType(T next)
  {
    if (next instanceof UnsignedShortType) {
      return NativeTypeEnum.UnsignedShort;
    }
    if (next instanceof UnsignedByteType) {
      return NativeTypeEnum.UnsignedByte;
    }
    if (next instanceof FloatType) {
      return NativeTypeEnum.Float;
    }
    // todo: complete type list

    return null;
  }
}
