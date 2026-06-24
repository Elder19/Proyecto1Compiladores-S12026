.data
    newline: .asciiz "\n"

.text
.globl main

main:
    move $fp, $sp
    li $t0, 5
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
    li $t0, 10
    addiu $sp, $sp, -4
    sw $t0, -20($fp)
    addiu $sp, $sp, -4  # c (int) @ -24($fp)
    lw $t0, -20($fp)
    sw $t0, -24($fp)
    addiu $sp, $sp, -4  # rAnd (bool) @ -28($fp)
    addiu $sp, $sp, -4  # rOr (bool) @ -32($fp)
    addiu $sp, $sp, -4  # rNot (bool) @ -36($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -40($fp)
    addiu $sp, $sp, -4  # resultado (int) @ -44($fp)
    lw $t0, -40($fp)
    sw $t0, -44($fp)
    lw $t0, -8($fp)
    addiu $sp, $sp, -4
    sw $t0, -48($fp)
    lw $t0, -16($fp)
    addiu $sp, $sp, -4
    sw $t0, -52($fp)
    lw $t0, -48($fp)
    lw $t1, -52($fp)
    sgt $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -56($fp)
    lw $t0, -16($fp)
    addiu $sp, $sp, -4
    sw $t0, -60($fp)
    lw $t0, -24($fp)
    addiu $sp, $sp, -4
    sw $t0, -64($fp)
    lw $t0, -60($fp)
    lw $t1, -64($fp)
    slt $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -68($fp)
    lw $t0, -56($fp)
    lw $t1, -68($fp)
    sne $t0, $t0, $zero
    sne $t1, $t1, $zero
    and $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -72($fp)
    lw $t0, -72($fp)
    sw $t0, -28($fp)
    lw $t0, -8($fp)
    addiu $sp, $sp, -4
    sw $t0, -76($fp)
    lw $t0, -16($fp)
    addiu $sp, $sp, -4
    sw $t0, -80($fp)
    lw $t0, -76($fp)
    lw $t1, -80($fp)
    slt $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -84($fp)
    lw $t0, -24($fp)
    addiu $sp, $sp, -4
    sw $t0, -88($fp)
    lw $t0, -16($fp)
    addiu $sp, $sp, -4
    sw $t0, -92($fp)
    lw $t0, -88($fp)
    lw $t1, -92($fp)
    sgt $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -96($fp)
    lw $t0, -84($fp)
    lw $t1, -96($fp)
    sne $t0, $t0, $zero
    sne $t1, $t1, $zero
    or $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -100($fp)
    lw $t0, -100($fp)
    sw $t0, -32($fp)
    lw $t0, -8($fp)
    addiu $sp, $sp, -4
    sw $t0, -104($fp)
    lw $t0, -24($fp)
    addiu $sp, $sp, -4
    sw $t0, -108($fp)
    lw $t0, -104($fp)
    lw $t1, -108($fp)
    sgt $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -112($fp)
    lw $t0, -112($fp)
    seq $t2, $t0, $zero
    addiu $sp, $sp, -4
    sw $t2, -116($fp)
    lw $t0, -116($fp)
    sw $t0, -36($fp)
    lw $t0, -28($fp)
    addiu $sp, $sp, -4
    sw $t0, -120($fp)
    lw $t0, -32($fp)
    addiu $sp, $sp, -4
    sw $t0, -124($fp)
    lw $t0, -36($fp)
    addiu $sp, $sp, -4
    sw $t0, -128($fp)
    lw $t0, -28($fp)
    addiu $sp, $sp, -4
    sw $t0, -132($fp)
    lw $t0, -36($fp)
    addiu $sp, $sp, -4
    sw $t0, -136($fp)
    lw $t0, -132($fp)
    lw $t1, -136($fp)
    sne $t0, $t0, $zero
    sne $t1, $t1, $zero
    and $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -140($fp)
    lw $t0, -140($fp)
    beq $t0, $zero, if_else_1
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -144($fp)
    lw $t0, -144($fp)
    sw $t0, -44($fp)
    j if_end_1

if_else_1:
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -148($fp)
    lw $t0, -148($fp)
    sw $t0, -44($fp)

if_end_1:
    lw $t0, -44($fp)
    addiu $sp, $sp, -4
    sw $t0, -152($fp)

    lw $a0, -44($fp)
    li $v0, 1
    syscall

    la $a0, newline
    li $v0, 4
    syscall

main_end:
    li $v0, 10
    syscall
