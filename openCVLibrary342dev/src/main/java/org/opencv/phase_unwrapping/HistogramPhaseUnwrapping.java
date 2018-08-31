//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.phase_unwrapping;

import org.opencv.core.Mat;

// C++: class HistogramPhaseUnwrapping
//javadoc: HistogramPhaseUnwrapping

public class HistogramPhaseUnwrapping extends PhaseUnwrapping {

    protected HistogramPhaseUnwrapping(long addr) { super(addr); }

    // internal usage only
    public static HistogramPhaseUnwrapping __fromPtr__(long addr) { return new HistogramPhaseUnwrapping(addr); }

    //
    // C++:  void cv::phase_unwrapping::HistogramPhaseUnwrapping::getInverseReliabilityMap(Mat& reliabilityMap)
    //

    //javadoc: HistogramPhaseUnwrapping::getInverseReliabilityMap(reliabilityMap)
    public  void getInverseReliabilityMap(Mat reliabilityMap)
    {
        
        getInverseReliabilityMap_0(nativeObj, reliabilityMap.nativeObj);
        
        return;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:  void cv::phase_unwrapping::HistogramPhaseUnwrapping::getInverseReliabilityMap(Mat& reliabilityMap)
    private static native void getInverseReliabilityMap_0(long nativeObj, long reliabilityMap_nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
