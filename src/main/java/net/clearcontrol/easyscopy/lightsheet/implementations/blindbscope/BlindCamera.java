package net.clearcontrol.easyscopy.lightsheet.implementations.blindbscope;

import clearcontrol.core.device.queue.QueueInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.cameras.StackCameraDeviceInterface;
import clearcontrol.devices.cameras.StackCameraQueue;
import clearcontrol.devices.cameras.devices.hamamatsu.HamStackCameraQueue;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmQueue;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.RecyclerInterface;

import java.util.concurrent.Future;

/**
 * The blind camera is an empty implementation of a StackCameraDeviceInterface. It is setup in the BlindBScope to
 * prevent it connecting to the real camera.
 *
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * March 2018
 */
@Deprecated
public class BlindCamera implements StackCameraDeviceInterface {
    @Override
    public void trigger() {

    }

    @Override
    public void setExposureInSeconds(double v) {

    }

    @Override
    public double getExposureInSeconds() {
        return 0;
    }

    @Override
    public Variable<Long> getMaxWidthVariable() {
        return null;
    }

    @Override
    public Variable<Long> getMaxHeightVariable() {
        return new Variable<Long>("", 0L);
    }

    @Override
    public Variable<Double> getLineReadOutTimeInMicrosecondsVariable() {
        return new Variable<Double>("", 100.0);
    }

    @Override
    public Variable<Double> getPixelSizeInMicrometersVariable() {

        return new Variable<Double>("", 0.1);
    }

    @Override
    public Variable<Long> getBytesPerPixelVariable() {
        return new Variable<Long>("", 100L);
    }

    @Override
    public Variable<Number> getExposureInSecondsVariable() {
        return new Variable<Number>("", 0.01);
    }

    @Override
    public Variable<Long> getCurrentIndexVariable() {
        return new Variable<Long>("", 0L);
    }

    @Override
    public Variable<Boolean> getIsAcquiringVariable() {
        return new Variable<Boolean>("", false);
    }

    @Override
    public Variable<Boolean> getTriggerVariable() {
        return new Variable<Boolean>("", false);
    }

    @Override
    public long getCurrentStackIndex() {
        return 0;
    }

    @Override
    public RecyclerInterface<StackInterface, StackRequest> getStackRecycler() {
        return null;
    }

    @Override
    public Variable<StackInterface> getStackVariable() {
        return new Variable<StackInterface>("", null);
    }

    @Override
    public Variable<Boolean> getStackModeVariable() {
        return new Variable<Boolean>("", false);
    }

    @Override
    public Variable<Long> getStackWidthVariable() {
        return new Variable<Long>("", 256L);
    }

    @Override
    public Variable<Long> getStackHeightVariable() {
        return new Variable<Long>("", 256L);
    }

    @Override
    public Variable<Long> getStackDepthVariable() {
        return new Variable<Long>("", 100L);
    }

    @Override
    public void setStackRecycler(RecyclerInterface recyclerInterface) {

    }

    @Override
    public void setName(String s) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public QueueInterface requestQueue() {
        return new HamStackCameraQueue();
    }

    @Override
    public Future<Boolean> playQueue(QueueInterface queueInterface) {
        return null;
    }

    @Override
    public boolean open() {
        return true;
    }

    @Override
    public boolean close() {
        return true;
    }

    @Override
    public boolean isReOpenDeviceNeeded() {
        return false;
    }

    @Override
    public void requestReOpen() {

    }

    @Override
    public void clearReOpen() {

    }

    @Override
    public void reopen() {

    }
}
