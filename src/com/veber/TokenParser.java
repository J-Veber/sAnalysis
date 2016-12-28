package com.veber;

import java.util.ArrayList;

/**
 * Created by Veiber on 19.10.2016.
 */

public class TokenParser {
    private int line;
    private String tokenName;
    private String tokenType;
    private int currentPosition;
    private static int index; //index for ArrayList<TokenParser> allTokens
    private static int _countBEGINEND;
    private static int _countBrackets;


    public TokenParser(){
        line = -1;
        currentPosition = -1;
        tokenName = "";
        tokenType = "";
        _countBEGINEND = 0;
        _countBrackets = 0;
    }

    public void setTokenName (String input) { tokenName = input; }
    public void setLine (int input) {line = input; }
    public void setTokenType (String input) {tokenType = input; }
    public void setCurrentPosition (int input) {currentPosition = input;};

    public int getLine() {return line; }
    public String getTokenName() {return tokenName; }
    public String getTokenType() {return tokenType; }
    public int getCurrentPosition() {return currentPosition; }

    public void print(){
        System.out.println(tokenName + " ; " + tokenType + " ; " + line + " ; " + currentPosition);
    }

    public static void init(Tree<String> semantTree, ArrayList<TokenParser> _inputTokens) {
        String input_token = _inputTokens.get(index).getTokenName();
        switch (input_token){
            case "PROGRAM":
                index++;
                //для дальнейших изменений нам нужен
                // список всех токенов,
                // текущий индекс,
                // дерево, в которое вносятся изменения
                identProg(_inputTokens, semantTree);
                break;
            default:
                System.out.println("init: expected keyword PROGRAM in line - " +
                        _inputTokens.get(index).getLine());
                System.exit(1);
        }
    }

    private static void identProg(ArrayList<TokenParser> inputTokens, Tree<String> _tree) {
        switch (inputTokens.get(index).getTokenType()){
            case "Variable":
                _tree.addLeaf(inputTokens.get(index).getTokenName());
                index++;
                System.out.println(_tree.printTree(index));
                if (inputTokens.get(index).getTokenType().equals("End")){
                    _tree.addLeaf(inputTokens.get(index).getTokenName());
                    index++;
                    System.out.println(_tree.printTree(index));
                    progBlock(inputTokens, _tree);
                } else {
                    System.out.println("identProg : expected End sign ( ; ) in line - " +
                            inputTokens.get(index).getLine());
                    System.exit(1);
                }
                break;
            default:
                System.out.println("identProg : expected variable in line - " +
                        inputTokens.get(index).getLine());
                System.exit(1);
        }
    }

    private static void progBlock(ArrayList<TokenParser> inputTokens, Tree<String> _tree){
        switch (inputTokens.get(index).getTokenName()){
            case "VAR":
                area_var_dec(inputTokens, _tree);
                break;
            default:
                System.out.println("progBlock: expected VAR in line - " +
                        inputTokens.get(index).getLine());
                System.exit(1);
        }
    }

    private static void area_var_dec(ArrayList<TokenParser> inputTokens, Tree<String> _tree){
        _tree.addLeaf(inputTokens.get(index).getTokenName());
        index++;
        System.out.println(_tree.printTree(index));
        //----- Здесь надо модифицировать while чтобы не было ошибки
        // ----- если begin'a с проге нет вообще -----
        while (!(inputTokens.get(index).getTokenName().equals("BEGIN")) && inputTokens.size()>=index){
            if (inputTokens.get(index).getTokenType().equals("Variable")){
                var_dec(inputTokens, _tree);
                if (inputTokens.get(index).getTokenType().equals("End")){
                    _tree.addLeaf(inputTokens.get(index).getTokenName());
                    index++;
                    System.out.println(_tree.printTree(index));
                } else {
                    System.out.println("area_var_dec : expected End sign ( ; ) in line - " +
                            inputTokens.get(index).getLine());
                    System.exit(1);
                }
            } else if (!inputTokens.get(index).getTokenName().equals("BEGIN")) {
                System.out.println("area_var_dec : expected BEGIN in line - " +
                        inputTokens.get(index).getLine());
                System.exit(1);
            } else {
                System.out.println("area_var_dec : expected Variable in line - " +
                        inputTokens.get(index).getLine());
                System.exit(1);
            }
        }

        if (inputTokens.get(index).getTokenName().equals("BEGIN")){
            _countBEGINEND++;
            area_operators(inputTokens, _tree);
        } else {
            System.out.println("area_var_dec : expected BEGIN in line - " +
                    inputTokens.get(index).getLine());
            System.exit(1);
        }
    }

