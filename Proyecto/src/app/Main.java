import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

public class Main {

    static String SEP = System.getProperty("path.separator");

    public static void main(String[] args) {
        try {
            String rutaFlex = "src/lexer/Lexer.flex";
            String rutaCup = "src/parser/Parser.cup";
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
                    "-cp", ".;lib/java-cup-runtime.jar;src/lexer;src/parser",
                    "src/lexer/Lexer.java",
                    "src/parser/Parser.java",
                    "src/parser/sym.java");

            ejecutarEjemplos(carpetaEjemplos);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void ejecutarEjemplos(String carpeta) throws Exception {
        URLClassLoader loader = new URLClassLoader(new URL[]{
                new File("src/lexer").toURI().toURL(),
                new File("src/parser").toURI().toURL(),
                new File("lib/java-cup-runtime.jar").toURI().toURL()
        });

        Class<?> lexerClass = Class.forName("Lexer", true, loader);
        Class<?> parserClass = Class.forName("Parser", true, loader);
        Class<?> symClass = Class.forName("sym", true, loader);

        int EOF = symClass.getField("EOF").getInt(null);

        File dir = new File(carpeta);
        File[] archivos = dir.listFiles((d, name) -> name.endsWith(".txt"));

        if (archivos == null || archivos.length == 0) {
            System.out.println("No hay ejemplos .txt en " + carpeta);
            return;
        }

        for (File archivo : archivos) {
            System.out.println("\n==============================");
            System.out.println("Archivo: " + archivo.getName());
            System.out.println("==============================");

            System.out.println("\nTokens:");
            Object lexer = lexerClass.getConstructor(Reader.class)
                    .newInstance(new FileReader(archivo));

            Method nextToken = lexerClass.getMethod("next_token");

            while (true) {
                Object token = nextToken.invoke(lexer);
                int sym = token.getClass().getField("sym").getInt(token);

                if (sym == EOF) break;

                Object value = token.getClass().getField("value").get(token);
                int line = token.getClass().getField("left").getInt(token);
                int column = token.getClass().getField("right").getInt(token);

                System.out.println("Token: " + sym +
                        " | Lexema: " + value +
                        " | Linea: " + line +
                        " | Columna: " + column);
            }

            System.out.println("\nParser:");
            Object lexerParser = lexerClass.getConstructor(Reader.class)
                    .newInstance(new FileReader(archivo));

            Object parser = parserClass
                    .getConstructor(java_cup.runtime.Scanner.class)
                    .newInstance(lexerParser);

            parserClass.getMethod("parse").invoke(parser);

            System.out.println("Archivo procesado.");
        }
    }

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
        Process p = pb.start();
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