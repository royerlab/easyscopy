package net.clearcontrol.easyscopy.lightsheet.implementations.clearcl;

import clearcl.*;
import clearcl.backend.ClearCLBackendInterface;
import clearcl.backend.javacl.ClearCLBackendJavaCL;
import clearcl.enums.ImageChannelDataType;
import clearcontrol.microscope.lightsheet.processor.LightSheetFastFusionProcessor;
import clearcontrol.stack.OffHeapPlanarStack;
import coremem.enums.NativeTypeEnum;
import fastfuse.FastFusionMemoryPool;
import net.clearcontrol.easyscopy.EasyMicroscope;
import net.clearcontrol.easyscopy.EasyScope;
import net.clearcontrol.easyscopy.lightsheet.EasyLightsheetMicroscope;
import net.clearcontrol.easyscopy.lightsheet.implementations.clearcl.utilities.GenericUnaryFastFuseTask;
import xwing.XWingMicroscope;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
@EasyScope
public class ClearCLScope extends EasyMicroscope
{

  protected ClearCLContext mClearCLContext;
  private final ClearCLDevice mClearCLDevice;
  private ClearCL mClearCL;
  private static ClearCLScope sInstance = null;

  private MiniFastFusionEngine mMiniFastFusionEngine;

  public static ClearCLScope getInstance() {
    if (sInstance == null) {
      sInstance = new ClearCLScope();
    }
    return sInstance;
  }

  private ClearCLScope() {
    super(null, null);

    ClearCLBackendInterface
        lClearCLBackend =
        new ClearCLBackendJavaCL();

    mClearCL = new ClearCL(lClearCLBackend);
    mClearCLDevice = mClearCL.getFastestGPUDeviceForImages();
    mClearCLContext = mClearCLDevice.createContext();

    mMiniFastFusionEngine = new MiniFastFusionEngine(mClearCLContext);

    FastFusionMemoryPool.getInstance(mClearCLContext);
  }

  public OffHeapPlanarStack executeUnaryFunction(Class pAnchorClass, String pProgramFilename, String pKernelname, String pKernelInputImageParameterName, String pKernelOutputImageParameterName, OffHeapPlanarStack pInputImageStack , Map<String, Object> pParameterMap) throws
                                                                                                                                                                                           IOException
  {
    String lGenericSrcImageName = "tempA" + System.currentTimeMillis();
    String lGenericDstImageName = "tempB" + System.currentTimeMillis();

    GenericUnaryFastFuseTask lGenericUnaryFastFuseTask = new GenericUnaryFastFuseTask(
        mMiniFastFusionEngine,
        pAnchorClass,
        pProgramFilename,
        pKernelname,
        pKernelInputImageParameterName,
        pKernelOutputImageParameterName,
        lGenericSrcImageName,
        lGenericDstImageName
    );
    lGenericUnaryFastFuseTask.setParameterMap(pParameterMap);

    mMiniFastFusionEngine.addTask(lGenericUnaryFastFuseTask);

    ImageChannelDataType lType = determineType(pInputImageStack.getDataType());

    mMiniFastFusionEngine.passImage(lGenericSrcImageName, pInputImageStack.getContiguousMemory(), lType, pInputImageStack.getDimensions());

    mMiniFastFusionEngine.executeAllTasks();

    if (mMiniFastFusionEngine.isImageAvailable(lGenericDstImageName)) {
      return convertToOffHeapPlanarStack(mMiniFastFusionEngine.getImage(lGenericDstImageName));
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
