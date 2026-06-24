.data
    newline: .asciiz "\n"

.text
.globl main

main:
    move $fp, $sp
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -4($fp)
    addiu $sp, $sp, -4  # i (int) @ -8($fp)
    lw $t0, -4($fp)
    sw $t0, -8($fp)
    li $t0, 3
    addiu $sp, $sp, -4
    sw $t0, -12($fp)
    addiu $sp, $sp, -4  # limite (int) @ -16($fp)
    lw $t0, -12($fp)
    sw $t0, -16($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -20($fp)
    addiu $sp, $sp, -4  # resultado (int) @ -24($fp)
    lw $t0, -20($fp)
    sw $t0, -24($fp)

do_start_1:
    lw $t0, -8($fp)
    addiu $sp, $sp, -4
    sw $t0, -28($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -32($fp)
    lw $t0, -28($fp)
    lw $t1, -32($fp)
    add $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -36($fp)
    lw $t0, -36($fp)
    sw $t0, -8($fp)
    lw $t0, -8($fp)
    addiu $sp, $sp, -4
    sw $t0, -40($fp)
    lw $t0, -40($fp)
    sw $t0, -24($fp)
    lw $t0, -8($fp)
    addiu $sp, $sp, -4
    sw $t0, -44($fp)
    lw $t0, -16($fp)
    addiu $sp, $sp, -4
    sw $t0, -48($fp)
    lw $t0, -44($fp)
    lw $t1, -48($fp)
    slt $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -52($fp)
    lw $t0, -52($fp)
    bne $t0, $zero, do_start_1

do_end_1:

main_end:
    li $v0, 10
    syscall
