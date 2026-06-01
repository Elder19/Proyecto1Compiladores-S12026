import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ErroresSemanticos {

    public static List<String> errores = new ArrayList<>();

    // true  = imprime todos
    // false = imprime solo un error por línea
    public static boolean imprimirTodos = true;

    public static void agregar(String mensaje) {
        errores.add(mensaje);
    }

    public static boolean hayErrores() {
        return !errores.isEmpty();
    }

    public static void imprimir() {
        System.out.println("\n[ ERRORES SEMÁNTICOS ]");
        System.out.println("──────────────────────────────────────────────────────");

        List<String> erroresAImprimir = obtenerErroresParaImprimir();

        if (erroresAImprimir.isEmpty()) {
            System.out.println(" Sin errores semánticos.");
            return;
        }

        for (String error : erroresAImprimir) {
            System.out.println(error);
        }
    }

    public static List<String> obtenerErroresParaImprimir() {
        if (imprimirTodos) {
            return errores;
        }

        List<String> filtrados = new ArrayList<>();
        Set<Integer> lineasReportadas = new HashSet<>();

        for (String error : errores) {
            int linea = extraerLinea(error);

            if (linea == -1) {
                filtrados.add(error);
                continue;
            }

            if (lineasReportadas.contains(linea)) {
                continue;
            }

            lineasReportadas.add(linea);
            filtrados.add(error);
        }

        return filtrados;
    }

    private static int extraerLinea(String error) {
        try {
            String marcador = "línea ";
            int inicio = error.indexOf(marcador);

            if (inicio == -1) {
                marcador = "linea ";
                inicio = error.indexOf(marcador);
            }

            if (inicio == -1) {
                return -1;
            }

            inicio += marcador.length();

            int fin = error.indexOf(",", inicio);
            if (fin == -1) fin = error.indexOf(":", inicio);
            if (fin == -1) fin = error.indexOf(" ", inicio);
            if (fin == -1) fin = error.length();

            return Integer.parseInt(error.substring(inicio, fin).trim());
        } catch (Exception e) {
            return -1;
        }
    }

    public static void limpiar() {
        errores.clear();
    }

    public static List<String> obtenerErrores() {
        return errores;
    }

    // ============================================================
    // VALIDACIONES DE TIPOS
    // ============================================================

    public static boolean tiposCompatibles(String esperado, String recibido) {
        if (esperado == null || recibido == null) return false;
        if (esperado.equals(recibido)) return true;
        if (esperado.equals("float") && recibido.equals("int")) return true;
        return false;
    }

    public static boolean esNumerico(String tipo) {
        return "int".equals(tipo) || "float".equals(tipo);
    }

    public static boolean esEntero(String tipo) {
        return "int".equals(tipo);
    }

    public static String validarAritmetica(String t1, String t2, String op, int linea, int columna) {
        if ("error".equals(t1) || "error".equals(t2)) return "error";

        if (!esNumerico(t1) || !esNumerico(t2)) {
            agregar(
                "Error semántico en línea " + linea + ", columna " + columna +
                ": operación aritmética inválida '" + t1 + " " + op + " " + t2 + "'."
            );
            return "error";
        }

        if ("%".equals(op) || "//".equals(op)) {
            if (!esEntero(t1) || !esEntero(t2)) {
                agregar(
                    "Error semántico en línea " + linea + ", columna " + columna +
                    ": el operador '" + op + "' solo permite operandos de tipo int."
                );
                return "error";
            }
            return "int";
        }

        return ("float".equals(t1) || "float".equals(t2)) ? "float" : "int";
    }

    public static String validarRelacionalNumerica(String t1, String t2, String op, int linea, int columna) {
        if ("error".equals(t1) || "error".equals(t2)) return "error";

        if (esNumerico(t1) && esNumerico(t2)) {
            return "bool";
        }

        agregar(
            "Error semántico en línea " + linea + ", columna " + columna +
            ": el operador '" + op +
            "' solo permite operandos int o float. Se recibió '" +
            t1 + "' y '" + t2 + "'."
        );

        return "error";
    }

   public static String validarIgualdad(String t1, String t2, String op, int linea, int columna) {
    if ("error".equals(t1) || "error".equals(t2)) return "error";

    if (esNumerico(t1) && esNumerico(t2)) {
        return "bool";
    }

    agregar(
        "Error semántico en línea " + linea + ", columna " + columna +
        ": el operador '" + op +
        "' solo permite comparar valores int o float. Se recibió '" +
        t1 + "' y '" + t2 
    );

    return "error";
}

    public static String validarLogica(String t1, String t2, String op, int linea, int columna) {
        if ("error".equals(t1) || "error".equals(t2)) return "error";

        if ("bool".equals(t1) && "bool".equals(t2)) {
            return "bool";
        }

        agregar(
            "Error semántico en línea " + linea + ", columna " + columna +
            ": el operador '" + op + "' solo permite operandos de tipo bool. Se recibió '" +
            t1 + "' y '" + t2 + "'."
        );

        return "error";
    }

    public static String validarNot(String tipo, int linea, int columna) {
        if ("error".equals(tipo)) return "error";

        if ("bool".equals(tipo)) {
            return "bool";
        }

        agregar(
            "Error semántico en línea " + linea + ", columna " + columna +
            ": el operador 'NOT' solo puede aplicarse a expresiones de tipo bool. Se recibió '" +
            tipo + "'."
        );

        return "error";
    }

    public static void validarCondicionBooleana(String tipo, String estructura, int linea, int columna) {
        if ("error".equals(tipo)) return;

        if (!"bool".equals(tipo)) {
            agregar(
                "Error semántico en línea " + linea + ", columna " + columna +
                ": la condición de '" + estructura +
                "' debe ser de tipo bool. Se recibió '" + tipo + "'."
            );
        }
    }

    public static void validarCondicionSwitch(String tipo, int linea, int columna) {
        if ("error".equals(tipo)) return;

        if (!"int".equals(tipo) && !"char".equals(tipo)) {
            agregar(
                "Error semántico en línea " + linea + ", columna " + columna +
                ": la expresión de 'switch' debe ser de tipo int o char. Se recibió '" +
                tipo + "'."
            );
        }
    }
}