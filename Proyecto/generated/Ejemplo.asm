.data
    newline: .asciiz "\n"
    .align 2
    arr_arregloInt: .space 24
    .align 2
    arr_arregloFloat: .space 16

.text
.globl main

suma:
    addiu $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    addiu $sp, $sp, -4  # parametro a (int) @ -4($fp)
    sw $a0, -4($fp)
    addiu $sp, $sp, -4  # parametro b (int) @ -8($fp)
    sw $a1, -8($fp)
    lw $t0, -4($fp)
    addiu $sp, $sp, -4
    sw $t0, -12($fp)
    lw $t0, -8($fp)
    addiu $sp, $sp, -4
    sw $t0, -16($fp)
    lw $t0, -12($fp)
    lw $t1, -16($fp)
    add $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -20($fp)
    lw $v0, -20($fp)
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addiu $sp, $sp, 8
    jr $ra

multiplicar:
    addiu $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    addiu $sp, $sp, -4  # parametro a (int) @ -4($fp)
    sw $a0, -4($fp)
    addiu $sp, $sp, -4  # parametro b (int) @ -8($fp)
    sw $a1, -8($fp)
    lw $t0, -4($fp)
    addiu $sp, $sp, -4
    sw $t0, -12($fp)
    lw $t0, -8($fp)
    addiu $sp, $sp, -4
    sw $t0, -16($fp)
    lw $t0, -12($fp)
    lw $t1, -16($fp)
    mul $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -20($fp)
    lw $v0, -20($fp)
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addiu $sp, $sp, 8
    jr $ra

promedioFlotante:
    addiu $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    addiu $sp, $sp, -4  # parametro x (float) @ -4($fp)
    s.s $f12, -4($fp)
    addiu $sp, $sp, -4  # parametro y (float) @ -8($fp)
    s.s $f14, -8($fp)
    addiu $sp, $sp, -4  # parametro z (float) @ -12($fp)
    s.s $f16, -12($fp)
    addiu $sp, $sp, -4  # total (float) @ -16($fp)
    l.s $f0, -4($fp)
    addiu $sp, $sp, -4
    s.s $f0, -20($fp)
    l.s $f0, -8($fp)
    addiu $sp, $sp, -4
    s.s $f0, -24($fp)
    l.s $f0, -20($fp)
    l.s $f1, -24($fp)
    add.s $f2, $f0, $f1
    addiu $sp, $sp, -4
    s.s $f2, -28($fp)
    l.s $f0, -28($fp)
    s.s $f0, -16($fp)
    l.s $f0, -16($fp)
    addiu $sp, $sp, -4
    s.s $f0, -32($fp)
    l.s $f0, -12($fp)
    addiu $sp, $sp, -4
    s.s $f0, -36($fp)
    l.s $f0, -32($fp)
    l.s $f1, -36($fp)
    add.s $f2, $f0, $f1
    addiu $sp, $sp, -4
    s.s $f2, -40($fp)
    l.s $f0, -40($fp)
    s.s $f0, -16($fp)
    l.s $f0, -16($fp)
    addiu $sp, $sp, -4
    s.s $f0, -44($fp)
    l.s $f0, -44($fp)
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addiu $sp, $sp, 8
    jr $ra

escalar:
    addiu $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    addiu $sp, $sp, -4  # parametro v (float) @ -4($fp)
    s.s $f12, -4($fp)
    addiu $sp, $sp, -4  # parametro factor (int) @ -8($fp)
    sw $a0, -8($fp)
    addiu $sp, $sp, -4  # resultado (float) @ -12($fp)
    l.s $f0, -4($fp)
    addiu $sp, $sp, -4
    s.s $f0, -16($fp)
    l.s $f0, -4($fp)
    addiu $sp, $sp, -4
    s.s $f0, -20($fp)
    l.s $f0, -16($fp)
    l.s $f1, -20($fp)
    add.s $f2, $f0, $f1
    addiu $sp, $sp, -4
    s.s $f2, -24($fp)
    l.s $f0, -24($fp)
    s.s $f0, -12($fp)
    l.s $f0, -12($fp)
    addiu $sp, $sp, -4
    s.s $f0, -28($fp)
    l.s $f0, -28($fp)
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addiu $sp, $sp, 8
    jr $ra

