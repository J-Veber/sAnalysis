package com.veber;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import static javax.swing.UIManager.get;

/**
 * Created by Veiber on 19.10.2016.
 */

public class TokenParser {
    private int line;
    private String tokenName;
    private String tokenType;
    private int currentPosition;
    private static int index; //index for ArrayList<TokenParser> allTokens
    private static Tree<String> semantTree;
    private String[] typeMas = {"INTEGER", "BOOLEAN", "STRING", "REAL", "Keyword"};

    public TokenParser(){
        line = -1;
        currentPosition = -1;
        tokenName = "";
        tokenType = "";
    }
    public void clearCurrentData(){
        line = -1;
        currentPosition = -1;
        tokenName = "";
        tokenType = "";
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

    public static void init(ArrayList<TokenParser> _inputTokens) {
        String input_token = _inputTokens.get(index).getTokenName();
        switch (input_token){
            case "PROGRAM":
                semantTree = new Tree<>(_inputTokens.get(index).getTokenName());
                index++;
                //для дальнейших изменений нам нужен
                // список всех токенов,
                // текущий индекс,
                // дерево, в которое вносятся изменения
                identProg(_inputTokens, semantTree);
                break;
            default:
                System.out.println("init: expected keyword PROGRAM");
                break;
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
                } else System.out.println("identProg : expected End sign ( ; )");
                break;
            default:
                System.out.println("identProg : expected variable");
                break;
        }
    }

    private static void progBlock(ArrayList<TokenParser> inputTokens, Tree<String> _tree){
        switch (inputTokens.get(index).getTokenName()){
            case "VAR":
                area_var_dec(inputTokens, _tree);
                break;
            default:
                System.out.println("progBlock: expected VAR");
                break;
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
                } else System.out.println("area_var_dec : expected End sign ( ; )");
            }
// ----- костыль -----
            //if (!inputTokens.get(index).getTokenType().equals("End")) break;
        }
        if (inputTokens.get(index).getTokenName().equals("BEGIN")){
            area_operators(inputTokens, _tree);
        } else {
            System.out.println("area_var_dec : expected BEGIN");
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
                    System.out.println("var_dec : expected TYPENAME INT or BOOLEAN or STRING or REAL");
                    break;
            } //System.out.println("var_dec : expected define sign ( : )");
        }
    }

    private static void list_ident(ArrayList<TokenParser> inputTokens, Tree<String> _tree){

        _tree.addLeaf(inputTokens.get(index).getTokenName());
        index++;
        System.out.println(_tree.printTree(index));
        while (inputTokens.get(index).getTokenName().equals(",")){
            if (inputTokens.get(index + 1).getTokenType().equals("Variable")){
                _tree.addLeaf(inputTokens.get(index).getTokenName()); // добавляем запятую
                index++;
                _tree.addLeaf(inputTokens.get(index).getTokenName()); //добавляем переменную
                index++;
            } else {
                System.out.println("list_ident : expected Variable");
                break;
            }
        }
    }

    private static void area_operators(ArrayList<TokenParser> inputTokens, Tree<String> _tree){
//----- проверено наличие BEGIN ------
        _tree.addLeaf(inputTokens.get(index).getTokenName());
        index++;
// ----- BEGIN добавлен -----
        while (!inputTokens.get(index).getTokenType().equals("END.")) { // пока не достигли конца проги
            switch (inputTokens.get(index).getTokenType()){
                case "Variable":
                    operator_assign(inputTokens, _tree);
                    if (inputTokens.get(index).getTokenType().equals("End")) {
                        _tree.addLeaf(inputTokens.get(index).getTokenName());
                        index++;
                        System.out.println(_tree.printTree(index));
                    } else System.out.println("area_operators : expected ( ; )");
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
                            break;
                        default:
                            System.out.println("area_operators : expected some KEYWORD");
                            break;
                    }
                    break;
//                case "END.":
//                    operator_EXIT(inputTokens, _tree);
////                        //-----нужно отследить ";" и понять что это конец команды -----
//                    break;
//                default:
//                    System.out.println("area_operators : expected Variable or END. or Keyword type");
//                    break;
            }
        }
        switch (inputTokens.get(index).getTokenType()){
            case "END.":
                operator_EXIT(inputTokens, _tree);
//                        //-----нужно отследить ";" и понять что это конец команды -----
                break;
            default:
                System.out.println("area_operators : expected END. in line - " +
                                        inputTokens.get(index).getLine());
                break;
        }

