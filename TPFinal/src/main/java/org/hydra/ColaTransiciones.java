package org.hydra;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 *  Representa las colas de espera por las transiciones. Extiende de la clase Semaphore.
 */
public class ColaTransiciones extends Semaphore {

    private final int transicion;
    private final List<String> threads = new ArrayList<>();

    /**
     * Constructor de la clase.
     *
     * @param transicion transición por la que se espera en esta cola
     * @param permitidos número de mutex permitidos
     */
    public ColaTransiciones(int transicion, int permitidos) {
        // Se llama al constructor de la clase padre Semaphore
        super(permitidos);

        // Se almacena el parametro transicion en la variable global
        this.transicion = transicion;
    }

    /**
     * Añade el nombre del hilo actual a un listado de hilos bloqueados y ejecuta el método acquire() de
     *  la superclase. Una vez que el hilo es despertado por la ejecución del método release() por parte de otro hilo
     *  su nombre es eliminado de la lista.
     * @throws InterruptedException excepción por interrupción
     */
    @Override
    public void acquire() throws InterruptedException {
        // Se obtiene el nombre del hilo actual
        String hiloActual = Thread.currentThread().getName();

        // Se agrega el nombre del hilo actual a la lista de hilos en espera
        this.threads.add(hiloActual);

        // Se llama al metodo acquire de la clase padre
        super.acquire();

        // Se elimina el nombre del hilo actual de la lista al ser despertado
        this.threads.remove(Thread.currentThread().getName());
    }

    /**
     * Ejecuta el método release de la superclase.
     */
    @Override
    public void release() {
        // Se llama al metodo release de la clase padre
        super.release();
    }

    /**
     * Retorna el listado de hilos esperando en la cola.
     *
     * @return threadList
     */
    @Override
    public String toString() {
        // Se devuelve una representacion en cadena de los hilos en espera
        return  "T" + this.threads;
    }

    /**
     * Retorna la transición que es representada por esta cola.
     *
     * @return transicion
     */
    public int getTransicion() {
        // Se devuelve la transicion asociada a esta cola
        return this.transicion;
    }
}
