package clearcl.imagej;

import clearcl.*;
import clearcl.backend.ClearCLBackendInterface;
import clearcl.backend.javacl.ClearCLBackendJavaCL;
import clearcl.enums.ImageChannelDataType;
import clearcl.imagej.utilities.GenericBinaryFastFuseTask;
import clearcl.imagej.utilities.MiniFastFusionEngine;
import clearcontrol.stack.OffHeapPlanarStack;
import coremem.enums.NativeTypeEnum;
import fastfuse.FastFusionEngine;
import fastfuse.FastFusionMemoryPool;
import net.clearcontrol.easyscopy.EasyMicroscope;
import net.clearcontrol.easyscopy.EasyScope;


import java.io.IOException;
import java.util.Map;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
@EasyScope
public class ClearCLIJ extends EasyMicroscope
{

  protected ClearCLContext mClearCLContext;
  private final ClearCLDevice mClearCLDevice;
  private ClearCL mClearCL;
  private static ClearCLIJ sInstance = null;

  private FastFusionEngine mFastFusionEngine;

  public static ClearCLIJ getInstance() {
    if (sInstance == null) {
      sInstance = new ClearCLIJ();
    }
    return sInstance;
  }

  private ClearCLIJ() {
    super(null);

    ClearCLBackendInterface
        lClearCLBackend =
        new ClearCLBackendJavaCL();

    mClearCL = new ClearCL(lClearCLBackend);
    mClearCLDevice = mClearCL.getFastestGPUDeviceForImages();
    mClearCLContext = mClearCLDevice.createContext();

    mFastFusionEngine = new FastFusionEngine(mClearCLContext);

    FastFusionMemoryPool.getInstance(mClearCLContext);
  }

  public OffHeapPlanarStack executeUnaryFunction(Class pAnchorClass, String pProgramFilename, String pKernelname, String pKernelInputImageParameterName, String pKernelOutputImageParameterName, OffHeapPlanarStack pInputImageStack , Map<String, Object> pParameterMap) throws
                                                                                                                                                                                           IOException
  {
    String lGenericSrcImageName = "tempA" + System.currentTimeMillis();
    String lGenericDstImageName = "tempB" + System.currentTimeMillis();

    GenericBinaryFastFuseTask lGenericUnaryFastFuseTask = new GenericBinaryFastFuseTask(
        mFastFusionEngine,
        pAnchorClass,
        pProgramFilename,
        pKernelname,
        pKernelInputImageParameterName,
        pKernelOutputImageParameterName,
        lGenericSrcImageName,
        lGenericDstImageName
    );
    lGenericUnaryFastFuseTask.setParameterMap(pParameterMap);

    mFastFusionEngine.addTask(lGenericUnaryFastFuseTask);

    ImageChannelDataType lType = determineType(pInputImageStack.getDataType());

    mFastFusionEngine.passImage(lGenericSrcImageName, pInputImageStack.getContiguousMemory(), lType, pInputImageStack.getDimensions());

    mFastFusionEngine.executeAllTasks();

    if (mFastFusionEngine.isImageAvailable(lGenericDstImageName)) {
      return convertToOffHeapPlanarStack(mFastFusionEngine.getImage(lGenericDstImageName));
    } else {
      return null;
    }








  }

  private OffHeapPlanarStack convertToOffHeapPlanarStack(ClearCLImage pClearCLImage)
  {
    OffHeapPlanarStack lResultStack = new OffHeapPlanarStack( true, 0, pClearCLImage.getNativeType(), pClearCLImage.getNumberOfChannels(), pClearCLImage.getDimensions());
    pClearCLImage.writeTo(lResultStack.getContiguousMemory(), true);



    return lResultStack;
  }

  private ImageChannelDataType determineType(NativeTypeEnum dataType)
  {
    if (dataType == NativeTypeEnum.Byte) {
      return ImageChannelDataType.UnsignedInt8;
    }
    if (dataType == NativeTypeEnum.UnsignedShort) {
      return ImageChannelDataType.UnsignedInt16;
    }
    if (dataType == NativeTypeEnum.Float) {
      return ImageChannelDataType.Float;
    }
    // todo: complete conversion list


    return null;
  }


  private ClearCLProgram initializeProgram(Class pAnchorClass,
                                           String pProgramFilename) throws
                                                          IOException
  {
    return mClearCLContext.createProgram(pAnchorClass, pProgramFilename);
  }

  public static void cleanup() {
    if (sInstance != null) {
      sInstance.terminate();
      sInstance = null;
    }
  }

  @Override public void terminate()
  {
    mClearCLContext.close();
    if (sInstance == this) {
      sInstance = null;
    }
  }


  public ClearCLContext getClearCLContext() {
    return mClearCLContext;
  }
}