    private static void var_dec(ArrayList<TokenParser> inputTokens, Tree<String> _tree){
        list_ident(inputTokens,_tree); //проверка была выше

        if (inputTokens.get(index).getTokenName().equals(":")){

            _tree.addLeaf(inputTokens.get(index).getTokenName());
            index++;
            System.out.println(_tree.printTree(index));

            switch (inputTokens.get(index).getTokenName()){
                case "INTEGER":
                case "BOOLEAN":
                case "STRING":
                case "REAL":
                    _tree.addLeaf(inputTokens.get(index).getTokenName());
                    index++;
                    System.out.println(_tree.printTree(index));
                    break;
                default:
                    System.out.println("var_dec : expected TYPENAME : INT or BOOLEAN or STRING or REAL in line - " +
                            inputTokens.get(index).getLine());
                    System.exit(1);
            }
        } else {
            System.out.println("var_dec : expected ( : ) sign in line - " +
                    inputTokens.get(index).getLine());
            System.exit(1);
        }
    }

    private static void list_ident(ArrayList<TokenParser> inputTokens, Tree<String> _tree){

        _tree.addLeaf(inputTokens.get(index).getTokenName());
        index++;
        System.out.println(_tree.printTree(index));

        if (inputTokens.get(index).getTokenName().equals(",") &&
                inputTokens.get(index+1).getTokenType().equals("Variable")) {
            while (inputTokens.get(index).getTokenName().equals(",")){

                if (inputTokens.get(index + 1).getTokenType().equals("Variable")){
                    _tree.addLeaf(inputTokens.get(index).getTokenName()); // добавляем запятую
                    index++;
                    _tree.addLeaf(inputTokens.get(index).getTokenName()); //добавляем переменную
                    index++;
                } else {
                    System.out.println("list_ident : expected Variable in line - " +
                            inputTokens.get(index).getLine());
                    System.exit(1);
                }
            }
        } else if (inputTokens.get(index).getTokenName().equals(":")) {
            return;
        }
        else {
            System.out.println("list_ident : expected Comma or Variable in line - " +
                    inputTokens.get(index).getLine());
            System.exit(1);
        }
    }

    private static void area_operators(ArrayList<TokenParser> inputTokens, Tree<String> _tree){

//----- проверено наличие BEGIN ------
        _tree.addLeaf(inputTokens.get(index).getTokenName());
        index++;
// ----- BEGIN добавлен -----

        for (int i = index; index < inputTokens.size() && !inputTokens.get(index).getTokenName().equals("ELSE"); i++) {
            // пока не достигли конца проги
            switch (inputTokens.get(index).getTokenType()){
                case "Variable":
                    operator_assign(inputTokens, _tree);
                    switch (inputTokens.get(index).getTokenType()){
                        case "END.":
                            operator_EXIT(inputTokens, _tree);
                            break;
                        case "Keyword":
                            if (inputTokens.get(index).getTokenName().equals("BEGIN")){
                                _countBEGINEND++;
                                area_operators(inputTokens, _tree);
                            }
                            if (inputTokens.get(index).getTokenName().equals("END")) {
                                operator_END(inputTokens, _tree);
                                return;
                            }
                            break;
                        case "Variable":
                            operator_assign(inputTokens, _tree);
                            break;
                        default:
                            System.out.println("area_operators : expected ( ; ) in line - " +
                                    inputTokens.get(index).getLine());
                            System.exit(1);
                    }
                    break;
//-----здесь разбираемся с тем какое именно слово к нам попало -----
                case "Keyword":
                    switch (inputTokens.get(index).getTokenName()){
                        case "IF":
                            operator_IF(inputTokens, _tree);
                            break;
                        case "WHILE":
                            operator_WHILE(inputTokens, _tree);
                            break;
                        case "FOR":
                            operator_FOR(inputTokens, _tree);
                            break;
                        case "END":
                            operator_END(inputTokens, _tree);
                            return;
                        case "END.":
                            operator_EXIT(inputTokens, _tree);
                            return;
                        default:
                            System.out.println("area_operators : expected some KEYWORD in line - " +
                                    inputTokens.get(index).getLine());
                            System.exit(1);
                    }
                    break;
                case "END.":
                    operator_EXIT(inputTokens, _tree);
                    return;
                default:
                    System.out.println("area_operators : expected Variable in line - " +
                            inputTokens.get(index).getLine());
                    System.exit(1);
            }

            if (index == inputTokens.size() && !inputTokens.get(index-1).getTokenName().equals("END.")){
                System.out.println("area_operators : expected END. in line - " +
                        inputTokens.get(index-1).getLine());
                System.exit(1);
            }
        }
    }

