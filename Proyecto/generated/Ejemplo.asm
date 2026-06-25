.data
    newline: .asciiz "\n"

.text
.globl main

f1:
    addiu $sp, $sp, -4
    lw $t0, -4($fp)
    addiu $sp, $sp, -4
    sw $t0, -8($fp)

main:
    move $fp, $sp
    li $t0, 10
    addiu $sp, $sp, -4
    sw $t0, -12($fp)
    addiu $sp, $sp, -4
    lw $t0, -16($fp)
    addiu $sp, $sp, -4
    sw $t0, -20($fp)
    addiu $sp, $sp, -4  # z (int) @ -24($fp)
    lw $t0, -20($fp)
    sw $t0, -24($fp)

main_end:
    li $v0, 10
    syscall
