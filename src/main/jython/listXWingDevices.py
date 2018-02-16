from net.clearcontrol.easyscopy.lightsheet.implementations.xwing import SimulatedXWingScope;
from ij import IJ;

lScope = SimulatedXWingScope.getInstance();

IJ.log("XWing supports devices:");
for lDevice in lScope.getDevices():
    IJ.log(" * " + str(lDevice));
    