    private static void operator_END(ArrayList<TokenParser> inputTokens, Tree<String> _tree) {

        if (inputTokens.get(index+1).getTokenName().equals(";") &&
                inputTokens.get(index).getTokenName().equals("END")) {

            _tree.addLeaf(inputTokens.get(index).getTokenName());
            index++;
            _tree.addLeaf(inputTokens.get(index).getTokenName());
            index++;
            _countBEGINEND--;
            System.out.println(_tree.printTree(index));

        } else if (inputTokens.get(index).getTokenName().equals(";")) {

            _tree.addLeaf(inputTokens.get(index).getTokenName());
            index++;
            _countBEGINEND--; //??????
            System.out.println(_tree.printTree(index));

            } else if (inputTokens.get(index).getTokenName().equals("END") &&
                inputTokens.get(index+1).getTokenName().equals("ELSE")) {
            _tree.addLeaf(inputTokens.get(index).getTokenName());
            index++;
            _countBEGINEND--;
            System.out.println(_tree.printTree(index));
            //break;
        } else  {
            System.out.println("operator_END : expected END statement in line - " +
                    inputTokens.get(index).getLine());
            System.exit(1);
        }
    }

    private static void operator_EXIT(ArrayList<TokenParser> inputTokens, Tree<String> _tree) {

        if (inputTokens.get(index).getTokenType().equals("END.")){

            _tree.addLeaf(inputTokens.get(index).getTokenName());
            index++;
            _countBEGINEND--;
            System.out.println(_tree.printTree(index));

        } else {
            System.out.println("operator_END : expected END. in line - " +
                    inputTokens.get(index).getLine());
            System.exit(1);
        }
        if (_countBEGINEND != 0) {
            System.out.println("operator_EXIT: some problems with BEGIN-END in program");
            System.exit(1);
        } else {
            return;
        }
    }

