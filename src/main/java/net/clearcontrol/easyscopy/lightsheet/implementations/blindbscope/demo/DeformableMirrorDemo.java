package net.clearcontrol.easyscopy.lightsheet.implementations.blindbscope.demo;

import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.microscope.lightsheet.spatialphasemodulation.io.DenseMatrix64FReader;
import clearcontrol.microscope.lightsheet.spatialphasemodulation.slms.SpatialPhaseModulatorDeviceInterface;
import fiji.util.gui.GenericDialogPlus;
import ij.ImageJ;
import net.clearcontrol.easyscopy.lightsheet.implementations.blindbscope.BlindBScope;
import org.ejml.data.DenseMatrix64F;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * This demo checks if the Defromable mirror works as we would want it to.
 *
 * You can identify the control of various actuators using this peice of code and a Wavefront sensor.
 *
 *
 *
 *
 * Author: Debayan Saha at MPI CBG (http://mpi-cbg.de)
 * April 2018
 */

public class DeformableMirrorDemo {
    public static void main(String... args) throws InterruptedException, ExecutionException, TimeoutException {
        new ImageJ();
        BlindBScope lScope = BlindBScope.getInstance();



        //Parameters
        int lnumRows = 11;
        int lnumCols = 11;
        int checkrow = 0;

        //Initialize the DM
        DenseMatrix64F lMatrix = new DenseMatrix64F(lnumRows,lnumCols);
        SpatialPhaseModulatorDeviceInterface lMirror = lScope.getSpatialPhaseModulatorDevice("BIL118");

        /*Read the matrix from a file
        //File lMatrixFile = new File("C:\\Users\\myerslab\\.clearcontrol\\MirrorModes\\Neuronal_flat_deb.json");
        //DenseMatrix64FReader lMatrixReader = new DenseMatrix64FReader(lMatrixFile, lMatrix);
        //lMatrixReader.read();
        */


        int flag = 0;

        while(true) {//This loop is just for the sake of laser
            while(true) {
                GenericDialogPlus lDialog = new GenericDialogPlus("Actuator");
                //Run through all columns for the given checkrow and ask for usr input
                for(int i = 0; i<lnumCols; i++){
                    lDialog.addNumericField("Actuator Number "+i, 0, 4);
                }
                lDialog.showDialog();

                //Put the user inputed values in the given actuators
                for(int j = 0;j<lnumCols;j++){
                    lMatrix.set(checkrow,j,lDialog.getNextNumber());
                }

                System.out.println("I AM MODIFIED" + lMatrix);

                //Set the matrix on the DM
                lMirror.getMatrixReference().set(lMatrix);

                //If the check is complete end the program
                if (lDialog.wasCanceled()) {
                    flag = 1;
                    break;
                }
            }

            // Turn Laser On
            LaserDeviceInterface lLaser = lScope.getLaserDevice("488");
            lLaser.setTargetPowerInPercent(1.0);
            lLaser.setLaserOn(true);
            lLaser.setLaserPowerOn(true);
            lLaser.setLaserOn(true);
            lLaser.setLaserPowerOn(true);

            // Experiment Done
            if(flag ==1){
                break;
            }
        }

        // That's always a good idea by the end
        lScope.shutDownAllLasers();

        // cleanup lab
        BlindBScope.cleanup();
    }
}
