package com.yoeld99apps.axegrl.displayer;

import java.util.ArrayList;

public class DShapeGroup extends DShape {
    
    private final ArrayList<DShape> shapes;
    
    public DShapeGroup() {
        super();
        shapes = new ArrayList<>();
        initialize();
    }
    
    public DShape add(DShape shape){
        if(!shapes.contains(shape)){
            shapes.add(shape);
            return shape;
        }
        
        return null;
    }
    
    public DShape remove(DShape shape){
        return shapes.remove(shapes.indexOf(shape));
    }
    
    public ArrayList<DShape> getShapes(){
        return shapes;
    }
    
    public int count(){
        return shapes.size();
    }
    
    public boolean isEmpty(){
        return shapes.isEmpty();
    }
    
    public void clear(){
        shapes.clear();
    }
    
    @Override
    public void enable(){
        super.enable();
        shapes.forEach(shape->shape.enable());
    }
    
    @Override
    public void disable(){
        super.disable();
        shapes.forEach(shape->shape.disable());
    }
    
}