    private static void operator_assign(ArrayList<TokenParser> inputTokens, Tree<String> _tree){

        _tree.addLeaf(inputTokens.get(index).getTokenName());
        index++;
        System.out.println(_tree.printTree(index));

        String cur_token_name = inputTokens.get(index).getTokenName();
        String cur_token_type = inputTokens.get(index).getTokenType();

        if (cur_token_type.equals("Assignment")) {
            _tree.addLeaf(inputTokens.get(index).getTokenName());
            index++;
        } else {
            System.out.println("operator_assign : expected := in line - " +
                    inputTokens.get(index).getLine());
            System.exit(1);
        }

        cur_token_type = inputTokens.get(index).getTokenType();
        cur_token_name = inputTokens.get(index).getTokenName();

        switch (cur_token_type){
            case "Variable":
            case "Integer":
            case "Real":
            case "String":
                expression(inputTokens, _tree); //-----вход в правило ВЫРАЖЕНИЕ -----

                cur_token_type = inputTokens.get(index).getTokenType();
                cur_token_name = inputTokens.get(index).getTokenName();
                break;
            default:
                switch (cur_token_name){
                    case "TRUE":
                    case "FALSE":
                    case "(":
                        expression(inputTokens, _tree); //-----вход в правило ВЫРАЖЕНИЕ -----

                        cur_token_type = inputTokens.get(index).getTokenType();
                        cur_token_name = inputTokens.get(index).getTokenName();
                        break;
                    default:
                        System.out.println("operator_assign : expected some Operation symb or Boolean|Variable|Integer|" +
                                "Real|String|Brackets in line - " + inputTokens.get(index).getLine());
                        System.exit(1);
                }
        }

        switch (cur_token_name){
            case ";":
                if (!inputTokens.get(index+1).getTokenName().equals("ELSE")) {

                    _tree.addLeaf(inputTokens.get(index).getTokenName());
                    index++;
                    System.out.println(_tree.printTree(index));

                    if (_countBrackets != 0) {
                        System.out.println("expression : something wrong with brackets in line " +
                                inputTokens.get(index).getLine());
                        System.exit(1);
                    }
                } else {
                    System.out.println("operator_assign : unexpected (;) in line - " +
                            inputTokens.get(index).getLine());
                    System.exit(1);
                }
                break;
            case "ELSE":
                break;
            default:
                System.out.println("operator_assign : expected (;) in line - " + inputTokens.get(index).getLine());
                System.exit(1);
        }

        cur_token_type = inputTokens.get(index).getTokenType();
        cur_token_name = inputTokens.get(index).getTokenName();
        if (cur_token_name.equals(";") && !inputTokens.get(index+1).getTokenName().equals("ELSE")){
            _tree.addLeaf(inputTokens.get(index).getTokenName());
            index++;
            System.out.println(_tree.printTree(index));
        } else if (cur_token_name.equals("END") && inputTokens.get(index+1).getTokenName().equals("ELSE")) {
            _tree.addLeaf(inputTokens.get(index).getTokenName());
            index++;
            if (cur_token_name.equals("END")) {
                _countBEGINEND--;
            }
            System.out.println(_tree.printTree(index));
        }
    }

    private static void expression (ArrayList<TokenParser> inputTokens, Tree<String> _tree) {

        if (inputTokens.get(index).getTokenType().equals("Variable") &&
                inputTokens.get(index + 1).getTokenType().equals("Variable")) {

            System.out.println("expression : expected relation or assign sign in line " +
                    inputTokens.get(index + 1).getLine());
            System.exit(1);

        } else {
            simple_expression(inputTokens, _tree);
        }

        if (inputTokens.get(index).getTokenType().equals("Relation") &&
                (inputTokens.get(index+1).getTokenType().equals("Operation") ||
                inputTokens.get(index+1).getTokenType().equals("Variable") ||
                inputTokens.get(index+1).getTokenType().equals("Integer") ||
                inputTokens.get(index+1).getTokenType().equals("Real") ||
                inputTokens.get(index+1).getTokenName().equals("TRUE") ||
                inputTokens.get(index+1).getTokenName().equals("FALSE") ||
                inputTokens.get(index+1).getTokenType().equals("String") ||
                inputTokens.get(index+1).getTokenName().equals("("))){

            while (inputTokens.get(index).getTokenType().equals("Relation") &&
                    (inputTokens.get(index+1).getTokenType().equals("Operation") ||
                            inputTokens.get(index+1).getTokenType().equals("Variable") ||
                            inputTokens.get(index+1).getTokenType().equals("Integer") ||
                            inputTokens.get(index+1).getTokenType().equals("Real") ||
                            inputTokens.get(index+1).getTokenName().equals("TRUE") ||
                            inputTokens.get(index+1).getTokenName().equals("FALSE") ||
                            inputTokens.get(index+1).getTokenType().equals("String") ||
                            inputTokens.get(index+1).getTokenName().equals("("))){
                _tree.addLeaf(inputTokens.get(index).getTokenName());
                index++;
                simple_expression(inputTokens, _tree);
            }

        } else switch (inputTokens.get(index).getTokenName()){
            case ";":
                break;
            case ")":
                break;
            default:
                System.out.println("expression : expected operation or variable in line " +
                    inputTokens.get(index + 1).getLine());
            System.exit(1);
        }
    }

