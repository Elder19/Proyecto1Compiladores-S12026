import java.io.*;
import java_cup.runtime.Symbol;
/* cd Proyecto
>> java -jar lib/jflex.jar src/lexer/Lexer.flex
>> javac -cp ".;lib/jflex.jar;lib/java-cup-runtime.jar;src/lexer" src/app/Main.java src/lexer/Lexer.java
>> java -cp ".;lib/jflex.jar;lib/java-cup-runtime.jar;src/app;src/lexer" Main */
public class Main {

    public static void main(String[] args) {
        try {

            String rutaFlex = "src/lexer/Lexer.flex";
            String rutaSalida = "src/lexer/";
            String rutaEjemplo = "EjemplosPruebas/Ejemplo.txt";

        
            File lexerFile = new File(rutaSalida + "Lexer.java");
            if (lexerFile.exists()) {
                lexerFile.delete();
                System.out.println("✔ Lexer.java eliminado");
            }

        
            System.out.println("⚙ Generando lexer...");
           jflex.Main.generate(new String[]{rutaFlex});
            System.out.println("✔ Lexer generado");

           
            System.out.println("⚙ Compilando lexer...");

            ProcessBuilder pb = new ProcessBuilder(
                    "javac",
                    "-cp", ".;lib/java-cup-runtime.jar",
                    "src/lexer/Lexer.java"
            );

            pb.inheritIO(); // muestra salida en consola
            Process p = pb.start();
            p.waitFor();

            System.out.println("✔ Lexer compilado");

        
            System.out.println("\n🧪 Probando lexer...\n");

            Reader reader = new FileReader(rutaEjemplo);
            Lexer lexer = new Lexer(reader);

            Symbol token;

            while ((token = lexer.next_token()) != null) {
                System.out.println(
                        "Token: " + token.sym +
                        " | Lexema: " + token.value +
                        " | Linea: " + token.left +
                        " | Columna: " + token.right
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}