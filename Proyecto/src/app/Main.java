import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

public class Main {

    static String SEP = System.getProperty("path.separator");


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

   
    //  GENERACIÓN
    
    /* Elimina los archivos Java generados en compilaciones anteriores. */
    private static void limpiarArchivosAnteriores() {
        borrar("src/lexer/Lexer.java");
        borrar("src/parser/MyParser.java");
        borrar("src/parser/sym.java");
    }

    /** Genera el Lexer a partir del archivo .flex con JFlex. */
    private static void generarLexer(String rutaFlex) throws Exception {
        System.out.println("Generando Lexer...");
        ejecutarIgnorandoError("java", "-jar", "lib/jflex.jar", rutaFlex);
    }

    /* Genera el Parser y la clase sym a partir del archivo .cup con CUP. */
    private static void generarParser(String rutaCup) throws Exception {
        System.out.println("Generando Parser...");
        ejecutar("java", "-jar", "lib/java-cup.jar",
               "-parser", "MyParser",
                "-symbols", "sym",
                "-destdir", "src/parser",
                rutaCup);
    }

    /* Compila todas las clases  */
    private static void compilarProyecto() throws Exception {
        compilarSym();
        compilarTablaSimbolos();
        compilarLexer();
        compilarParser();
    }

    private static void compilarSym() throws Exception {
        System.out.println("Compilando sym...");
        ejecutar("javac",
                "-cp", "." + SEP + "lib/java-cup-runtime.jar",
                "-d", "src/parser",
                "src/parser/sym.java");
    }

    private static void compilarTablaSimbolos() throws Exception {
        System.out.println("Compilando TablaSimbolos...");
        ejecutar("javac",
                "-cp", "." + SEP + "lib/java-cup-runtime.jar" + SEP + "src/parser",
                "-d", "src/parser",
                "src/parser/TablaSimbolos.java");
    }

    private static void compilarLexer() throws Exception {
        System.out.println("Compilando Lexer...");
        ejecutar("javac",
                "-cp", "." + SEP + "lib/java-cup-runtime.jar" + SEP + "src/parser",
                "-d", "src/lexer",
                "src/lexer/Lexer.java");
    }

    private static void compilarParser() throws Exception {
        System.out.println("Compilando Parser...");
        ejecutar("javac",
                "-cp", "." + SEP + "lib/java-cup-runtime.jar" + SEP + "src/lexer" + SEP + "src/parser",
                "-d", "src/parser",
                "src/parser/MyParser.java");
    }

    private static void ejecutarEjemplos(String carpeta) throws Exception {
        URLClassLoader loader = crearClassLoader();

        Class<?> tablaClass  = Class.forName("TablaSimbolos", true, loader);
        Class<?> lexerClass  = Class.forName("Lexer",         true, loader);
        Class<?> parserClass = Class.forName("MyParser",        true, loader);
        Class<?> symClass    = Class.forName("sym",           true, loader);

        int      EOF           = symClass.getField("EOF").getInt(null);
        String[] terminalNames = (String[]) symClass.getField("terminalNames").get(null);
        Field    campoErroresLex = obtenerCampo(lexerClass, "erroresLexicos");

        File[] archivos = obtenerArchivosDePrueba(carpeta);
        if (archivos == null) return;

        for (File archivo : archivos) {
            procesarArchivo(archivo, carpeta, tablaClass, lexerClass, parserClass,
                            EOF, terminalNames, campoErroresLex);
        }
    }

    /** Procesa un único archivo: análisis léxico, sintáctico y tabla de símbolos. */
    private static void procesarArchivo(File archivo, String carpeta,
                                        Class<?> tablaClass, Class<?> lexerClass,
                                        Class<?> parserClass,
                                        int EOF, String[] terminalNames,
                                        Field campoErroresLex) throws Exception {
        tablaClass.getMethod("limpiar").invoke(null);

        imprimirEncabezado(archivo.getName());

        List<?> erroresLexicos = limpiarLista(campoErroresLex, null);
        realizarAnalisisLexico(archivo, carpeta, lexerClass, EOF, terminalNames);

        mostrarResumenLexico(erroresLexicos);

        List<?> erroresSintacticos = realizarAnalisisSintactico(archivo, lexerClass, parserClass, erroresLexicos);

        mostrarTablaSimbolos(tablaClass);
        mostrarResumenSintactico(erroresSintacticos);
        mostrarVeredictoFinal(erroresLexicos, erroresSintacticos);

        System.out.println("══════════════════════════════════════\n");
    }

    //  ANÁLISIS LÉXICO
  

    /** Recorre todos los tokens del archivo y los escribe en un archivo de salida. */
    private static void realizarAnalisisLexico(File archivo, String carpeta,
                                               Class<?> lexerClass,
                                               int EOF, String[] terminalNames) throws Exception {
        File archivoSalida = new File(carpeta, "tokens_" + archivo.getName());
        PrintWriter escritor = new PrintWriter(new FileWriter(archivoSalida));

        Object lexer    = crearLexer(lexerClass, archivo);
        Method nextToken = lexerClass.getMethod("next_token");

        while (true) {
            Object token  = nextToken.invoke(lexer);
            int    symNum = (int) token.getClass().getField("sym").get(token);
            if (symNum == EOF) break;

            Object value  = token.getClass().getField("value").get(token);
            String nombre = obtenerNombreToken(symNum, terminalNames);
            String lexema = value != null ? value.toString() : "";

            escritor.printf("%-20s %-25s%n", "Token: " + nombre, "Lexema: " + lexema);
        }
        escritor.close();
    }