    private static void simple_expression(ArrayList<TokenParser> inputTokens, Tree<String> _tree){

        String token_name = inputTokens.get(index).getTokenName();
        String token_type = inputTokens.get(index).getTokenType();

        if (token_type.equals("Operation")) { // -----else go to TERM -----

            _tree.addLeaf(inputTokens.get(index).getTokenName());
            index++;
            System.out.println(_tree.printTree(index));

            token_name = inputTokens.get(index).getTokenName();
            token_type = inputTokens.get(index).getTokenType();

        } else if (token_type.equals("Variable") ||
                    token_type.equals("Integer") ||
                    token_type.equals("Real") ||
                    token_type.equals("String") ||
                    token_name.equals("(") ||
                    token_name.equals("TRUE") || token_name.equals("FALSE")) {
                //----- TERM -----
                term(inputTokens, _tree);

            } else {
                System.out.println("simple_expression : expected some Operation symb or Boolean|Variable|Integer|" +
                        "Real|String|Brackets in line - " + inputTokens.get(index).getLine());
                System.exit(1);
            }

        token_name = inputTokens.get(index).getTokenName();
        token_type = inputTokens.get(index).getTokenType();

        while ((token_name.equals("+") || token_name.equals("-")) &&
                (inputTokens.get(index + 1).getTokenType().equals("Variable") ||
                        inputTokens.get(index + 1).getTokenType().equals("Integer") ||
                        inputTokens.get(index + 1).getTokenType().equals("Real") ||
                        inputTokens.get(index + 1).getTokenType().equals("String") ||
                        inputTokens.get(index + 1).getTokenName().equals("(")  ||
                        inputTokens.get(index + 1).getTokenName().equals("TRUE") ||
                        inputTokens.get(index + 1).getTokenName().equals("FALSE"))) {

            _tree.addLeaf(inputTokens.get(index).getTokenName());
            index++;
            _tree.addLeaf(inputTokens.get(index).getTokenName());
            index++;
            System.out.println(_tree.printTree(index));
            token_name = inputTokens.get(index).getTokenName();
            token_type = inputTokens.get(index).getTokenType();
        } //-----END WHILE -----
    }

    private static void term (ArrayList<TokenParser> inputTokens, Tree<String> _tree){

        String token_name = inputTokens.get(index).getTokenName();
        String token_type = inputTokens.get(index).getTokenType();

        if (token_type.equals("Variable") ||
                token_type.equals("Integer") ||
                token_type.equals("Real") ||
                token_type.equals("String") ||
                token_name.equals("(") ||
                token_name.equals("TRUE") || token_name.equals("FALSE")) {

            mult(inputTokens, _tree);
            token_name = inputTokens.get(index).getTokenName();
            token_type = inputTokens.get(index).getTokenType();

        } else {
            System.out.println("term : expected some Operation symb or Boolean|Variable|Integer|" +
                    "Real|String|Brackets in line - " + inputTokens.get(index).getLine());
            System.exit(1);
        }

        while ((token_name.equals("*") || token_name.equals("AND")) &&
                (inputTokens.get(index + 1).getTokenType().equals("Variable") ||
                        inputTokens.get(index + 1).getTokenType().equals("Integer") ||
                        inputTokens.get(index + 1).getTokenType().equals("Real") ||
                        inputTokens.get(index + 1).getTokenType().equals("String") ||
                        inputTokens.get(index + 1).getTokenName().equals("(")  ||
                        inputTokens.get(index + 1).getTokenName().equals("TRUE") ||
                        inputTokens.get(index + 1).getTokenName().equals("FALSE"))){

            _tree.addLeaf(inputTokens.get(index).getTokenName());
            index++;
            _tree.addLeaf(inputTokens.get(index).getTokenName());
            index++;
            token_name = inputTokens.get(index).getTokenName();
            System.out.println(_tree.printTree(index));
        }
    }

