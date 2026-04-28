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

        // ── Obtener campo erroresLexicos una sola vez ─────────────
        Field campoErrores = lexerClass.getField("erroresLexicos");
        campoErrores.setAccessible(true);

        File   dir      = new File(carpeta);
        File[] archivos = dir.listFiles((d, name) -> name.endsWith(".txt") && !name.startsWith("tokens_"));

        if (archivos == null || archivos.length == 0) {
            System.out.println("No hay ejemplos .txt en " + carpeta);
            return;
        }

        for (File archivo : archivos) {
            System.out.println("\n══════════════════════════════════════");
            System.out.println("  Archivo: " + archivo.getName());
            System.out.println("══════════════════════════════════════");

            // ── Limpiar errores léxicos del archivo anterior ───────
            List<?> erroresLexicos = (List<?>) campoErrores.get(null);
            erroresLexicos.clear();

            // ── Análisis léxico ───────────────────────────────────
            File archivoSalida = new File(carpeta, "tokens_" + archivo.getName());
            PrintWriter escritor = new PrintWriter(new FileWriter(archivoSalida));

            Object lexer = lexerClass
                    .getConstructor(Reader.class)
                    .newInstance(new FileReader(archivo));

            Method nextToken = lexerClass.getMethod("next_token");

            while (true) {
                Object token  = nextToken.invoke(lexer);
                int    symNum = (int) token.getClass().getField("sym").get(token);
                if (symNum == EOF) break;

                Object value  = token.getClass().getField("value").get(token);
                int    line   = (int) token.getClass().getField("left").get(token);

                String nombreToken = (symNum >= 0 && symNum < terminalNames.length)
                        ? terminalNames[symNum]
                        : "UNKNOWN(" + symNum + ")";
                String lexema = value != null ? value.toString() : "";

                escritor.printf("%-20s %-25s%n",
                        "Token: " + nombreToken,
                        "Lexema: " + lexema);
            }
            escritor.close();

            // ── Resumen léxico ────────────────────────────────────
            System.out.println("\n[ LÉXICO ]");
            System.out.println("──────────────────────────────────────────────────────");
            if (erroresLexicos.isEmpty()) {
                System.out.println("✔ Sin errores léxicos.");
            } else {
                System.out.println("✘ Errores léxicos encontrados: " + erroresLexicos.size());
                erroresLexicos.forEach(e -> System.out.println("  " + e));
            }

            // ── Análisis sintáctico ───────────────────────────────
            System.out.println("\n[ PARSER ]");

            Field campoErroresSint = parserClass.getField("erroresSintacticos");
            campoErroresSint.setAccessible(true);
            List<?> erroresSintacticos = (List<?>) campoErroresSint.get(null);
            erroresSintacticos.clear();

            // Limpiar léxicos antes del parser para evitar duplicados
            erroresLexicos.clear();

            try {
                Constructor<?> lexerConstructor = null;
                for (Constructor<?> c : lexerClass.getConstructors()) {
                    if (c.getParameterCount() == 1 &&
                        c.getParameterTypes()[0].getName().equals("java.io.Reader")) {
                        lexerConstructor = c;
                        break;
                    }
                }

                Object lexerParser = lexerConstructor.newInstance(new FileReader(archivo));

                Constructor<?> parserConstructor = null;
                for (Constructor<?> c : parserClass.getConstructors()) {
                    if (c.getParameterCount() == 1) {
                        parserConstructor = c;
                        break;
                    }
                }

                Object parserObj = parserConstructor.newInstance(lexerParser);
                parserClass.getMethod("parse").invoke(parserObj);

            } catch (InvocationTargetException e) {
                // El unrecovered_syntax_error ya agregó el mensaje a la lista
            }

            // ── Resumen sintáctico ────────────────────────────────
            System.out.println("──────────────────────────────────────────────────────");
            if (erroresSintacticos.isEmpty()) {
                System.out.println("✔ Sin errores sintácticos.");
            } else {
                System.out.println("✘ Errores sintácticos encontrados: " + erroresSintacticos.size());
                erroresSintacticos.forEach(e -> System.out.println("  " + e));
            }

            // ── Veredicto final ───────────────────────────────────
            System.out.println("──────────────────────────────────────────────────────");
            if (erroresLexicos.isEmpty() && erroresSintacticos.isEmpty()) {
                System.out.println("✔ El archivo cumple con la gramatica y puede ser procesado.");
            } else {
                int total = erroresLexicos.size() + erroresSintacticos.size();
                System.out.println("✘ El archivo NO cumple con la gramatica. Total de errores: " + total);
            }
            System.out.println("══════════════════════════════════════\n");
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