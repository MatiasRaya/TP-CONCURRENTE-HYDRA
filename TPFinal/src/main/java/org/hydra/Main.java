package org.hydra;

import com.google.common.collect.Iterables;
import org.hydra.beans.Segmento;
import org.hydra.beans.VectorSensibilizado;
import org.hydra.Estadistica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * Aquí se definen los objetos y variables que representan el sistema.
 *  Luego se da comienzo a la ejecución del programa.
 */
public class Main {

    private static final int TIEMPO = 1; /*Tiempo de ejecución*/ /* Tiempo de ejecucion en Ubuntu 22.04 */
//    private static final int TIEMPO = 95; /*Tiempo de ejecución*/ /* Tiempo de ejecucion en Windows 11 */

    private static final double[] TOKENSINICIALES = /*Marcado inicial de la red*/
            //           1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17
            new double[]{0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 4, 2, 2, 3, 1, 2, 1};
    private static final double[][] MATRIZFLUJODATA = { /*Matriz de flujo de incidencia*/
            //T1 T2 T3 T4 T5 T6 T7 T8 T9 T10 T11 T12
            { 1,-1,-1, 0, 0, 0, 0, 0, 0, 0,  0,  0}, //P1
            { 0, 1, 0,-1, 0, 0, 0, 0, 0, 0,  0,  0}, //P2
            { 0, 0, 1, 0,-1, 0, 0, 0, 0, 0,  0,  0}, //P3
            { 0, 0, 0, 1, 0,-1, 0, 0, 0, 0,  0,  0}, //P4
            { 0, 0, 0, 0, 1, 0,-1, 0, 0, 0,  0,  0}, //P5
            { 0, 0, 0, 0, 0, 1, 1,-1, 0, 0,  0,  0}, //P6
            {-1, 0, 0, 0, 0, 0, 0, 1, 0, 0,  0,  0}, //P7
            { 0, 0, 0, 0, 0, 0, 0, 0, 1,-1,  0,  0}, //P8
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1,  0}, //P9
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  1, -1}, //P10
            { 0, 0, 0, 0, 0, 0, 0, 0,-1, 0,  0,  1}, //P11
            {-1, 1, 1, 0, 0, 0, 0, 0, 0, 0, -1,  1}, //P12
            { 0,-1,-1, 1, 1, 0, 0, 0, 0,-1,  1,  0}, //P13
            { 0, 0, 0,-1,-1, 1, 1, 0,-1, 1,  0,  0}, //P14
            { 0, 0, 0, 0, 0,-1,-1, 1, 0, 0,  0,  0}, //P15
            { 0, 0, 0, 0, 0, 0, 0, 0,-1, 1,  0,  0}, //P16
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,-1,  1,  0}, //P17
    };
    private static final Segmento[] segmentos = { /*segmentos de la red*/
            new Segmento(2, Iterables.cycle(0).iterator(), "A"),
            new Segmento(4, Iterables.cycle(1, 3, 5).iterator(), "B"),
            new Segmento(4, Iterables.cycle(2, 4, 6).iterator(), "C"),
            new Segmento(1, Iterables.cycle(7).iterator(), "D"),
            new Segmento(4, Iterables.cycle(8, 9, 10, 11).iterator(), "E")
    };
    private static final List<List<Integer>> plazasTransiciones = Arrays.asList( /*Caminos de producción de la RdP*/
            Arrays.asList(0,1,3,5,7), /*Bloques cuadrados de madera*/
            Arrays.asList(0,2,4,6,7), /*Bloques circulares de madera*/
            Arrays.asList(8,9,10,11)); /*Figuras de madera*/

    /**
     * Se crean los objetos necesarios para el modelado del sistema utilizando las variables
     *  previamente definidas y mediante uno de estos objetos se inicia la ejecución del programa.
     * @param args none
     * @throws InterruptedException excepción por interrupción
     */
    public static void main(String[] args) throws InterruptedException {
        long inicio = System.currentTimeMillis();

        RDP rdp = new RDP(MATRIZFLUJODATA, TOKENSINICIALES);
        ProcesosModelados procesoModelado = new ProcesosModelados(rdp, plazasTransiciones);
        AdminMonitor monitor = new AdminMonitor(procesoModelado);
        VectorSensibilizado vectorSensibilizado = new VectorSensibilizado(monitor);
        rdp.setVectorSensibilizado(vectorSensibilizado);
        Inicializador initializer = new Inicializador(monitor, segmentos, procesoModelado);

        Estadistica estadistica = rdp.crearEstadistica();

//        initializer.start(TIEMPO);

        estadistica.setTimeStart();

        initializer.start();

//        estadistica.

        initializer.finish();

        estadistica.setTimeStop();

        String path = "../Regex/regex1.py";
//        String[] cmd = {"python3",path}; /* Ejecucion del script en Ubuntu 22.04 */
        String[] cmd = {"python",path}; /* Ejecucion del script en Windows 11 */
        try {
            Process proceso = Runtime.getRuntime().exec(cmd);
            System.out.println("\nEjecucion de la REGEX");
            String commandRead;
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
            while ((commandRead = stdInput.readLine()) != null)
                System.out.println(commandRead);
            proceso.destroy();
            System.out.printf("REGEX termino con el codigo %d",proceso.exitValue());
        } catch (IOException e) {
            e.printStackTrace();
        }

        long fin = System.currentTimeMillis();

        long tiempoTotal = fin - inicio;

        System.out.println("Hora de inicio: " + inicio);
        System.out.println("Hora al finalizar: " + fin);
        System.out.println("Tiempo transcurrido (milisegundos): " + tiempoTotal);
        System.out.println("Tiempo transcurrido (segundos): " + (tiempoTotal / 1000.0));
    }
}
