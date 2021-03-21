package polishcalc

import arrow.core.Tuple2
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.applicative.just
import arrow.fx.extensions.io.unsafeRun.runBlocking
import arrow.unsafe

val OPERATORS_SYMBOLS = mapOf(
    TokenType.OP_PLUS to "+",
    TokenType.OP_MINUS to "-",
    TokenType.OP_MULT to "*",
    TokenType.OP_DIV to "/",
    TokenType.OP_UNO_MINUS to "-u",
    TokenType.OP_UNO_PLUS to "+u"
)

val OPERATORS = mapOf(
    TokenType.OP_PLUS to 0,
    TokenType.OP_MINUS to 0,
    TokenType.OP_MULT to 1,
    TokenType.OP_DIV to 1,
    TokenType.OP_UNO_MINUS to 2,
    TokenType.OP_UNO_PLUS to 2,
    TokenType.OP_PAREHTESES_OPEN to -1,
    TokenType.OP_PAREHTESES_CLOSE to -1
)

val OPERATOR_TYPES = mapOf(
    '+' to TokenType.OP_PLUS,
    '-' to TokenType.OP_MINUS,
    '*' to TokenType.OP_MULT,
    '/' to TokenType.OP_DIV,
    '(' to TokenType.OP_PAREHTESES_OPEN,
    ')' to TokenType.OP_PAREHTESES_CLOSE)

fun main(args: Array<String>) {
    unsafe{
        runBlocking {
            //IO{ readLine()!!}
            IO{"1+2*3*(2+10)"}
                .map { a -> parseTokens(a) }
                .map { a -> createRPN(a) }
                .map { a -> RPNtoString(a) }
                .map { a -> println(a)}
        }
    }
}

fun RPNtoString(tokens: List<Token>): String{
    var result = tokens.joinToString(separator=" ") { t -> tokenToString(t) }
    return result
}

fun tokenToString(token: Token): String {
    if (token.isnumber())
        return token.value.toString()
    else if (token.isoperator ())
        return OPERATORS_SYMBOLS[token.type]!!.toString()
    return ""
}
fun createRPN(tokens: List<Token>):List<Token>{
    var state = Tuple2<List<Token>,List<Token>>(listOf<Token>(), listOf<Token>())
    val endState = tokens.fold(state,{curState,token -> processToken(curState,token)})
    return transferRest(endState) .b
}

fun processToken(state: Tuple2<List<Token>,List<Token>>, token:Token):Tuple2<List<Token>,List<Token>>{
    var stack = state.a.toMutableList()
    var result = state.b.toMutableList()

    // if it's number
    if (token.isnumber())
        result.add(token)
    // if it's openening parentheses
    else if(token.type == TokenType.OP_PAREHTESES_OPEN)
        stack.add(token)
    // if it's closing parentheses
    else if(token.type == TokenType.OP_PAREHTESES_CLOSE) {
        // then moving tokens from stack to result until we meet opening parentheses
        var t = stack.removeLast()
        while (t.type != TokenType.OP_PAREHTESES_OPEN) {
            result.add(t)
            t = stack.removeLast()
        }
    }
    // if it's operator
    else if( token.isoperator()) {
        // taking priority of current operator
        var p0 = OPERATORS[token.type]!!
        // moving tokens from stack to result while operators in stack have same or higher priority
        while (stack.count() > 0) {
            var t1 = stack.removeLast()
            var p1 = OPERATORS[t1.type]!!
            if(p1 >= p0)
                result.add(t1)
            // returning token back to stack and exiting cycle
            else {
                stack.add(t1)
                break
            }
        }
        // finaly adding current token to stack
        stack.add(token)
    }

    return Tuple2(stack,result)
}

fun transferRest(state: Tuple2<List<Token>,List<Token>> ): Tuple2<List<Token>,List<Token>>{
    var stack = state.a.toMutableList()
    var result = state.b.toMutableList()
    stack.reverse()
    result.addAll(stack)
    return Tuple2(listOf(),result)
}

fun parseTokens(str: String): List<Token>{
    var result = arrayListOf<Token>()
    for ((index, token) in str.withIndex()) {
        if (token.isDigit()) {
            // if previous token was a number adding this digit to it
            if (index > 0 && result.last().isnumber())
                result.last().value = result.last().value * 10 + token.toString().toFloat()
            //otherwise add as new
            else
                result.add(Token(TokenType.NUMBER, token.toString().toFloat()))
        }
        //if it's operator
        else if(OPERATOR_TYPES.keys.contains(token)){
            var t = Token(OPERATOR_TYPES[token]!!)
            //if prevouse token in input was NOT a digit then current token is unary operator
            if ( index == 0 || !result.last().isnumber()) {
                //mark as unary
                if(token == '-')
                    t.type = TokenType.OP_UNO_MINUS
                else if( token == '+')
                    t.type = TokenType.OP_UNO_PLUS
            }
            result.add(t)
        }
    }
    return result
}