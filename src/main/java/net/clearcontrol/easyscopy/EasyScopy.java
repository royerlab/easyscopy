package net.clearcontrol.easyscopy;

import org.atteo.classindex.ClassIndex;

import java.util.ArrayList;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * February 2018
 */
public class EasyScopy
{
  public static Class[] listEasyScopes() {
    Iterable<Class<?>> lList = ClassIndex.getAnnotated(EasyScope.class);

    ArrayList<Class> lArrayList = new ArrayList<Class>();
    for (Class lClass : lList) {
      lArrayList.add(lClass);
    }
    Class[] lClassArray = new Class[lArrayList.size()];
    lArrayList.toArray(lClassArray);
    return lClassArray;
  }
}
