.data
    newline: .asciiz "\n"
    .align 2
    arr_matriz: .space 16
    .align 2
    arr_tabla: .space 16
    str_0: .asciiz "YA YA CORRALO"
    str_1: .asciiz "Valor diferente"
    str_2: .asciiz "Hola erin fea"

.text
.globl main

sumar:
    addiu $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    addiu $sp, $sp, -4  # parametro a (int) @ -4($fp)
    sw $a0, -4($fp)
    addiu $sp, $sp, -4  # parametro b (int) @ -8($fp)
    sw $a1, -8($fp)
    addiu $sp, $sp, -4  # resultado (int) @ -12($fp)
    lw $t0, -4($fp)
    addiu $sp, $sp, -4
    sw $t0, -16($fp)
    lw $t0, -8($fp)
    addiu $sp, $sp, -4
    sw $t0, -20($fp)
    lw $t0, -16($fp)
    lw $t1, -20($fp)
    add $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -24($fp)
    lw $t0, -24($fp)
    sw $t0, -12($fp)
    li $t0, 5
    addiu $sp, $sp, -4
    sw $t0, -28($fp)
    lw $v0, -28($fp)
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addiu $sp, $sp, 8
    jr $ra

calcular:
    addiu $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    addiu $sp, $sp, -4  # parametro x (float) @ -4($fp)
    sw $a0, -4($fp)
    addiu $sp, $sp, -4  # parametro y (float) @ -8($fp)
    sw $a1, -8($fp)
    addiu $sp, $sp, -4  # r (float) @ -12($fp)
    li $t0, 32
    addiu $sp, $sp, -4
    sw $t0, -16($fp)
    addiu $sp, $sp, -4  # hi (int) @ -20($fp)
    lw $t0, -16($fp)
    sw $t0, -20($fp)
    li $t9, 0x41826666
    mtc1 $t9, $f0
    addiu $sp, $sp, -4
    s.s $f0, -24($fp)
    addiu $sp, $sp, -4  # expo (float) @ -28($fp)
    l.s $f0, -24($fp)
    s.s $f0, -28($fp)
    li $t9, 0x3f5b6db7
    mtc1 $t9, $f0
    addiu $sp, $sp, -4
    s.s $f0, -32($fp)
    addiu $sp, $sp, -4  # frac (float) @ -36($fp)
    l.s $f0, -32($fp)
    s.s $f0, -36($fp)
    l.s $f0, -4($fp)
    addiu $sp, $sp, -4
    s.s $f0, -40($fp)
    l.s $f0, -8($fp)
    addiu $sp, $sp, -4
    s.s $f0, -44($fp)
    li $t9, 0x4019999a
    mtc1 $t9, $f0
    addiu $sp, $sp, -4
    s.s $f0, -48($fp)
    l.s $f0, -44($fp)
    l.s $f1, -48($fp)
    div.s $f2, $f0, $f1
    addiu $sp, $sp, -4
    s.s $f2, -52($fp)
    l.s $f0, -40($fp)
    l.s $f1, -52($fp)
    add.s $f2, $f0, $f1
    addiu $sp, $sp, -4
    s.s $f2, -56($fp)
    l.s $f0, -56($fp)
    s.s $f0, -12($fp)
    l.s $f0, -12($fp)
    addiu $sp, $sp, -4
    s.s $f0, -60($fp)
    l.s $f0, -28($fp)
    addiu $sp, $sp, -4
    s.s $f0, -64($fp)
    l.s $f0, -60($fp)
    l.s $f1, -64($fp)
    add.s $f2, $f0, $f1
    addiu $sp, $sp, -4
    s.s $f2, -68($fp)
    l.s $f0, -36($fp)
    addiu $sp, $sp, -4
    s.s $f0, -72($fp)
    l.s $f0, -68($fp)
    l.s $f1, -72($fp)
    sub.s $f2, $f0, $f1
    addiu $sp, $sp, -4
    s.s $f2, -76($fp)
    l.s $f0, -76($fp)
    s.s $f0, -12($fp)
    l.s $f0, -12($fp)
    addiu $sp, $sp, -4
    s.s $f0, -80($fp)
    l.s $f0, -80($fp)
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addiu $sp, $sp, 8
    jr $ra

