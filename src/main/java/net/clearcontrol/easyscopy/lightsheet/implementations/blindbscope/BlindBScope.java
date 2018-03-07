package net.clearcontrol.easyscopy.lightsheet.implementations.blindbscope;

import net.clearcontrol.easyscopy.EasyScope;
import net.clearcontrol.easyscopy.lightsheet.EasyLightsheetMicroscope;
import net.clearcontrol.easyscopy.lightsheet.implementations.bscope.BScopeBuilder;
import xwing.BScopeMicroscope;

/**
 * The blind BScope is equal to the BScope but it does not connect to its Orca Camera. This class exists only for
 * debugging the camera and may be deleted in the future (that's why it's marked as deprecated)
 *
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * March 2018
 */
@Deprecated
@EasyScope
public class BlindBScope extends EasyLightsheetMicroscope {
    public static boolean sUseStages = false;

    private static BlindBScope sInstance = null;
    public static BlindBScope getInstance() {
        if (sInstance == null) {
            sInstance = new BlindBScope();
        }
        return sInstance;
    }

    private BScopeMicroscope mBScopeMicroscope;
    private BlindBScope() {
        super(new BScopeBuilder(false, true).getBScopeMicroscope());
        mBScopeMicroscope = (BScopeMicroscope) getLightSheetMicroscope();
    }

    public static void cleanup() {
        if (sInstance != null) {
            sInstance.terminate();
            sInstance = null;
        }
    }
}
