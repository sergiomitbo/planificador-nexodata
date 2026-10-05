package planificador;

import java.util.Collections;
import java.util.List;

/**
 * Todo lo que produce una simulación: los procesos ya terminados (copias, en el
 * orden del fichero), el diagrama de Gantt, los cambios de contexto y la traza.
 * No sabe nada de cómo se imprime: eso es cosa de {@link Informe}.
 */
public class ResultadoSimulacion {

    private final String algoritmo;
    private final List<Proceso> procesos;
    /** Una casilla por unidad de tiempo; {@code null} = CPU ociosa. */
    private final List<String> gantt;
    private final int cambiosContexto;
    private final List<Transicion> traza;

    public ResultadoSimulacion(String algoritmo, List<Proceso> procesos, List<String> gantt,
                               int cambiosContexto, List<Transicion> traza) {
        this.algoritmo = algoritmo;
        this.procesos = Collections.unmodifiableList(procesos);
        this.gantt = Collections.unmodifiableList(gantt);
        this.cambiosContexto = cambiosContexto;
        this.traza = Collections.unmodifiableList(traza);
    }

    public double getRetornoMedio() {
        return procesos.stream().mapToInt(Proceso::getRetorno).average().orElse(0);
    }

    public double getEsperaMedia() {
        return procesos.stream().mapToInt(Proceso::getEspera).average().orElse(0);
    }

    public double getRespuestaMedia() {
        return procesos.stream().mapToInt(Proceso::getRespuesta).average().orElse(0);
    }

    public String getAlgoritmo() { return algoritmo; }
    public List<Proceso> getProcesos() { return procesos; }
    public List<String> getGantt() { return gantt; }
    public int getCambiosContexto() { return cambiosContexto; }
    public List<Transicion> getTraza() { return traza; }
}