escogerLetra:
    addiu $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    addiu $sp, $sp, -4  # parametro bandera (bool) @ -4($fp)
    sw $a0, -4($fp)
    addiu $sp, $sp, -4  # parametro letra1 (char) @ -8($fp)
    sw $a1, -8($fp)
    addiu $sp, $sp, -4  # parametro letra2 (char) @ -12($fp)
    sw $a2, -12($fp)
    lw $t0, -4($fp)
    addiu $sp, $sp, -4
    sw $t0, -16($fp)
    lw $t0, -16($fp)
    beq $t0, $zero, if_else_1
    lw $t0, -8($fp)
    addiu $sp, $sp, -4
    sw $t0, -20($fp)
    lw $v0, -20($fp)
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addiu $sp, $sp, 8
    jr $ra
    j if_end_1

if_else_1:
    lw $t0, -12($fp)
    addiu $sp, $sp, -4
    sw $t0, -24($fp)
    lw $v0, -24($fp)
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addiu $sp, $sp, 8
    jr $ra

if_end_1:

main:
    move $fp, $sp
    addiu $sp, $sp, -4  # mensaje2 (string) @ -4($fp)
    addiu $sp, $sp, -4  # mensaje (string) @ -8($fp)
    addiu $sp, $sp, -4  # entrada (int) @ -12($fp)
    li $t0, 5
    addiu $sp, $sp, -4
    sw $t0, -16($fp)
    addiu $sp, $sp, -4  # a (int) @ -20($fp)
    lw $t0, -16($fp)
    sw $t0, -20($fp)
    li $t0, 7
    addiu $sp, $sp, -4
    sw $t0, -24($fp)
    addiu $sp, $sp, -4  # b (int) @ -28($fp)
    lw $t0, -24($fp)
    sw $t0, -28($fp)
    addiu $sp, $sp, -4  # total (int) @ -32($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -36($fp)
    addiu $sp, $sp, -4  # contador (int) @ -40($fp)
    lw $t0, -36($fp)
    sw $t0, -40($fp)
    li $t9, 0x408ccccd
    mtc1 $t9, $f0
    addiu $sp, $sp, -4
    s.s $f0, -44($fp)
    addiu $sp, $sp, -4  # decimal (float) @ -48($fp)
    l.s $f0, -44($fp)
    s.s $f0, -48($fp)
    li $t9, 0x411e6666
    mtc1 $t9, $f0
    addiu $sp, $sp, -4
    s.s $f0, -52($fp)
    addiu $sp, $sp, -4  # otroDecimal (float) @ -56($fp)
    l.s $f0, -52($fp)
    s.s $f0, -56($fp)
    addiu $sp, $sp, -4  # resultadoFloat (float) @ -60($fp)
    li $t0, 120
    addiu $sp, $sp, -4
    sw $t0, -64($fp)
    addiu $sp, $sp, -4  # letra (char) @ -68($fp)
    lw $t0, -64($fp)
    sw $t0, -68($fp)
    addiu $sp, $sp, -4  # letraFinal (char) @ -72($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -76($fp)
    addiu $sp, $sp, -4  # activo (bool) @ -80($fp)
    lw $t0, -76($fp)
    sw $t0, -80($fp)
    addiu $sp, $sp, -4  # condicion1 (bool) @ -84($fp)
    addiu $sp, $sp, -4  # condicion2 (bool) @ -88($fp)
    addiu $sp, $sp, -4  # condicionFinal (bool) @ -92($fp)
    la $t8, arr_matriz
    li $t6, 0
    li $t7, 0
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    li $t0, 1
    sw $t0, 0($t8)
    la $t8, arr_matriz
    li $t6, 0
    li $t7, 1
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    li $t0, 2
    sw $t0, 0($t8)
    la $t8, arr_matriz
    li $t6, 1
    li $t7, 0
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    li $t0, 3
    sw $t0, 0($t8)
    la $t8, arr_matriz
    li $t6, 1
    li $t7, 1
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    li $t0, 4
    sw $t0, 0($t8)
    la $t8, arr_tabla
    li $t6, 0
    li $t7, 0
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    li $t9, 0x3f8ccccd
    mtc1 $t9, $f0
    s.s $f0, 0($t8)
    la $t8, arr_tabla
    li $t6, 0
    li $t7, 1
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    li $t9, 0x400ccccd
    mtc1 $t9, $f0
    s.s $f0, 0($t8)
    la $t8, arr_tabla
    li $t6, 1
    li $t7, 0
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    li $t9, 0x40533333
    mtc1 $t9, $f0
    s.s $f0, 0($t8)
    la $t8, arr_tabla
    li $t6, 1
    li $t7, 1
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    li $t9, 0x408ccccd
    mtc1 $t9, $f0
    s.s $f0, 0($t8)
    lw $t0, -20($fp)
    addiu $sp, $sp, -4
    sw $t0, -96($fp)
    lw $t0, -28($fp)
    addiu $sp, $sp, -4
    sw $t0, -100($fp)
    lw $a0, -96($fp)
    lw $a1, -100($fp)
    jal sumar
    addiu $sp, $sp, -4
    sw $v0, -104($fp)
    lw $t0, -104($fp)
    sw $t0, -32($fp)
    l.s $f0, -48($fp)
    addiu $sp, $sp, -4
    s.s $f0, -108($fp)
    l.s $f0, -56($fp)
    addiu $sp, $sp, -4
    s.s $f0, -112($fp)
    lw $a0, -108($fp)
    lw $a1, -112($fp)
    jal calcular
    addiu $sp, $sp, -4
    s.s $f0, -116($fp)
    l.s $f0, -116($fp)
    s.s $f0, -60($fp)
    lw $t0, -80($fp)
    addiu $sp, $sp, -4
    sw $t0, -120($fp)
    li $t0, 65
    addiu $sp, $sp, -4
    sw $t0, -124($fp)
    li $t0, 66
    addiu $sp, $sp, -4
    sw $t0, -128($fp)
    lw $a0, -120($fp)
    lw $a1, -124($fp)
    lw $a2, -128($fp)
    jal escogerLetra
    addiu $sp, $sp, -4
    sw $v0, -132($fp)
    lw $t0, -132($fp)
    sw $t0, -72($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -136($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -140($fp)
    lw $t0, -32($fp)
    addiu $sp, $sp, -4
    sw $t0, -144($fp)
    la $t8, arr_matriz
    lw $t6, -136($fp)
    lw $t7, -140($fp)
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, -144($fp)
    sw $t0, 0($t8)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -148($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -152($fp)
    la $t8, arr_matriz
    lw $t6, -148($fp)
    lw $t7, -152($fp)
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, 0($t8)
    addiu $sp, $sp, -4
    sw $t0, -156($fp)
    lw $t0, -156($fp)
    sw $t0, -32($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -160($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -164($fp)
    l.s $f0, -60($fp)
    addiu $sp, $sp, -4
    s.s $f0, -168($fp)
    la $t8, arr_tabla
    lw $t6, -160($fp)
    lw $t7, -164($fp)
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    l.s $f0, -168($fp)
    s.s $f0, 0($t8)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -172($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -176($fp)
    la $t8, arr_tabla
    lw $t6, -172($fp)
    lw $t7, -176($fp)
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    l.s $f0, 0($t8)
    addiu $sp, $sp, -4
    s.s $f0, -180($fp)
    l.s $f0, -180($fp)
    s.s $f0, -60($fp)
    lw $t0, -20($fp)
    addiu $sp, $sp, -4
    sw $t0, -184($fp)
    lw $t0, -28($fp)
    addiu $sp, $sp, -4
    sw $t0, -188($fp)
    lw $t0, -184($fp)
    lw $t1, -188($fp)
    slt $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -192($fp)
    lw $t0, -192($fp)
    sw $t0, -84($fp)
    lw $t0, -32($fp)
    addiu $sp, $sp, -4
    sw $t0, -196($fp)
    lw $t0, -40($fp)
    addiu $sp, $sp, -4
    sw $t0, -200($fp)
    lw $t0, -196($fp)
    lw $t1, -200($fp)
    sne $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -204($fp)
    lw $t0, -204($fp)
    sw $t0, -88($fp)
    lw $t0, -84($fp)
    addiu $sp, $sp, -4
    sw $t0, -208($fp)
    lw $t0, -88($fp)
    addiu $sp, $sp, -4
    sw $t0, -212($fp)
    lw $t0, -208($fp)
    lw $t1, -212($fp)
    sne $t0, $t0, $zero
    sne $t1, $t1, $zero
    and $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -216($fp)
    lw $t0, -216($fp)
    sw $t0, -92($fp)
    lw $t0, -32($fp)
    addiu $sp, $sp, -4
    sw $t0, -220($fp)
    lw $a0, -220($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    l.s $f0, -60($fp)
    addiu $sp, $sp, -4
    s.s $f0, -224($fp)
    l.s $f12, -224($fp)
    li $v0, 2
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    lw $t0, -72($fp)
    addiu $sp, $sp, -4
    sw $t0, -228($fp)
    lw $a0, -228($fp)
    li $v0, 11
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    lw $t0, -92($fp)
    addiu $sp, $sp, -4
    sw $t0, -232($fp)
    lw $a0, -232($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    li $v0, 5
    syscall
    sw $v0, -12($fp)
    lw $t0, -92($fp)
    addiu $sp, $sp, -4
    sw $t0, -236($fp)
    lw $t0, -236($fp)
    beq $t0, $zero, if_else_2
    lw $t0, -20($fp)
    addiu $sp, $sp, -4
    sw $t0, -240($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -244($fp)
    lw $t0, -240($fp)
    lw $t1, -244($fp)
    add $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -248($fp)
    lw $t0, -248($fp)
    sw $t0, -20($fp)
    lw $t0, -28($fp)
    addiu $sp, $sp, -4
    sw $t0, -252($fp)
    lw $t0, -248($fp)
    lw $t1, -252($fp)
    add $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -256($fp)
    lw $t0, -256($fp)
    sw $t0, -32($fp)
    lw $t0, -32($fp)
    addiu $sp, $sp, -4
    sw $t0, -260($fp)
    lw $a0, -260($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    j if_end_2

if_else_2:
    lw $t0, -28($fp)
    addiu $sp, $sp, -4
    sw $t0, -264($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -268($fp)
    lw $t0, -264($fp)
    lw $t1, -268($fp)
    sub $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -272($fp)
    lw $t0, -272($fp)
    sw $t0, -28($fp)
    lw $t0, -20($fp)
    addiu $sp, $sp, -4
    sw $t0, -276($fp)
    lw $t0, -272($fp)
    lw $t1, -276($fp)
    add $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -280($fp)
    lw $t0, -280($fp)
    sw $t0, -32($fp)
    lw $t0, -32($fp)
    addiu $sp, $sp, -4
    sw $t0, -284($fp)
    lw $a0, -284($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall

if_end_2:

do_start_1:
    lw $t0, -40($fp)
    addiu $sp, $sp, -4
    sw $t0, -288($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -292($fp)
    lw $t0, -288($fp)
    lw $t1, -292($fp)
    add $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -296($fp)
    lw $t0, -296($fp)
    sw $t0, -40($fp)
    lw $t0, -40($fp)
    addiu $sp, $sp, -4
    sw $t0, -300($fp)
    lw $a0, -300($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    lw $t0, -40($fp)
    addiu $sp, $sp, -4
    sw $t0, -304($fp)
    li $t0, 3
    addiu $sp, $sp, -4
    sw $t0, -308($fp)
    lw $t0, -304($fp)
    lw $t1, -308($fp)
    slt $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -312($fp)
    lw $t0, -312($fp)
    bne $t0, $zero, do_start_1

do_end_1:
    la $t0, str_0
    addiu $sp, $sp, -4
    sw $t0, -316($fp)
    lw $t0, -316($fp)
    sw $t0, -4($fp)
    lw $t0, -4($fp)
    addiu $sp, $sp, -4
    sw $t0, -320($fp)
    lw $a0, -320($fp)
    li $v0, 4
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    lw $t0, -12($fp)
    addiu $sp, $sp, -4
    sw $t0, -324($fp)

switch_case_1:
    lw $t0, -324($fp)
    li $t1, 1
    seq $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -328($fp)
    lw $t0, -328($fp)
    beq $t0, $zero, switch_case_2
    lw $t0, -32($fp)
    addiu $sp, $sp, -4
    sw $t0, -332($fp)
    li $t0, 10
    addiu $sp, $sp, -4
    sw $t0, -336($fp)
    lw $t0, -332($fp)
    lw $t1, -336($fp)
    add $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -340($fp)
    lw $t0, -340($fp)
    sw $t0, -32($fp)
    lw $t0, -32($fp)
    addiu $sp, $sp, -4
    sw $t0, -344($fp)
    lw $a0, -344($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    j switch_end_1

switch_case_2:
    lw $t0, -324($fp)
    li $t1, 2
    seq $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -348($fp)
    lw $t0, -348($fp)
    beq $t0, $zero, switch_case_3
    lw $t0, -32($fp)
    addiu $sp, $sp, -4
    sw $t0, -352($fp)
    li $t0, 20
    addiu $sp, $sp, -4
    sw $t0, -356($fp)
    lw $t0, -352($fp)
    lw $t1, -356($fp)
    add $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -360($fp)
    lw $t0, -360($fp)
    sw $t0, -32($fp)
    lw $t0, -32($fp)
    addiu $sp, $sp, -4
    sw $t0, -364($fp)
    lw $a0, -364($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    j switch_end_1

switch_case_3:
    la $t0, str_1
    addiu $sp, $sp, -4
    sw $t0, -368($fp)
    lw $a0, -368($fp)
    li $v0, 4
    syscall
    la $a0, newline
    li $v0, 4
    syscall

switch_end_1:
    la $t0, str_2
    addiu $sp, $sp, -4
    sw $t0, -372($fp)
    lw $t0, -372($fp)
    sw $t0, -8($fp)

main_end:
    li $v0, 10
    syscall
