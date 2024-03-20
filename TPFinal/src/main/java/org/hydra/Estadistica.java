package org.hydra;

public class Estadistica {
    private long timeStart;
    private long timeStop;

    public Estadistica(RDP rdp) {

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
}
