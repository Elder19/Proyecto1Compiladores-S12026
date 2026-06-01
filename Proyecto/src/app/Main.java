import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

/**
 * Clase principal del proyecto.
 *
 * Este programa automatiza el flujo completo del compilador:
 *
 * 1. Elimina archivos generados anteriormente por JFlex y CUP.
 * 2. Genera el Lexer a partir del archivo Lexer.flex.
 * 3. Genera el Parser y la clase sym a partir del archivo Parser.cup.
 * 4. Compila las clases necesarias del proyecto.
 * 5. Ejecuta el análisis léxico, sintáctico y semántico sobre todos los
 *    archivos .txt de prueba ubicados en la carpeta EjemplosPruebas.
 *
 * Además, imprime en consola:
 * - Errores léxicos.
 * - Tabla de símbolos.
 * - Errores sintácticos.
 * - Errores semánticos.
 * - Veredicto final del archivo analizado.
 */
public class Main {

    private static final String SEP = System.getProperty("path.separator");

    public static void main(String[] args) {
        try {
            limpiarArchivosAnteriores();

            generarLexer("src/lexer/Lexer.flex");
            generarParser("src/parser/Parser.cup");

            compilarProyecto();

            ejecutarEjemplos("EjemplosPruebas");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    // LIMPIEZA DE ARCHIVOS GENERADOS
    // ============================================================

    /**
     * Elimina los archivos Java generados en compilaciones anteriores.
     */
    private static void limpiarArchivosAnteriores() {
        borrar("src/lexer/Lexer.java");
        borrar("src/parser/MyParser.java");
        borrar("src/parser/sym.java");
    }

    /**
     * Elimina un archivo si existe.
     */
    private static void borrar(String ruta) {
        File f = new File(ruta);

        if (f.exists()) {
            f.delete();
            System.out.println("Eliminado: " + ruta);
        }
    }

    // ============================================================
    // GENERACIÓN DE LEXER Y PARSER
    // ============================================================

    /**
     * Genera el Lexer a partir del archivo .flex usando JFlex.
     */
    private static void generarLexer(String rutaFlex) throws Exception {
        System.out.println("Generando Lexer...");

        ejecutarIgnorandoError(
                "java",
                "-jar",
                "lib/jflex.jar",
                rutaFlex
        );
    }

    /**
     * Genera el Parser y la clase sym a partir del archivo .cup usando CUP.
     */
    private static void generarParser(String rutaCup) throws Exception {
        System.out.println("Generando Parser...");

        ejecutar(
                "java",
                "-jar",
                "lib/java-cup.jar",
                "-parser", "MyParser",
                "-symbols", "sym",
                "-destdir", "src/parser",
                rutaCup
        );
    }

    // ============================================================
    // COMPILACIÓN DEL PROYECTO
    // ============================================================

    /**
     * Compila todas las clases necesarias del proyecto.
     */
    private static void compilarProyecto() throws Exception {
        compilarSym();
        compilarTablaSimbolos();
        compilarLexer();
        compilarParser();
    }

    /**
     * Compila la clase sym generada por CUP.
     */
    private static void compilarSym() throws Exception {
        System.out.println("Compilando sym...");

        ejecutar(
                "javac",
                "-cp", "." + SEP + "lib/java-cup-runtime.jar",
                "-d", "src/parser",
                "src/parser/sym.java"
        );
    }

    /**
     * Compila TablaSimbolos y ErroresSemanticos.
     */
    private static void compilarTablaSimbolos() throws Exception {
        System.out.println("Compilando TablaSimbolos...");

        ejecutar(
                "javac",
                "-cp", "." + SEP + "lib/java-cup-runtime.jar" + SEP + "src/parser",
                "-d", "src/parser",
                "src/parser/TablaSimbolos.java",
                "src/parser/ErroresSemanticos.java",
                "src/parser/CodigoIntermedio.java"   // ← línea nueva
        );
    }

    /**
     * Compila el Lexer generado por JFlex.
     */
    private static void compilarLexer() throws Exception {
        System.out.println("Compilando Lexer...");

        ejecutar(
                "javac",
                "-cp", "." + SEP + "lib/java-cup-runtime.jar" + SEP + "src/parser",
                "-d", "src/lexer",
                "src/lexer/Lexer.java"
        );
    }

    /**
     * Compila el Parser generado por CUP.
     */
    private static void compilarParser() throws Exception {
        System.out.println("Compilando Parser...");

        ejecutar(
                "javac",
                "-cp", "." + SEP + "lib/java-cup-runtime.jar" + SEP + "src/lexer" + SEP + "src/parser",
                "-d", "src/parser",
                "src/parser/MyParser.java"
        );
    }

    // ============================================================
    // EJECUCIÓN DE ARCHIVOS DE PRUEBA
    // ============================================================

    /**
     * Ejecuta el análisis para todos los archivos .txt de la carpeta indicada.
     */
    private static void ejecutarEjemplos(String carpeta) throws Exception {
        URLClassLoader loader = crearClassLoader();

        Class<?> tablaClass           = Class.forName("TablaSimbolos",    true, loader);
        Class<?> erroresSemanticosClass = Class.forName("ErroresSemanticos", true, loader);
        Class<?> codigoIntermedioClass  = Class.forName("CodigoIntermedio",  true, loader); // ← nueva
        Class<?> lexerClass           = Class.forName("Lexer",            true, loader);
        Class<?> parserClass          = Class.forName("MyParser",         true, loader);
        Class<?> symClass             = Class.forName("sym",              true, loader);

        int EOF = symClass.getField("EOF").getInt(null);
        String[] terminalNames = (String[]) symClass.getField("terminalNames").get(null);

        Field campoErroresLex = obtenerCampo(lexerClass, "erroresLexicos");

        File[] archivos = obtenerArchivosDePrueba(carpeta);

        if (archivos == null) return;

        for (File archivo : archivos) {
            procesarArchivo(
                    archivo,
                    carpeta,
                    tablaClass,
                    erroresSemanticosClass,
                    codigoIntermedioClass,
                    lexerClass,
                    parserClass,
                    EOF,
                    terminalNames,
                    campoErroresLex
            );
        }
    }

/**
 * Procesa un único archivo:
 * - Limpia estructuras anteriores.
 * - Realiza análisis léxico.
 * - Realiza análisis sintáctico.
 * - Imprime tabla de símbolos.
 * - Imprime errores semánticos.
 * - Muestra el veredicto final.
 */
private static void procesarArchivo(
        File archivo,
        String carpeta,
        Class<?> tablaClass,
        Class<?> erroresSemanticosClass,
        Class<?> codigoIntermedioClass,
        Class<?> lexerClass,
        Class<?> parserClass,
        int EOF,
        String[] terminalNames,
        Field campoErroresLex
) throws Exception {

    tablaClass.getMethod("limpiar").invoke(null);
    erroresSemanticosClass.getMethod("limpiar").invoke(null);
    codigoIntermedioClass.getMethod("limpiar").invoke(null);

    imprimirEncabezado(archivo.getName());

    List<?> erroresLexicos = limpiarLista(campoErroresLex, null);

    realizarAnalisisLexico(
            archivo,
            carpeta,
            lexerClass,
            EOF,
            terminalNames
    );

    mostrarResumenLexico(erroresLexicos);

    List<?> erroresSintacticos = realizarAnalisisSintactico(
            archivo,
            lexerClass,
            parserClass,
            erroresLexicos
    );

    mostrarTablaSimbolos(tablaClass);

    mostrarResumenSintactico(erroresSintacticos);

    // false = imprime solo un error por línea
    // true  = imprime todos los errores
    try {
        erroresSemanticosClass
                .getField("imprimirTodos")
                .setBoolean(null, false);//MOSTAR TODOS LOS ERRROES
    } catch (NoSuchFieldException e) {
        System.out.println("Aviso: no existe la bandera imprimirTodos en ErroresSemanticos.");
    }

    erroresSemanticosClass.getMethod("imprimir").invoke(null);

    List<?> erroresSemanticos;

    try {
        erroresSemanticos = (List<?>) erroresSemanticosClass
                .getMethod("obtenerErroresParaImprimir")
                .invoke(null);
    } catch (NoSuchMethodException e) {
        erroresSemanticos = (List<?>) erroresSemanticosClass
                .getMethod("obtenerErrores")
                .invoke(null);
    }

    mostrarCodigoIntermedio(codigoIntermedioClass);

    mostrarVeredictoFinal(
            erroresLexicos,
            erroresSintacticos,
            erroresSemanticos.size(),
            erroresSemanticosClass
    );
}
    /**
     * Lista todos los archivos .txt de prueba que no sean archivos de salida tokens_*.
     */
    private static File[] obtenerArchivosDePrueba(String carpeta) {
        File dir = new File(carpeta);

        File[] archivos = dir.listFiles(
                (d, name) -> name.endsWith(".txt") && !name.startsWith("tokens_")
        );

        if (archivos == null || archivos.length == 0) {
            System.out.println("No hay ejemplos .txt en " + carpeta);
            return null;
        }

        return archivos;
    }

    // ============================================================
    // ANÁLISIS LÉXICO
    // ============================================================

    /**
     * Recorre todos los tokens del archivo y los escribe en un archivo de salida.
     */
    private static void realizarAnalisisLexico(
            File archivo,
            String carpeta,
            Class<?> lexerClass,
            int EOF,
            String[] terminalNames
    ) throws Exception {

        File archivoSalida = new File(carpeta, "tokens_" + archivo.getName());

        PrintWriter escritor = new PrintWriter(new FileWriter(archivoSalida));

        Object lexer = crearLexer(lexerClass, archivo);
        Method nextToken = lexerClass.getMethod("next_token");

        while (true) {
            Object token = nextToken.invoke(lexer);

            int symNum = (int) token
                    .getClass()
                    .getField("sym")
                    .get(token);

            if (symNum == EOF) {
                break;
            }

            Object value = token
                    .getClass()
                    .getField("value")
                    .get(token);

            String nombre = obtenerNombreToken(symNum, terminalNames);
            String lexema = value != null ? value.toString() : "";

            escritor.printf(
                    "%-20s %-25s%n",
                    "Token: " + nombre,
                    "Lexema: " + lexema
            );
        }

        escritor.close();
    }

    /**
     * Devuelve el nombre legible de un token según su número de símbolo.
     */
    private static String obtenerNombreToken(int symNum, String[] terminalNames) {
        return (symNum >= 0 && symNum < terminalNames.length)
                ? terminalNames[symNum]
                : "UNKNOWN(" + symNum + ")";
    }

    // ============================================================
    // ANÁLISIS SINTÁCTICO
    // ============================================================

    /**
     * Crea el lexer y el parser, ejecuta el análisis sintáctico
     * y retorna la lista de errores sintácticos encontrados.
     */
    private static List<?> realizarAnalisisSintactico(
            File archivo,
            Class<?> lexerClass,
            Class<?> parserClass,
            List<?> erroresLexicos
    ) throws Exception {

        Field campoErroresSint = obtenerCampo(parserClass, "erroresSintacticos");
        List<?> erroresSintacticos = limpiarLista(campoErroresSint, null);

        erroresLexicos.clear();

        try {
            Object lexer = crearLexer(lexerClass, archivo);
            Object parser = crearParser(parserClass, lexer);

            parserClass.getMethod("parse").invoke(parser);

        } catch (InvocationTargetException e) {
            System.err.println("Error durante el parse:");
            e.getCause().printStackTrace();
        }

        return erroresSintacticos;
    }

    // ============================================================
    // PRESENTACIÓN DE RESULTADOS
    // ============================================================

    /**
     * Imprime el encabezado del archivo analizado.
     */
    private static void imprimirEncabezado(String nombreArchivo) {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("  Archivo: " + nombreArchivo);
        System.out.println("══════════════════════════════════════");
    }

    /**
     * Imprime el resumen de errores léxicos.
     */
    private static void mostrarResumenLexico(List<?> errores) {
        System.out.println("\n[ LÉXICO ]");
        System.out.println("──────────────────────────────────────────────────────");

        if (errores.isEmpty()) {
            System.out.println(" Sin errores léxicos.");
        } else {
            System.out.println(" Errores léxicos encontrados: " + errores.size());
            errores.forEach(e -> System.out.println("  " + e));
        }
    }

    /**
     * Imprime la tabla de símbolos.
     */
    private static void mostrarTablaSimbolos(Class<?> tablaClass) throws Exception {
        System.out.println("\n[ TABLA DE SÍMBOLOS ]");

        tablaClass.getMethod("imprimir").invoke(null);
    }

    /**
     * Imprime el resumen de errores sintácticos.
     */
    private static void mostrarResumenSintactico(List<?> errores) {
        System.out.println("\n[ PARSER ]");
        System.out.println("──────────────────────────────────────────────────────");

        if (errores.isEmpty()) {
            System.out.println("Sin errores sintácticos.");
        } else {
            System.out.println("Errores sintácticos encontrados: " + errores.size());
            errores.forEach(e -> System.out.println("  " + e));
        }
    }

    /**
     * Imprime el resultado final del análisis del archivo.
     */
    private static void mostrarVeredictoFinal(
            List<?> erroresLex,
            List<?> erroresSint,
            int erroresSemanticos,
            Class<?> erroresSemanticosClass
    ) throws Exception {

        System.out.println("──────────────────────────────────────────────────────");

        boolean hayErroresSem = (boolean) erroresSemanticosClass
                .getMethod("hayErrores")
                .invoke(null);

        if (erroresLex.isEmpty() && erroresSint.isEmpty() && !hayErroresSem) {
            System.out.println(" El archivo cumple con la gramática y puede ser procesado.");
        } else {
            System.out.println("Errores léxicos: " + erroresLex.size());
            System.out.println("Errores sintácticos: " + erroresSint.size());
            System.out.println("Errores semánticos: " + erroresSemanticos);
        }

        erroresSemanticosClass.getMethod("limpiar").invoke(null);
    }


    /**
     * Imprime el resultado final del codigo intermedio.
     */
    @SuppressWarnings("unchecked")
    private static void mostrarCodigoIntermedio(Class<?> codigoIntermedioClass) throws Exception {
        System.out.println("\n[ CÓDIGO INTERMEDIO ]");
        System.out.println("──────────────────────────────────────────────────────");

        List<String> instrucciones = (List<String>) codigoIntermedioClass
                .getMethod("getInstrucciones")
                .invoke(null);

        if (instrucciones.isEmpty()) {
            System.out.println(" No se generó código intermedio.");
        } else {
            int numero = 1;
            for (String instruccion : instrucciones) {
                System.out.printf(" %3d.  %s%n", numero++, instruccion);
            }
        }
    }

    // ============================================================
    // CREACIÓN DINÁMICA DE CLASES
    // ============================================================

    /**
     * Crea el ClassLoader con las rutas del lexer, parser y runtime de CUP.
     */
    private static URLClassLoader crearClassLoader() throws Exception {
        return new URLClassLoader(
                new URL[]{
                        new File("src/lexer").toURI().toURL(),
                        new File("src/parser").toURI().toURL(),
                        new File("lib/java-cup-runtime.jar").toURI().toURL()
                },
                Main.class.getClassLoader()
        );
    }

    /**
     * Crea una instancia del Lexer a partir de un archivo.
     */
    private static Object crearLexer(Class<?> lexerClass, File archivo) throws Exception {
        return lexerClass
                .getConstructor(Reader.class)
                .newInstance(new FileReader(archivo));
    }

    /**
     * Crea una instancia del Parser usando el lexer.
     */
    private static Object crearParser(Class<?> parserClass, Object lexer) throws Exception {
        for (Constructor<?> c : parserClass.getConstructors()) {
            if (c.getParameterCount() == 1) {
                return c.newInstance(lexer);
            }
        }

        throw new RuntimeException("No se encontró un constructor de un parámetro en Parser.");
    }

    // ============================================================
    // UTILIDADES DE REFLEXIÓN
    // ============================================================

    /**
     * Obtiene un campo accesible de una clase por nombre.
     */
    private static Field obtenerCampo(Class<?> clase, String nombre) throws Exception {
        Field f = clase.getField(nombre);
        f.setAccessible(true);
        return f;
    }

    /**
     * Limpia la lista estática almacenada en un campo y la retorna.
     */
    @SuppressWarnings("unchecked")
    private static List<?> limpiarLista(Field campo, Object instancia) throws Exception {
        List<?> lista = (List<?>) campo.get(instancia);
        lista.clear();
        return lista;
    }

    // ============================================================
    // EJECUCIÓN DE COMANDOS
    // ============================================================

    /**
     * Ejecuta un comando y lanza excepción si el proceso falla.
     */
    private static void ejecutar(String... comando) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(comando);
        pb.inheritIO();

        int codigo = pb.start().waitFor();

        if (codigo != 0) {
            throw new RuntimeException("Error ejecutando: " + Arrays.toString(comando));
        }
    }

    /**
     * Ejecuta un comando descartando su salida.
     */
    private static void ejecutarIgnorandoError(String... comando) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(comando);

        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);

        pb.start().waitFor();
    }
}

/*
 

    cd Proyecto

    javac -cp ".;lib/jflex.jar;lib/java-cup-runtime.jar" src/app/Main.java

    java -cp ".;lib/jflex.jar;lib/java-cup-runtime.jar;src/app" Main
*/
