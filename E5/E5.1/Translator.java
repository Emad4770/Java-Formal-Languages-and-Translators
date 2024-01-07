import java.io.*;

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count = 0;

    public Translator(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
        throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF)
                move();
        } else {
            error("syntax error. Expected: " + t + ", Found: " + look);
        }
    }

    public void prog() {
        // ... completare ...
        int lnext_prog = code.newLabel();
        statlist(lnext_prog);
        // code.emitLabel(lnext_prog);
        match(Tag.EOF);
        code.emit(OpCode.GOto, lnext_prog);
        code.emitLabel(lnext_prog);

        try {
            code.toJasmin();
        } catch (java.io.IOException e) {
            System.out.println("IO error\n");
        }
        ;
        // ... completare ...
    }

    private void statlist(int lnext_prog) {
        stat(lnext_prog);
        statlistp(lnext_prog);
    }

    private void statlistp(int lnext_prog) {
        switch (look.tag) {
            case ';':
                match(';');
                // lnext_prog = code.newLabel();
                // code.emit(OpCode.GOto, lnext_prog);
                // code.emitLabel(lnext_prog);
                stat(lnext_prog);
                statlistp(lnext_prog);
                break;

        }
    }

    public void stat(int lnext_prog) {
        switch (look.tag) {
            // ... completare ...

            case Tag.ASSIGN:
                match(Tag.ASSIGN);
                assignlist(lnext_prog);
                lnext_prog = code.newLabel();
                code.emit(OpCode.GOto, lnext_prog);
                code.emitLabel(lnext_prog);
                break;

            case Tag.PRINT:
                match(Tag.PRINT);
                match('(');
                exprlist(lnext_prog);
                match(')');
                code.emit(OpCode.invokestatic, 1);
                lnext_prog = code.newLabel();
                code.emit(OpCode.GOto, lnext_prog);
                code.emitLabel(lnext_prog);
                break;

            case Tag.READ:
                match(Tag.READ);
                code.emit(OpCode.invokestatic, 0);
                match('(');
                idlist(lnext_prog);
                match(')');
                lnext_prog = code.newLabel();
                code.emit(OpCode.GOto, lnext_prog);
                code.emitLabel(lnext_prog);
                break;

            case Tag.FOR:
                match(Tag.FOR);
                match('(');

                int loopStart = code.newLabel();
                int loopBody = code.newLabel();
                int loopEnd = code.newLabel();

                if (look.tag == Tag.ID) {
                    String loopVar = ((Word) look).lexeme;
                    int idAddr = st.lookupAddress(loopVar);
                    if (idAddr == -1) {
                        idAddr = count;
                        st.insert(loopVar, count++);
                    }

                    match(Tag.ID);
                    match(Tag.INIT);
                    expr(lnext_prog);
                    code.emit(OpCode.istore, idAddr);
                    match(';');
                    code.emitLabel(loopStart);
                    bexpr(loopEnd);
                    match(')');
                    match(Tag.DO);
                    // code.emit(OpCode.GOto, loopEnd);
                    code.emitLabel(loopBody);
                    stat(lnext_prog);
                    code.emit(OpCode.GOto, loopStart);
                    code.emitLabel(loopEnd);

                } else {

                    code.emitLabel(loopStart);
                    bexpr(loopEnd);
                    match(')');
                    match(Tag.DO);
                    stat(lnext_prog);
                    code.emit(OpCode.GOto, loopStart);
                    code.emitLabel(loopEnd);
                }
                break;

            case Tag.IF:
                match(Tag.IF);
                match('(');
                bexpr(lnext_prog);
                match(')');
                stat(lnext_prog);
                if (look.tag == Tag.ELSE) {
                    match(Tag.ELSE);
                    stat(lnext_prog);
                }
                match(Tag.END);
                break;

            case '{':
                match('{');
                statlist(lnext_prog);
                match('}');

                break;

            default:
                error("Invalid Syntax in stat");
                break;

            // ... completare ...
        }

    }

    private void assignlist(int lnext_prog) {

        switch (look.tag) {
            case '[':
                match('[');
                expr(lnext_prog);
                match(Tag.TO);
                idlist(lnext_prog);
                match(']');
                assignlistp(lnext_prog);
                break;

            default:
                error("Syntax error");
                break;
        }

    }

    private void assignlistp(int lnext_prog) {
        switch (look.tag) {
            case '[':
                match('[');
                expr(lnext_prog);
                match(Tag.TO);
                idlist(lnext_prog);
                match(']');
                assignlistp(lnext_prog);
                break;

        }
    }

    private void idlist(int lnext_prog) {
        switch (look.tag) {
            case Tag.ID:
                // match(Tag.ID);
                int id_addr = st.lookupAddress(((Word) look).lexeme);
                if (id_addr == -1) {
                    id_addr = count;
                    st.insert(((Word) look).lexeme, count++);
                }
                code.emit(OpCode.istore, id_addr);
                match(Tag.ID);
                idlistp(lnext_prog);

                break;
            default:
                error("Syntax error in idlist");
                break;

        }
    }

    private void idlistp(int lnext_prog) {
        switch (look.tag) {
            case ',':
                match(',');
                int id_addr = st.lookupAddress(((Word) look).lexeme);
                if (id_addr == -1) {
                    id_addr = count;
                    st.insert(((Word) look).lexeme, count++);
                }
                match(Tag.ID);
                code.emit(OpCode.istore, id_addr);
                idlistp(lnext_prog);

                break;
        }

    }

    private void bexpr(int lnext_prog) {
        switch (look.tag) {
            case Tag.RELOP:

                String op = ((Word) look).lexeme;

                match(Tag.RELOP);
                expr(lnext_prog);
                expr(lnext_prog);

                switch (op) {
                    case "==":
                        code.emit(OpCode.if_icmpeq, lnext_prog);

                        break;
                    case "<>":
                        code.emit(OpCode.if_icmpne, lnext_prog);
                        break;
                    case "<":
                        code.emit(OpCode.if_icmplt, lnext_prog);
                        break;
                    case ">":
                        code.emit(OpCode.if_icmpgt, lnext_prog);
                        break;
                    case "<=": // <=
                        code.emit(OpCode.if_icmple, lnext_prog);
                        break;
                    case ">=": // >=
                        code.emit(OpCode.if_icmpge, lnext_prog);
                        break;
                    default:
                        error("Invalid relational operator in bexpr");

                }
                break;

            default:
                error("Syntax error at bexpr");
                break;
        }
    }

    private void expr(int lnext_prog) {
        switch (look.tag) {
            case '+':
                match('+');
                match('(');
                exprlist(lnext_prog);
                match(')');
                code.emit(OpCode.iadd);
                break;
            case '-':
                match('-');
                expr(lnext_prog);
                expr(lnext_prog);
                code.emit(OpCode.isub);
                break;
            case '*':
                match('*');
                match('(');
                exprlist(lnext_prog);
                match(')');
                code.emit(OpCode.imul);
                break;
            case '/':
                match('/');
                expr(lnext_prog);
                expr(lnext_prog);
                code.emit(OpCode.idiv);
                break;

            case Tag.NUM:
                code.emit(OpCode.ldc, ((NumberTok) look).value);
                match(Tag.NUM);
                break;

            case Tag.ID:
                int id_addr = st.lookupAddress(((Word) look).lexeme);
                if (id_addr == -1) {
                    error("Undeclared variable: " + (((Word) look).lexeme));
                }
                code.emit(OpCode.iload, id_addr);
                match(Tag.ID);
                break;
            default:
                error("Syntax error in expr");
                // ... completare ...
        }
    }

    private void exprlist(int lnext_prog) {
        expr(lnext_prog);
        exprlistp(lnext_prog);
    }

    private void exprlistp(int lnext_prog) {
        switch (look.tag) {
            case ',':
                match(',');
                expr(lnext_prog);
                exprlistp(lnext_prog);
                break;

        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "./sample2.txt"; // the path to the file to be read
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator translator = new Translator(lex, br);
            translator.prog();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}