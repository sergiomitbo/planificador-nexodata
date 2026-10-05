package planificador;

import java.util.ArrayList;
import java.util.List;

/**
 * Planificador de corto plazo para una CPU (patrón «método plantilla»).
 *
 * El bucle de simulación es el mismo para todos los algoritmos y está aquí,
 * una sola vez, en {@link #simular(List)}. Cada algoritmo solo decide dos cosas:
 * <ol>
 *   <li>{@link #elegir(List)}: a quién da la CPU cuando queda libre.</li>
 *   <li>{@link #agotaTurno(int)}: si el proceso en CPU debe dejarla aunque no
 *       haya terminado (por defecto, nunca: algoritmos sin desalojo).</li>
 * </ol>
 *
 * En cada instante t se sigue SIEMPRE este orden:
 * <ol>
 *   <li>Llegadas en t (en el orden del fichero) → entran en la cola de listos.</li>
 *   <li>El proceso en CPU: ¿ha terminado? → TERMINADO. ¿Agota su turno? →
 *       vuelve al final de la cola (detrás de los que acaban de llegar: regla 3),
 *       salvo que la cola esté vacía: sigue con un turno nuevo (regla 4).</li>
 *   <li>Si la CPU está libre, el algoritmo elige entre los listos.</li>
 *   <li>Se ejecuta una unidad (o la CPU queda ociosa) y se avanza a t+1.</li>
 * </ol>
 */
public abstract class Planificador {

    /** Nombre que aparece en la cabecera del informe. */
    public abstract String getNombre();

    /**
     * Elige el siguiente proceso entre los listos. La lista está en el orden en
     * que entraron en la cola (FIFO) y nunca está vacía. No hay que quitarlo de
     * la lista: lo hace {@link #simular(List)}.
     */
    protected abstract Proceso elegir(List<Proceso> listos);

    /**
     * ¿Debe el proceso en ejecución dejar la CPU tras haberla usado
     * {@code usadoEnTurno} unidades seguidas? Sin desalojo: nunca.
     */
    protected boolean agotaTurno(int usadoEnTurno) {
        return false;
    }

    /** Motivo que aparece en la traza cuando {@link #agotaTurno(int)} devuelve true. */
    protected String motivoDesalojo() {
        return "desalojado";
    }

    /**
     * Simula la ejecución de los procesos. Trabaja sobre copias, así que la
     * lista recibida no se modifica y se puede reutilizar con otro algoritmo.
     */
    public final ResultadoSimulacion simular(List<Proceso> originales) {
        List<Proceso> procesos = new ArrayList<>();
        for (Proceso p : originales) {
            procesos.add(p.copia());
        }

        List<Proceso> listos = new ArrayList<>();   // cola de listos, en orden de entrada
        List<String> gantt = new ArrayList<>();
        List<Transicion> traza = new ArrayList<>();

        Proceso enCpu = null;          // proceso en EJECUCION (null = CPU libre)
        Proceso ultimoEjecutado = null; // para contar cambios de contexto
        int usadoEnTurno = 0;
        int cambiosContexto = 0;
        int terminados = 0;
        int t = 0;

        while (terminados < procesos.size()) {

            // 1) Llegadas en t, en el orden del fichero (regla 2).
            for (Proceso p : procesos) {
                if (p.getLlegada() == t) {
                    mover(p, EstadoProceso.LISTO, t, "llega al sistema", traza);
                    listos.add(p);
                }
            }

            // 2) ¿El proceso en CPU termina o agota su turno?
            if (enCpu != null) {
                if (enCpu.haTerminadoRafaga()) {
                    enCpu.registrarFin(t);
                    mover(enCpu, EstadoProceso.TERMINADO, t, "completa su ráfaga", traza);
                    terminados++;
                    enCpu = null;
                } else if (agotaTurno(usadoEnTurno)) {
                    if (listos.isEmpty()) {
                        // Regla 4: nadie espera → sigue con un turno nuevo, sin pasar por LISTO.
                        usadoEnTurno = 0;
                        traza.add(new Transicion(t, enCpu.getNombre(), EstadoProceso.EJECUCION,
                                EstadoProceso.EJECUCION, motivoDesalojo() + " sin nadie en la cola: turno nuevo"));
                    } else {
                        // Regla 3: los que han llegado en t ya están en la cola, delante de él.
                        mover(enCpu, EstadoProceso.LISTO, t, motivoDesalojo(), traza);
                        listos.add(enCpu);
                        enCpu = null;
                    }
                }
            }

            if (terminados == procesos.size()) {
                break;
            }

            // 3) CPU libre: el algoritmo elige entre los que ya están listos.
            if (enCpu == null && !listos.isEmpty()) {
                Proceso elegido = elegir(listos);
                listos.remove(elegido);
                mover(elegido, EstadoProceso.EJECUCION, t, "el planificador lo elige", traza);
                elegido.registrarInicio(t);
                if (ultimoEjecutado != null && ultimoEjecutado != elegido) {
                    cambiosContexto++;   // la primera carga no cuenta
                }
                enCpu = elegido;
                usadoEnTurno = 0;
            }

            // 4) Ejecutar una unidad (casilla [t, t+1)) y avanzar.
            if (enCpu != null) {
                enCpu.ejecutarUnaUnidad();
                usadoEnTurno++;
                ultimoEjecutado = enCpu;
                gantt.add(enCpu.getNombre());
            } else {
                gantt.add(null);   // CPU ociosa
            }
            t++;
        }

        return new ResultadoSimulacion(getNombre(), procesos, gantt, cambiosContexto, traza);
    }

    private static void mover(Proceso p, EstadoProceso destino, int t, String motivo, List<Transicion> traza) {
        traza.add(new Transicion(t, p.getNombre(), p.getEstado(), destino, motivo));
        p.cambiarEstado(destino);
    }
}
