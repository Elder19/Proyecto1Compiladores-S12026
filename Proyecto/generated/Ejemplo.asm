.data
    newline: .asciiz "\n"
    str_0: .asciiz "Ingrese un numero del 1 al 10:"
    str_1: .asciiz "Numero valido:"
    str_2: .asciiz "El numero es mayor que 5"
    str_3: .asciiz "El numero es menor o igual que 5"
    str_4: .asciiz "Contando hasta el numero ingresado:"
    str_5: .asciiz "La suma total es:"
    str_6: .asciiz "Numero fuera de rango"

.text
.globl main

main:
    move $fp, $sp
    addiu $sp, $sp, -4  # numero (int) @ -4($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -8($fp)
    addiu $sp, $sp, -4  # contador (int) @ -12($fp)
    lw $t0, -8($fp)
    sw $t0, -12($fp)
    li $t0, 0
    addiu $sp, $sp, -4
    sw $t0, -16($fp)
    addiu $sp, $sp, -4  # suma (int) @ -20($fp)
    lw $t0, -16($fp)
    sw $t0, -20($fp)
    addiu $sp, $sp, -4  # valido (bool) @ -24($fp)
    la $a0, str_0
    li $v0, 4
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    li $v0, 5
    syscall
    sw $v0, -4($fp)
    lw $t0, -4($fp)
    addiu $sp, $sp, -4
    sw $t0, -28($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -32($fp)
    lw $t0, -28($fp)
    lw $t1, -32($fp)
    sge $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -36($fp)
    lw $t0, -4($fp)
    addiu $sp, $sp, -4
    sw $t0, -40($fp)
    li $t0, 10
    addiu $sp, $sp, -4
    sw $t0, -44($fp)
    lw $t0, -40($fp)
    lw $t1, -44($fp)
    sle $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -48($fp)
    lw $t0, -36($fp)
    lw $t1, -48($fp)
    sne $t0, $t0, $zero
    sne $t1, $t1, $zero
    and $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -52($fp)
    lw $t0, -52($fp)
    sw $t0, -24($fp)
    lw $t0, -24($fp)
    addiu $sp, $sp, -4
    sw $t0, -56($fp)
    lw $t0, -56($fp)
    beq $t0, $zero, if_else_1
    la $a0, str_1
    li $v0, 4
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    lw $t0, -4($fp)
    addiu $sp, $sp, -4
    sw $t0, -60($fp)
    lw $a0, -60($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    lw $t0, -4($fp)
    addiu $sp, $sp, -4
    sw $t0, -64($fp)
    li $t0, 5
    addiu $sp, $sp, -4
    sw $t0, -68($fp)
    lw $t0, -64($fp)
    lw $t1, -68($fp)
    sgt $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -72($fp)
    lw $t0, -72($fp)
    beq $t0, $zero, if_else_2
    la $a0, str_2
    li $v0, 4
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    j if_end_2

if_else_2:
    la $a0, str_3
    li $v0, 4
    syscall
    la $a0, newline
    li $v0, 4
    syscall

if_end_2:
    la $a0, str_4
    li $v0, 4
    syscall
    la $a0, newline
    li $v0, 4
    syscall

do_start_1:
    lw $t0, -12($fp)
    addiu $sp, $sp, -4
    sw $t0, -76($fp)
    lw $a0, -76($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    lw $t0, -20($fp)
    addiu $sp, $sp, -4
    sw $t0, -80($fp)
    lw $t0, -12($fp)
    addiu $sp, $sp, -4
    sw $t0, -84($fp)
    lw $t0, -80($fp)
    lw $t1, -84($fp)
    add $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -88($fp)
    lw $t0, -88($fp)
    sw $t0, -20($fp)
    lw $t0, -12($fp)
    addiu $sp, $sp, -4
    sw $t0, -92($fp)
    li $t0, 1
    addiu $sp, $sp, -4
    sw $t0, -96($fp)
    lw $t0, -92($fp)
    lw $t1, -96($fp)
    add $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -100($fp)
    lw $t0, -100($fp)
    sw $t0, -12($fp)
    lw $t0, -12($fp)
    addiu $sp, $sp, -4
    sw $t0, -104($fp)
    lw $t0, -4($fp)
    addiu $sp, $sp, -4
    sw $t0, -108($fp)
    lw $t0, -104($fp)
    lw $t1, -108($fp)
    sle $t2, $t0, $t1
    addiu $sp, $sp, -4
    sw $t2, -112($fp)
    lw $t0, -112($fp)
    bne $t0, $zero, do_start_1

do_end_1:
    la $a0, str_5
    li $v0, 4
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    lw $t0, -20($fp)
    addiu $sp, $sp, -4
    sw $t0, -116($fp)
    lw $a0, -116($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall
    j if_end_1

if_else_1:
    la $a0, str_6
    li $v0, 4
    syscall
    la $a0, newline
    li $v0, 4
    syscall

if_end_1:

main_end:
    li $v0, 10
    syscall
