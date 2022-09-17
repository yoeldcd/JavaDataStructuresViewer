package ADT;

public class DoubleLinkedList<T> implements ADTList<T> {

    private DoubleNode<T> head;
    private DoubleNode<T> tail;
    private int size;
    
    public DoubleLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void add(T value) {
        DoubleNode<T> newNode = new DoubleNode(value);
        DoubleNode pointer = head;

        if (size == 0) {
            // convierto el nuevo nodo en la cabeza
            head = newNode;

        } else {

            // corro el puntero hasta el ultimo nodo de la lista 
            while (pointer.hasNextNode()) {
                pointer = pointer.getNextNode();
            }

            // coloco el nodo apuntado como anterior al nuevo nodo 
            newNode.setBeforeNode(pointer);

            // coloco el nuevo nodo como siguiente del nodo apuntado
            pointer.setNextNode(newNode);
        }

        // convierto el nuevo nodo en la cola de la lista
        tail = newNode;
        size++;
    }

    @Override
    public void insert(T value, int index) {
        DoubleNode<T> newNode = new DoubleNode(value);
        DoubleNode<T> pointer = head;
        int pointerIndex = 0;

        if (index == 0) {

            // coloco como siguiente al nuevo nodo el valor de la cabeza
            newNode.setNextNode(head);

            // convierto el nuvo nodo en la cabeza de la lista
            head = newNode;

            // incremento el tamanio de la lista
            size++;

            // Si la lista tiene un unico nodo luego de la insercion
            // este sera usado a la vez: como cabeza y cola.
            if (size == 1) {
                tail = newNode;
            }

        } else if (index >= 0 && index < size) {

            /**
             * Corro el puntero hasta la posicion del indice de incersion:
             *
             * ¡Notar Que! * En cada paso del while el puntero queda ubicado en el nodo siguiente, por lo que para
             * llegar al nodo en el indice donde vamos a insertar debemos dar un paso menos a su posicion real.
             *
             * Ejemplo: Para insertar en el indice 3 debemos llegar al nodo en el.
             *
             * ( 0 => 1 => 2 => [3] => 4 ) :: IR (0 -> 3) = 3 PASOS
             *
             * - Al evaluar el while en indice 0, el puntero se desplaza 1 posicion y queda ubicado en el nodo con el
             * indice 1.
             *
             * - Al evaluar el while en indice 1, el puntero se desplaza 1 posicion y queda ubicado en el nodo con el
             * indice 2.
             *
             * - Al evaluar el while en indice 2, el puntero se desplaza 1 posicion y queda ubicado en el nodo con el
             * indice 3.
             *
             * - Como la condicion de parada es que sea menor, cuando se evalua con indice 3 se cierra el while y el
             * puntero quedara ubicado sobre el nodo en el idice de insercion deseado.
             *
             */
            while (pointerIndex < index) {
                pointer = pointer.getNextNode();
                pointerIndex++;
            }

            // PASO 1: Coloco el nodo anterior al apuntado como anterior al nuevo nodo
            newNode.setBeforeNode(pointer.getBeforeNode());

            // PASO 2: Coloco el nodo apuntado como siguiente del nuevo nodo
            newNode.setNextNode(pointer);

            // PASO 3: Coloco el nuevo nodo como siguiente del anterior al apuntado
            pointer.getBeforeNode().setNextNode(newNode);

            // PASO 4: Coloco el nuevo nodo como anterior al apuntado 
            pointer.setBeforeNode(newNode);

            size++;
        }

    }

    @Override
    public T get(int index) {

        T value = null;
        DoubleNode<T> pointer = head;
        int pointerIndex = 0;

        if (index >= 0 && index < size) {

            if (index == 0) {
                // guardo el valor del nodo cabeza para retornarlo
                value = head.getValue();

            } else if (index == size - 1) {
                // guardo el valor del nodo cola para retornarlo
                value = head.getValue();

            } else {

                // corro el puntero hasta la posicion anterior al indice deseado
                while (pointerIndex < index) {
                    pointer = pointer.getNextNode();
                    pointerIndex++;
                }

                // guardo el valor en el nodo para retornarlo
                value = pointer.getValue();
            }
        }

        return value;
    }

    @Override
    public T remove(int index) {

        T value = null;
        DoubleNode<T> pointer = head;
        int pointerIndex = 0;

        if (index >= 0 && index < size) {

            if (index == 0) {
                // tomo el valor de la cabeza para retornarlo
                value = head.getValue();

                // convierto el nodo siguiente en la cabeza
                head = head.getNextNode();

            } else {

                // corro el puntero hasta la posicion del indice eliminado
                /* Se aplica el mismo principio explicado en la insercion */
                while (pointerIndex < index) {
                    pointer = pointer.getNextNode();
                    pointerIndex++;
                }

                // tomo el valor del nodo siguiente al pntero para retornarlo
                value = pointer.getNextNode().getValue();

                // PASO 1: Coloco como anterior del siguiente del nodo apuntado
                //         el nodo anterior al nodo apuntado
                pointer.getBeforeNode().setNextNode(pointer.getNextNode());
                
                // PASO 2: Coloco como siguiente del nodo anterior al apuntado
                //         el nodo siguiente al apuntado
                pointer.getNextNode().setBeforeNode(pointer.getBeforeNode());
                
                // Si el nodo apuntado es la cola de la lista
                // la cola pasa a ser el nodo anterior a el
                if(pointer == tail){
                    tail = pointer.getBeforeNode();
                }
                
                // elimino los enlaces del nodo eliminado
                pointer.setNextNode(null);
                pointer.setBeforeNode(null);
                
            }
            
            // decremento el tamnio de la lista
            size--;
        }

        return value;
    }

    @Override
    public int indexOf(T searchedValue) {

        DoubleNode<T> pointer = head;
        int pointerIndex = 0;

        // corro el puntero por toda la lista hasta encontrar el valor o acabarla
        while (pointer != null) {
            
            if (pointer.getValue().equals(searchedValue)) {
                return pointerIndex;
            }

            pointer = pointer.getNextNode();
            pointerIndex++;
        }

        return -1;
    }


}
