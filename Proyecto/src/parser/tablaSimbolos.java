import java.util.*;


public class TablaSimbolos {

    // ============================================================
    // CLASE INTERNA: SÍMBOLO
    // ============================================================

    /**
     * Representa un identificador almacenado en la tabla de símbolos.
     */
    public static class Simbolo {
        public String nombreToken;
        public String tipoSimbolo;
        public String fila;
        public String columna;
        public String tipoDato;
        public boolean VarInicializada; 

        public Simbolo(
                String nombreToken,
                String tipoSimbolo,
                String fila,
                String columna,
                String tipoDato,
                boolean VarInicializada
        ) {
            this.nombreToken = nombreToken;
            this.tipoSimbolo = tipoSimbolo;
            this.fila = fila;
            this.columna = columna;
            this.tipoDato = tipoDato;
            this.VarInicializada=VarInicializada;
        }
    }

    // ============================================================
    // ATRIBUTOS DE LA TABLA
    // ============================================================

    /**
     * Guarda los símbolos agrupados por scope.
     */
    private static LinkedHashMap<String, List<Simbolo>> tablas = new LinkedHashMap<>();

    /**
     * Pila de scopes activos. El scope actual siempre está en la cima.
     */
    private static Deque<String> scopes = new ArrayDeque<>();

    /**
     * Controla nombres repetidos de scopes para generar claves únicas.
     */
    private static Map<String, Integer> contadores = new HashMap<>();

    // ============================================================
    // MANEJO DE SCOPES
    // ============================================================

    /**
     * Entra a un nuevo scope.
     *
     * Si existen scopes con el mismo nombre, se genera una clave diferente
     * usando un contador.
     */
    public static void entrarScope(String nombre) {
        String padre = scopes.isEmpty() ? "" : scopes.peek();
        String claveBase = nombre + (padre.isEmpty() ? "" : "_" + padre);

        int n = contadores.getOrDefault(claveBase, 0) + 1;
        contadores.put(claveBase, n);

        String clave = claveBase;

        if (n > 1) {
            clave = nombre + n + (padre.isEmpty() ? "" : "_" + padre);
        }

        scopes.push(clave);
        tablas.putIfAbsent(clave, new ArrayList<>());
    }

    /**
     * Sale del scope actual.
     */
    public static void salirScope() {
        if (!scopes.isEmpty()) {
            scopes.pop();
        }
    }

    /**
     * Devuelve el scope actual.
     *
     * Si no hay scopes activos, se considera que el scope actual es global.
     */
    public static String scopeActual() {
        return scopes.isEmpty() ? "global" : scopes.peek();
    }

    // ============================================================
    // BÚSQUEDAS Y VALIDACIONES
    // ============================================================

