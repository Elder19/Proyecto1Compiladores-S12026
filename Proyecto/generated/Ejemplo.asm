.data
    newline: .asciiz "\n"

.text

main:
    move $fp, $sp
    li $t0, 5
    addiu $sp, $sp, -4
    sw $t0, -4($fp)
    # var x en offset -8($fp)
    addiu $sp, $sp, -4
    lw $t0, -4($fp)
    sw $t0, -8($fp)
    lw $t0, -8($fp)
    addiu $sp, $sp, -4
    sw $t0, -12($fp)
    lw $a0, -12($fp)
    li $v0, 1
    syscall
    la $a0, newline
    li $v0, 4
    syscall

main_end:
    li $v0, 10
    syscall
