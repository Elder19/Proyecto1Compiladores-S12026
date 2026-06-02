import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/** Clase de generacion de codigo intermedio
 * - Almacena instrucciones generadas durante el analisis
 * - crea etiquetas temporales
 * - crea etiquetas unicas para control (pistas: if_bloque:, main:)
 */
public class CodigoIntermedio {

    // Almacena todas las instrucciones generadas por el codigo 3d
    private static List<String> instrucciones = new ArrayList<>();
    // contador de temporales t1, t2
    private static int contadorTemporales = 0;
    // contador de etiquetas de sentencias if_else, while
    private static HashMap<String, Integer> contadoresEtiquetas = new HashMap<>();

    // agrega una nueva instruccion a la lista
    public static void emitir(String instruccion) {
        instrucciones.add(instruccion);
    }

    // genera temporal
    public static String nuevoTemporal() {
        return "t" + (++contadorTemporales);
    }

    // Genera nueva etiqueta utilizando un prefijo unico
    // param: prefijo base de la etiqueta
    // return: etiqueta unica generada
    public static String nuevaEtiqueta(String prefijo) {
        int contador = contadoresEtiquetas.getOrDefault(prefijo, 0) + 1;
        contadoresEtiquetas.put(prefijo, contador);
        return prefijo + "_" + contador;
    }

    // Genera etiquetas unicas utilizando prefijo L: L1, L2
    public static String nuevaEtiqueta() {
        return nuevaEtiqueta("L");
    }

    //retorna lista de instrucciones
    public static List<String> getInstrucciones() {
        return instrucciones;
    }

    // Reinicia la estrucura por completo, elimina las intrucciones, temporales
    // y contadores
    public static void limpiar() {
        instrucciones.clear();
        contadorTemporales = 0;
        contadoresEtiquetas.clear();
    }
}