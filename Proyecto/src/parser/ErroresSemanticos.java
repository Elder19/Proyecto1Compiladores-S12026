import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ErroresSemanticos {

    public static List<String> errores = new ArrayList<>();

    // true  = imprime todos
    // false = imprime solo un error por línea
    public static boolean imprimirTodos = true;
    //ingresa el mensaje a la lista
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
    //filtra los mensajes para solo mostrar uno por linea
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
    //retorna la linea de un error para evitar mostrarmas de un error de la misma
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
    //Esa función sirve para saber si un tipo puede recibir otro tipo en una asignación o inicialización
    public static boolean tiposCompatibles(String esperado, String recibido) {
    if (esperado == null || recibido == null) return false;
    if ("error".equals(esperado) || "error".equals(recibido)) return false;

    // Tipado fuerte: solo son compatibles si son exactamente iguales
    return esperado.equals(recibido);
}
    //algunas operaciones solo reciben valores tipo int o float 
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
            ": operación aritmética inválida '" + t1 + " " + op + " " + t2 +
            "'. Solo se permiten operandos int o float."
        );
        return "error";
    }

    // Tipado fuerte: no se permite mezclar int con float.
    if (!t1.equals(t2)) {
        agregar(
            "Error semántico en línea " + linea + ", columna " + columna +
            ": no se puede aplicar el operador '" + op +
            "' entre tipos diferentes: '" + t1 + "' y '" + t2 +
            "'. El lenguaje es de tipado fuerte."
        );
        return "error";
    }

    // El módulo solo permite enteros.
    if ("%".equals(op)) {
        if (!"int".equals(t1)) {
            agregar(
                "Error semántico en línea " + linea + ", columna " + columna +
                ": el operador '%' solo permite operandos de tipo int. Se recibió '" +
                t1 + "'."
            );
            return "error";
        }

        return "int";
    }

    // División normal:
    // int / int -> int
    // float / float -> float
    if ("/".equals(op)) {
        return t1;
    }

    // +, -, *, ^:
    // int con int -> int
    // float con float -> float
    return t1;
}
    // Valida operadores relacionales numéricos como <, <=, > y >=.
    // Solo permite comparar int o float, y si es válido retorna bool.
   public static String validarRelacionalNumerica(String t1, String t2, String op, int linea, int columna) {
    if ("error".equals(t1) || "error".equals(t2)) return "error";

    if (!esNumerico(t1) || !esNumerico(t2)) {
        agregar(
            "Error semántico en línea " + linea + ", columna " + columna +
            ": el operador '" + op +
            "' solo permite operandos int o float. Se recibió '" +
            t1 + "' y '" + t2 + "'."
        );
        return "error";
    }

    // Tipado fuerte: int con int o float con float.
    if (!t1.equals(t2)) {
        agregar(
            "Error semántico en línea " + linea + ", columna " + columna +
            ": no se puede comparar con '" + op +
            "' valores de tipos diferentes: '" + t1 + "' y '" + t2 +
            "'. El lenguaje es de tipado fuerte."
        );
        return "error";
    }

    return "bool";
}
    // Valida operaciones de igualdad como equal y n_equal.
// En este lenguaje se permite comparar valores numéricos y el resultado es bool.
    public static String validarIgualdad(String t1, String t2, String op, int linea, int columna) {
    if ("error".equals(t1) || "error".equals(t2)) return "error";

    // Tipado fuerte: solo se comparan valores del mismo tipo.
    if (!t1.equals(t2)) {
        agregar(
            "Error semántico en línea " + linea + ", columna " + columna +
            ": no se puede usar '" + op +
            "' entre tipos diferentes: '" + t1 + "' y '" + t2 +
            "'. El lenguaje es de tipado fuerte."
        );
        return "error";
    }

    // Tipos permitidos para igualdad.
    if (
        "int".equals(t1) ||
        "float".equals(t1) ||
        "bool".equals(t1) ||
        "char".equals(t1) ||
        "string".equals(t1)
    ) {
        return "bool";
    }

    agregar(
        "Error semántico en línea " + linea + ", columna " + columna +
        ": el operador '" + op +
        "' no puede aplicarse al tipo '" + t1 + "'."
    );

    return "error";
}
    // Valida operaciones lógicas como AND y OR.
    // Ambos operandos deben ser bool y el resultado también es bool.   
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
    // Valida el operador NOT.
// Solo puede aplicarse sobre expresiones de tipo bool.
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
        // Valida que la condición de estructuras como if o while sea de tipo bool.
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
        // Valida que la expresión del switch sea de tipo int o char,
// ya que esos son los tipos permitidos para comparar contra los case.
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