    private static void mult(ArrayList<TokenParser> inputTokens, Tree<String> _tree){

        String token_name = inputTokens.get(index).getTokenName();
        String token_type = inputTokens.get(index).getTokenType();

        switch (token_type){
            case "Variable":
            case "Integer":
            case "Real":
            case "String":

                _tree.addLeaf(inputTokens.get(index).getTokenName());
                index++;
                System.out.println(_tree.printTree(index));

                break;
            default:
                switch (token_name) {
                    case "(":

                        _tree.addLeaf(inputTokens.get(index).getTokenName());
                        index++;
                        System.out.println(_tree.printTree(index));
                        _countBrackets++;

                        token_name = inputTokens.get(index).getTokenName();
                        token_type = inputTokens.get(index).getTokenType();
                        switch (token_type) {
                            case "Variable":
                            case "Integer":
                            case "Real":
                            case "String":
                            case "Bracket":
                                expression(inputTokens, _tree); //-----вход в правило ВЫРАЖЕНИЕ -----

                                token_type = inputTokens.get(index).getTokenType();
                                token_name = inputTokens.get(index).getTokenName();

                                if (token_name.equals(")")){

                                    _tree.addLeaf(inputTokens.get(index).getTokenName());
                                    index++;
                                    System.out.println(_tree.printTree(index));
                                    _countBrackets--;

                                } else {
                                    System.out.println("mult  : expected close bracket in line - " +
                                            inputTokens.get(index).getLine());
                                    System.exit(1);
                                }
                                break;
                        }
                        break;
                    case ";":

                        _tree.addLeaf(inputTokens.get(index).getTokenName());
                        index++;
                        System.out.println(_tree.printTree(index));

                        token_name = inputTokens.get(index).getTokenName();
                        token_type = inputTokens.get(index).getTokenType();
                        break;
                    case "=":
                        System.out.println("mult  : expected variable in line - " +
                                inputTokens.get(index).getLine()); //проверить переменную ли ждет
                        System.exit(1);
                    default:
                        System.out.println("mult : expected expression in line - " + inputTokens.get(index).getLine());
                        System.exit(1);
                }
                break;
        }
    }

    private static void operator_IF(ArrayList<TokenParser> inputTokens, Tree<String> _tree){

        _tree.addLeaf(inputTokens.get(index).getTokenName());
        index++;
        System.out.println(_tree.printTree(index));

        String token_name = inputTokens.get(index).getTokenName();
        String token_type = inputTokens.get(index).getTokenType();

        switch (token_type){
            case "Variable":
            case "Integer":
            case "Real":
            case "String":
            case "Bracket":
                expression(inputTokens,_tree);
                break;
            default:
                if (token_name.equals("(")) {
                    expression(inputTokens, _tree);
                } else System.out.println("operator_IF : expected expression in line - " +
                        inputTokens.get(index).getLine());
                System.exit(1);
        }

        token_name = inputTokens.get(index).getTokenName();
// ----- if THEN here or BEGIN or some VARIABLE
        if (token_name.equals("THEN") && (inputTokens.get(index+1).getTokenName().equals("BEGIN") ||
                inputTokens.get(index+1).getTokenType().equals("Variable"))){

            _tree.addLeaf(inputTokens.get(index).getTokenName());
            index++;
            token_name = inputTokens.get(index).getTokenName();
            token_type = inputTokens.get(index).getTokenType();

            if (token_name.equals("BEGIN")){
//------составной оператор-----
                _countBEGINEND++;
                area_operators(inputTokens,_tree);
            } else { // ----- if VARIABLE -----
//-----simple operator -----

                switch (token_type){
                    case "Variable":
                        while (token_type.equals("Variable")){
                            operator_assign(inputTokens, _tree);
                            token_name = inputTokens.get(index).getTokenName();
                            token_type = inputTokens.get(index).getTokenType();

                        }
                        if (token_name.equals("ELSE") && inputTokens.get(index-1).getTokenName().equals(";")){
                            System.out.println("operator_IF : unexpected keyword (ELSE) in line - " +
                                    inputTokens.get(index).getLine());
                            System.exit(1);
                        }
                        break;
//-----здесь разбираемся с тем какое именно слово к нам попало -----
                    case "Keyword":
                        switch (token_name){
                            case "IF":
                                operator_IF(inputTokens, _tree);
                                break;
                            case "WHILE":
                                operator_WHILE(inputTokens, _tree);
                                break;
                            case "FOR":
                                operator_FOR(inputTokens, _tree);
                                break;
                            case "END":
                                operator_END(inputTokens, _tree);
                                return;
                            case "END.":
                                operator_EXIT(inputTokens, _tree);
                                return;
                            default:
                                System.out.println("operator_IF : expected some KEYWORD in line - " +
                                        inputTokens.get(index).getLine());
                                System.exit(1);
                        }
                        break;
                    default:
                        System.out.println("operator_IF : expected Variable or END. or Keyword type in line - " +
                                inputTokens.get(index).getLine());
                        System.exit(1);
                }
            }

            token_name = inputTokens.get(index).getTokenName();

//-------- ELSE block --------
            if (inputTokens.get(index).getTokenName().equals("ELSE") &&
                    !inputTokens.get(index-1).getTokenName().equals(";")) {
                _tree.addLeaf(inputTokens.get(index).getTokenName());
                index++;
                _tree.printTree(index);
                if (inputTokens.get(index).getTokenName().equals("BEGIN") ||
                        inputTokens.get(index).getTokenType().equals("Variable")) {

                    token_name = inputTokens.get(index).getTokenName();
                    token_type = inputTokens.get(index).getTokenType();

                    if (token_name.equals("BEGIN")){
//------составной оператор-----
                        _countBEGINEND++;
                        area_operators(inputTokens,_tree);
                    } else { // ----- if VARIABLE -----
//-----simple operator -----
                        switch (token_type){
                            case "Variable":
                                operator_assign(inputTokens, _tree);
                                break;
//-----здесь разбираемся с тем какое именно слово к нам попало -----
                            case "Keyword":
                                switch (token_name){
                                    case "IF":
                                        operator_IF(inputTokens, _tree);
                                        break;
                                    case "WHILE":
                                        operator_WHILE(inputTokens, _tree);
                                        break;
                                    case "FOR":
                                        operator_FOR(inputTokens, _tree);
                                        break;
                                    default:
                                        System.out.println("operator_IF : expected some KEYWORD in line - " +
                                                inputTokens.get(index).getLine());
                                        System.exit(1);
                                }
                                break;
                            default:
                                System.out.println("operator_IF : expected Variable or END. or Keyword type in line - "
                                        + inputTokens.get(index).getLine());
                                System.exit(1);
                        }
                    }
                }
            } else {
                switch (token_name){
                    case "THEN":
                        System.out.println("operator_IF : expected some operator(s) in line - " +
                                inputTokens.get(index).getLine());
                        System.exit(1);
                    case "ELSE":
                        System.out.println("operator_IF : unexpected keyword (ELSE) in line " +
                                inputTokens.get(index).getLine());
                        System.exit(1);
                    default:
                        //System.exit(1);
                }
        }
        }
    }

