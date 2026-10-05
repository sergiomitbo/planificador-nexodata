package planificador;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

/**
 * Muestra por consola los resultados de una simulación: diagrama de Gantt,
 * tabla de métricas con sus medias, cambios de contexto y, si se pide, la
 * traza de estados.
 */
public class Informe {

    /** Decimales con coma, como en la salida de referencia (7,00). */
    private static final Locale ES = Locale.forLanguageTag("es-ES");

    private final PrintStream out;

    public Informe(PrintStream out) {
        this.out = out;
    }

    public void imprimir(ResultadoSimulacion r, boolean conTraza) {
        out.println("=== " + r.getAlgoritmo() + " ===");
        imprimirGantt(r);
        out.println();
        imprimirTabla(r);
        out.println();
        out.println("Cambios de contexto: " + r.getCambiosContexto());
        if (conTraza) {
            out.println();
            imprimirTraza(r);
        }
        out.println();
    }

    // --- Diagrama de Gantt -----------------------------------------------

    private void imprimirGantt(ResultadoSimulacion r) {
        List<String> gantt = r.getGantt();
        int total = gantt.size();
        int ancho = String.valueOf(total).length();
        for (Proceso p : r.getProcesos()) {
            ancho = Math.max(ancho, p.getNombre().length());
        }
        StringBuilder filaT = new StringBuilder("t   ");
        StringBuilder filaCpu = new StringBuilder("CPU ");
        for (int i = 0; i < total; i++) {
            filaT.append(String.format("%-" + ancho + "s ", i));
            String casilla = gantt.get(i) == null ? "-" : gantt.get(i);   // "-" = CPU ociosa
            filaCpu.append(String.format("%-" + ancho + "s ", casilla));
        }
        filaT.append(total);   // instante final (regla 1)
        out.println(filaT);
        out.println(filaCpu.toString().stripTrailing());
    }

    // --- Tabla de métricas -------------------------------------------------

    private void imprimirTabla(ResultadoSimulacion r) {
        int anchoNombre = "Proceso".length();
        for (Proceso p : r.getProcesos()) {
            anchoNombre = Math.max(anchoNombre, p.getNombre().length());
        }
        String fmtCab = "%-" + anchoNombre + "s  %7s  %6s  %4s  %7s  %6s  %9s%n";
        String fmtFila = "%-" + anchoNombre + "s  %7d  %6d  %4d  %7d  %6d  %9d%n";
        out.printf(fmtCab, "Proceso", "Llegada", "Ráfaga", "Fin", "Retorno", "Espera", "Respuesta");
        for (Proceso p : r.getProcesos()) {
            out.printf(fmtFila, p.getNombre(), p.getLlegada(), p.getRafaga(), p.getFin(),
                    p.getRetorno(), p.getEspera(), p.getRespuesta());
        }
        out.println(String.format(ES, "Medias: retorno %.2f | espera %.2f | respuesta %.2f",
                r.getRetornoMedio(), r.getEsperaMedia(), r.getRespuestaMedia()));
    }

    // --- Traza de estados --------------------------------------------------

    private void imprimirTraza(ResultadoSimulacion r) {
        int anchoNombre = 1;
        for (Proceso p : r.getProcesos()) {
            anchoNombre = Math.max(anchoNombre, p.getNombre().length());
        }
        int anchoT = ("t=" + r.getGantt().size()).length();
        out.println("Traza de estados:");
        for (Transicion tr : r.getTraza()) {
            String prefijo = String.format("%-" + anchoT + "s %-" + anchoNombre + "s ", "t=" + tr.t(), tr.proceso());
            if (tr.esContinuacion()) {
                out.println(prefijo + "sigue en EJECUCION (" + tr.motivo() + ")");
            } else {
                out.println(prefijo + tr.origen() + " -> " + tr.destino() + " (" + tr.motivo() + ")");
            }
        }
    }
}
