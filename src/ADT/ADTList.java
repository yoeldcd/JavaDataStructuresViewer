package ADT;

public interface ADTList<T> {
    
    int size();
    
    void add(T value);
    
    void insert(T value, int index);
    
    T get(int index);
    
    T remove(int index);
    
    int indexOf(T value);
    
}