    /**
     * Verifica si un identificador existe en el scope actual.
     */
    public static boolean existeEnScopeActual(String nombreToken) {
        String scope = scopeActual();

        if (!tablas.containsKey(scope)) {
            return false;
        }

        for (Simbolo s : tablas.get(scope)) {
            if (s.nombreToken.equals(nombreToken)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Verifica si un identificador existe en algún scope activo o en global.
     */
    public static boolean existe(String nombre) {
        for (String scope : scopes) {
            List<Simbolo> simbolos = tablas.get(scope);

            if (simbolos != null) {
                for (Simbolo s : simbolos) {
                    if (s.nombreToken.equals(nombre)) {
                        return true;
                    }
                }
            }
        }

        List<Simbolo> globales = tablas.get("global");

        if (globales != null) {
            for (Simbolo s : globales) {
                if (s.nombreToken.equals(nombre)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Busca un símbolo por nombre dentro de los scopes activos y luego en global.
     */
    public static Simbolo buscar(String nombreToken) {
        for (String scope : scopes) {
            if (tablas.containsKey(scope)) {
                for (Simbolo s : tablas.get(scope)) {
                    if (s.nombreToken.equals(nombreToken)) {
                        return s;
                    }
                }
            }
        }

        if (tablas.containsKey("global")) {
            for (Simbolo s : tablas.get("global")) {
                if (s.nombreToken.equals(nombreToken)) {
                    return s;
                }
            }
        }

        return null;
    }

    /**
     * Busca una función declarada en el scope global.
     */
    public static Simbolo buscarFuncion(String nombreToken) {
        if (!tablas.containsKey("global")) {
            return null;
        }

        for (Simbolo s : tablas.get("global")) {
            if (s.nombreToken.equals(nombreToken) && s.tipoSimbolo.equals("FUNCION")) {
                return s;
            }
        }

        return null;
    }

    // ============================================================
    // INSERCIÓN DE SÍMBOLOS
    // ============================================================

    /**
     * Agrega un símbolo a la tabla de símbolos.
     *
     * Si el símbolo ya existe en el scope actual, se registra un error semántico.
     * Las llamadas no se almacenan como símbolos declarados.
     */
    public static boolean agregar(
        String nombreToken,
        String tipoSimbolo,
        String fila,
        String columna,
        String tipoDato
) {
    return agregar(nombreToken, tipoSimbolo, fila, columna, tipoDato, true);
}
public static boolean agregar(
        String nombreToken,
        String tipoSimbolo,
        String fila,
        String columna,
        String tipoDato,
        boolean varInicializada
) {
    if ("LLAMADA".equals(tipoSimbolo)) {
        return true;
    }

    String scope = scopeActual();

    if (!tablas.containsKey(scope)) {
        tablas.put(scope, new ArrayList<>());
    }

    if (existeEnScopeActual(nombreToken)) {
        ErroresSemanticos.agregar(
            "Error semántico en fila " + fila +
            ", columna " + columna +
            ": el identificador '" + nombreToken +
            "' ya fue declarado en el scope '" + scope + "'"
        );

        return false; // hubo duplicidad
    }

    tablas.get(scope).add(
        new Simbolo(nombreToken, tipoSimbolo, fila, columna, tipoDato, varInicializada)
    );

    return true; // se agregó correctamente
}
    // ============================================================
    // LIMPIEZA
    // ============================================================

    /**
     * Limpia toda la tabla de símbolos y reinicia el scope global.
     */
    public static void limpiar() {
        tablas.clear();
        scopes.clear();
        contadores.clear();

        tablas.put("global", new ArrayList<>());
    }

    // ============================================================
    // IMPRESIÓN
    // ============================================================

    /**
     * Imprime la tabla de símbolos agrupada por scopes.
     */
    public static void imprimir() {
        System.out.println("\n[ TABLA DE SÍMBOLOS ]");

        if (tablas.isEmpty()) {
            System.out.println("(tabla vacía)");
            return;
        }

        for (Map.Entry<String, List<Simbolo>> entry : tablas.entrySet()) {
            imprimirScope(entry);
        }
    }

    /**
     * Imprime un scope específico de la tabla de símbolos.
     */
    private static void imprimirScope(Map.Entry<String, List<Simbolo>> entry) {
        System.out.println("\nScope: [ " + entry.getKey() + " ]");
        System.out.println("─".repeat(70));

        System.out.printf(
                "%-20s %-18s %-12s %-10s %-10s%n",
                "TOKEN",
                "TIPO",
                "TIPO_DATO",
                "FILA",
                "COLUMNA"
        );

        System.out.println("─".repeat(70));

        if (entry.getValue().isEmpty()) {
            System.out.println("(sin identificadores)");
            return;
        }

        for (Simbolo s : entry.getValue()) {
            System.out.printf(
                    "%-20s %-18s %-12s %-10s %-10s%n",
                    s.nombreToken,
                    s.tipoSimbolo,
                    s.tipoDato,
                    s.fila,
                    s.columna
            );
        }
    }
}