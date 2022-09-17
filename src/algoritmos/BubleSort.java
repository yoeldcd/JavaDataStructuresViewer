package algoritmos;

public class BubleSort {
    
    public int A(int n){
        int c = 0;
        int j = 0;
        
        for(int i = 0; i < n * n; i++){
            j = n;
            
            while(j > 0){
                c += B(n);
                j--;
            }
        }
        
        return c;
    }
    
    public int B(int n){
        int c = 0;
        int i = n;
        
        while(i > 0){
            c ++;
            i /= 2;
        }
        
        return c;
    }
    
}
