import java.io.*;

public class Parser3_2 {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser3_2(Lexer l, BufferedReader br) {
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
        statlist();
        match(Tag.EOF);
        System.out.println("Input OK");

    }

    private void statlist() {
        stat();
        statlistp();
    }

    private void statlistp() {
        switch (look.tag) {
            case ';':
                match(';');
                stat();
                statlistp();

                break;
        }
    }

    // private void stat() {
    // if (look.tag == Tag.ASSIGN) {
    // match(Tag.ASSIGN);
    // assignlist();
    // } else if (look.tag == Tag.PRINT) {
    // match(Tag.PRINT);
    // match(Tag.LPT);
    // exprlist();
    // match(Tag.RPT);
    // } else if (look.tag == Tag.READ) {
    // match(Tag.READ);
    // match('(');
    // idlist();
    // match(')');
    // } else if (look.tag == Tag.FOR) {
    // match(Tag.FOR);
    // match('('); // Opening parenthesis
    // if (look.tag == Tag.ID) {
    // match(Tag.ID);
    // match(Tag.INIT);
    // }
    // expr();
    // match(';');
    // bexpr();
    // match(')'); // Closing parenthesis
    // match(Tag.DO);
    // stat();
    // } else if (look.tag == Tag.FOR) {
    // match(Tag.FOR);
    // match('('); // Opening parenthesis
    // bexpr();
    // match(')'); // Closing parenthesis
    // match(Tag.DO);
    // stat();
    // } else if (look.tag == Tag.IF) {
    // match(Tag.IF);
    // bexpr();
    // stat();
    // if (look.tag == Tag.ELSE) {
    // match(Tag.ELSE);
    // stat();
    // }
    // match(Tag.END);
    // } else if (look.tag == Tag.IF) {
    // match(Tag.IF);
    // bexpr();
    // stat();
    // match(Tag.END);
    // } else if (look.tag == Tag.LPG) {
    // match(Tag.LPG);
    // statlist();
    // match(Tag.RPG);
    // } else {
    // error("syntax error");
    // }
    // }
    public void stat() {
        switch (look.tag) {
            // ... completare ...

            case Tag.ASSIGN:
                match(Tag.ASSIGN);
                assignlist();
                break;

            case Tag.PRINT:
                match(Tag.PRINT);
                match('(');
                exprlist();
                match(')');
                break;

            case Tag.READ:
                match(Tag.READ);
                match('(');
                idlist();
                match(')');
                break;

            case Tag.FOR:
                match(Tag.FOR);
                match('(');

                if (look.tag == Tag.ID) {

                    match(Tag.ID);
                    match(Tag.INIT);
                    expr();
                    match(';');
                    bexpr();
                    match(')');
                    match(Tag.DO);
                    stat();

                } else {

                    bexpr();
                    match(')');
                    match(Tag.DO);
                    stat();
                }
                break;

            case Tag.IF:

                match(Tag.IF);
                match('(');
                bexpr();
                match(')');
                stat();
                if (look.tag == Tag.ELSE) {
                    match(Tag.ELSE);
                    stat();

                }

                match(Tag.END);
                break;

            case '{':
                match('{');
                statlist();
                match('}');

                break;

            default:
                error("Invalid Syntax in stat");
                break;

            // ... completare ...
        }

    }

    private void assignlist() {
        match('['); // [
        expr();
        match(Tag.TO);
        idlist();
        match(']'); // ]
        assignlistp();
    }

    private void assignlistp() {
        switch (look.tag) {
            case '[':
                match('['); // [
                expr();
                match(Tag.TO);
                idlist();
                match(']'); // ]
                assignlistp();
                break;
            default:
                // ε (empty string), do nothing
        }
    }

    private void idlist() {

        match(Tag.ID);
        idlistp();

    }

    private void idlistp() {
        switch (look.tag) {
            case ',':
                match(',');
                match(Tag.ID);
                idlistp();
                break;

        }

    }

    private void bexpr() {

        match(Tag.RELOP);
        expr();
        expr();
    }

    private void expr() {
        switch (look.tag) {
            case '+':
                match('+');
                match('(');
                exprlist();
                match(')');
                break;
            case '-':
                match('-');
                expr();
                expr();
                break;
            case '*':
                match('*');
                match('(');
                exprlist();
                match(')');
                break;
            case '/':
                match('/');
                expr();
                expr();
                break;
            case Tag.NUM:
                match(Tag.NUM);
                break;
            case Tag.ID:
                match(Tag.ID);

                break;

            default:
                error("Syntax error in expr");
        }
    }

    private void exprlist() {
        expr();
        exprlistp();
    }

    private void exprlistp() {

        switch (look.tag) {
            case ',':
                match(',');
                expr();
                exprlistp();
                break;

        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "E3.2/sample3_2.lft"; // the path to the file to be read
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser3_2 parser = new Parser3_2(lex, br);
            parser.prog();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}