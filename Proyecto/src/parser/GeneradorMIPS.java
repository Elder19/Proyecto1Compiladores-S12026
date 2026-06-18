import java.io.*;
import java.util.*;

public class GeneradorMIPS {

    private List<String> instrucciones;
    private StringBuilder data;
    private StringBuilder text;

    // offset de cada variable respecto al $fp
    private HashMap<String, Integer> offsetVars;
    private int offsetActual;

    public GeneradorMIPS(List<String> instrucciones) {
        this.instrucciones  = instrucciones;
        this.data           = new StringBuilder();
        this.text           = new StringBuilder();
        this.offsetVars     = new HashMap<>();
        this.offsetActual   = 0;
    }

    public void generar(String rutaSalida) throws IOException {
        data.append(".data\n");
        data.append("    newline: .asciiz \"\\n\"\n");
        text.append("\n.text\n");

        for (String instruccion : instrucciones) {
            traducir(instruccion.trim());
        }

        // escribir el archivo .asm
        PrintWriter writer = new PrintWriter(new FileWriter(rutaSalida));
        writer.print(data.toString());
        writer.print(text.toString());
        writer.close();
    }

    private void traducir(String inst) {

        // ── etiqueta (termina en :) ──────────────────────────
        if (inst.endsWith(":")) {
            String etiqueta = inst.replace(":", "").trim();

            if (etiqueta.equals("main")) {
                text.append("\nmain:\n");
                text.append("    move $fp, $sp\n");  // inicializar frame pointer
            } else {
                text.append("\n" + etiqueta + ":\n");
            }

            // si es main_end, agregar exit
            if (etiqueta.equals("main_end")) {
                text.append("    li $v0, 10\n");
                text.append("    syscall\n");
            }
            return;
        }

        // ── var int x o var float x ──────────────────────────
        if (inst.startsWith("var int ") || inst.startsWith("var float ") ||
            inst.startsWith("var bool ") || inst.startsWith("var string ") ||
            inst.startsWith("var char ")) {

            String nombre = inst.substring(inst.lastIndexOf(" ") + 1);
            offsetActual -= 4;
            offsetVars.put(nombre, offsetActual);
            text.append("    # var " + nombre + " en offset " + offsetActual + "($fp)\n");
            text.append("    addiu $sp, $sp, -4\n");
            return;
        }

        // ── t1 = 5  (asignación de literal a temporal) ───────
        // ── x = t1  (asignación de temporal a variable) ──────
        // ── t3 = t1 + t2  (operación aritmética) ─────────────
        if (inst.contains(" = ") && !inst.contains("==")) {
            String[] partes = inst.split(" = ", 2);
            String dest  = partes[0].trim();
            String fuente = partes[1].trim();

            // operación aritmética: t3 = t1 + t2
            if (fuente.contains(" + ") || fuente.contains(" - ") ||
                fuente.contains(" * ") || fuente.contains(" / ")) {

                String op;
                String[] operandos;

                if (fuente.contains(" + ")) {
                    op = "add"; operandos = fuente.split(" \\+ ");
                } else if (fuente.contains(" - ")) {
                    op = "sub"; operandos = fuente.split(" - ");
                } else if (fuente.contains(" * ")) {
                    op = "mul"; operandos = fuente.split(" \\* ");
                } else {
                    op = "div"; operandos = fuente.split(" / ");
                }

                String izq = operandos[0].trim();
                String der = operandos[1].trim();

                cargarEnRegistro(izq, "$t0");
                cargarEnRegistro(der, "$t1");

                if (op.equals("div")) {
                    text.append("    div $t0, $t1\n");
                    text.append("    mflo $t2\n");
                    guardarDesdeRegistro("$t2", dest);
                } else {
                    text.append("    " + op + " $t2, $t0, $t1\n");
                    guardarDesdeRegistro("$t2", dest);
                }

            } else {
                // asignación simple: dest = fuente
                cargarEnRegistro(fuente, "$t0");
                guardarDesdeRegistro("$t0", dest);
            }
            return;
        }

        // ── print t1 ─────────────────────────────────────────
        if (inst.startsWith("print ")) {
            String valor = inst.substring(6).trim();
            cargarEnRegistro(valor, "$a0");
            text.append("    li $v0, 1\n");   // print int
            text.append("    syscall\n");
            // salto de línea
            text.append("    la $a0, newline\n");
            text.append("    li $v0, 4\n");
            text.append("    syscall\n");
            return;
        }

        // ── goto L1 ──────────────────────────────────────────
        if (inst.startsWith("goto ")) {
            String etiqueta = inst.substring(5).trim();
            text.append("    j " + etiqueta + "\n");
            return;
        }

        // instrucción no reconocida → comentario
        text.append("    # TODO: " + inst + "\n");
    }

    // ── Helpers ───────────────────────────────────────────────

    // carga un valor (literal, temporal o variable) en un registro MIPS
    private void cargarEnRegistro(String valor, String registro) {
        if (esNumero(valor)) {
            // literal numérico
            text.append("    li " + registro + ", " + valor + "\n");
        } else if (offsetVars.containsKey(valor)) {
            // variable local → leer de la pila
            int offset = offsetVars.get(valor);
            text.append("    lw " + registro + ", " + offset + "($fp)\n");
        } else {
            // temporal → también está en la pila
            // por ahora tratamos temporales como variables
            if (offsetVars.containsKey(valor)) {
                int offset = offsetVars.get(valor);
                text.append("    lw " + registro + ", " + offset + "($fp)\n");
            } else {
                // temporal no declarado aún → reservar espacio
                offsetActual -= 4;
                offsetVars.put(valor, offsetActual);
                text.append("    addiu $sp, $sp, -4\n");
                text.append("    lw " + registro + ", " + offsetActual + "($fp)\n");
            }
        }
    }

    // guarda el valor de un registro en la variable destino
    private void guardarDesdeRegistro(String registro, String destino) {
        if (!offsetVars.containsKey(destino)) {
            // reservar espacio si no existe
            offsetActual -= 4;
            offsetVars.put(destino, offsetActual);
            text.append("    addiu $sp, $sp, -4\n");
        }
        int offset = offsetVars.get(destino);
        text.append("    sw " + registro + ", " + offset + "($fp)\n");
    }

    // verifica si un string es un número
    private boolean esNumero(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}