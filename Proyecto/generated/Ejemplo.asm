.data
    newline: .asciiz "\n"
    .align 2
    arr_mat: .space 24
    .align 2
    arr_fmat: .space 16

.text

main:
    la $t0, arr_mat
    move $fp, $sp
    li $t0, 10
    addiu $sp, $sp, -4
    sw $t0, -4($fp)
    addiu $sp, $sp, -4  # a (int) @ -8($fp)
    lw $t0, -4($fp)
    sw $t0, -8($fp)
    li $t0, 3
    addiu $sp, $sp, -4
    sw $t0, -12($fp)
    addiu $sp, $sp, -4  # b (int) @ -16($fp)
    lw $t0, -12($fp)
    sw $t0, -16($fp)
    addiu $sp, $sp, -4  # suma (int) @ -20($fp)
    lw $t0, -8($fp)
    addiu $sp, $sp, -4
    sw $t0, -24($fp)
    lw $t0, -16($fp)
    addiu $sp, $sp, -4
    sw $t0, -28($fp)
    lw $t0, -24($fp)
    lw $t1, -28($fp)
    add $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -32($fp)
    lw $t0, -32($fp)
    sw $t0, -20($fp)
    addiu $sp, $sp, -4  # resta (int) @ -36($fp)
    lw $t0, -8($fp)
    addiu $sp, $sp, -4
    sw $t0, -40($fp)
    lw $t0, -16($fp)
    addiu $sp, $sp, -4
    sw $t0, -44($fp)
    lw $t0, -40($fp)
    lw $t1, -44($fp)
    sub $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -48($fp)
    lw $t0, -48($fp)
    sw $t0, -36($fp)
    addiu $sp, $sp, -4  # mult (int) @ -52($fp)
    lw $t0, -8($fp)
    addiu $sp, $sp, -4
    sw $t0, -56($fp)
    lw $t0, -16($fp)
    addiu $sp, $sp, -4
    sw $t0, -60($fp)
    lw $t0, -56($fp)
    lw $t1, -60($fp)
    mul $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -64($fp)
    lw $t0, -64($fp)
    sw $t0, -52($fp)
    addiu $sp, $sp, -4  # div (int) @ -68($fp)
    lw $t0, -8($fp)
    addiu $sp, $sp, -4
    sw $t0, -72($fp)
    lw $t0, -16($fp)
    addiu $sp, $sp, -4
    sw $t0, -76($fp)
    lw $t0, -72($fp)
    lw $t1, -76($fp)
    div $t0, $t1
    mflo $t2
    addiu $sp, $sp, -4
    sw $t2, -80($fp)
    lw $t0, -80($fp)
    sw $t0, -68($fp)
    addiu $sp, $sp, -4  # mod (int) @ -84($fp)
    lw $t0, -8($fp)
    addiu $sp, $sp, -4
    sw $t0, -88($fp)
    lw $t0, -16($fp)
    addiu $sp, $sp, -4
    sw $t0, -92($fp)
    lw $t0, -88($fp)
    lw $t1, -92($fp)
    div $t0, $t1
    mfhi $t2
    addiu $sp, $sp, -4
    sw $t2, -96($fp)
    lw $t0, -96($fp)
    sw $t0, -84($fp)
    li $t9, 0x40900000
    mtc1 $t9, $f0
    addiu $sp, $sp, -4
    s.s $f0, -100($fp)
    addiu $sp, $sp, -4  # x (float) @ -104($fp)
    l.s $f0, -100($fp)
    s.s $f0, -104($fp)
    li $t9, 0x3fc00000
    mtc1 $t9, $f0
    addiu $sp, $sp, -4
    s.s $f0, -108($fp)
    addiu $sp, $sp, -4  # y (float) @ -112($fp)
    l.s $f0, -108($fp)
    s.s $f0, -112($fp)
    addiu $sp, $sp, -4  # fsuma (float) @ -116($fp)
    l.s $f0, -104($fp)
    addiu $sp, $sp, -4
    s.s $f0, -120($fp)
    l.s $f0, -112($fp)
    addiu $sp, $sp, -4
    s.s $f0, -124($fp)
    l.s $f0, -120($fp)
    l.s $f1, -124($fp)
    add.s $f2, $f0, $f1
    addiu $sp, $sp, -4
    s.s $f2, -128($fp)
    l.s $f0, -128($fp)
    s.s $f0, -116($fp)
    addiu $sp, $sp, -4  # fresta (float) @ -132($fp)
    l.s $f0, -104($fp)
    addiu $sp, $sp, -4
    s.s $f0, -136($fp)
    l.s $f0, -112($fp)
    addiu $sp, $sp, -4
    s.s $f0, -140($fp)
    l.s $f0, -136($fp)
    l.s $f1, -140($fp)
    sub.s $f2, $f0, $f1
    addiu $sp, $sp, -4
    s.s $f2, -144($fp)
    l.s $f0, -144($fp)
    s.s $f0, -132($fp)
    addiu $sp, $sp, -4  # fmult (float) @ -148($fp)
    l.s $f0, -104($fp)
    addiu $sp, $sp, -4
    s.s $f0, -152($fp)
    l.s $f0, -112($fp)
    addiu $sp, $sp, -4
    s.s $f0, -156($fp)
    l.s $f0, -152($fp)
    l.s $f1, -156($fp)
    mul.s $f2, $f0, $f1
    addiu $sp, $sp, -4
    s.s $f2, -160($fp)
    l.s $f0, -160($fp)
    s.s $f0, -148($fp)
    addiu $sp, $sp, -4  # fdiv (float) @ -164($fp)
    l.s $f0, -104($fp)
    addiu $sp, $sp, -4
    s.s $f0, -168($fp)
    l.s $f0, -112($fp)
    addiu $sp, $sp, -4
    s.s $f0, -172($fp)
    l.s $f0, -168($fp)
    l.s $f1, -172($fp)
    div.s $f2, $f0, $f1
    addiu $sp, $sp, -4
    s.s $f2, -176($fp)
    l.s $f0, -176($fp)
    s.s $f0, -164($fp)
    la $t8, arr_mat
    li $t6, 0
    li $t7, 0
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    li $t0, 1
    sw $t0, 0($t8)
    la $t8, arr_mat
    li $t6, 0
    li $t7, 1
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    li $t0, 2
    sw $t0, 0($t8)
    la $t8, arr_mat
    li $t6, 0
    li $t7, 2
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    li $t0, 3
    sw $t0, 0($t8)
    la $t8, arr_mat
    li $t6, 1
    li $t7, 0
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    li $t0, 4
    sw $t0, 0($t8)
    la $t8, arr_mat
    li $t6, 1
    li $t7, 1
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    li $t0, 5
    sw $t0, 0($t8)
    la $t8, arr_mat
    li $t6, 1
    li $t7, 2
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    li $t0, 6
    sw $t0, 0($t8)
    addiu $sp, $sp, -4  # celda (int) @ -180($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -184($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -188($fp)
    la $t8, arr_mat
    lw $t6, -184($fp)
    lw $t7, -188($fp)
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, 0($t8)
    addiu $sp, $sp, -4
    sw $t0, -192($fp)
    lw $t0, -192($fp)
    sw $t0, -180($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -196($fp)
    li $t0, 2
    addiu $sp, $sp, -4
    sw $t0, -200($fp)
    la $t8, arr_mat
    lw $t6, -196($fp)
    lw $t7, -200($fp)
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, 0($t8)
    addiu $sp, $sp, -4
    sw $t0, -204($fp)
    lw $t0, -204($fp)
    sw $t0, -180($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -208($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -212($fp)
    la $t8, arr_mat
    lw $t6, -208($fp)
    lw $t7, -212($fp)
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, 0($t8)
    addiu $sp, $sp, -4
    sw $t0, -216($fp)
    lw $t0, -216($fp)
    sw $t0, -180($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -220($fp)
    li $t0, 2
    addiu $sp, $sp, -4
    sw $t0, -224($fp)
    li $t0, 99
    addiu $sp, $sp, -4
    sw $t0, -228($fp)
    la $t8, arr_mat
    lw $t6, -220($fp)
    lw $t7, -224($fp)
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, -228($fp)
    sw $t0, 0($t8)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -232($fp)
    li $t0, 2
    addiu $sp, $sp, -4
    sw $t0, -236($fp)
    la $t8, arr_mat
    lw $t6, -232($fp)
    lw $t7, -236($fp)
    li $t5, 3
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    lw $t0, 0($t8)
    addiu $sp, $sp, -4
    sw $t0, -240($fp)
    lw $t0, -240($fp)
    sw $t0, -180($fp)
    la $t8, arr_fmat
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
    la $t8, arr_fmat
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
    la $t8, arr_fmat
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
    la $t8, arr_fmat
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
    addiu $sp, $sp, -4  # fcelda (float) @ -244($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -248($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -252($fp)
    la $t8, arr_fmat
    lw $t6, -248($fp)
    lw $t7, -252($fp)
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    l.s $f0, 0($t8)
    addiu $sp, $sp, -4
    s.s $f0, -256($fp)
    l.s $f0, -256($fp)
    s.s $f0, -244($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -260($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -264($fp)
    la $t8, arr_fmat
    lw $t6, -260($fp)
    lw $t7, -264($fp)
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    l.s $f0, 0($t8)
    addiu $sp, $sp, -4
    s.s $f0, -268($fp)
    l.s $f0, -268($fp)
    s.s $f0, -244($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -272($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -276($fp)
    li $t9, 0x411e6666
    mtc1 $t9, $f0
    addiu $sp, $sp, -4
    s.s $f0, -280($fp)
    la $t8, arr_fmat
    lw $t6, -272($fp)
    lw $t7, -276($fp)
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    l.s $f0, -280($fp)
    s.s $f0, 0($t8)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -284($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -288($fp)
    la $t8, arr_fmat
    lw $t6, -284($fp)
    lw $t7, -288($fp)
    li $t5, 2
    mul $t5, $t6, $t5
    add $t5, $t5, $t7
    sll $t5, $t5, 2
    add $t8, $t8, $t5
    l.s $f0, 0($t8)
    addiu $sp, $sp, -4
    s.s $f0, -292($fp)
    l.s $f0, -292($fp)
    s.s $f0, -244($fp)

main_end:
    li $v0, 10
    syscall
