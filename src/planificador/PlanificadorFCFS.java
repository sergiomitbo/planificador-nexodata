package planificador;

import java.util.List;

/** FCFS: primero en llegar, primero en ser atendido. Sin desalojo. */
public class PlanificadorFCFS extends Planificador {

    @Override
    public String getNombre() {
        return "FCFS";
    }

    /** La cola de listos ya está en orden de llegada (y de fichero en empates). */
    @Override
    protected Proceso elegir(List<Proceso> listos) {
        return listos.get(0);
    }
}
