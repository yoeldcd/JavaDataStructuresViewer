package ADT;

public class DoubleNode<T> {
    
    private T value;
    private DoubleNode<T> beforeNode;
    private DoubleNode<T> nextNode;
    
    public DoubleNode(T value){
        this.value = value;
        this.nextNode = null;
        this.beforeNode = null;
        
    }

    public void setValue(T value){
        this.value = value;
    }
    
    public T getValue() {
        return value;
    }

    public void setNextNode(DoubleNode<T> nextNode) {
        this.nextNode = nextNode;
    }
    
    public boolean hasNextNode(){
        return nextNode != null;
    }
    
    public DoubleNode<T> getNextNode() {
        return nextNode;
    }

    public void setBeforeNode(DoubleNode<T> nextNode) {
        this.beforeNode = nextNode;
    }
    
    public boolean hasBeforeNode(){
        return beforeNode != null;
    }
    
    public DoubleNode<T> getBeforeNode() {
        return beforeNode;
    }

}
