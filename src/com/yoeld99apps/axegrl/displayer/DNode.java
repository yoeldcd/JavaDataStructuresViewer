package com.yoeld99apps.axegrl.displayer;

public class DNode extends DShape {
    
    public DNode() {
        this(null);
    }
    
    public DNode(String text) {
        super();
        
        setText(text != null ? text.toString() : "NULL");
        setSize(50, 50);
        initialize();
        
    }
    
}
