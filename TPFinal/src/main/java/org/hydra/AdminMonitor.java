package org.hydra;

import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Clase que implementa un monitor de concurrencia para el disparo de transiciones en una Red de Petri.
 */
public class AdminMonitor {
    private final Semaphore mutex = new Semaphore(1);
    private final RDP rdp;
    private final Politicas politicas;
    private final Colas colaTransicion;

    /**
     * Constructor de la clase.
     *
     * @param procesoModelado proceso modelado por la RdP
     */
    public AdminMonitor(ProcesosModelados procesoModelado) {
        // Se almacena el puntero de la red en la variable local
        this.rdp = procesoModelado.getRDP();

        // Se almacena el puntero de la politica en la variable local
        this.politicas = procesoModelado.getPolitica();

        // Se crea un nuevo elemento de la clase Colas con el total de las transiciones de laa red
        this.colaTransicion = new Colas(rdp.getTotaltransiciones());
    }

    /**
     * Este método modela el diagrama de secuencias de un monitor de concurrencia para el disparo de una transición.
     *
     * @param transicion Transición a disparar
     * @throws RuntimeException Excepción manejada en shooter
     */
    public void disparoTransicion(int transicion) throws RuntimeException {
        try {
            // Se intenta tomar el mutex del monitor para poder ingresar
            this.mutex.acquire();
        }
        catch (InterruptedException e) {
            // Se lanza una excepcion que se resuelve en el run del disparador
            throw new RuntimeException(e);
        }

        // Se declara la variable en true
        boolean k = true;

        // Se ejecuta siempre que el indicador sea verdadero
        while(k) {
            /*En el método disparo de la RdP, si la transición a disparar está sensibilizada por tokens y
             * temporalmente, se dispara y se actualiza el vector de marcado mediante la ecuación
             * fundamental, luego retorna true.
             * Si la transición no se puede disparar se retorna false.
             */

            // Se realiza el disparo de la transicion en la RdP y retorna si fue exitosa o no
            k = rdp.disparo(transicion, false);

            // Se verifica si se pudo disparar la transicion
            if(k) {
                // Se obtienen las transiciones sensibilizadas
                List<Integer> sensibilizado = rdp.getSensibilizadas();

                // Se obtienen las transiciones en espera
                List<Integer> hilosListos = colaTransicion.getTransicionesEspera();

                // Se filtran las transiciones sensibilizadas que tienen hilos esperando
                sensibilizado.retainAll(hilosListos);

                // Se verifica que el listado de hilos sensibilizados no este vacio
                if (!sensibilizado.isEmpty()) {
                    // Se determina cual es la siguiente transicion a despertar
                    int siguienteTransicion = politicas.getDisparoPrioritario(sensibilizado);

                    // Se despierta el hilo correspondiente
                    colaTransicion.release(siguienteTransicion);

                    // Se sale del monitor sin liberar el mutex del monitor
                    return;

                }
                else {
                    // Se sale del loop
                    k = false;
                }

            }
            else {
                // Se libera el mutex del monitor
                mutex.release();

                // Se ingresa a la cola de transiciones la transicion y se bloquea el hilo
                colaTransicion.acquire(transicion);

                // Se continua el loop despues de ser despertado
                k = true;
            }
        }

        // Se libera el mutex del monitor al salir del loop
        mutex.release();
    }

    /**
     * Retorna la RdP asociada al monitor.
     *
     * @return Instancia de RDP del monitor
     */
    public RDP getRDP() {
        // Retorna la instancia de la RdP asociada al monitor
        return rdp;
    }

    /**
     * Retorna el semáforo del monitor.
     *
     * @return Mutex del monitor
     */
    public Semaphore getMutex() {
        // Retorna el semáforo (mutex) del monitor
        return mutex;
    }
}
