
package com.yoeld99apps.axegrl;

import java.util.Stack;
import java.awt.Container;
import javax.swing.JInternalFrame;

public class InternalFrameContainer extends Container {

    private Stack<JInternalFrame> frames;
    
    public InternalFrameContainer() {
        frames = new Stack();
    }
    
    public void addInternalFrame(){
        //add new internal frame to render-stack
    }
    
    
    
}