main:
    move $fp, $sp
    li $t0, 10
    addiu $sp, $sp, -4
    sw $t0, -4($fp)
    addiu $sp, $sp, -4  # a (int) @ -8($fp)
    lw $t0, -4($fp)
    sw $t0, -8($fp)
    li $t0, 20
    addiu $sp, $sp, -4
    sw $t0, -12($fp)
    addiu $sp, $sp, -4  # b (int) @ -16($fp)
    lw $t0, -12($fp)
    sw $t0, -16($fp)
    li $t0, 30
    addiu $sp, $sp, -4
    sw $t0, -20($fp)
    addiu $sp, $sp, -4  # c (int) @ -24($fp)
    lw $t0, -20($fp)
    sw $t0, -24($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -28($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -32($fp)
    lw $t0, -8($fp)
    addiu $sp, $sp, -4
    sw $t0, -36($fp)
    lw $t0, -16($fp)
    addiu $sp, $sp, -4
    sw $t0, -40($fp)
    lw $a0, -36($fp)
    lw $a1, -40($fp)
    jal suma
    addiu $sp, $sp, -4
    sw $v0, -44($fp)
    la $t8, arr_arregloInt
    lw $t6, -28($fp)
    lw $t7, -32($fp)
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, -44($fp)
    sw $t0, 0($t8)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -48($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -52($fp)
    lw $t0, -16($fp)
    addiu $sp, $sp, -4
    sw $t0, -56($fp)
    lw $t0, -24($fp)
    addiu $sp, $sp, -4
    sw $t0, -60($fp)
    lw $a0, -56($fp)
    lw $a1, -60($fp)
    jal suma
    addiu $sp, $sp, -4
    sw $v0, -64($fp)
    la $t8, arr_arregloInt
    lw $t6, -48($fp)
    lw $t7, -52($fp)
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, -64($fp)
    sw $t0, 0($t8)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -68($fp)
    li $t0, 2
    addiu $sp, $sp, -4
    sw $t0, -72($fp)
    lw $t0, -8($fp)
    addiu $sp, $sp, -4
    sw $t0, -76($fp)
    lw $t0, -16($fp)
    addiu $sp, $sp, -4
    sw $t0, -80($fp)
    lw $a0, -76($fp)
    lw $a1, -80($fp)
    jal multiplicar
    addiu $sp, $sp, -4
    sw $v0, -84($fp)
    la $t8, arr_arregloInt
    lw $t6, -68($fp)
    lw $t7, -72($fp)
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, -84($fp)
    sw $t0, 0($t8)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -88($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -92($fp)
    lw $t0, -16($fp)
    addiu $sp, $sp, -4
    sw $t0, -96($fp)
    lw $t0, -24($fp)
    addiu $sp, $sp, -4
    sw $t0, -100($fp)
    lw $a0, -96($fp)
    lw $a1, -100($fp)
    jal multiplicar
    addiu $sp, $sp, -4
    sw $v0, -104($fp)
    la $t8, arr_arregloInt
    lw $t6, -88($fp)
    lw $t7, -92($fp)
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, -104($fp)
    sw $t0, 0($t8)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -108($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -112($fp)
    lw $t0, -8($fp)
    addiu $sp, $sp, -4
    sw $t0, -116($fp)
    lw $t0, -16($fp)
    addiu $sp, $sp, -4
    sw $t0, -120($fp)
    lw $a0, -116($fp)
    lw $a1, -120($fp)
    jal multiplicar
    addiu $sp, $sp, -4
    sw $v0, -124($fp)
    lw $t0, -16($fp)
    addiu $sp, $sp, -4
    sw $t0, -128($fp)
    lw $t0, -24($fp)
    addiu $sp, $sp, -4
    sw $t0, -132($fp)
    lw $a0, -128($fp)
    lw $a1, -132($fp)
    jal suma
    addiu $sp, $sp, -4
    sw $v0, -136($fp)
    lw $a0, -124($fp)
    lw $a1, -136($fp)
    jal suma
    addiu $sp, $sp, -4
    sw $v0, -140($fp)
    la $t8, arr_arregloInt
    lw $t6, -108($fp)
    lw $t7, -112($fp)
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, -140($fp)
    sw $t0, 0($t8)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -144($fp)
    li $t0, 2
    addiu $sp, $sp, -4
    sw $t0, -148($fp)
    lw $t0, -8($fp)
    addiu $sp, $sp, -4
    sw $t0, -152($fp)
    lw $t0, -16($fp)
    addiu $sp, $sp, -4
    sw $t0, -156($fp)
    lw $a0, -152($fp)
    lw $a1, -156($fp)
    jal suma
    addiu $sp, $sp, -4
    sw $v0, -160($fp)
    lw $t0, -16($fp)
    addiu $sp, $sp, -4
    sw $t0, -164($fp)
    lw $t0, -24($fp)
    addiu $sp, $sp, -4
    sw $t0, -168($fp)
    lw $a0, -164($fp)
    lw $a1, -168($fp)
    jal suma
    addiu $sp, $sp, -4
    sw $v0, -172($fp)
    lw $a0, -160($fp)
    lw $a1, -172($fp)
    jal multiplicar
    addiu $sp, $sp, -4
    sw $v0, -176($fp)
    la $t8, arr_arregloInt
    lw $t6, -144($fp)
    lw $t7, -148($fp)
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, -176($fp)
    sw $t0, 0($t8)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -180($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -184($fp)
    la $t8, arr_arregloInt
    lw $t6, -180($fp)
    lw $t7, -184($fp)
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, 0($t8)
    addiu $sp, $sp, -4
    sw $t0, -188($fp)
    lw $a0, -188($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -192($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -196($fp)
    la $t8, arr_arregloInt
    lw $t6, -192($fp)
    lw $t7, -196($fp)
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, 0($t8)
    addiu $sp, $sp, -4
    sw $t0, -200($fp)
    lw $a0, -200($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -204($fp)
    li $t0, 2
    addiu $sp, $sp, -4
    sw $t0, -208($fp)
    la $t8, arr_arregloInt
    lw $t6, -204($fp)
    lw $t7, -208($fp)
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, 0($t8)
    addiu $sp, $sp, -4
    sw $t0, -212($fp)
    lw $a0, -212($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -216($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -220($fp)
    la $t8, arr_arregloInt
    lw $t6, -216($fp)
    lw $t7, -220($fp)
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, 0($t8)
    addiu $sp, $sp, -4
    sw $t0, -224($fp)
    lw $a0, -224($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -228($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -232($fp)
    la $t8, arr_arregloInt
    lw $t6, -228($fp)
    lw $t7, -232($fp)
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, 0($t8)
    addiu $sp, $sp, -4
    sw $t0, -236($fp)
    lw $a0, -236($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -240($fp)
    li $t0, 2
    addiu $sp, $sp, -4
    sw $t0, -244($fp)
    la $t8, arr_arregloInt
    lw $t6, -240($fp)
    lw $t7, -244($fp)
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, 0($t8)
    addiu $sp, $sp, -4
    sw $t0, -248($fp)
    lw $a0, -248($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    li $t9, 0x3fc00000
    mtc1 $t9, $f0
    addiu $sp, $sp, -4
    s.s $f0, -252($fp)
    addiu $sp, $sp, -4  # x (float) @ -256($fp)
    l.s $f0, -252($fp)
    s.s $f0, -256($fp)
    li $t9, 0x40200000
    mtc1 $t9, $f0
    addiu $sp, $sp, -4
    s.s $f0, -260($fp)
    addiu $sp, $sp, -4  # y (float) @ -264($fp)
    l.s $f0, -260($fp)
    s.s $f0, -264($fp)
    li $t9, 0x40600000
    mtc1 $t9, $f0
    addiu $sp, $sp, -4
    s.s $f0, -268($fp)
    addiu $sp, $sp, -4  # z (float) @ -272($fp)
    l.s $f0, -268($fp)
    s.s $f0, -272($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -276($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -280($fp)
    l.s $f0, -256($fp)
    addiu $sp, $sp, -4
    s.s $f0, -284($fp)
    l.s $f0, -264($fp)
    addiu $sp, $sp, -4
    s.s $f0, -288($fp)
    l.s $f0, -272($fp)
    addiu $sp, $sp, -4
    s.s $f0, -292($fp)
    l.s $f12, -284($fp)
    l.s $f14, -288($fp)
    l.s $f16, -292($fp)
    jal promedioFlotante
    addiu $sp, $sp, -4
    s.s $f0, -296($fp)
    la $t8, arr_arregloFloat
    lw $t6, -276($fp)
    lw $t7, -280($fp)
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    l.s $f0, -296($fp)
    s.s $f0, 0($t8)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -300($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -304($fp)
    l.s $f0, -264($fp)
    addiu $sp, $sp, -4
    s.s $f0, -308($fp)
    l.s $f0, -272($fp)
    addiu $sp, $sp, -4
    s.s $f0, -312($fp)
    l.s $f0, -256($fp)
    addiu $sp, $sp, -4
    s.s $f0, -316($fp)
    l.s $f12, -308($fp)
    l.s $f14, -312($fp)
    l.s $f16, -316($fp)
    jal promedioFlotante
    addiu $sp, $sp, -4
    s.s $f0, -320($fp)
    la $t8, arr_arregloFloat
    lw $t6, -300($fp)
    lw $t7, -304($fp)
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    l.s $f0, -320($fp)
    s.s $f0, 0($t8)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -324($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -328($fp)
    l.s $f0, -272($fp)
    addiu $sp, $sp, -4
    s.s $f0, -332($fp)
    l.s $f0, -256($fp)
    addiu $sp, $sp, -4
    s.s $f0, -336($fp)
    l.s $f0, -264($fp)
    addiu $sp, $sp, -4
    s.s $f0, -340($fp)
    l.s $f12, -332($fp)
    l.s $f14, -336($fp)
    l.s $f16, -340($fp)
    jal promedioFlotante
    addiu $sp, $sp, -4
    s.s $f0, -344($fp)
    la $t8, arr_arregloFloat
    lw $t6, -324($fp)
    lw $t7, -328($fp)
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    l.s $f0, -344($fp)
    s.s $f0, 0($t8)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -348($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -352($fp)
    l.s $f0, -256($fp)
    addiu $sp, $sp, -4
    s.s $f0, -356($fp)
    l.s $f0, -256($fp)
    addiu $sp, $sp, -4
    s.s $f0, -360($fp)
    l.s $f0, -256($fp)
    addiu $sp, $sp, -4
    s.s $f0, -364($fp)
    l.s $f12, -356($fp)
    l.s $f14, -360($fp)
    l.s $f16, -364($fp)
    jal promedioFlotante
    addiu $sp, $sp, -4
    s.s $f0, -368($fp)
    la $t8, arr_arregloFloat
    lw $t6, -348($fp)
    lw $t7, -352($fp)
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    l.s $f0, -368($fp)
    s.s $f0, 0($t8)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -372($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -376($fp)
    la $t8, arr_arregloFloat
    lw $t6, -372($fp)
    lw $t7, -376($fp)
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    l.s $f0, 0($t8)
    addiu $sp, $sp, -4
    s.s $f0, -380($fp)
    l.s $f12, -380($fp)
    li $v0, 2
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -384($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -388($fp)
    la $t8, arr_arregloFloat
    lw $t6, -384($fp)
    lw $t7, -388($fp)
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    l.s $f0, 0($t8)
    addiu $sp, $sp, -4
    s.s $f0, -392($fp)
    l.s $f12, -392($fp)
    li $v0, 2
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -396($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -400($fp)
    la $t8, arr_arregloFloat
    lw $t6, -396($fp)
    lw $t7, -400($fp)
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    l.s $f0, 0($t8)
    addiu $sp, $sp, -4
    s.s $f0, -404($fp)
    l.s $f12, -404($fp)
    li $v0, 2
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -408($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -412($fp)
    la $t8, arr_arregloFloat
    lw $t6, -408($fp)
    lw $t7, -412($fp)
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    l.s $f0, 0($t8)
    addiu $sp, $sp, -4
    s.s $f0, -416($fp)
    l.s $f12, -416($fp)
    li $v0, 2
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    lw $t0, -8($fp)
    addiu $sp, $sp, -4
    sw $t0, -420($fp)
    lw $t0, -16($fp)
    addiu $sp, $sp, -4
    sw $t0, -424($fp)
    lw $a0, -420($fp)
    lw $a1, -424($fp)
    jal multiplicar
    addiu $sp, $sp, -4
    sw $v0, -428($fp)
    lw $t0, -16($fp)
    addiu $sp, $sp, -4
    sw $t0, -432($fp)
    lw $t0, -24($fp)
    addiu $sp, $sp, -4
    sw $t0, -436($fp)
    lw $a0, -432($fp)
    lw $a1, -436($fp)
    jal multiplicar
    addiu $sp, $sp, -4
    sw $v0, -440($fp)
    lw $a0, -428($fp)
    lw $a1, -440($fp)
    jal suma
    addiu $sp, $sp, -4
    sw $v0, -444($fp)
    lw $t0, -8($fp)
    addiu $sp, $sp, -4
    sw $t0, -448($fp)
    lw $t0, -24($fp)
    addiu $sp, $sp, -4
    sw $t0, -452($fp)
    lw $a0, -448($fp)
    lw $a1, -452($fp)
    jal suma
    addiu $sp, $sp, -4
    sw $v0, -456($fp)
    lw $t0, -444($fp)
    lw $t1, -456($fp)
    add $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -460($fp)
    addiu $sp, $sp, -4  # resultadoInt (int) @ -464($fp)
    lw $t0, -460($fp)
    sw $t0, -464($fp)
    lw $t0, -464($fp)
    addiu $sp, $sp, -4
    sw $t0, -468($fp)
    lw $a0, -468($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    l.s $f0, -256($fp)
    addiu $sp, $sp, -4
    s.s $f0, -472($fp)
    l.s $f0, -264($fp)
    addiu $sp, $sp, -4
    s.s $f0, -476($fp)
    l.s $f0, -272($fp)
    addiu $sp, $sp, -4
    s.s $f0, -480($fp)
    l.s $f12, -472($fp)
    l.s $f14, -476($fp)
    l.s $f16, -480($fp)
    jal promedioFlotante
    addiu $sp, $sp, -4
    s.s $f0, -484($fp)
    l.s $f0, -272($fp)
    addiu $sp, $sp, -4
    s.s $f0, -488($fp)
    l.s $f0, -264($fp)
    addiu $sp, $sp, -4
    s.s $f0, -492($fp)
    l.s $f0, -256($fp)
    addiu $sp, $sp, -4
    s.s $f0, -496($fp)
    l.s $f12, -488($fp)
    l.s $f14, -492($fp)
    l.s $f16, -496($fp)
    jal promedioFlotante
    addiu $sp, $sp, -4
    s.s $f0, -500($fp)
    l.s $f0, -484($fp)
    l.s $f1, -500($fp)
    add.s $f2, $f0, $f1
    addiu $sp, $sp, -4
    s.s $f2, -504($fp)
    addiu $sp, $sp, -4  # resultadoFloat (float) @ -508($fp)
    l.s $f0, -504($fp)
    s.s $f0, -508($fp)
    l.s $f0, -508($fp)
    addiu $sp, $sp, -4
    s.s $f0, -512($fp)
    l.s $f12, -512($fp)
    li $v0, 2
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -516($fp)
    addiu $sp, $sp, -4  # contador (int) @ -520($fp)
    lw $t0, -516($fp)
    sw $t0, -520($fp)

do_start_1:
    lw $t0, -520($fp)
    addiu $sp, $sp, -4
    sw $t0, -524($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -528($fp)
    lw $t0, -524($fp)
    lw $t1, -528($fp)
    add $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -532($fp)
    lw $t0, -532($fp)
    sw $t0, -520($fp)
    lw $t0, -520($fp)
    addiu $sp, $sp, -4
    sw $t0, -536($fp)
    lw $a0, -536($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    lw $t0, -520($fp)
    addiu $sp, $sp, -4
    sw $t0, -540($fp)
    li $t0, 5
    addiu $sp, $sp, -4
    sw $t0, -544($fp)
    lw $t0, -540($fp)
    lw $t1, -544($fp)
    slt $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -548($fp)
    lw $t0, -548($fp)
    bne $t0, $zero, do_start_1

do_end_1:
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -552($fp)
    addiu $sp, $sp, -4  # i (int) @ -556($fp)
    lw $t0, -552($fp)
    sw $t0, -556($fp)

do_start_2:
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -560($fp)
    lw $t0, -556($fp)
    addiu $sp, $sp, -4
    sw $t0, -564($fp)
    la $t8, arr_arregloInt
    lw $t6, -560($fp)
    lw $t7, -564($fp)
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, 0($t8)
    addiu $sp, $sp, -4
    sw $t0, -568($fp)
    addiu $sp, $sp, -4  # fila (int) @ -572($fp)
    lw $t0, -568($fp)
    sw $t0, -572($fp)
    lw $t0, -572($fp)
    addiu $sp, $sp, -4
    sw $t0, -576($fp)
    lw $a0, -576($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    lw $t0, -556($fp)
    addiu $sp, $sp, -4
    sw $t0, -580($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -584($fp)
    lw $t0, -580($fp)
    lw $t1, -584($fp)
    add $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -588($fp)
    lw $t0, -588($fp)
    sw $t0, -556($fp)
    lw $t0, -556($fp)
    addiu $sp, $sp, -4
    sw $t0, -592($fp)
    li $t0, 3
    addiu $sp, $sp, -4
    sw $t0, -596($fp)
    lw $t0, -592($fp)
    lw $t1, -596($fp)
    slt $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -600($fp)
    lw $t0, -600($fp)
    bne $t0, $zero, do_start_2

do_end_2:
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -604($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -608($fp)
    la $t8, arr_arregloInt
    lw $t6, -604($fp)
    lw $t7, -608($fp)
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, 0($t8)
    addiu $sp, $sp, -4
    sw $t0, -612($fp)
    li $t0, 30
    addiu $sp, $sp, -4
    sw $t0, -616($fp)
    lw $t0, -612($fp)
    lw $t1, -616($fp)
    seq $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -620($fp)
    lw $t0, -620($fp)
    beq $t0, $zero, if_else_1
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -624($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -628($fp)
    la $t8, arr_arregloInt
    lw $t6, -624($fp)
    lw $t7, -628($fp)
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, 0($t8)
    addiu $sp, $sp, -4
    sw $t0, -632($fp)
    lw $a0, -632($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    j if_end_1

if_else_1:

if_end_1:
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -636($fp)
    li $t0, 2
    addiu $sp, $sp, -4
    sw $t0, -640($fp)
    la $t8, arr_arregloInt
    lw $t6, -636($fp)
    lw $t7, -640($fp)
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, 0($t8)
    addiu $sp, $sp, -4
    sw $t0, -644($fp)
    addiu $sp, $sp, -4  # sw (int) @ -648($fp)
    lw $t0, -644($fp)
    sw $t0, -648($fp)
    lw $t0, -648($fp)
    addiu $sp, $sp, -4
    sw $t0, -652($fp)

switch_case_1:
    lw $t0, -652($fp)
    li $t1, 200
    seq $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -656($fp)
    lw $t0, -656($fp)
    beq $t0, $zero, switch_case_2
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -660($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -664($fp)
    la $t8, arr_arregloInt
    lw $t6, -660($fp)
    lw $t7, -664($fp)
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, 0($t8)
    addiu $sp, $sp, -4
    sw $t0, -668($fp)
    lw $a0, -668($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    j switch_end_1

switch_case_2:
    lw $t0, -652($fp)
    li $t1, 500
    seq $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -672($fp)
    lw $t0, -672($fp)
    beq $t0, $zero, switch_case_3
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -676($fp)
    li $t0, 2
    addiu $sp, $sp, -4
    sw $t0, -680($fp)
    la $t8, arr_arregloInt
    lw $t6, -676($fp)
    lw $t7, -680($fp)
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, 0($t8)
    addiu $sp, $sp, -4
    sw $t0, -684($fp)
    lw $a0, -684($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    j switch_end_1

switch_case_3:

switch_end_1:

main_end:
    li $v0, 10
    syscall
