
package com.yoeld99apps.toolsTDAs;

import java.util.ArrayList;

public class DataNode<T> {
    
    private T value;
    private ArrayList<DataNode> links; 
    
    public DataNode(T value){
        this.value = value;
    }
    
    private void setValue(T value){
        this.value = value;
    }
    
    private T getValue(){
        return this.value;
    }
    
    public void conectTo(DataNode otherNode, boolean invertible){
        
        // add reference to next nodes
        this.links.add(otherNode);
        
        // add inversed reference
        if(invertible)
            otherNode.links.add(this);
        
    }
    
    public void disconectTo(DataNode otherNode){
        
        // remove references between nodes
        this.links.remove(otherNode);
        otherNode.links.remove(this);
        
    }
    
    public void disconect(boolean invertible){
        
        // remove any references on linkeds nodes
        if(invertible){
            for(DataNode otherNode : this.links){
                otherNode.links.remove(this);
            }
        }
        
        this.links.clear();
    }
    
}
