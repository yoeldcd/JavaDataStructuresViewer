package ADT;

public class LinealArrayList<T> implements ADTList<T> {
    
    private T[] valuesArray;
    private int size;

    public LinealArrayList() {
        this.valuesArray = (T[]) new Object[100];
        this.size = 0;
    }
    
    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void add(T value) {
        
        if(size == valuesArray.length){
            increaseArray();
        }
        
        // guardo el nuevo valor en la posicion final de la lista
        valuesArray[size] = value;
        size ++;
    }

    @Override
    public void insert(T value, int index) {
        if(index >= 0 && index < size){
            
            if(size == valuesArray.length){
                increaseArray();
            }
            
            // corro los elementos hacia la derecha
            // para liberar la posicion
            for(int i = size; i > index; i--){
                valuesArray[i] = valuesArray[i - 1];
            }
            
            // coloco el nuevo dato en la posicion liberada
            valuesArray[index] = value;
            size ++;
        }
    }

    @Override
    public T get(int index) {
        
        if(index >= 0 && index < size){
            return valuesArray[index];
        }
        
        return null;
    }

    @Override
    public T remove(int index) {
        T removedValue = null;
        
        if(index >= 0 && index < size){
            // guardo para retornarlo
            removedValue = valuesArray[index];
            
            // desplazo hacia la izquierda
            for(int i = index; i < size; i++){
                valuesArray[index] = valuesArray[index + 1];
            }
            
            // borro el valor repetido en la ultima posicion
            valuesArray[size] = null;
            
            size --;
        }
        
        return removedValue;
    }

    @Override
    public int indexOf(T value) {
        int index = 0;
        
        while(index < size){
            
            if(valuesArray[index].equals(value)){
                return index;
            }
            
            index ++;
        }
        
        // cuando la busqueda es infructuosa se retorna -1 por (CONVENSION)
        return -1;
    }
    
    private void increaseArray(){
        // creo un arreglo de mayor longitud que el actula
        T[] biggest = (T[]) new Object[size + 100];
        
        // copio los elementos en el arreglo grande
        for(int i = 0; i < size; i++){
            biggest[i] = valuesArray[i];
        }
        
        valuesArray = biggest;
    }
    
}
