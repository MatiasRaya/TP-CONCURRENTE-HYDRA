package org.hydra;

import com.google.common.collect.Iterables;

import org.hydra.beans.Segmento;
import org.hydra.beans.VectorSensibilizado;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Arrays;
import java.util.List;

/**
 * Clase principal que contiene el método principal para ejecutar la simulación de la red de Petri y
 * una función para correr un analizador de invariantes.
 */
public class Main {
    // Se declara un arreglo con los tokens iniciales de la red
    private static final double[] TOKENSINICIALES =
            //           1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17
            new double[]{0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 4, 2, 2, 3, 1, 2, 1};

    // Se declara una matriz que representa la matriz de incidencia
    private static final double[][] MATRIZFLUJODATA = {
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

    // Se declara un arreglo de segmentos, donde se le pasa la cantidad de hilos, los elementos que componen el
    // segmento y el nombre de dicho segmento
    private static final Segmento[] segmentos = {
            new Segmento(2, Iterables.cycle(0).iterator(), "A"),
            new Segmento(4, Iterables.cycle(1, 3, 5).iterator(), "B"),
            new Segmento(4, Iterables.cycle(2, 4, 6).iterator(), "C"),
            new Segmento(1, Iterables.cycle(7).iterator(), "D"),
            new Segmento(4, Iterables.cycle(8, 9, 10, 11).iterator(), "E")
    };

    // Se declara una lista que conteiene los caminos de produccion
    private static final List<List<Integer>> plazasTransiciones = Arrays.asList(
            // Bloques cuadrados de madera
            Arrays.asList(0,1,3,5,7),

            // Bloques circulares de madera
            Arrays.asList(0,2,4,6,7),

            // Figuras de madera
            Arrays.asList(8,9,10,11));

    /**
     * Método principal que inicializa y ejecuta la simulación de la red de Petri.
     *
     * @param args Los argumentos de la línea de comandos (no se utilizan en este caso).
     * @throws InterruptedException Si se produce una interrupción mientras el hilo está esperando.
     */
    public static void main(String[] args) throws InterruptedException {
        // Se declara un nuevo elemento de la RDP pasandole la matriz de incidencia y los tokens iniciales
        RDP rdp = new RDP(MATRIZFLUJODATA, TOKENSINICIALES);

        // Se declara un nuevo elemento de los ProcesosModelados pasandole la rdp y el listado de los t-invariantes
        ProcesosModelados procesoModelado = new ProcesosModelados(rdp, plazasTransiciones);

        // Se crea un nuevo elemento de AdminMonitor pasandole los procesoModelado de la red
        AdminMonitor monitor = new AdminMonitor(procesoModelado);

        // Se declara un nuevo elemento de VectorSensibilizado pasandole el monitor
        VectorSensibilizado vectorSensibilizado = new VectorSensibilizado(monitor);

        // Se setea el vector de sensibilizado de la red bansadose en vectorSensibilizaddo
        rdp.setVectorSensibilizado(vectorSensibilizado);

        // Se declara un nuevo elemento de Inicializador pasandole el monitor, los segmentos y los procesos
        // que modela la red
        Inicializador initializer = new Inicializador(monitor, segmentos, procesoModelado);

        // Se crea un nuevo elemento de Estadistica con la lista de t-invariantes
        Estadistica estadistica = new Estadistica(plazasTransiciones);

        // Se llama al metodo para registrar el tiempo de inicio
        estadistica.setTimeStart();

        // Se inicializan y lanzan los hilos
        initializer.start();

        // Se espera a que se tengan 1000 invariantes en total
        estadistica.esperar1000TInvariantes();

        // Se finalizan todos los hilos
        initializer.finish();

        // Se llama al metodo para registrar el tiempo de finalizacion
        estadistica.setTimeStop();

        // Se imprimen las estadisticas de la ejecucion
        estadistica.imprimirEstadisticas();

        // Se llama a la funcion que corre el analizador para ver que las ejecuciones sean validas
        correrAnalizadorTInvariantes();
    }

    /**
     * Funcion que se encarga de analizar los invariantes y de imprimir el resultado de ejecutar el
     * archivo encargado de esto
     */
    public static void correrAnalizadorTInvariantes() {
        // Se declara un String con el path del archivo a ejecutar
        String path = "../Regex/regex1.py";

        // Se declara un arreglo de String que tiene el parametro para ejecutar el script y el path donde se encuentra
        String[] cmd = {"python3", path};

        try {
            // Se ejecuta el comando del script
            Process proceso = Runtime.getRuntime().exec(cmd);

            // Se imprime por consola que se termino la ejecucion de la REGEX
            System.out.println("\nEjecucion de la REGEX");

            String commandRead;

            // Se crea un BufferedReader para leer la salida del proceso a través de su flujo de entrada estándar
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proceso.getInputStream()));

            // Se itera sobre cada línea de la salida del proceso hasta que no haya más líneas
            // Se lee una línea del proceso y la almacena en commandRead
            while ((commandRead = stdInput.readLine()) != null) {
                // Se imprime la linea leida del proceso
                System.out.println(commandRead);
            }

            // Se destruye el proceso una vez que se termino de leer la salida
            proceso.destroy();

            try {
                // Se espera a que el proceso termine
                proceso.waitFor();

                // Se imprime el codigo con el que termino la REGEX
                System.out.printf("REGEX termino con el codigo %d\n", proceso.exitValue());
            }
            catch (InterruptedException e) {
                // Se crea un elemento de la clase Logger
                Logger logger = Logger.getLogger(Main.class);

                // Se almacena en el log que se produjo una excepcion al tratar de finalizar el proceso
                logger.error("Se produjo una excepción al tratar de finalizar el proceso:", e);
            }
        }
        catch (IOException e) {
            // Se crea un elemento de la clase Logger
            Logger logger = Logger.getLogger(Main.class);

            // Se almacena en el log que se produjo una excepcion al tratar de ejecutar el comando
            logger.error("Se produjo una excepción al ejecutar el comando:", e);
        }
    }
}