    //  ANÁLISIS SINTÁCTICO
  

    /** Crea el lexer y el parser, ejecuta el análisis y retorna los errores encontrados. */
    private static List<?> realizarAnalisisSintactico(File archivo,Class<?> lexerClass,Class<?> parserClass,List<?> erroresLexicos) throws Exception {
        Field campoErroresSint = obtenerCampo(parserClass, "erroresSintacticos");
        List<?> erroresSintacticos = limpiarLista(campoErroresSint, null);
        erroresLexicos.clear(); // evitar duplicados antes del parser

        try {
            Object lexer  = crearLexer(lexerClass, archivo);
            Object parser = crearParser(parserClass, lexer);
            parserClass.getMethod("parse").invoke(parser);
        } catch (InvocationTargetException e) {
            
        }

        return erroresSintacticos;
    }

    
    //  PRESENTACIÓN DE RESULTADOS
   
    private static void imprimirEncabezado(String nombreArchivo) {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("  Archivo: " + nombreArchivo);
        System.out.println("══════════════════════════════════════");
    }

    private static void mostrarResumenLexico(List<?> errores) {
        System.out.println("\n[ LÉXICO ]");
        System.out.println("──────────────────────────────────────────────────────");
        if (errores.isEmpty()) {
            System.out.println("✔ Sin errores léxicos.");
        } else {
            System.out.println("✘ Errores léxicos encontrados: " + errores.size());
            errores.forEach(e -> System.out.println("  " + e));
        }
    }

    private static void mostrarTablaSimbolos(Class<?> tablaClass) throws Exception {
        System.out.println("\n[ TABLA DE SÍMBOLOS ]");
        tablaClass.getMethod("imprimir").invoke(null);
    }

    private static void mostrarResumenSintactico(List<?> errores) {
        System.out.println("\n[ PARSER ]");
        System.out.println("──────────────────────────────────────────────────────");
        if (errores.isEmpty()) {
            System.out.println("✔ Sin errores sintácticos.");
        } else {
            System.out.println("✘ Errores sintácticos encontrados: " + errores.size());
            errores.forEach(e -> System.out.println("  " + e));
        }
    }

    private static void mostrarVeredictoFinal(List<?> erroresLex, List<?> erroresSint) {
        System.out.println("──────────────────────────────────────────────────────");
        if (erroresLex.isEmpty() && erroresSint.isEmpty()) {
            System.out.println(" El archivo cumple con la gramática y puede ser procesado.");
        } else {
            int total = erroresLex.size() + erroresSint.size();
            System.out.println("El archivo NO cumple con la gramática. Total de errores: " + total);
        }
    }

    /** Crea el ClassLoader con las rutas del lexer, parser y runtime de CUP. */
    private static URLClassLoader crearClassLoader() throws Exception {
        return new URLClassLoader(new URL[]{
                new File("src/lexer").toURI().toURL(),
                new File("src/parser").toURI().toURL(),
                new File("lib/java-cup-runtime.jar").toURI().toURL()
        }, Main.class.getClassLoader());
    }

    /** Crea una instancia del Lexer a partir de un archivo. */
    private static Object crearLexer(Class<?> lexerClass, File archivo) throws Exception {
        return lexerClass.getConstructor(Reader.class).newInstance(new FileReader(archivo));
    }

    
    private static Object crearParser(Class<?> parserClass, Object lexer) throws Exception {
        for (Constructor<?> c : parserClass.getConstructors()) {
            if (c.getParameterCount() == 1) return c.newInstance(lexer);
        }
        throw new RuntimeException("No se encontró un constructor de un parámetro en Parser.");
    }

    /** Obtiene un campo accesible de una clase por nombre. */
    private static Field obtenerCampo(Class<?> clase, String nombre) throws Exception {
        Field f = clase.getField(nombre);
        f.setAccessible(true);
        return f;
    }

    /** Limpia la lista estática almacenada en un campo y la retorna. */
    @SuppressWarnings("unchecked")
    private static List<?> limpiarLista(Field campo, Object instancia) throws Exception {
        List<?> lista = (List<?>) campo.get(instancia);
        lista.clear();
        return lista;
    }

    /* Devuelve el nombre legible de un token según su número de símbolo. */
    private static String obtenerNombreToken(int symNum, String[] terminalNames) {
        return (symNum >= 0 && symNum < terminalNames.length)
                ? terminalNames[symNum]
                : "UNKNOWN(" + symNum + ")";
    }

    /* Lista todos los archivos .txt de prueba que no sean de salida (tokens_*). */
    private static File[] obtenerArchivosDePrueba(String carpeta) {
        File dir      = new File(carpeta);
        File[] archivos = dir.listFiles((d, name) -> name.endsWith(".txt") && !name.startsWith("tokens_"));
        if (archivos == null || archivos.length == 0) {
            System.out.println("No hay ejemplos .txt en " + carpeta);
            return null;
        }
        return archivos;
    }

    /* Elimina un archivo si existe. */
    private static void borrar(String ruta) {
        File f = new File(ruta);
        if (f.exists()) {
            f.delete();
            System.out.println("Eliminado: " + ruta);
        }
    }

    /* Ejecuta un comando y lanza excepción si el proceso falla. */
    private static void ejecutar(String... comando) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(comando);
        pb.inheritIO();
        int codigo = pb.start().waitFor();
        if (codigo != 0) throw new RuntimeException("Error ejecutando: " + Arrays.toString(comando));
    }

    /* Ejecuta un comando descartando su salida. */
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
  java  -cp ".;lib/jflex.jar;lib/java-cup-runtime.jar;src/app" Main
*/