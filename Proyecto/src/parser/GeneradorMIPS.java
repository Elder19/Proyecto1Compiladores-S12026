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
    private HashMap<String, String> etiquetaArreglo; // nombre -> "arr_nombre"
    private HashMap<String, int[]> dimensionesArreglo; // nombre -> {filas, columnas}
    private HashMap<String, String> tipoArreglos;
    private HashMap<String, String> etiquetasCadenas = new HashMap<>();
    private int contadorCadenas = 0;
    private int contadorLabelsMips = 0;

    private java.util.List<String> parametrosPendientes = new java.util.ArrayList<>();// contador de parametros
    private int indiceParametroActual = 0; // funciones

    private HashMap<String, String> tipoRetornoFunciones = new HashMap<>();
    private String funcionActualMips = "";

    private String nuevaEtiquetaMips(String base) {
        return base + "_" + (contadorLabelsMips++);
    }

    public GeneradorMIPS(List<String> instrucciones) {
        this.instrucciones = instrucciones;
        this.data = new StringBuilder();
        this.text = new StringBuilder();
        this.offsetVars = new HashMap<>();
        this.tipoVars = new HashMap<>();
        this.etiquetaArreglo = new HashMap<>();
        this.dimensionesArreglo = new HashMap<>();
        this.offsetActual = 0;
        this.tipoArreglos = new HashMap<>();
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
            String inst = instruccion.trim();
           
            traducir(inst);
        }

        PrintWriter writer = new PrintWriter(new FileWriter(rutaSalida));
        writer.print(data.toString());
        writer.print(text.toString());
        writer.close();
    }

    private void registrarArregloEnData(String inst) {
        String[] partes = inst.split(" ");
        String tipoBase = partes[1].replace("[]", "");
        String nombre = partes[2];
        String[] dims = partes[3].split(",");
        int filas = Integer.parseInt(dims[0].trim());
        int columnas = Integer.parseInt(dims[1].trim());

        String etiqueta = "arr_" + nombre;
        int totalBytes = filas * columnas * 4;

        etiquetaArreglo.put(nombre, etiqueta);
        dimensionesArreglo.put(nombre, new int[] { filas, columnas });
        tipoArreglos.put(nombre, tipoBase);

        data.append("    .align 2\n");
        data.append("    " + etiqueta + ": .space " + totalBytes + "\n");
    }

    private void traducir(String inst) {

        if (inst.startsWith("func ") && inst.endsWith(":")) {
            String nombreFuncion = inst.substring("func ".length(), inst.length() - 1).trim();

            funcionActualMips = nombreFuncion;
            indiceParametroActual = 0;
            reiniciarFrameActual();

            text.append("\n").append(nombreFuncion).append(":\n");
            text.append("    addiu $sp, $sp, -8\n");
            text.append("    sw $ra, 4($sp)\n");
            text.append("    sw $fp, 0($sp)\n");
            text.append("    move $fp, $sp\n");

            return;
        }

        // ── etiquetas normales: main:, if_else_1:, do_start_1:, etc. ──
        if (inst.endsWith(":")) {
            String etiqueta = inst.substring(0, inst.length() - 1).trim();

            if (etiqueta.equals("main")) {
                funcionActualMips = "main";
                indiceParametroActual = 0;
                reiniciarFrameActual();

                text.append("\nmain:\n");
                text.append("    move $fp, $sp\n");
            } else if (etiqueta.equals("main_end")) {
                text.append("\nmain_end:\n");
                text.append("    li $v0, 10\n");
                text.append("    syscall\n");
            } else {
                text.append("\n").append(etiqueta).append(":\n");
            }

            return;
        }
        if (inst.startsWith("param_def ")) {
            escribirParamDef(inst);
            return;
        }

        if (inst.startsWith("end func ")) {
            return;
        }
        // ── etiqueta ─────────────────────────────────────────
        if (inst.startsWith("print ")) {
            escribirPrint(inst);
            return;
        }

        if (inst.startsWith("read ")) {
            escribirRead(inst);
            return;
        }

        if (inst.startsWith("cout ")) {
            escribirCout(inst);
            return;
        }

        if (inst.startsWith("cin ")) {
            escribirCin(inst);
            return;
        }
        // ── if_true condicion goto etiqueta para el do while─────────────────────
        if (inst.startsWith("if_true ")) {
            emitirIfTrue(inst);
            return;
        }
        if (inst.startsWith("return ")) {
            escribirReturn(inst);
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

        // ── declaración de arreglo (ya procesada en .data) ───
        if (inst.startsWith("var int[] ") || inst.startsWith("var float[] ")) {
            return; // ya está en .data
        }

        // ── declaración de variable simple ────────────────────
        if (inst.startsWith("var ")) {
            String[] partes = inst.split(" ");
            String tipo = partes[1];
            String nombre = partes[2];
            offsetActual -= 4;
            offsetVars.put(nombre, offsetActual);
            tipoVars.put(nombre, tipo);
            text.append("    addiu $sp, $sp, -4  # " + nombre + " (" + tipo + ") @ " + offsetActual + "($fp)\n");
            return;
        }

        // ── escritura en arreglo: nombre[i][j] = val
        if (inst.matches("^\\w+\\[.+\\]\\[.+\\]\\s*=\\s*.+$") && !inst.contains("==")) {
            int posEq = inst.indexOf(" = ");
            String ladoIzq = inst.substring(0, posEq).trim();
            String fuente = inst.substring(posEq + 3).trim();
            String nombreArr = ladoIzq.substring(0, ladoIzq.indexOf('['));
            String resto = ladoIzq.substring(ladoIzq.indexOf('['));
            int c1 = resto.indexOf(']');
            String idxI = resto.substring(1, c1).trim();
            String idxJ = resto.substring(resto.indexOf('[', c1) + 1, resto.lastIndexOf(']')).trim();
            emitirEscrituraArreglo(nombreArr, idxI, idxJ, fuente);
            return;
        }

        // ── lectura de arreglo: dest = nombre[i][j]
        if (inst.matches("^\\w+\\s*=\\s*\\w+\\[.+\\]\\[.+\\]$") && !inst.contains("==")) {
            int posEq = inst.indexOf(" = ");
            String dest = inst.substring(0, posEq).trim();
            String ladoDer = inst.substring(posEq + 3).trim();
            String nombreArr = ladoDer.substring(0, ladoDer.indexOf('['));
            String resto = ladoDer.substring(ladoDer.indexOf('['));
            int c1 = resto.indexOf(']');
            String idxI = resto.substring(1, c1).trim();
            String idxJ = resto.substring(resto.indexOf('[', c1) + 1, resto.lastIndexOf(']')).trim();
            emitirLecturaArreglo(nombreArr, idxI, idxJ, dest);
            return;
        }
        if (inst.startsWith("return ")) {
            escribirReturn(inst);
            return;
        }
        // ── asignación / operación aritmética
        if (inst.contains(" = ")) {
            String[] partes = inst.split(" = ", 2);
            String dest = partes[0].trim();
            String fuente = partes[1].trim();
            // ── asignación de string literal: t1 = "Hola" ─────────

            if (esCadenaLiteral(fuente)) {
                String etiqueta = obtenerEtiquetaCadena(fuente);

                tipoVars.put(dest, "string");
                etiquetasCadenas.put(dest, etiqueta);

                text.append("    la $t0, ").append(etiqueta).append("\n");
                guardarInt("$t0", dest);

                return;
            }

            if (fuente.startsWith("call ")) {
                Escribir_llamada(fuente, dest);
                return;
            }
            if (tipoVars.containsKey(fuente)) {
                String tipoFuente = tipoVars.get(fuente);
                tipoVars.put(dest, tipoFuente);

                if ("float".equals(tipoFuente)) {
                    cargarFloat(fuente, "$f0");
                    guardarFloat("$f0", dest);
                } else {
                    // int, bool, char y string se copian con registros enteros.
                    // En string se copia la dirección.
                    cargarInt(fuente, "$t0");
                    guardarInt("$t0", dest);
                }

                return;
            }

            if (fuente.startsWith("!") || fuente.startsWith("$")) {
                String valor = fuente.substring(1).trim();
                escribirNot(valor, dest);
                return;
            }

            // ── AND / OR lógico: t1 = a && b, t1 = a || b ─────────
            for (String opLog : new String[] { " && ", " || ", " @ ", " # " }) {
                if (fuente.contains(opLog)) {
                    String[] ops = fuente.split(Pattern.quote(opLog), 2);

                    String izq = ops[0].trim();
                    String der = ops[1].trim();

                    escribirLogicoBinario(izq, opLog.trim(), der, dest);
                    return;
                }
                // ── comparación relacional: t1 = a > b, t1 = a == b, etc.
                for (String opRel : new String[] { " >= ", " <= ", " == ", " != ", " > ", " < " }) {
                    if (fuente.contains(opRel)) {
                        String[] ops = fuente.split(Pattern.quote(opRel), 2);

                        String izq = ops[0].trim();
                        String der = ops[1].trim();
                        String operador = opRel.trim();

                        boolean flt = esFloat(izq) || esFloat(der);

                        if (flt) {
                            EscribirComparacionFloat(izq, operador, der, dest);
                        } else {
                            cargarInt(izq, "$t0");
                            cargarInt(der, "$t1");

                            switch (operador) {
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
                        }

                        return;
                    }
                }

                // ── operación aritmética binaria: +, -, *, /, %
                for (String opArit : new String[] { " + ", " - ", " * ", " / ", " % " }) {
                    if (fuente.contains(opArit)) {
                        String[] ops = fuente.split(Pattern.quote(opArit), 2);

                        String izq = ops[0].trim();
                        String der = ops[1].trim();

                        boolean flt = esFloat(izq) || esFloat(der);
                        tipoVars.put(dest, flt ? "float" : "int");

                        if (flt) {
                            cargarFloat(izq, "$f0");
                            cargarFloat(der, "$f1");

                            String mips;

                            switch (opArit.trim()) {
                                case "+":
                                    mips = "add.s";
                                    break;
                                case "-":
                                    mips = "sub.s";
                                    break;
                                case "*":
                                    mips = "mul.s";
                                    break;
                                default:
                                    mips = "div.s";
                                    break;
                            }

                            text.append("    ").append(mips).append(" $f2, $f0, $f1\n");
                            guardarFloat("$f2", dest);
                        } else {
                            cargarInt(izq, "$t0");
                            cargarInt(der, "$t1");

                            switch (opArit.trim()) {
                                case "/":
                                    text.append("    div $t0, $t1\n");
                                    text.append("    mflo $t2\n");
                                    break;
                                case "%":
                                    text.append("    div $t0, $t1\n");
                                    text.append("    mfhi $t2\n");
                                    break;
                                case "+":
                                    text.append("    add $t2, $t0, $t1\n");
                                    break;
                                case "-":
                                    text.append("    sub $t2, $t0, $t1\n");
                                    break;
                                default:
                                    text.append("    mul $t2, $t0, $t1\n");
                                    break;
                            }

                            guardarInt("$t2", dest);
                        }

                        return;
                    }
                }

                // ── asignación simple: dest = fuente
                boolean flt = esFloat(fuente);

                if (!tipoVars.containsKey(dest)) {
                    tipoVars.put(dest, flt ? "float" : "int");
                }

                if ("float".equals(tipoVars.get(dest))) {
                    cargarFloat(fuente, "$f0");
                    guardarFloat("$f0", dest);
                } else {
                    cargarInt(fuente, "$t0");
                    guardarInt("$t0", dest);
                }

                return;

            }
        }
        // VALIDA PARAMETOS DE UNA FUNCION.
        if (inst.startsWith("param ")) {
            String valor = inst.substring("param ".length()).trim();
            parametrosPendientes.add(valor);
            return;
        }
        // instrucción no manejada aún
        // text.append(" # TODO: " + inst + "\n");

    }

    /*
     * metodo para el proceso de desarrrollo de un if desarma
     * toda intruccion y la escribe segun corresponde.
     */
    private void emitirIfTrue(String inst) {
        String resto = inst.substring("if_true ".length()).trim();

        int posGoto = resto.lastIndexOf(" goto ");

        if (posGoto == -1) {
            text.append("    # ERROR if_true mal formado: ").append(inst).append("\n");
            return;
        }

        String condicion = resto.substring(0, posGoto).trim(); // guarda el valor de la condicion
        String etiqueta = resto.substring(posGoto + " goto ".length()).trim(); // guarda el valor de la etiqueta a la
                                                                               // que se va a saltar

        cargarInt(condicion, "$t0"); // metodo para cargar el valor a un registro.

        text.append("    bne $t0, $zero, ")
                .append(etiqueta)
                .append("\n");
    }

    /// if false metodo para trabajar la etiqueta if false
    private void emitirIfFalse(String inst) {

        String resto = inst.substring("if_false ".length()).trim();// toma la intruccion despues del if false

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
        int columnas = dimensionesArreglo.get(nombreArr)[1];
        String label = etiquetaArreglo.get(nombreArr);

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
        if ("float".equals(tipoArreglos.get(arr))) {
            cargarFloat(fuente, "$f0");
            text.append("    s.s $f0, 0($t8)\n");
        } else {
            cargarInt(fuente, "$t0");
            text.append("    sw $t0, 0($t8)\n");
        }
    }

    private void emitirLecturaArreglo(String arr, String idxI, String idxJ, String dest) {
        calcularDireccionArreglo(arr, idxI, idxJ);
        boolean flt = "float".equals(tipoArreglos.get(arr));
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
        if (esCadenaLiteral(val)) {
            String etiqueta = obtenerEtiquetaCadena(val);
            text.append("    la ").append(reg).append(", ").append(etiqueta).append("\n");
        } else if ("true".equals(val)) {
            text.append("    li ").append(reg).append(", 1\n");
        } else if ("false".equals(val)) {
            text.append("    li ").append(reg).append(", 0\n");
        } else if (esCharLiteral(val)) {
            text.append("    li ").append(reg).append(", ")
                    .append(obtenerAsciiChar(val))
                    .append("\n");
        } else if (esEnteroLiteral(val)) {
            text.append("    li ").append(reg).append(", ").append(val).append("\n");
        } else {
            int off = obtenerOffset(val);
            text.append("    lw ").append(reg).append(", ")
                    .append(off)
                    .append("($fp)\n");
        }
    }

    private void guardarInt(String reg, String dest) {
        int off = obtenerOffset(dest);
        text.append("    sw " + reg + ", " + off + "($fp)\n");
    }

    // ── Carga / guardado float ────────────────────────────────

    private void cargarFloat(String val, String reg) {
        if (esFraccionLiteral(val)) {
            float valor = evaluarFraccionLiteral(val);
            int bits = Float.floatToIntBits(valor);

            text.append("    li $t9, 0x")
                    .append(Integer.toHexString(bits))
                    .append("\n");

            text.append("    mtc1 $t9, ")
                    .append(reg)
                    .append("\n");

            return;
        }

        if (esFloatLiteral(val)) {
            float valor = Float.parseFloat(val);
            int bits = Float.floatToIntBits(valor);

            text.append("    li $t9, 0x")
                    .append(Integer.toHexString(bits))
                    .append("\n");

            text.append("    mtc1 $t9, ")
                    .append(reg)
                    .append("\n");

            return;
        }

        int off = obtenerOffset(val);
        text.append("    l.s ")
                .append(reg)
                .append(", ")
                .append(off)
                .append("($fp)\n");
    }

    private boolean esFraccionLiteral(String val) {
        return val != null && val.matches("-?\\d+//-?\\d+");
    }

    private void guardarFloat(String reg, String dest) {
        int off = obtenerOffset(dest);
        text.append("    s.s " + reg + ", " + off + "($fp)\n");
    }

    // ── Offset en stack ───────────────────────────────────────
    private void reiniciarFrameActual() {
        offsetVars.clear();
        tipoVars.clear();
        offsetActual = 0;
    }

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
        if (esFloatLiteral(val))
            return true;
        return "float".equals(tipoVars.get(val));
    }

    private boolean esFloatLiteral(String s) {
        try {
            Double.parseDouble(s);
            return s.contains(".");
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private float evaluarFraccionLiteral(String s) {
        String[] partes = s.split("//");

        float numerador = Float.parseFloat(partes[0]);
        float denominador = Float.parseFloat(partes[1]);

        return numerador / denominador;
    }

    private boolean esEnteroLiteral(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean esCadenaLiteral(String s) {
        return s != null && s.length() >= 2 &&
                s.startsWith("\"") && s.endsWith("\"");
    }

    private boolean esCharLiteral(String s) {
        return s != null && s.length() >= 3 &&
                s.startsWith("'") && s.endsWith("'");
    }

    private int obtenerAsciiChar(String literal) {

        if (literal.length() >= 3) {
            return (int) literal.charAt(1);
        }

        return 0;
    }

    private String obtenerEtiquetaCadena(String literal) {
        if (etiquetasCadenas.containsKey(literal)) {
            return etiquetasCadenas.get(literal);
        }

        String etiqueta = "str_" + contadorCadenas++;
        etiquetasCadenas.put(literal, etiqueta);

        data.append("    ")
                .append(etiqueta)
                .append(": .asciiz ")
                .append(literal)
                .append("\n");

        return etiqueta;
    }

    private void EscribirComparacionFloat(String izq, String op, String der, String dest) {
        String lTrue = nuevaEtiquetaMips("cmp_float_true");
        String lFin = nuevaEtiquetaMips("cmp_float_fin");

        cargarFloat(izq, "$f0");
        cargarFloat(der, "$f1");

        switch (op) {
            case "<":
                text.append("    c.lt.s $f0, $f1\n");
                text.append("    bc1t ").append(lTrue).append("\n");
                break;

            case "<=":
                text.append("    c.le.s $f0, $f1\n");
                text.append("    bc1t ").append(lTrue).append("\n");
                break;

            case ">":
                text.append("    c.lt.s $f1, $f0\n");
                text.append("    bc1t ").append(lTrue).append("\n");
                break;

            case ">=":
                text.append("    c.le.s $f1, $f0\n");
                text.append("    bc1t ").append(lTrue).append("\n");
                break;

            case "==":
                text.append("    c.eq.s $f0, $f1\n");
                text.append("    bc1t ").append(lTrue).append("\n");
                break;

            case "!=":
                text.append("    c.eq.s $f0, $f1\n");
                text.append("    bc1f ").append(lTrue).append("\n");
                break;
        }

        text.append("    li $t2, 0\n");
        text.append("    j ").append(lFin).append("\n");

        text.append(lTrue).append(":\n");
        text.append("    li $t2, 1\n");

        text.append(lFin).append(":\n");

        tipoVars.put(dest, "int");
        guardarInt("$t2", dest);
    }

    private void escribirLogicoBinario(String izq, String operador, String der, String dest) {

        cargarBoolComoInt(izq, "$t0");
        cargarBoolComoInt(der, "$t1");

        text.append("    sne $t0, $t0, $zero\n");
        text.append("    sne $t1, $t1, $zero\n");

        switch (operador) {
            case "&&":
            case "@":
                text.append("    and $t2, $t0, $t1\n");
                break;

            case "||":
            case "#":
                text.append("    or $t2, $t0, $t1\n");
                break;
        }

        tipoVars.put(dest, "bool");
        guardarInt("$t2", dest);
    }

    private void escribirNot(String fuente, String dest) {

        cargarBoolComoInt(fuente, "$t0");

        text.append("    seq $t2, $t0, $zero\n");

        tipoVars.put(dest, "bool");
        guardarInt("$t2", dest);
    }

    private void cargarBoolComoInt(String val, String reg) {
        if ("true".equals(val)) {
            text.append("    li ").append(reg).append(", 1\n");
        } else if ("false".equals(val)) {
            text.append("    li ").append(reg).append(", 0\n");
        } else {
            cargarInt(val, reg);
        }
    }

    private void Escribir_llamada(String fuente, String dest) {
        String resto = fuente.substring("call ".length()).trim();
        String[] partes = resto.split(",", 2);

        String nombreFuncion = partes[0].trim();

        String[] regsArgs = { "$a0", "$a1", "$a2", "$a3" };

        for (int i = 0; i < parametrosPendientes.size() && i < regsArgs.length; i++) {
            cargarInt(parametrosPendientes.get(i), regsArgs[i]);
        }

        parametrosPendientes.clear();

        text.append("    jal ").append(nombreFuncion).append("\n");

        String tipoRetorno = tipoRetornoFunciones.getOrDefault(nombreFuncion, "int");
        tipoVars.put(dest, tipoRetorno);

        if ("float".equals(tipoRetorno)) {
            guardarFloat("$f0", dest);
        } else {
            guardarInt("$v0", dest);
        }
    }

    private void escribirReturn(String inst) {
        String valor = inst.substring("return ".length()).trim();
        String tipo = obtenerTipoValor(valor);

        if (funcionActualMips != null && !funcionActualMips.isEmpty() && !"main".equals(funcionActualMips)) {
            tipoRetornoFunciones.put(funcionActualMips, tipo);
        }

        if ("float".equals(tipo)) {
            cargarFloat(valor, "$f0");
            emitirEpilogoFuncion();
        } else {
            cargarInt(valor, "$v0");
            emitirEpilogoFuncion();
        }
    }

    private void emitirEpilogoFuncion() {
        text.append("    move $sp, $fp\n");
        text.append("    lw $fp, 0($sp)\n");
        text.append("    lw $ra, 4($sp)\n");
        text.append("    addiu $sp, $sp, 8\n");
        text.append("    jr $ra\n");
    }

    private void escribirParamDef(String inst) {
        String[] partes = inst.split("\\s+");

        if (partes.length < 3) {
            text.append("    # ERROR param_def mal formado: ")
                    .append(inst)
                    .append("\n");
            return;
        }

        String tipo = partes[1].trim();
        String nombre = partes[2].trim();

        String[] registrosParametros = { "$a0", "$a1", "$a2", "$a3" };

        if (indiceParametroActual >= registrosParametros.length) {
            text.append("    # ERROR: demasiados parametros para ")
                    .append(nombre)
                    .append("\n");
            return;
        }

        offsetActual -= 4;
        offsetVars.put(nombre, offsetActual);
        tipoVars.put(nombre, tipo);

        text.append("    addiu $sp, $sp, -4  # parametro ")
                .append(nombre)
                .append(" (")
                .append(tipo)
                .append(") @ ")
                .append(offsetActual)
                .append("($fp)\n");

        text.append("    sw ")
                .append(registrosParametros[indiceParametroActual])
                .append(", ")
                .append(offsetActual)
                .append("($fp)\n");

        indiceParametroActual++;
    }

    private void escribirCout(String inst) {
        String valor = inst.substring("cout ".length()).trim();
        String tipo = tipoVars.get(valor);

        if (esCadenaLiteral(valor)) {
            String etiqueta = obtenerEtiquetaCadena(valor);
            text.append("    la $a0, ").append(etiqueta).append("\n");
            text.append("    li $v0, 4\n");
            text.append("    syscall\n");
        }

        else if ("string".equals(tipo)) {
            cargarInt(valor, "$a0");
            text.append("    li $v0, 4\n");
            text.append("    syscall\n");
        }

        else if (esCharLiteral(valor)) {
            int ascii = obtenerAsciiChar(valor);
            text.append("    li $a0, ").append(ascii).append("\n");
            text.append("    li $v0, 11\n");
            text.append("    syscall\n");
        }

        else if ("float".equals(tipo) || esFloatLiteral(valor)) {
            cargarFloat(valor, "$f12");
            text.append("    li $v0, 2\n");
            text.append("    syscall\n");
        }

        else if ("char".equals(tipo)) {
            cargarInt(valor, "$a0");
            text.append("    li $v0, 11\n");
            text.append("    syscall\n");
        }

        else {
            cargarInt(valor, "$a0");
            text.append("    li $v0, 1\n");
            text.append("    syscall\n");
        }

        text.append("    la $a0, newline\n");
        text.append("    li $v0, 4\n");
        text.append("    syscall\n");
    }

    private void escribirCin(String inst) {
        String destino = inst.substring("cin ".length()).trim();
        String tipo = tipoVars.get(destino);

        if ("float".equals(tipo)) {
            text.append("    li $v0, 6\n");
            text.append("    syscall\n");
            guardarFloat("$f0", destino);
        } else if ("char".equals(tipo)) {
            text.append("    li $v0, 12\n");
            text.append("    syscall\n");
            guardarInt("$v0", destino);
        } else {
            text.append("    li $v0, 5\n");
            text.append("    syscall\n");
            guardarInt("$v0", destino);
        }
    }

    private void escribirPrint(String inst) {
        String valor = inst.substring("print ".length()).trim();

        // Reutiliza el método de cout
        escribirCout("cout " + valor);

    }

    private void escribirRead(String inst) {
        String destino = inst.substring("read ".length()).trim();

        // Reutiliza el método de cin
        escribirCin("cin " + destino);
    }

    private String obtenerTipoValor(String valor) {
        if (esCadenaLiteral(valor)) {
            return "string";
        }

        if (esFloatLiteral(valor)) {
            return "float";
        }

        if (esCharLiteral(valor)) {
            return "char";
        }

        if ("true".equals(valor) || "false".equals(valor)) {
            return "bool";
        }

        return tipoVars.getOrDefault(valor, "int");
    }
}