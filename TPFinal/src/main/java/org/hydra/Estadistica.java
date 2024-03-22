package org.hydra;

import java.util.List;

public class Estadistica {
    private long timeStart;
    private long timeStop;
    private final List<List<Integer>> tInvariantes;
    private final Object notificador = new Object();

    public Estadistica(List<List<Integer>> plazasTransiciones) {
        this.tInvariantes = plazasTransiciones;

        for (List<Integer> lista : tInvariantes) {
            System.out.print("[ ");
            for (Integer elemento : lista) {
                System.out.print(elemento + " ");
            }
            System.out.println("]");
        }
    }

    /**
     * Se setea el tiempo de inicio del programa
     */
    public void setTimeStart() {
        // Se almacena la hora actual del sistema en milisegundos
        this.timeStart = System.currentTimeMillis();
    }

    /**
     * Se setea el tiempo de finalizacion del programa
     */
    public void setTimeStop() {
        // Se almacena la hora actual del sistema en milisegundos
        this.timeStop = System.currentTimeMillis();
    }

    /**
     * Se imprimen por consola las estadisticas de la ejecucion
     */
    public void imprimirEstadisticas() {
        // Se imprime por pantalla el tiempo total que se demoro la ejecucion
        System.out.printf("Tiempo total de ejecucion: %d\n", timeStop - timeStart);
    }

    /**
     * Funcion que espera a que se tengan los 1000 invariantes
     */
    public void esperar1000TInvariantes() throws InterruptedException {
        // Bloque de codigo que espera a que se reciba una notificacion
        synchronized (notificador) {
            try {
                // Se espera hasta que otro hilo llame a notificador para salir de aca
                notificador.wait();
            }
            catch (InterruptedException e) {
                // Se imprime la interrupcion
                e.printStackTrace();
            }
        }
    }
}
