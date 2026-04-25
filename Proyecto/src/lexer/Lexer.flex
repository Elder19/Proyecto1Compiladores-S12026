import java_cup.runtime.Symbol;

%%

%cup
%class Lexer
%unicode
%line
%column

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline + 1, yycolumn + 1);
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }
%}

LETRA              = [a-zA-Z]
DIGITO             = [0-9]
DIGITO_SINCERO     = [1-9]

ID                 = ({LETRA}|"_")({LETRA}|{DIGITO}|"_")*


ENTERO             = 0|{DIGITO_SINCERO}{DIGITO}*
FLOTANTE           = {ENTERO}"."{ENTERO}
EXPONENCIAL        = {ENTERO}"e"{ENTERO}
FRACCION           = {ENTERO}"//"{ENTERO}

CADENA             = \"([^\"\\]|\\.)*\"
CARACTER           = \'([^\'\\]|\\.)\'

ESPACIO            = [ \t\r\n\f]+
COMENTARIO_LINEA   = "¡¡"[^\r\n]*
COMENTARIO_BLOQUE  = "\{-"([^*-]|"-"[^}]|"*"[^-])*"-\}"

%%
%{
    public Lexer(java.io.Reader in) {
        this.zzReader = in;
    }
%}
<YYINITIAL> {ESPACIO}           { /* ignorar */ }
<YYINITIAL> {COMENTARIO_LINEA}  { /* ignorar */ }
<YYINITIAL> {COMENTARIO_BLOQUE} { /* ignorar */ }



<YYINITIAL> "int"        { return symbol(sym.INT, yytext()); }

<YYINITIAL> "bool"       { return symbol(sym.BOOL, yytext()); }
<YYINITIAL> "float"      { return symbol(sym.FLOAT, yytext()); }
<YYINITIAL> "string"     { return symbol(sym.STRING, yytext()); }
<YYINITIAL> "char"       { return symbol(sym.CHAR, yytext()); }

<YYINITIAL> "if"         { return symbol(sym.IF, yytext()); }
<YYINITIAL> "else"       { return symbol(sym.ELSE, yytext()); }
<YYINITIAL> "switch"     { return symbol(sym.SWITCH, yytext()); }
<YYINITIAL> "case"       { return symbol(sym.CASE, yytext()); }
<YYINITIAL> "default"    { return symbol(sym.DEFAULT, yytext()); }

<YYINITIAL> "return"     { return symbol(sym.RETURN, yytext()); }
<YYINITIAL> "break"      { return symbol(sym.BREAK, yytext()); }

<YYINITIAL> "cin"        { return symbol(sym.CIN, yytext()); }
<YYINITIAL> "cout"       { return symbol(sym.COUT, yytext()); }

<YYINITIAL> "true"       { return symbol(sym.TRUE, yytext()); }
<YYINITIAL> "false"      { return symbol(sym.FALSE, yytext()); }

<YYINITIAL> "empty"      { return symbol(sym.EMPTY, yytext()); }
<YYINITIAL> "__main__"   { return symbol(sym.MAIN, yytext()); }

<YYINITIAL> "do"         { return symbol(sym.DO, yytext()); }
<YYINITIAL> "while"      { return symbol(sym.WHILE, yytext()); }



<YYINITIAL> "equal"        { return symbol(sym.EQUAL, yytext()); }
<YYINITIAL> "n_equal"      { return symbol(sym.N_EQUAL, yytext()); }
<YYINITIAL> "less_t"       { return symbol(sym.LESS_T, yytext()); }
<YYINITIAL> "less_te"      { return symbol(sym.LESS_TE, yytext()); }
<YYINITIAL> "greather_t"   { return symbol(sym.GREATHER_T, yytext()); }
<YYINITIAL> "greather_te"  { return symbol(sym.GREATHER_TE, yytext()); }



<YYINITIAL> "|:"   { return symbol(sym.ABRIR_BLOQUE, yytext()); }
<YYINITIAL> ":|"   { return symbol(sym.CERRAR_BLOQUE, yytext()); }

<YYINITIAL> "<-"   { return symbol(sym.ASIGNACION, yytext()); }

<YYINITIAL> "<|"   { return symbol(sym.PARENTESIS_APERTURA, yytext()); }
<YYINITIAL> "|>"   { return symbol(sym.PARENTESIS_CIERRE, yytext()); }

<YYINITIAL> "<<"   { return symbol(sym.ABRIR_PARENTESIS_CUADRADO, yytext()); }
<YYINITIAL> ">>"   { return symbol(sym.CERRAR_PARENTESIS_CUADRADO, yytext()); }

<YYINITIAL> "++"   { return symbol(sym.INCREMENTO, yytext()); }
<YYINITIAL> "--"   { return symbol(sym.DECREMENTO, yytext()); }



<YYINITIAL> "!"   { return symbol(sym.FIN_SENTENCIA, yytext()); }
<YYINITIAL> "~"   { return symbol(sym.SEPARADOR, yytext()); }
<YYINITIAL> ","   { return symbol(sym.COMA, yytext()); }
<YYINITIAL> ":"   { return symbol(sym.DOS_PUNTOS, yytext()); }



<YYINITIAL> "+"   { return symbol(sym.SUMA, yytext()); }
<YYINITIAL> "-"   { return symbol(sym.RESTA, yytext()); }
<YYINITIAL> "*"   { return symbol(sym.MULTIPLICACION, yytext()); }
<YYINITIAL> "/"   { return symbol(sym.DIVISION, yytext()); }
<YYINITIAL> "%"   { return symbol(sym.MODULO, yytext()); }
<YYINITIAL> "^"   { return symbol(sym.POTENCIA, yytext()); }



<YYINITIAL> "@"   { return symbol(sym.AND, yytext()); }
<YYINITIAL> "#"   { return symbol(sym.OR, yytext()); }
<YYINITIAL> "$"   { return symbol(sym.NOT, yytext()); }


<YYINITIAL> {EXPONENCIAL}  { return symbol(sym.EXPONENCIAL, yytext()); }
<YYINITIAL> {FLOTANTE}     { return symbol(sym.FLOTANTE, yytext()); }
<YYINITIAL> {FRACCION}     { return symbol(sym.FRACCION, yytext()); }
<YYINITIAL> {ENTERO}       { return symbol(sym.ENTERO, yytext()); }

<YYINITIAL> "//"           { return symbol(sym.DIVISION_ENTERA, yytext()); }

<YYINITIAL> {CADENA}       { return symbol(sym.CADENA, yytext()); }
<YYINITIAL> {CARACTER}     { return symbol(sym.CARACTER, yytext()); }



<YYINITIAL> {ID} { return symbol(sym.ID, yytext()); }


<YYINITIAL> [^] {
    System.out.println(
        "Error lexico: '" + yytext() +
        "' en linea " + (yyline + 1) +
        ", columna " + (yycolumn + 1)
    );
    return symbol(sym.ERROR, yytext());

}