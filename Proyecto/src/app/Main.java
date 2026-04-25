import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

public class Main {

    static String SEP = System.getProperty("path.separator");

    public static void main(String[] args) {
        try {
            String rutaFlex        = "src/lexer/Lexer.flex";
            String rutaCup         = "src/parser/Parser.cup";
            String carpetaEjemplos = "EjemplosPruebas";

            borrar("src/lexer/Lexer.java");
            borrar("src/parser/Parser.java");
            borrar("src/parser/sym.java");

            System.out.println("Generando Lexer...");
            jflex.Main.generate(new String[]{rutaFlex});

            System.out.println("Generando Parser...");
            ejecutar("java", "-jar", "lib/java-cup.jar",
                    "-parser", "Parser",
                    "-symbols", "sym",
                    "-destdir", "src/parser",
                    rutaCup);

            System.out.println("Compilando generados...");
            ejecutar("javac",
                    "-cp", "." + SEP + "lib/java-cup-runtime.jar" + SEP + "src/lexer" + SEP + "src/parser",
                    "src/lexer/Lexer.java",
                    "src/parser/Parser.java",
                    "src/parser/sym.java");

            ejecutarEjemplos(carpetaEjemplos);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ─────────────────────────────────────────────────────────────
    private static void ejecutarEjemplos(String carpeta) throws Exception {

        URLClassLoader loader = new URLClassLoader(new URL[]{
                new File("src/lexer").toURI().toURL(),
                new File("src/parser").toURI().toURL(),
                new File("lib/java-cup-runtime.jar").toURI().toURL()
        }, Main.class.getClassLoader());

        Class<?> lexerClass  = Class.forName("Lexer",  true, loader);
        Class<?> parserClass = Class.forName("Parser", true, loader);
        Class<?> symClass    = Class.forName("sym",    true, loader);

        int      EOF           = symClass.getField("EOF").getInt(null);
        String[] terminalNames = (String[]) symClass.getField("terminalNames").get(null);
        Field    campoErrores  = lexerClass.getField("erroresLexicos");
        campoErrores.setAccessible(true);

        File   dir      = new File(carpeta);
        File[] archivos = dir.listFiles((d, name) -> name.endsWith(".txt"));

        if (archivos == null || archivos.length == 0) {
            System.out.println("No hay ejemplos .txt en " + carpeta);
            return;
        }

        for (File archivo : archivos) {
            System.out.println("\n══════════════════════════════════════");
            System.out.println("  Archivo: " + archivo.getName());
            System.out.println("══════════════════════════════════════");

            // ── Limpiar errores del archivo anterior ──────────────
            List<?> erroresLexicos = (List<?>) campoErrores.get(null);
            erroresLexicos.clear();

            // ── Análisis léxico ───────────────────────────────────
            System.out.println("\n[ TOKENS ]");
            System.out.printf("%-20s %-25s %-8s %-8s%n", "TOKEN", "LEXEMA", "LÍNEA", "COLUMNA");
            System.out.println("──────────────────────────────────────────────────────");

            Object lexer = lexerClass
                    .getConstructor(Reader.class)
                    .newInstance(new FileReader(archivo));

            Method nextToken = lexerClass.getMethod("next_token");

            while (true) {
                Object token  = nextToken.invoke(lexer);
                int    symNum = (int) token.getClass().getField("sym").get(token);
                if (symNum == EOF) break;

                Object value = token.getClass().getField("value").get(token);
                int    line  = (int) token.getClass().getField("left").get(token);
                int    col   = (int) token.getClass().getField("right").get(token);

                String etiqueta = (symNum < terminalNames.length)
                        ? terminalNames[symNum]
                        : String.valueOf(symNum);

                String prefijo = etiqueta.equals("ERROR") ? "  ✘ " : "    ";
                System.out.printf("%s%-20s %-25s %-8d %-8d%n",
                        prefijo, etiqueta, value, line, col);
            }

            // ── Resumen léxico ────────────────────────────────────
            System.out.println("──────────────────────────────────────────────────────");
            if (erroresLexicos.isEmpty()) {
                System.out.println("✔ Sin errores léxicos.\n");
            } else {
                System.out.println("✘ Errores léxicos encontrados: " + erroresLexicos.size());
                erroresLexicos.forEach(e -> System.out.println("  " + e));
                System.out.println();
            }

/*
            // ── Análisis sintáctico ───────────────────────────────
            System.out.println("\n[ PARSER ]");
            try {
                Object lexerParser = lexerClass
                        .getConstructor(Reader.class)
                        .newInstance(new FileReader(archivo));

                Object parser = parserClass
                        .getConstructor(java_cup.runtime.Scanner.class)
                        .newInstance(lexerParser);

                parserClass.getMethod("parse").invoke(parser);
                System.out.println("✔ Análisis sintáctico exitoso.");

            } catch (InvocationTargetException e) {
                System.out.println("✘ Error sintáctico: " + e.getCause().getMessage());
            }

            System.out.println("\n  Archivo procesado: " + archivo.getName());
*/
        }
    }

    // ─────────────────────────────────────────────────────────────
    private static void borrar(String ruta) {
        File f = new File(ruta);
        if (f.exists()) {
            f.delete();
            System.out.println("Eliminado: " + ruta);
        }
    }

    private static void ejecutar(String... comando) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(comando);
        pb.inheritIO();
        Process p  = pb.start();
        int codigo = p.waitFor();
        if (codigo != 0) {
            throw new RuntimeException("Error ejecutando: " + Arrays.toString(comando));
        }
    }
}
/*
cd Proyecto
javac -cp ".;lib/jflex.jar;lib/java-cup-runtime.jar" src/app/Main.java
java -cp ".;lib/jflex.jar;lib/java-cup-runtime.jar;src/app" Main */