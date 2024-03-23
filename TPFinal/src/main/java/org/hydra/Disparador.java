package org.hydra;

import java.util.Iterator;

/**
 * Clase que implementa la interfaz Runnable para realizar el disparo de transiciones y la realización de tareas
 * en la simulación de una Red de Petri.
 */
public class Disparador implements Runnable {
    private final AdminMonitor monitor;
    private final Iterator<Integer> transiciones;
    private final ProcesosModelados procesoModelado;
    private boolean estaInterrumpido = false;

    /**
     * Constructor de la clase.
     *
     * @param monitor Monitor de concurrencia de la RdP
     * @param transiciones Transiciones de la RdP
     * @param procesoModelado Proceso modelado de la RdP
     */
    public Disparador(AdminMonitor monitor, Iterator<Integer> transiciones, ProcesosModelados procesoModelado) {
        // Se almacena el monitor pasado como parametro en la variable global
        this.monitor = monitor;

        // Se almacenan las transiciones pasadas como parametro en la variable global
        this.transiciones = transiciones;

        // Se almaccena el procesmo modelado pasado como parametro en la variable global
        this.procesoModelado = procesoModelado;
    }

    /**
     * Método que ejecuta el disparo de transiciones y la realización de tareas.
     * El disparo de la transición ocurre en exclusión mutua, mientras que la realización de las tareas puede ocurrir
     * de manera concurrente, ya que los recursos ya fueron asignados. Los hilos se ejecutarán hasta ser interrumpidos
     * y se complete un ciclo completo de producción.
     */
    @Override
    public void run() {
        // Se obtiene la siguiente transicion de la lista
        int currentTransition = this.transiciones.next();

        // Se itera siempre que no se haya interrumpido el hilo
        while(!(this.estaInterrumpido)) {
            try {
                // Se realiza el disparo de la transicion en el monitor
                this.monitor.disparoTransicion(currentTransition);

                // Se realiza la tarea asociada a la transicion del disparo
                this.procesoModelado.realizarTarea(currentTransition);

                // Se obtiene la siguiente transicion de la lista
                currentTransition = this.transiciones.next();
            }
            catch (RuntimeException e) {
                //Se marca el hilo como interrumpido
                this.estaInterrumpido = true;
            }
        }
    }

}
