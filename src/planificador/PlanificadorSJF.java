package planificador;

import java.util.Comparator;
import java.util.List;

/**
 * SJF sin desalojo: cuando la CPU queda libre entra el proceso LISTO con la
 * ráfaga más corta. Solo elige entre los que ya han llegado (los que están en
 * la cola). Empates (regla 5): llegó antes; si coincide, aparece antes en el fichero.
 */
public class PlanificadorSJF extends Planificador {

    private static final Comparator<Proceso> CRITERIO = Comparator
            .comparingInt(Proceso::getRafaga)
            .thenComparingInt(Proceso::getLlegada)
            .thenComparingInt(Proceso::getOrden);

    @Override
    public String getNombre() {
        return "SJF sin desalojo";
    }

    @Override
    protected Proceso elegir(List<Proceso> listos) {
        return listos.stream().min(CRITERIO).orElseThrow();
    }
}
