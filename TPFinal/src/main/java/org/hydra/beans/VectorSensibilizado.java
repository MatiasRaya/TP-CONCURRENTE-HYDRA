package org.hydra.beans;

import org.hydra.AdminMonitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Vector de sensibilizados, utilizado para el manejo de la temporalidad de las transiciones temporizadas.
 */
public class VectorSensibilizado {
    List<Integer> sensibilizado; //Transiciones sensibilizadas
    private final long[] alpha = {0,10,10,10,10,10,10,10,0,10,10,10}; //Límite inferior de la ventana
    private final long[] beta = new long[alpha.length]; //Límite superior de la ventana
    private final long[] tiempoEspera = new long[alpha.length]; //Tiempo en el que se sensibilizó una transición (por tokens).
    private final boolean[] esperandoPor = new boolean[alpha.length]; //Banderas de espera por transición.
    AdminMonitor adminMonitor;


    /**
     * Constructor de la clase
     *
     * @param adminMonitor monitor de concurrencia de la RdP
     */
    public VectorSensibilizado(AdminMonitor adminMonitor) {
        // Se inicializa la lista de sensibilizado
        this.sensibilizado = new ArrayList<>();

        // Se completa el vector de tiempo de espera con la hora actual del sistema
        Arrays.fill(this.tiempoEspera, System.currentTimeMillis());

        // Se setea en false cada elemento del array de esperandoPor
        Arrays.fill(this.esperandoPor, false);

        // Se setea en un valor alto el limite superior de la ventana de tiempo
        Arrays.fill(this.beta,0xFFFFFFF);

        // Se almacena el parametro del puntero de adminMonitor en la variable global
        this.adminMonitor = adminMonitor;
    }

    /**
     * Retorna un valor boolean que representa si la transición está sensibilizada por tokens y por tiempo.
     *
     * @param transicion transición consultada
     * @param disparoFinal bandera de finalización del programa
     * @return true si está sensibilizada, false si no lo está
     */
    public boolean estaSensibilizada(int transicion, boolean disparoFinal) {
        // Se inicializa la variable a retornar en false
        boolean retval = false;

        // Se verifica si el vector de sensibilizado contiene la transicion
        if(this.sensibilizado.contains(transicion)) {
            // Se verifica si es el disparo final
            if(disparoFinal)
            {
                // Se almacena un true en la variable a retornar
                retval = true;
            }
            else {
                // Se almacena el resultado del metodo que verifica si esta sensibilizado por tiempo
                retval = estaSensibilizadaPorTiempo(transicion);
            }
        }

        // Se devuelve el resultado de verificar si esta sensibilizada
        return retval;
    }

    /**
     * Retorna un valor booleano que representa si la transición está temporizada temporalmente o no. A demás
     *  maneja los diferentes casos de llegada del hilo a la ventana temporal.
     *
     * @param transicion transición consultada
     * @return true si está sensibilizada temporalmente, false si no
     * @throws RuntimeException si el hilo es interrumpido
     */
    public boolean estaSensibilizadaPorTiempo(int transicion) throws RuntimeException {
        if (this.alpha[transicion] == 0){ /*Alpha = 0 representa una transición no temporizada, por lo tanto sólo importan los tokens*/
            System.out.printf("Hilo %s, transicion T%d sin temporizar\n",Thread.currentThread().getName(),transicion+1);
            return true; /*Se dispara*/
        }

        if (esperandoPor[transicion]){ /*Hay un hilo durmiendo a la espera de esta transición*/
            System.out.printf("Hilo %s, transicion T%d hay un hilo esperando\n",Thread.currentThread().getName(),transicion+1);
            return false; /*El hilo se coloca en la cola de la transición*/
        }

        /*Cota inferior de la ventana = tiempo en que la transición fue sensibilizada por tokens + alpha*/
        /*Cota superior de la ventana = tiempo en que la transición fue sensibilizada por tokens + beta*/
        long tiempoActual = System.currentTimeMillis();
        boolean enVentana = tiempoActual >= this.tiempoEspera[transicion] + this.alpha[transicion] && tiempoActual <= this.tiempoEspera[transicion] + this.beta[transicion];
        if (enVentana){ /*Dentro de la ventana temporal*/
            System.out.printf("Hilo %s, transicion T%d en la ventana\n",Thread.currentThread().getName(),transicion+1);
            return true; /*Se dispara*/
        }

        boolean preVentana = tiempoActual < this.tiempoEspera[transicion] + this.alpha[transicion];
        if (!preVentana){ /*Si no está antes de la ventana ni adentro, entonces se pasó*/
            System.out.printf("Hilo %s, transicion T%d despues de la ventana\n",Thread.currentThread().getName(),transicion+1);
            return false; //Ver la posibilidad de lanzar excepción para que no entre a la cola
        }

        /*En este punto, el hilo está antes de la ventana temporal*/
        this.esperandoPor[transicion] = true; /*Actualiza el vector de espera*/
        long tiempoDescanso = this.tiempoEspera[transicion] + this.alpha[transicion] - tiempoActual; /*Tiempo que le falta para llegar a la cota inferior*/
        this.adminMonitor.getMutex().release(); /*Abandona el mutex*/
        System.out.printf("Hilo %s, transicion T%d antes de la ventana, listo para dormir\n",Thread.currentThread().getName(),transicion+1);

        try {
            TimeUnit.MILLISECONDS.sleep(tiempoDescanso); /*Duerme el tiempo que le falta para llegar*/
            this.adminMonitor.getMutex().acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
        /*En este punto ya tomó el mutex del monitor*/
        this.esperandoPor[transicion] = false; /*Actualiza el vector de espera*/
        setSensibilizar(this.adminMonitor.getRDP().getSensibilizadas()); /*Actualiza el vector de sensibilizadas, ya que pudo cambiar*/
        System.out.printf("Hilo %s, transicion T%d despierto\n",Thread.currentThread().getName(),transicion+1);
        return estaSensibilizada(transicion, false); /*Verifica si sigue estando sensibilizada*/
    }

    /**
     * Actualiza el vector de tiempos de las transiciones. Solo actualiza el de las que no estaban sensibilizadas
     *  anteriormente pero luego sí.
     *
     * @param nuevaSensibilizacion nuevas transiciones sensibilizadas
     */
    public void actualizarTiempoEspera(List<Integer> nuevaSensibilizacion) {
        // Se recorren las nuevas transiciones
        for (Integer i : nuevaSensibilizacion) {
            // Se verifica si el arreglo de sensibilizado contiene a la nueva transicion
            if(!this.sensibilizado.contains(i)) {
                // Se actualiza el tiempo de espera de la transicion
                this.tiempoEspera[i] = System.currentTimeMillis();
            }
        }
    }

    /**
     * Actualiza el listado de transiciones sensibilizadas.
     *
     * @param sensibilizado nuevas transiciones sensibilizadas
     */
    public void setSensibilizar(List<Integer> sensibilizado) {
        // Se almacena la parametro sensibilizado en la variable global
        this.sensibilizado = sensibilizado;
    }
}
