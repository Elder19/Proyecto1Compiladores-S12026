.data
    newline: .asciiz "\n"

.text
.globl main

__potencia_int:
    addiu $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    li $v0, 1
    ble $a1, $zero, __pot_int_fin
__pot_int_loop:
    mul $v0, $v0, $a0
    addiu $a1, $a1, -1
    bgt $a1, $zero, __pot_int_loop
__pot_int_fin:
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addiu $sp, $sp, 8
    jr $ra

__potencia_float:
    addiu $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    li $t9, 0x3f800000
    mtc1 $t9, $f0
    cvt.w.s $f14, $f14
    mfc1 $t0, $f14
    ble $t0, $zero, __pot_float_fin
__pot_float_loop:
    mul.s $f0, $f0, $f12
    addiu $t0, $t0, -1
    bgt $t0, $zero, __pot_float_loop
__pot_float_fin:
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addiu $sp, $sp, 8
    jr $ra

main:
    move $fp, $sp
    li $t0, 2
    addiu $sp, $sp, -4
    sw $t0, -4($fp)
    addiu $sp, $sp, -4  # base (int) @ -8($fp)
    lw $t0, -4($fp)
    sw $t0, -8($fp)
    li $t0, 3
    addiu $sp, $sp, -4
    sw $t0, -12($fp)
    addiu $sp, $sp, -4  # exp (int) @ -16($fp)
    lw $t0, -12($fp)
    sw $t0, -16($fp)
    addiu $sp, $sp, -4  # resultado (int) @ -20($fp)
    lw $t0, -8($fp)
    addiu $sp, $sp, -4
    sw $t0, -24($fp)
    lw $t0, -16($fp)
    addiu $sp, $sp, -4
    sw $t0, -28($fp)
    lw $a0, -24($fp)
    lw $a1, -28($fp)
    jal __potencia_int
    addiu $sp, $sp, -4
    sw $v0, -32($fp)
    lw $t0, -32($fp)
    sw $t0, -20($fp)
    lw $t0, -20($fp)
    addiu $sp, $sp, -4
    sw $t0, -36($fp)
    lw $a0, -36($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall

main_end:
    li $v0, 10
    syscall