    private static void operator_WHILE(ArrayList<TokenParser> inputTokens, Tree<String> _tree){

//----- проверено наличие WHILE ------
        _tree.addLeaf(inputTokens.get(index).getTokenName());
        index++;
        System.out.println(_tree.printTree(index));
// ----- WHILE добавлен -----

        String token_name = inputTokens.get(index).getTokenName();
        String token_type = inputTokens.get(index).getTokenType();

        if ((token_type.equals("Variable") ||
                token_type.equals("Integer") ||
                token_type.equals("Real") ||
                token_type.equals("String") ||
                token_name.equals("(") ||
                token_name.equals("TRUE") || token_name.equals("FALSE"))) {

            expression(inputTokens, _tree);

            _tree.addLeaf(inputTokens.get(index).getTokenName());
            index++;
            System.out.println(_tree.printTree(index));

// ----- we add DO in our tree -----
            token_name = inputTokens.get(index).getTokenName();
            token_type = inputTokens.get(index).getTokenType();

            if (token_name.equals("BEGIN") || token_type.equals("Variable")) {
//                if (token_name.equals("BEGIN")) {
//                    _countBEGINEND++;
//                }

                token_name = inputTokens.get(index).getTokenName();
                token_type = inputTokens.get(index).getTokenType();

                if (token_name.equals("BEGIN")){
//------составной оператор-----
                    _countBEGINEND++;
                    area_operators(inputTokens,_tree);
                } else {
                    // ----- if VARIABLE -----
//-----simple operator -----
                    switch (token_type){
                        case "Variable":
                            operator_assign(inputTokens, _tree);
                            break;
//-----здесь разбираемся с тем какое именно слово к нам попало -----
                        case "Keyword":
                            switch (inputTokens.get(index).getTokenName()){
                                case "IF":
                                    operator_IF(inputTokens, _tree);
                                    break;
                                case "WHILE":
                                    operator_WHILE(inputTokens, _tree);
                                    break;
                                case "FOR":
                                    operator_FOR(inputTokens, _tree);
                                    break;
                                case "END":
                                    operator_END(inputTokens, _tree);
                                    return;
                                default:
                                    System.out.println("operator_WHILE : expected some KEYWORD in line - " +
                                            inputTokens.get(index).getLine());
                                    System.exit(1);
                            }
                            break;
                        case "END.":
                            operator_EXIT(inputTokens, _tree);
                            break;
                        default:
                            System.out.println("operator_WHILE : expected Variable or END. or Keyword type or " +
                                    "Assignment sign in line - " +
                                    inputTokens.get(index).getLine());
                            System.exit(1);
                    }
                }
            }
        } else {
            System.out.println("operator_WHILE : expected some Operation symb or Boolean|Variable|Integer|" +
                    "Real|String|Brackets in line - " + inputTokens.get(index).getLine());
            System.exit(1);
        }
    }

