package ADT;

public class SingleLinkedList<T> implements ADTList<T> {

    private SingleNode<T> head;
    private int size;

    public SingleLinkedList() {
        this.head = null;
        this.size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void add(T value) {
        SingleNode<T> newNode = new SingleNode(value);
        SingleNode pointer = head;

        if (size == 0) {
            // convierto el nuevo nodo en la cabeza
            head = newNode;
        } else {

            // corro el puntero hasta el ultimo nodo de la lista 
            while (pointer.hasNextNode()) {
                pointer = pointer.getNextNode();
            }

            // coloco el nuevo nodo como siguiente del ultimo
            pointer.setNextNode(newNode);
        }

        size++;
    }

    @Override
    public void insert(T value, int index) {
        SingleNode<T> newNode = new SingleNode(value);
        SingleNode<T> pointer = head;
        int pointerIndex = 0;

        if (index == 0) {
            // coloco como nodo siguiente del nuevo nodo el valor de la cabeza 
            newNode.setNextNode(head);
            
            // convierto el nodo en la cabeza de la lista
            head = newNode;

            size++;

        } else if (index >= 0 && index < size) {

            /**
             * Corro el puntero hasta la posicion anterior al indice de incersion:
             *
             * ¡Notar Que! * En cada paso del while el puntero queda ubicado en el nodo siguiente, por lo que para
             * llegar al nodo anterior al indice donde vamos a insertar debemos dar un paso menos a su posicion real.
             *
             * Ejemplo: Para insertar en el indice 3 debemos llegar al nodo en el indice anterior o sea en 2. * -
             *
             * (0 => 1 => 2 => [3] => 4) :: IR (0 -> 3) = 2 PASOS
             *
             * - Al evaluar el while en indice 0, el puntero se desplaza 1 posicion y queda ubicado en el nodo con el indice
             * 1.
             *
             * - Al evaluar el while en indice 1, el puntero se desplaza 1 posicion y queda ubicado en el nodo con el
             * indice 2.
             *
             * - Como la condicion de parada es que sea menor, cuando se evalua con indice 2 se cierra el while y el
             * puntero queda ubicado sobre el nodo en el idice previo a el de insercion.
             *
             */
            while (pointerIndex < index - 1) {
                pointer = pointer.getNextNode();
                pointerIndex++;
            }

            // coloco el nodo siguiente al puntero como siguiente del nuevo nodo
            newNode.setNextNode(pointer.getNextNode());

            // coloco el nuevo nodo como siguiente del puntero
            pointer.setNextNode(newNode);


            size++;
        }

    }

    @Override
    public T get(int index) {

        T value = null;
        SingleNode<T> pointer = head;
        int pointerIndex = 0;

        if (index >= 0 && index < size) {

            if (index == 0) {
                // guardo el valor en el nodo cabeza para retornarlo
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
        SingleNode<T> pointer = head;
        int pointerIndex = 0;

        if (index >= 0 && index < size) {

            if (index == 0) {
                // tomo el valor de la cabeza para retornarlo
                value = head.getValue();

                // convierto el nodo siguiente en la cabeza
                head = head.getNextNode();

            } else {

                // corro el puntero hasta la posicion anterior al indice eliminado
                /* Se aplica el mismo principio explicado en la insercion */
                while (pointerIndex < index - 1) {
                    pointer = pointer.getNextNode();
                    pointerIndex++;
                }

                // tomo el valor del nodo siguiente al pntero para retornarlo
                value = pointer.getNextNode().getValue();

                // coloco el nodo siguiente al nodo en el indice eliminado
                // como siguiente del puntero
                pointer.setNextNode(pointer.getNextNode().getNextNode());

            }

            size--;
        }

        return value;
    }

    @Override
    public int indexOf(T searchedValue) {

        SingleNode<T> pointer = head;
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
