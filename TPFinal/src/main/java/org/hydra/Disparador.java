package org.hydra;


import java.util.Iterator;


/**
 * Representa a los trabajadores de la línea de producción, cada shooter tendrá un segmento asociado.
 * Implementa la interfaz Runnable Estos objetos serán utilizados para crear los hilos de ejecución.
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
        this.monitor = monitor;
        this.transiciones = transiciones;
        this.procesoModelado = procesoModelado;
    }

    /**
     * Método que ejecuta el disparo de transiciones y la realización de tareas. Es importante destacar que
     *  el disparo de la transición ocurre en exclusión mutua debido a que este método pertenece al monitor y la
     *  realización de las tareas puede ocurrir de manera concurrente, ya que los recursos ya fueron asignados.
     *  Los hilos se encontrarán en ejecución hasta que sean interrumpidos y se complete un ciclo completo de
     *  producción (no pueden quedar ciclos incompletos al fin).
     */
    @Override
    public void run() {
        int currentTransition = transiciones.next();
        while(!(estaInterrumpido)) {
            try {
                monitor.disparoTransicion(currentTransition);
                procesoModelado.realizeTask(currentTransition);
                currentTransition = transiciones.next();
            } catch (RuntimeException e) {
                estaInterrumpido = true;
            }
        }
    }

}