    private static void operator_FOR(ArrayList<TokenParser> inputTokens, Tree<String> _tree){
//----- проверено наличие FOR ------
// ----- FOR добавлен -----
        String token_name = inputTokens.get(index).getTokenName();
        String token_type = inputTokens.get(index).getTokenType();

        if (token_name.equals("FOR") && inputTokens.get(index+1).getTokenType().equals("Variable") &&
                inputTokens.get(index+2).getTokenType().equals("Assignment") &&
                inputTokens.get(index+3).getTokenType().equals("Integer") &&
                inputTokens.get(index+4).getTokenName().equals("TO") &&
                (inputTokens.get(index+5).getTokenType().equals("Integer") ||
                        inputTokens.get(index+3).getTokenType().equals("Variable")) &&
                inputTokens.get(index+6).getTokenName().equals("DO")){

            for (int i = 1; i<=7; i++){
                _tree.addLeaf(inputTokens.get(index).getTokenName());
                index++;
            }
            System.out.println(_tree.printTree(index));

// ----- пытаем составной/простой оператор -----
            token_name = inputTokens.get(index).getTokenName();
            token_type = inputTokens.get(index).getTokenType();

            if (token_name.equals("BEGIN") || token_type.equals("Variable")) {

//                if (token_name.equals("BEGIN")){
//                    _countBEGINEND++;
//                }
//                _tree.addLeaf(inputTokens.get(index).getTokenName());
//                index++;
//                System.out.println(_tree.printTree(index));

                token_name = inputTokens.get(index).getTokenName();
                token_type = inputTokens.get(index).getTokenType();
                if (token_name.equals("BEGIN")){
//------составной оператор-----
                    _countBEGINEND++;
                    area_operators(inputTokens,_tree);
                } else { // ----- if VARIABLE -----
//-----simple operator -----
                    switch (token_type){
                        case "Variable":
                            operator_assign(inputTokens, _tree);
                            if (inputTokens.get(index).getTokenName().equals(";") ||
                                    inputTokens.get(index).getTokenName().equals("END")) {
                                operator_END(inputTokens, _tree);
                                return;
                            } else {
                                System.out.println("operator_FOR : expected ( ; ) in line - " +
                                        inputTokens.get(index).getLine());
                                System.exit(1);
                            }
                            break;
//-----здесь разбираемся с тем какое именно слово к нам попало -----
                        case "Keyword":
                            switch (inputTokens.get(index).getTokenName()){
                                case "IF":
                                    operator_IF(inputTokens, _tree);
                                    break;
                                case "WHILE":
                                    operator_WHILE(inputTokens, _tree);
                                    break;
                                case "FOR":
                                    operator_FOR(inputTokens, _tree);
                                    break;
                                case "END":
                                    operator_END(inputTokens, _tree);
                                    return;
                                default:
                                    System.out.println("operator_FOR : expected some KEYWORD");
                                    System.exit(1);
                            }
                            break;
                        case "END.":
                            operator_EXIT(inputTokens, _tree);
                            return;
                        default:
                            System.out.println("operator_FOR : expected Variable or END. or Keyword type");
                            System.exit(1);
                    }
                }
            }
        } else {
            System.out.println("operator_FOR : can not build operator FOR");
            System.exit(1);
        }
    }
}

