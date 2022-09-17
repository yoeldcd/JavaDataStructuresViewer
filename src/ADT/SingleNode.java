
package ADT;

public class SingleNode<T> {
    
    private T value;
    
    private SingleNode<T> nextNode;
    
    public SingleNode(T value){
        this.value = value;
        this.nextNode = null;
    }
    
    public void setValue(T value){
        this.value = value;
    }
    
    public T getValue() {
        return value;
    }

    public void setNextNode(SingleNode<T> nextNode) {
        this.nextNode = nextNode;
    }
    
    public boolean hasNextNode(){
        return nextNode != null;
    }
    
    public SingleNode<T> getNextNode() {
        return nextNode;
    }
    
}
