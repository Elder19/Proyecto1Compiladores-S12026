import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class GeneradorMIPS {

    private List<String> instrucciones;
    private StringBuilder data;
    private StringBuilder text;

    // offset en stack de variables simples
    private HashMap<String, Integer> offsetVars;
    private int offsetActual;

    // tipo de cada variable/temporal ("int" o "float")
    private HashMap<String, String> tipoVars;

    // arreglos en .data
    private HashMap<String, String> etiquetaArreglo;   // nombre -> "arr_nombre"
    private HashMap<String, int[]>  dimensionesArreglo; // nombre -> {filas, columnas}

    public GeneradorMIPS(List<String> instrucciones) {
        this.instrucciones      = instrucciones;
        this.data               = new StringBuilder();
        this.text               = new StringBuilder();
        this.offsetVars         = new HashMap<>();
        this.tipoVars           = new HashMap<>();
        this.etiquetaArreglo    = new HashMap<>();
        this.dimensionesArreglo = new HashMap<>();
        this.offsetActual       = 0;
    }

    public void generar(String rutaSalida) throws IOException {
        data.append(".data\n");
        data.append("    newline: .asciiz \"\\n\"\n");
        text.append("\n.text\n");
        text.append(".globl main\n");

        // Primer paso: registrar arreglos en .data
        for (String instruccion : instrucciones) {
            String inst = instruccion.trim();
            if (inst.startsWith("var int[] ") || inst.startsWith("var float[] ")) {
                registrarArregloEnData(inst);
            }
        }

        // Segundo paso: generar código
        for (String instruccion : instrucciones) {
            traducir(instruccion.trim());
        }

        PrintWriter writer = new PrintWriter(new FileWriter(rutaSalida));
        writer.print(data.toString());
        writer.print(text.toString());
        writer.close();
    }

    private void registrarArregloEnData(String inst) {
        String[] partes = inst.split(" ");
        String tipoBase = partes[1].replace("[]", "");
        String nombre   = partes[2];
        String[] dims   = partes[3].split(",");
        int filas       = Integer.parseInt(dims[0].trim());
        int columnas    = Integer.parseInt(dims[1].trim());

        String etiqueta = "arr_" + nombre;
        int totalBytes  = filas * columnas * 4;

        etiquetaArreglo.put(nombre, etiqueta);
        dimensionesArreglo.put(nombre, new int[]{filas, columnas});
        tipoVars.put(nombre, tipoBase);

        data.append("    .align 2\n");
        data.append("    " + etiqueta + ": .space " + totalBytes + "\n");
    }
    private void traducir(String inst) {
                // ── inicio de función: func f1:
        if (inst.startsWith("func ") && inst.endsWith(":")) {
            String nombreFuncion = inst.substring("func ".length(), inst.length() - 1).trim();
            text.append("\n").append(nombreFuncion).append(":\n");
            return;
        }
        // ── etiqueta ─────────────────────────────────────────
        if (inst.endsWith(":")) {
            String etiqueta = inst.substring(0, inst.length() - 1).trim();
            if (etiqueta.equals("main")) {
                text.append("\nmain:\n");
                text.append("    move $fp, $sp\n");
            } else {
                text.append("\n" + etiqueta + ":\n");
            }
            if (etiqueta.equals("main_end")) {
                text.append("    li $v0, 10\n");
                text.append("    syscall\n");
            }
            return;
        }
            // ── if_true condicion goto etiqueta  para el do while─────────────────────
        if (inst.startsWith("if_true ")) {
            emitirIfTrue(inst);
            return;
        }

        // ── goto etiqueta ─────────────────────────────
        if (inst.startsWith("goto ")) {
            String etiqueta = inst.substring("goto ".length()).trim();
            text.append("    j ").append(etiqueta).append("\n");
            return;
                }
                // ── if_false condicion goto etiqueta ─────────────────────
        if (inst.startsWith("if_false ")) {
            emitirIfFalse(inst);
            return;
        }

        // ── goto etiqueta ───────────────────────────────────────
        if (inst.startsWith("goto ")) {
            String etiqueta = inst.substring("goto ".length()).trim();
            text.append("    j ").append(etiqueta).append("\n");
            return;
        }

        // ── declaración de arreglo (ya procesada en .data) ───
        if (inst.startsWith("var int[] ") || inst.startsWith("var float[] ")) {
            return; // ya está en .data
        }

        // ── declaración de variable simple ────────────────────
        if (inst.startsWith("var ")) {
            String[] partes = inst.split(" ");
            String tipo     = partes[1];
            String nombre   = partes[2];
            offsetActual -= 4;
            offsetVars.put(nombre, offsetActual);
            tipoVars.put(nombre, tipo);
            text.append("    addiu $sp, $sp, -4  # " + nombre + " (" + tipo + ") @ " + offsetActual + "($fp)\n");
            return;
        }

        // ── escritura en arreglo: nombre[i][j] = val ─────────
        if (inst.matches("^\\w+\\[.+\\]\\[.+\\]\\s*=\\s*.+$") && !inst.contains("==")) {
            int    posEq     = inst.indexOf(" = ");
            String ladoIzq   = inst.substring(0, posEq).trim();
            String fuente    = inst.substring(posEq + 3).trim();
            String nombreArr = ladoIzq.substring(0, ladoIzq.indexOf('['));
            String resto     = ladoIzq.substring(ladoIzq.indexOf('['));
            int c1   = resto.indexOf(']');
            String idxI = resto.substring(1, c1).trim();
            String idxJ = resto.substring(resto.indexOf('[', c1) + 1, resto.lastIndexOf(']')).trim();
            emitirEscrituraArreglo(nombreArr, idxI, idxJ, fuente);
            return;
        }

        // ── lectura de arreglo: dest = nombre[i][j] ──────────
        if (inst.matches("^\\w+\\s*=\\s*\\w+\\[.+\\]\\[.+\\]$") && !inst.contains("==")) {
            int    posEq     = inst.indexOf(" = ");
            String dest      = inst.substring(0, posEq).trim();
            String ladoDer   = inst.substring(posEq + 3).trim();
            String nombreArr = ladoDer.substring(0, ladoDer.indexOf('['));
            String resto     = ladoDer.substring(ladoDer.indexOf('['));
            int c1   = resto.indexOf(']');
            String idxI = resto.substring(1, c1).trim();
            String idxJ = resto.substring(resto.indexOf('[', c1) + 1, resto.lastIndexOf(']')).trim();
            emitirLecturaArreglo(nombreArr, idxI, idxJ, dest);
            return;
        }

        // ── asignación / operación aritmética ─────────────────
        if (inst.contains(" = ") ){//&& !inst.contains("==")) {
            String[] partes = inst.split(" = ", 2);
            String dest     = partes[0].trim();
            String fuente   = partes[1].trim();
            for (String op : new String[]{" >= ", " <= ", " == ", " != ", " > ", " < "}) {
                if (fuente.contains(op)) {
                    String[] ops = fuente.split(Pattern.quote(op), 2);

                    String izq = ops[0].trim();
                    String der = ops[1].trim();

                    cargarInt(izq, "$t0");
                    cargarInt(der, "$t1");

                    switch (op.trim()) {
                        case ">":
                            text.append("    sgt $t2, $t0, $t1\n");
                            break;
                        case "<":
                            text.append("    slt $t2, $t0, $t1\n");
                            break;
                        case ">=":
                            text.append("    sge $t2, $t0, $t1\n");
                            break;
                        case "<=":
                            text.append("    sle $t2, $t0, $t1\n");
                            break;
                        case "==":
                            text.append("    seq $t2, $t0, $t1\n");
                            break;
                        case "!=":
                            text.append("    sne $t2, $t0, $t1\n");
                            break;
                    }

                    tipoVars.put(dest, "int");
                    guardarInt("$t2", dest);
                    return;
                }
            }
            // operación aritmética binaria
            for (String op : new String[]{" + ", " - ", " * ", " / ", " % "}) {
                if (fuente.contains(op)) {
                    String[] ops = fuente.split(Pattern.quote(op), 2);
                    String izq = ops[0].trim(), der = ops[1].trim();
                    boolean flt = esFloat(izq) || esFloat(der);
                    tipoVars.put(dest, flt ? "float" : "int");
                    if (flt) {
                        cargarFloat(izq, "$f0");
                        cargarFloat(der, "$f1");
                        String mips;
                        switch (op.trim()) {
                            case "+": mips = "add.s"; break;
                            case "-": mips = "sub.s"; break;
                            case "*": mips = "mul.s"; break;
                            default:  mips = "div.s"; break;
                        }
                        text.append("    " + mips + " $f2, $f0, $f1\n");
                        guardarFloat("$f2", dest);
                    } else {
                        cargarInt(izq, "$t0");
                        cargarInt(der, "$t1");
                        switch (op.trim()) {
                            case "/": text.append("    div $t0, $t1\n    mflo $t2\n"); break;
                            case "%": text.append("    div $t0, $t1\n    mfhi $t2\n"); break;
                            case "+": text.append("    add $t2, $t0, $t1\n"); break;
                            case "-": text.append("    sub $t2, $t0, $t1\n"); break;
                            default:  text.append("    mul $t2, $t0, $t1\n"); break;
                        }
                        guardarInt("$t2", dest);
                    }
                    return;
                }
            }

            // asignación simple: dest = fuente
            boolean flt = esFloat(fuente);
            if (!tipoVars.containsKey(dest)) tipoVars.put(dest, flt ? "float" : "int");
            if ("float".equals(tipoVars.get(dest))) {
                cargarFloat(fuente, "$f0");
                guardarFloat("$f0", dest);
            } else {
                cargarInt(fuente, "$t0");
                guardarInt("$t0", dest);
            }
            return;
        }

        

        // instrucción no manejada aún
       // text.append("    # TODO: " + inst + "\n");







    }
    private void emitirIfTrue(String inst) {
   

    String resto = inst.substring("if_true ".length()).trim();

    int posGoto = resto.lastIndexOf(" goto ");

    if (posGoto == -1) {
        text.append("    # ERROR if_true mal formado: ")
            .append(inst)
            .append("\n");
        return;
    }

    String condicion = resto.substring(0, posGoto).trim();
    String etiqueta = resto.substring(posGoto + " goto ".length()).trim();

    cargarInt(condicion, "$t0");

    text.append("    bne $t0, $zero, ")
        .append(etiqueta)
        .append("\n");
}
    ///if false  metodo para trabajar la etiqueta if false 
    private void emitirIfFalse(String inst) {
  

    String resto = inst.substring("if_false ".length()).trim();//toma la intruccion despues del if false

    int posGoto = resto.lastIndexOf(" goto ");

    if (posGoto == -1) {
        text.append("    # ERROR if_false mal formado: ").append(inst).append("\n");
        return;
    }

    String condicion = resto.substring(0, posGoto).trim();
    String etiqueta = resto.substring(posGoto + " goto ".length()).trim();

    cargarInt(condicion, "$t0");

    text.append("    beq $t0, $zero, ").append(etiqueta).append("\n");
}

    // ── Arreglos ──────────────────────────────────────────────

    private void calcularDireccionArreglo(String nombreArr, String idxI, String idxJ) {
        int columnas  = dimensionesArreglo.get(nombreArr)[1];
        String label  = etiquetaArreglo.get(nombreArr);

        text.append("    la $t8, " + label + "\n");
        cargarInt(idxI, "$t6");
        cargarInt(idxJ, "$t7");
        text.append("    li $t5, " + columnas + "\n");
        text.append("    mul $t5, $t6, $t5\n");
        text.append("    add $t5, $t5, $t7\n");
        text.append("    sll $t5, $t5, 2\n");
        text.append("    add $t8, $t8, $t5\n");
    }

    private void emitirEscrituraArreglo(String arr, String idxI, String idxJ, String fuente) {
        calcularDireccionArreglo(arr, idxI, idxJ);
        if ("float".equals(tipoVars.get(arr))) {
            cargarFloat(fuente, "$f0");
            text.append("    s.s $f0, 0($t8)\n");
        } else {
            cargarInt(fuente, "$t0");
            text.append("    sw $t0, 0($t8)\n");
        }
    }

    private void emitirLecturaArreglo(String arr, String idxI, String idxJ, String dest) {
        calcularDireccionArreglo(arr, idxI, idxJ);
        boolean flt = "float".equals(tipoVars.get(arr));
        tipoVars.put(dest, flt ? "float" : "int");
        if (flt) {
            text.append("    l.s $f0, 0($t8)\n");
            guardarFloat("$f0", dest);
        } else {
            text.append("    lw $t0, 0($t8)\n");
            guardarInt("$t0", dest);
        }
    }

    // ── Carga / guardado int ──────────────────────────────────

    private void cargarInt(String val, String reg) {
        if (esEnteroLiteral(val)) {
            text.append("    li " + reg + ", " + val + "\n");
        } else {
            int off = obtenerOffset(val);
            text.append("    lw " + reg + ", " + off + "($fp)\n");
        }
    }

    private void guardarInt(String reg, String dest) {
        int off = obtenerOffset(dest);
        text.append("    sw " + reg + ", " + off + "($fp)\n");
    }

    // ── Carga / guardado float ────────────────────────────────

    private void cargarFloat(String val, String reg) {
        if (esFloatLiteral(val)) {
            String hex = "0x" + Integer.toHexString(Float.floatToIntBits(Float.parseFloat(val)));
            text.append("    li $t9, " + hex + "\n");
            text.append("    mtc1 $t9, " + reg + "\n");
        } else {
            int off = obtenerOffset(val);
            text.append("    l.s " + reg + ", " + off + "($fp)\n");
        }
    }

    private void guardarFloat(String reg, String dest) {
        int off = obtenerOffset(dest);
        text.append("    s.s " + reg + ", " + off + "($fp)\n");
    }

    // ── Offset en stack ───────────────────────────────────────

    private int obtenerOffset(String nombre) {
        if (!offsetVars.containsKey(nombre)) {
            offsetActual -= 4;
            offsetVars.put(nombre, offsetActual);
            text.append("    addiu $sp, $sp, -4\n");
        }
        return offsetVars.get(nombre);
    }

    // ── Helpers de tipo ───────────────────────────────────────

    private boolean esFloat(String val) {
        if (esFloatLiteral(val)) return true;
        return "float".equals(tipoVars.get(val));
    }

    private boolean esFloatLiteral(String s) {
        try {
            Double.parseDouble(s);
            return s.contains(".") || s.toLowerCase().contains("e");
        } catch (NumberFormatException e) { return false; }
    }

    private boolean esEnteroLiteral(String s) {
        try { Integer.parseInt(s); return true; }
        catch (NumberFormatException e) { return false; }
    }
}