//        if (!(inputTokens.get(index).getTokenType().equals("Variable") &&
//                inputTokens.get(index+1).getTokenType().equals("Keyword"))) {
//            System.out.println("area_operators : expected Variable or END. or Keyword type");
//        }
    }

    private static void operator_END(ArrayList<TokenParser> inputTokens, Tree<String> _tree) {
        if (inputTokens.get(index).getTokenName().equals("END") &&
                inputTokens.get(index+1).getTokenName().equals(";")) {
            _tree.addLeaf(inputTokens.get(index).getTokenName());
            index++;
            _tree.addLeaf(inputTokens.get(index).getTokenName());
            index++;
            System.out.println(_tree.printTree(index));
        } else System.out.println("operator_END : expected END statement in line - " +
                                        inputTokens.get(index).getLine());
    }

    private static void operator_EXIT(ArrayList<TokenParser> inputTokens, Tree<String> _tree) {
        if (inputTokens.get(index).getTokenType().equals("END.")){
            _tree.addLeaf(inputTokens.get(index).getTokenName());
            index++;
            System.out.println(_tree.printTree(index));
        } else System.out.println("operator_END : expected END.");
    }

    private static void operator_assign(ArrayList<TokenParser> inputTokens, Tree<String> _tree){
        _tree.addLeaf(inputTokens.get(index).getTokenName());
        index++;
        System.out.println(_tree.printTree(index));
        if (inputTokens.get(index).getTokenName().equals(":=")) {
            String next_token_type = inputTokens.get(index + 1).getTokenType();
            switch (next_token_type) { //-----это следующий символ-----
                case "Operation":  //+ or -
                case "Variable":   //ident
                case "Integer":    //number
                case "Boolean":    //TRUE ot FALSE
                case "String":     //STRING
                case "Real":       //number -> посмотреть что там у Юли с интом и реалом
                    _tree.addLeaf(inputTokens.get(index).getTokenName()); // add :=
                    index++;
                    _tree.addLeaf(inputTokens.get(index).getTokenName()); // add current symbol
                    index++;
                    System.out.println(_tree.printTree(index));
                    if (inputTokens.get(index + 1).getTokenType().equals("Operation") ||
                            inputTokens.get(index + 1).getTokenType().equals("Variable") ||
                            inputTokens.get(index + 1).getTokenType().equals("Integer") ||
                            inputTokens.get(index + 1).getTokenType().equals("Real") ||
                            inputTokens.get(index + 1).getTokenName().equals("TRUE") ||
                            inputTokens.get(index + 1).getTokenName().equals("FALSE") ||
                            inputTokens.get(index + 1).getTokenType().equals("String") ||
                            inputTokens.get(index + 1).getTokenName().equals("(")) {
                        expression(inputTokens, _tree); //-----вход в правило ВЫРАЖЕНИЕ -----
                    } else System.out.println("operator_assign : expected some Operation symb or Boolean|Variable|Integer|" +
                            "Real|String|Brackets in line - " + inputTokens.get(index).getLine());
                    break;
                default:
                    System.out.println("operator_assign : expected some expression sign in line - " +
                                            inputTokens.get(index).getLine());
                    break;
            }
        } else System.out.println("operator_assign : expected assign sign ( := ) in line - " +
                                        inputTokens.get(index).getLine());
    }

    private static void expression (ArrayList<TokenParser> inputTokens, Tree<String> _tree){
        simple_expression(inputTokens, _tree);
        if (inputTokens.get(index).getTokenType().equals("Relation")) {
            _tree.addLeaf(inputTokens.get(index).getTokenName());
            index++;
            System.out.println(_tree.printTree(index));
        }
        //String cur_symb = inputTokens.get(_index).getTokenName();
        while (inputTokens.get(index).getTokenType().equals("Operation") ||
                inputTokens.get(index).getTokenType().equals("Variable") ||
                inputTokens.get(index).getTokenType().equals("Integer") ||
                inputTokens.get(index).getTokenType().equals("Real") ||
                inputTokens.get(index).getTokenName().equals("TRUE") ||
                inputTokens.get(index).getTokenName().equals("FALSE") ||
                inputTokens.get(index).getTokenType().equals("String") ||
                inputTokens.get(index).getTokenName().equals("(")) {
            simple_expression(inputTokens, _tree);
        } //-----END WHILE -----
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
        } else
            if (token_type.equals("Variable") ||
                token_type.equals("Integer") ||
                token_type.equals("Real") ||
                token_type.equals("String") ||
                token_name.equals("(") ||
                token_name.equals("TRUE") || token_name.equals("FALSE")) {
                term(inputTokens, _tree);
            } else System.out.println("simple_expression : expected some Operation symb or Boolean|Variable|Integer|" +
                    "Real|String|Brackets. ");
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
        } else System.out.println("term : expected some Operation symb or Boolean|Variable|Integer|" +
                "Real|String|Brackets. ");
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
                if (inputTokens.get(index).getTokenName().equals(";") ||
                        inputTokens.get(index).getTokenName().equals("=")){
                    switch (inputTokens.get(index).getTokenName()){
                        case ";":
                            _tree.addLeaf(inputTokens.get(index).getTokenName());
                            index++;
                            System.out.println(_tree.printTree(index));
                            break;
                        case "=":
                            if (inputTokens.get(index+1).getTokenName().equals("=")) {
                                String rel = inputTokens.get(index).getTokenName() +
                                        inputTokens.get(index+1).getTokenName();
                                _tree.addLeaf(rel);
                                index++;
                                index++;
                                System.out.println(_tree.printTree(index));
                                break;
                            }
                    }

                } else System.out.println("mult : expected ; or == in line - " + inputTokens.get(index).getLine());
                break;
            default:
                if (token_name.equals("(")) {
                    expression(inputTokens, _tree);
                } else System.out.println("mult : expected expression");
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
            case "Brackets":
                expression(inputTokens,_tree);
                break;
            default:
                if (token_name.equals("(")) {
                    expression(inputTokens, _tree);
                } else System.out.println("operator_IF : expected expression");
                break;
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
                area_operators(inputTokens,_tree);
            } else { // ----- if VARIABLE -----
//-----simple operator -----
                switch (token_type){
                    case "Variable":
                        operator_assign(inputTokens, _tree);
                        if (inputTokens.get(index).getTokenType().equals("End")) {
                            _tree.addLeaf(inputTokens.get(index).getTokenName());
                            index++;
                        } else System.out.println("operator_IF : expected ( ; )");
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
                                System.out.println("operator_IF : expected some KEYWORD");
                                break;
                        }
                        break;
                    default:
                        System.out.println("operator_IF : expected Variable or END. or Keyword type");
                        break;
                }
            }
            token_name = inputTokens.get(index).getTokenName();

