package planificador;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Lee el fichero de procesos con formato {@code nombre;llegada;ráfaga}.
 *
 * <ul>
 *   <li>Ignora las líneas vacías y las que empiezan por {@code #}.</li>
 *   <li>Rechaza con un mensaje claro (número de línea incluido) las líneas que
 *       no tienen tres campos, los números no enteros, las llegadas negativas
 *       y las ráfagas menores o iguales que cero.</li>
 * </ul>
 */
public class LectorProcesos {

    private LectorProcesos() {
        // Clase de utilidad: no se instancia.
    }

    public static List<Proceso> leer(Path fichero) throws IOException, ErrorFormatoException {
        List<String> lineas = Files.readAllLines(fichero, StandardCharsets.UTF_8);
        List<Proceso> procesos = new ArrayList<>();

        for (int i = 0; i < lineas.size(); i++) {
            int numLinea = i + 1;
            String linea = lineas.get(i).strip();
            if (linea.isEmpty() || linea.startsWith("#")) {
                continue;
            }

            String[] campos = linea.split(";", -1);
            if (campos.length != 3) {
                throw error(numLinea, linea, "se esperaban 3 campos separados por ';' (nombre;llegada;ráfaga) y hay "
                        + campos.length);
            }
            String nombre = campos[0].strip();
            if (nombre.isEmpty()) {
                throw error(numLinea, linea, "el nombre del proceso está vacío");
            }
            int llegada = entero(campos[1], "llegada", numLinea, linea);
            int rafaga = entero(campos[2], "ráfaga", numLinea, linea);
            if (llegada < 0) {
                throw error(numLinea, linea, "la llegada no puede ser negativa (" + llegada + ")");
            }
            if (rafaga <= 0) {
                throw error(numLinea, linea, "la ráfaga debe ser mayor que cero (" + rafaga + ")");
            }
            procesos.add(new Proceso(nombre, llegada, rafaga, procesos.size()));
        }

        if (procesos.isEmpty()) {
            throw new ErrorFormatoException("El fichero " + fichero + " no contiene ningún proceso.");
        }
        return procesos;
    }

    private static int entero(String texto, String campo, int numLinea, String linea) throws ErrorFormatoException {
        try {
            return Integer.parseInt(texto.strip());
        } catch (NumberFormatException e) {
            throw error(numLinea, linea, "la " + campo + " debe ser un número entero y es '" + texto.strip() + "'");
        }
    }

    private static ErrorFormatoException error(int numLinea, String linea, String motivo) {
        return new ErrorFormatoException("Línea " + numLinea + " («" + linea + "»): " + motivo + ".");
    }
}
