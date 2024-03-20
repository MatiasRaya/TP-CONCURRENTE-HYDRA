package org.hydra;

import org.hydra.beans.Segmento;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * En esta clase se definen los métodos necesarios para la creación, iniciación e
 *  interrupción de los hilos de ejecución.
 */
public class Inicializador {

    private final AdminMonitor monitor;
    private final Segmento[] segmentos;
    private final ProcesosModelados procesosModelados;

    /**
     * Constructor de la clase.
     *
     * @param monitor monitor del sistema
     * @param segmentos Array de segmentos de la RdP
     * @param procesosModelados proceso modelado de la RdP
     */
    public Inicializador(AdminMonitor monitor, Segmento[] segmentos, ProcesosModelados procesosModelados) {
        this.monitor = monitor;
        this.segmentos = segmentos;
        this.procesosModelados = procesosModelados;
    }

    /**
     * A partir de un objeto de la clase Segmento recibido como parámetro, crea una cantidad definida
     *  (cantidad máxima de hilos del segmento) de objetos Runnable (Disparador).
     *  Luego utiliza estos objetos como parámetro para la creación de objetos Thread, a los cuales se les
     *  asigna un nombre en función del segmento y su orden de creación.
     *
     * @param segmento segmento particular para el que se quieren crear los disparadores
     * @return Threads Un stream de Threads que contiene los hilos creados para el segmento específico
     */
    private Stream<Thread> createShooters(Segmento segmento) {
        return IntStream.range(0, segmento.getNroHilo()).mapToObj(i -> {
            Runnable shooter = new Disparador(monitor, segmento.getTransiciones(), procesosModelados);
            return new Thread(shooter, String.format("S%sN%s", segmento, i));
        });
    }

    /**
     * Este método funciona como lanzador del sistema, inicialmente crea los disparadores necesarios para cada
     *  segmento definido de la red haciendo uso del método anterior.
     *  Luego se encarga de lanzar todos los hilos generados, y luego de un tiempo establecido interrumpirlos.
     *
     * @param time tiempo de ejecución del programa (luego de este tiempo se interrumpen los hilos)
     * @throws InterruptedException Excepción por interrupción
     */
    public void start(int time) throws InterruptedException {
        List<Thread> disparadores = Arrays.stream(segmentos)
                .flatMap(this::createShooters)
                .collect(Collectors.toList());
        disparadores.parallelStream().forEach(Thread::start);
        TimeUnit.SECONDS.sleep(time);
        disparadores.parallelStream().forEach(Thread::interrupt);

        try {
            for(Thread shooter:disparadores){
                shooter.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("El hilo ha sido interrumpido");
        System.out.println(procesosModelados.getRDP().getTokens());
        int[] finaltransiciones = {1,2,3,4,5,6,7,9,10,11};
        int tamanio = procesosModelados.getRDP().getTotaltransiciones()-2;
        boolean fin = false;
        while (!fin){
                     fin = true;
            for(int i=0; i<tamanio; i++){
                if(procesosModelados.getRDP().disparo(finaltransiciones[i], true)){
                    procesosModelados.realizeTask(finaltransiciones[i]);
                    fin = false;
                }
            }
        }

        System.out.printf("Tokens finales %s\n",procesosModelados.getRDP().getTokens());
        System.out.printf("Contador final de transiciones %s\n", Arrays.toString(procesosModelados.getContadorDisparoTransiciones()));
        System.out.println("La ejecucion del programa ha finalizado");
    }

    public void start() throws InterruptedException {
        List<Thread> disparadores = Arrays.stream(segmentos)
                .flatMap(this::createShooters)
                .collect(Collectors.toList());
        disparadores.parallelStream().forEach(Thread::start);
    }

    public void finish() throws InterruptedException {
        List<Thread> disparadores = Arrays.stream(segmentos)
                .flatMap(this::createShooters)
                .collect(Collectors.toList());
        disparadores.parallelStream().forEach(Thread::interrupt);

        try {
            for(Thread shooter:disparadores){
                shooter.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("El hilo ha sido interrumpido");
        System.out.println(procesosModelados.getRDP().getTokens());
        int[] finaltransiciones = {1,2,3,4,5,6,7,9,10,11};
        int tamanio = procesosModelados.getRDP().getTotaltransiciones()-2;
        boolean fin = false;
        while (!fin){
            fin = true;
            for(int i=0; i<tamanio; i++){
                if(procesosModelados.getRDP().disparo(finaltransiciones[i], true)){
                    procesosModelados.realizeTask(finaltransiciones[i]);
                    fin = false;
                }
            }
        }

        System.out.printf("Tokens finales %s\n",procesosModelados.getRDP().getTokens());
        System.out.printf("Contador final de transiciones %s\n", Arrays.toString(procesosModelados.getContadorDisparoTransiciones()));
        System.out.println("La ejecucion del programa ha finalizado");
    }
}
