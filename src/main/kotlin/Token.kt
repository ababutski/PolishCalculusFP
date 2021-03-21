package polishcalc

enum class TokenType{
    NONE,
    NUMBER,
    OP_PLUS,
    OP_UNO_PLUS,
    OP_MINUS,
    OP_UNO_MINUS,
    OP_MULT,
    OP_DIV,
    OP_PAREHTESES_OPEN,
    OP_PAREHTESES_CLOSE
}

class Token(var type: TokenType, var value: Float = 0f) {
    fun isnumber():Boolean = type == TokenType.NUMBER
    fun isoperator():Boolean =
            type == TokenType.OP_PLUS
            || type == TokenType.OP_UNO_PLUS
            || type == TokenType.OP_MINUS
            || type == TokenType.OP_UNO_MINUS
            || type == TokenType.OP_MULT
            || type == TokenType.OP_DIV
}