//-------- ELSE block --------
            if (token_name.equals("ELSE")) {
                _tree.addLeaf(inputTokens.get(index).getTokenName());
                index++;
                if (inputTokens.get(index+1).getTokenName().equals("BEGIN") ||
                        inputTokens.get(index+1).getTokenType().equals("Variable")) {
                    _tree.addLeaf(inputTokens.get(index).getTokenName());
                    index++;
                    token_name = inputTokens.get(index).getTokenName();
                    token_type = inputTokens.get(index).getTokenType();
                    if (token_name.equals("BEGIN")){
//------составной оператор-----
                        area_operators(inputTokens,_tree);
                    } else { // ----- if VARIABLE -----
//-----simple operator -----
                        switch (token_type){
                            case "Variable":
                                operator_assign(inputTokens, _tree);
                                if (inputTokens.get(index).getTokenType().equals("End")) {
                                    _tree.addLeaf(inputTokens.get(index).getTokenName());
                                    index++;
                                } else System.out.println("operator_IF : expected ( ; )");
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
                                        System.out.println("operator_IF : expected some KEYWORD");
                                        break;
                                }
                                break;
                            default:
                                System.out.println("operator_IF : expected Variable or END. or Keyword type");
                                break;
                        }
                    }
                }
            }
        } else if (token_name.equals("THEN")) {
            System.out.println("operator_IF : expected some operator(s)");
        } else System.out.println("operator_IF : expected THEN");

    }

    private static void operator_WHILE(ArrayList<TokenParser> inputTokens, Tree<String> _tree){
//----- проверено наличие WHILE ------
        _tree.addLeaf(inputTokens.get(index).getTokenName());
        index++;
        System.out.println(_tree.printTree(index));
// ----- WHILE добавлен -----
        String token_name = inputTokens.get(index).getTokenName();
        String token_type = inputTokens.get(index).getTokenType();
        if ((inputTokens.get(index+1).getTokenName().equals("DO")) && (token_type.equals("Variable") ||
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
                _tree.addLeaf(inputTokens.get(index).getTokenName());
                index++;
                System.out.println(_tree.printTree(index));
                token_name = inputTokens.get(index).getTokenName();
                token_type = inputTokens.get(index).getTokenType();
                if (token_name.equals("BEGIN")){
//------составной оператор-----
                    area_operators(inputTokens,_tree);
                } else { // ----- if VARIABLE -----
//-----simple operator -----
                    switch (token_type){
                        case "Variable":
                            operator_assign(inputTokens, _tree);
                            if (inputTokens.get(index).getTokenType().equals("End")) {
                                _tree.addLeaf(inputTokens.get(index).getTokenName());
                                index++;
                                System.out.println(_tree.printTree(index));
                            } else System.out.println("operator_WHILE : expected ( ; )");
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
                                    break;
                                default:
                                    System.out.println("operator_WHILE : expected some KEYWORD");
                                    break;
                            }
                            break;
                        case "END.":
                            operator_EXIT(inputTokens, _tree);
//                        //-----нужно отследить ";" и понять что это конец команды -----
                            break;
                        default:
                            System.out.println("operator_WHILE : expected Variable or END. or Keyword type");
                            break;
                    }
                }
            }
        } else System.out.println("operator_WHILE : expected some Operation symb or Boolean|Variable|Integer|" +
                "Real|String|Brackets. ");
    }

    private static void operator_FOR(ArrayList<TokenParser> inputTokens, Tree<String> _tree){
//----- проверено наличие FOR ------
//        _tree.addLeaf(inputTokens.get(index).getTokenName());
//        index++;
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
                _tree.addLeaf(inputTokens.get(index).getTokenName());
                index++;
                System.out.println(_tree.printTree(index));
                token_name = inputTokens.get(index).getTokenName();
                token_type = inputTokens.get(index).getTokenType();
                if (token_name.equals("BEGIN")){
//------составной оператор-----
                    area_operators(inputTokens,_tree);
                } else { // ----- if VARIABLE -----
//-----simple operator -----
                    switch (token_type){
                        case "Variable":
                            operator_assign(inputTokens, _tree);
                            if (inputTokens.get(index).getTokenType().equals("Keyword") &&
                                    inputTokens.get(index).getTokenName().equals("END") &&
                                    inputTokens.get(index+1).getTokenName().equals(";")) {
                                operator_END(inputTokens, _tree);
                            } else System.out.println("operator_FOR : expected ( ; ) in line - " +
                                                        inputTokens.get(index).getLine());
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
                                    break;
                                default:
                                    System.out.println("operator_FOR : expected some KEYWORD");
                                    break;
                            }
                            break;
                        case "END.":
                            operator_EXIT(inputTokens, _tree);
//                        //-----нужно отследить ";" и понять что это конец команды -----
                            break;
                        default:
                            System.out.println("operator_FOR : expected Variable or END. or Keyword type");
                            break;
                    }
                }
            }
        } else System.out.println("operator_FOR : can not build operator FOR");
    }

    //-----Semantic Analyze ----
    public void analyze_tree(ArrayList<DataForSemantAn> _inputArrayList) {
        _inputArrayList.get(0).setVarType(typeMas[4]); //keyword
            }
}

