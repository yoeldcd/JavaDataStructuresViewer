package com.yoeld99apps.toolsTDAs;

import java.util.ArrayList;

public class DataGraph<T> {

    private final ArrayList<T> vertexs;
    private final ArrayList<ArrayList<Integer>> links;
    public final boolean isDigraph;
    private int size;
    
    
    public DataGraph(boolean isDigraph) {
        this.vertexs = new ArrayList();
        this.links = new ArrayList();
        this.isDigraph = isDigraph;
        this.size = 0;
    }

    public ArrayList<T> getVertexs() {
        return this.vertexs;
    }
    
    public ArrayList<ArrayList<Integer>> getLinks(){
        return this.links;
    }
    
    public void generateByLinkMatrix(ArrayList<T> vertexs, int[][] linkMatrix) {
        int nodeI = 0;

        this.vertexs.clear();

        // make any DataNodes instances
        for (T value : vertexs) {
            this.vertexs.add(value);
        }

        // conect any data nodes
        for (int[] links : linkMatrix) {
            for (int otherI : links) {
                this.links.get(nodeI).add(linkMatrix[nodeI][otherI]);
            }

            nodeI++;
        }

    }

    public void add(T value) {
        this.vertexs.add(value);
        this.links.add(new ArrayList());
    }
    
    public void conect(T value1, T value2){
        int nodeI = this.vertexs.indexOf(value1);
        int otherI = this.vertexs.indexOf(value2);
        
        if(nodeI > -1 && otherI > -1){
            this.links.get(nodeI).add(otherI);
            
            // make ineverse reference
            if(this.isDigraph)
                this.links.get(otherI).add(nodeI);
            
        }
    }
    
    public void conect(T value, ArrayList<Integer> newLinks) {
        int nodeI = this.vertexs.indexOf(value);
        
        if(nodeI > -1){
            ArrayList<Integer> nodeLinks = this.links.get(nodeI);
            
            for(int otherI : newLinks){
                nodeLinks.add(otherI);
                
                // make ineverse reference
                if(this.isDigraph){
                    this.links.get(otherI).add(nodeI);
                }
            }
            
        }
    }
    
    private void disconectNode(int nodeI) {
        int i = 0;

        if (this.isDigraph) {
            // delete any iversed nodes reference of it
            for (int otherI : this.links.get(nodeI)) {
                this.links.get(otherI).remove(nodeI);
            }
            
        } else {
            // delete all nodes references to it
            for (ArrayList<Integer> nodeLinks : this.links) {
                if (i != nodeI) {
                    nodeLinks.remove(nodeI);
                }

                i++;
            }
            
        }

        // delete node conectivity
        this.links.remove(nodeI);

    }

    public void remove(T value) {
        int nodeI = this.vertexs.indexOf(value);

        if (nodeI > -1) {
            this.disconectNode(nodeI);
        }
    }
    
    public int size(){
        return this.vertexs.size();
    }
}
