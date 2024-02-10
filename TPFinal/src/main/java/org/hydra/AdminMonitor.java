package org.hydra;

import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Esta clase representa el monitor de concurrencia que controlará todos los accesos a los
 *  recursos compartidos de la RdP.
 */
public class AdminMonitor {

    private final Semaphore mutex = new Semaphore(1); /*Semáforo del monitor*/
    private final RDP rdp;
    private final Politicas politicas;
    private final Colas colaTransicion; /*Cola de espera*/

    /**
     * Constructor de la clase.
     *
     * @param procesoModelado proceso modelado por la RdP
     */
    public AdminMonitor(ProcesosModelados procesoModelado) {
        this.rdp = procesoModelado.getRDP();
        this.politicas = procesoModelado.getPolitics();
        this.colaTransicion = new Colas(rdp.getTotaltransiciones());
    }

    /**
     * Este método modela el diagrama de secuencias de un monitor de concurrencia para el disparo de
     *  una transición.
     *
     * @param transicion transición a disparar
     * @throws RuntimeException manejada en shooter
     */
    public void disparoTransicion(int transicion) throws RuntimeException{
        /*Intenta tomar el mutex del monitor para poder ingresar.*/
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e); /*manejada en el método run del shooter*/
        }

        /*En este punto el hilo logró ingresar al monitor.*/
        boolean k = true;

        while(k) {
            /*En el método disparo de la RdP, si la transición a disparar está sensibilizada por tokens y
             * temporalmente, se dispara y se actualiza el vector de marcado mediante la ecuación
             * fundamental, luego retorna true.
             * Si la transición no se puede disparar se retorna false.
             */
            k = rdp.disparo(transicion, false);

            if(k) {/*k = true --> Transición disparada, el hilo dentro del monitor intentará despertar otro*/

                List<Integer> sensibilizado = rdp.getSensibilizadas();
                List<Integer> hilosListos = colaTransicion.getTransicionesEspera();
                /*Se queda con las transiciones sensibilizadas que tienen hilos esperando para dispararlas*/
                sensibilizado.retainAll(hilosListos);

                if(sensibilizado.size() > 0) { /*Hay hilos esperando por transiciones sensibilizadas*/
                    int siguienteTransicion = politicas.getDisparoPrioritario(sensibilizado); /*Consulta a la política cuál hilo despertar*/
                    colaTransicion.release(siguienteTransicion); /*Despierta el hilo*/
                    return; /*Deja el monitor sin liberar el mutex, ya que queda el hilo que despertó, no hay owner*/

                } else { /*No hay hilos esperando por transiciones sensibilizadas*/
                    k = false; /*Sale del loop*/
                }

            } else { /*k = false --> No se puede disparar la transición*/
                mutex.release(); /*Devuelve el mutex del monitor*/
                colaTransicion.acquire(transicion); /*Ingresa a la cola de transiciones, acá el hilo se bloquea*/
                /*Cuando un hilo es despertado de una cola, continua su ejecución en este punto, por lo tanto,
                 * es necesario colocar k = true para que pueda ingresar nuevamente al loop*/
                k = true;
            }
        }
        /*Una vez que abandona el loop, devuelve el mutex del monitor*/
        mutex.release();
    }

    /**
     * Retorna la RdP asociada al monitor.
     * @return instancia de RDP del monitor
     */
    public RDP getRDP() {
        return rdp;
    }

    /**
     * Retorna el semáforo del monitor.
     * @return mutex
     */
    public Semaphore getMutex() {
        return mutex;
    }
}
