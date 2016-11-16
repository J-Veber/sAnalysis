package com.veber;

import java.util.ArrayList;
import java.util.List;

import static javax.swing.UIManager.get;

/**
 * Created by Veiber on 19.10.2016.
 */

public class TokenParser {
    private int line;
    private String tokenName;
    private String tokenType;
    private int currentPosition;
    private int index;

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

    public void init(ArrayList<TokenParser> _inputTokens) {
        index = 0;
        String input_token = _inputTokens.get(index).getTokenName();
        switch (input_token){
            case "PROGRAM":
                Tree<String> semantTree = new Tree<>(_inputTokens.get(index).getTokenName());
                //semantTree.addLeaf(_inputTokens.get(index).getTokenName());
                index++;
                //для дальнейших изменений нам нужен
                // список всех токенов,
                // текущий индекс,
                // дерево, в которое вносятся изменения
                identProg(index, _inputTokens, semantTree);
                break;
            default:
                System.out.println("init: expected keyword PROGRAM");
                break;
        }
    }

    private void identProg(int _index, ArrayList<TokenParser> inputTokens, Tree<String> _tree) {
        switch (inputTokens.get(_index).getTokenType()){
            case "Variable":
                _tree.addLeaf(inputTokens.get(_index).getTokenName());
                _index++;
                if (inputTokens.get(_index).getTokenType().equals("End")){
                    _tree.addLeaf(inputTokens.get(_index).getTokenName());
                    _index++;
                    progBlock(_index, inputTokens, _tree);
                } else System.out.println("identProg : expected End sign ( ; )");
                break;
            default:
                System.out.println("identProg : expected variable");
                break;
        }
    }

    private void progBlock(int _index, ArrayList<TokenParser> inputTokens, Tree<String> _tree){
        switch (inputTokens.get(_index).getTokenName()){
            case "VAR":
                area_var_dec(_index, inputTokens, _tree);
                break;
            default:
                System.out.println("progBlock: expected VAR");
                break;
        }
    }

    private void area_var_dec(int _index, ArrayList<TokenParser> inputTokens, Tree<String> _tree){
        _tree.addLeaf(inputTokens.get(_index).getTokenName());
        _index++;
        while (!(inputTokens.get(_index).getTokenName().equals("BEGIN"))){
            switch (inputTokens.get(_index).getTokenType()){
                case "Variable":
                    var_dec(_index, inputTokens, _tree);
                    if (inputTokens.get(_index).getTokenType().equals("End")){
                        _tree.addLeaf(inputTokens.get(_index).getTokenName());
                        _index++;
                    } else System.out.println("area_var_dec : expected End sign ( ; )");
                    break;
                default:
                    break;
            }
        }
        if (inputTokens.get(_index).getTokenName().equals("BEGIN")){
            area_operators(_index, inputTokens, _tree);
        } else {
            System.out.println("area_var_dec : expected BEGIN");
        }

    }

    private void var_dec(int _index, ArrayList<TokenParser> inputTokens, Tree<String> _tree){
        //list_ident(_index,inputTokens,_tree); //проверка была выше
        //_index++;
        _tree.addLeaf(inputTokens.get(_index-1).getTokenName());
        ++_index;
        while (inputTokens.get(_index).getTokenName().equals(",")){
            if (inputTokens.get(_index + 1).getTokenType().equals("Variable")){
                _tree.addLeaf(inputTokens.get(_index).getTokenName()); // добавляем запятую
                _index++;
                _tree.addLeaf(inputTokens.get(_index).getTokenName()); //добавляем переменную
                _index++;
            } else {
                System.out.println("list_ident : expected Variable");
                break;
            }
        }
        if (inputTokens.get(_index).getTokenName().equals(":")){
            _tree.addLeaf(inputTokens.get(_index).getTokenName());
            _index++;
            switch (inputTokens.get(_index).getTokenName()){
                case "INTEGER":
                case "BOOLEAN":
                case "STRING":
                case "REAL":
                    _tree.addLeaf(inputTokens.get(_index).getTokenName());
                    _index++;
                    break;
                default:
                    System.out.println("var_dec : expected TYPENAME INT or BOOLEAN or STRING or REAL");
                    break;
            } //System.out.println("var_dec : expected define sign ( : )");
        }
    }

    private void list_ident(int _index, ArrayList<TokenParser> inputTokens, Tree<String> _tree){

        _tree.addLeaf(inputTokens.get(_index-1).getTokenName());
        ++_index;
        while (inputTokens.get(_index).getTokenName().equals(",")){
            if (inputTokens.get(_index + 1).getTokenType().equals("Variable")){
                _tree.addLeaf(inputTokens.get(_index).getTokenName()); // добавляем запятую
                _index++;
                _tree.addLeaf(inputTokens.get(_index).getTokenName()); //добавляем переменную
                _index++;
            } else {
                System.out.println("list_ident : expected Variable");
                break;
            }
        }
    }

    private void area_operators(int _index, ArrayList<TokenParser> inputTokens, Tree<String> _tree){
//----- проверено наличие BEGIN ------
        _tree.addLeaf(inputTokens.get(_index).getTokenName());
        _index++;
// ----- BEGIN добавлен -----
        switch (inputTokens.get(_index).getTokenType()){
            case "Variable":
                operator_assign(_index, inputTokens, _tree);
                if (inputTokens.get(_index).getTokenType().equals("End")) {
                    _tree.addLeaf(inputTokens.get(_index).getTokenName());
                    _index++;
                } else System.out.println("area_operators : expected ( ; )");
                break;
//            case "END.":
//                operator_EXIT(_index, inputTokens, _tree);
//-----ЭТО КОНЕЦ ПРОГРАММЫ -----
                //break;
//-----здесь разбираемся с тем какое именно слово к нам попало -----
            case "Keyword":
                switch (inputTokens.get(_index).getTokenName()){
                    case "IF":
                        operator_IF(_index, inputTokens, _tree);
                        break;
                    case "WHILE":
                        operator_WHILE(_index, inputTokens, _tree);
                        break;
                    case "FOR":
                        operator_FOR(_index, inputTokens, _tree);
                        break;
                    case "END":
//                        operator_END(_index, inputTokens, _tree);
//                        //-----нужно отследить ";" и понять что это конец команды -----
                        break;
                    default:
                        System.out.println("area_operators : expected some KEYWORD");
                        break;
                }
                break;
            default:
                System.out.println("area_operators : expected Variable or END. or Keyword type");
                break;
        }
    }

    private void operator_assign(int _index, ArrayList<TokenParser> inputTokens, Tree<String> _tree){
        _tree.addLeaf(inputTokens.get(_index).getTokenName());
        _index++;
        if (inputTokens.get(_index).getTokenName().equals(":=")) {
            String next_token_type = inputTokens.get(_index + 1).getTokenType();
            switch (next_token_type) { //-----это следующий символ-----
                case "Operation":  //+ or -
                case "Variable":   //ident
                case "Integer":    //number
                case "Boolean":    //TRUE ot FALSE
                case "String":     //STRING
                case "Real":       //number -> посмотреть что там у Юли с интом и реалом
                    _tree.addLeaf(inputTokens.get(_index).getTokenName()); // add :=
                    _index++;
                    _tree.addLeaf(inputTokens.get(_index).getTokenName()); // add current symbol
                    _index++;
                    if (inputTokens.get(_index + 1).getTokenType().equals("Operation") ||
                            inputTokens.get(_index + 1).getTokenType().equals("Variable") ||
                            inputTokens.get(_index + 1).getTokenType().equals("Integer") ||
                            inputTokens.get(_index + 1).getTokenType().equals("Real") ||
                            inputTokens.get(_index + 1).getTokenName().equals("TRUE") ||
                            inputTokens.get(_index + 1).getTokenName().equals("FALSE") ||
                            inputTokens.get(_index + 1).getTokenType().equals("String") ||
                            inputTokens.get(_index + 1).getTokenName().equals("(")) {
                        expression(_index, inputTokens, _tree); //-----вход в правило ВЫРАЖЕНИЕ -----
                    } else System.out.println("operator_assign : expected some Operation symb or Boolean|Variable|Integer|" +
                            "Real|String|Brackets. ");
                default:
                    System.out.println("operator_assign : expected some expression sign");
                    break;
            }
        } else System.out.println("operator_assign : expected assign sign ( := )");
    }

    private void expression (int _index, ArrayList<TokenParser> inputTokens, Tree<String> _tree){
        simple_expression(_index, inputTokens, _tree);
        if (inputTokens.get(_index).getTokenType().equals("Relation")) {
            _tree.addLeaf(inputTokens.get(_index).getTokenName());
            _index++;
        }
        //String cur_symb = inputTokens.get(_index).getTokenName();
        while (inputTokens.get(_index).getTokenType().equals("Operation") ||
                inputTokens.get(_index).getTokenType().equals("Variable") ||
                inputTokens.get(_index).getTokenType().equals("Integer") ||
                inputTokens.get(_index).getTokenType().equals("Real") ||
                inputTokens.get(_index).getTokenName().equals("TRUE") ||
                inputTokens.get(_index).getTokenName().equals("FALSE") ||
                inputTokens.get(_index).getTokenType().equals("String") ||
                inputTokens.get(_index).getTokenName().equals("(")) {
            simple_expression(_index, inputTokens, _tree);
        } //-----END WHILE -----
    }

    private void simple_expression(int _index, ArrayList<TokenParser> inputTokens, Tree<String> _tree){
        String token_name = inputTokens.get(_index).getTokenName();
        String token_type = inputTokens.get(_index).getTokenType();
        if (token_type.equals("Operation")) { // -----else go to TERM -----
            _tree.addLeaf(inputTokens.get(_index).getTokenName());
            _index++;
        } else
            if (token_type.equals("Variable") ||
                token_type.equals("Integer") ||
                token_type.equals("Real") ||
                token_type.equals("String") ||
                token_name.equals("(") ||
                token_name.equals("TRUE") || token_name.equals("FALSE")) {
                term(_index, inputTokens, _tree);
            } else System.out.println("simple_expression : expected some Operation symb or Boolean|Variable|Integer|" +
                    "Real|String|Brackets. ");
        while ((token_name.equals("+") || token_name.equals("-")) &&
                (inputTokens.get(_index + 1).getTokenType().equals("Variable") ||
                        inputTokens.get(_index + 1).getTokenType().equals("Integer") ||
                        inputTokens.get(_index + 1).getTokenType().equals("Real") ||
                        inputTokens.get(_index + 1).getTokenType().equals("String") ||
                        inputTokens.get(_index + 1).getTokenName().equals("(")  ||
                        inputTokens.get(_index + 1).getTokenName().equals("TRUE") ||
                        inputTokens.get(_index + 1).getTokenName().equals("FALSE"))) {
            _tree.addLeaf(inputTokens.get(_index).getTokenName());
            _index++;
            _tree.addLeaf(inputTokens.get(_index).getTokenName());
            _index++;
        } //-----END WHILE -----
    }

    private void term (int _index, ArrayList<TokenParser> inputTokens, Tree<String> _tree){
        String token_name = inputTokens.get(_index).getTokenName();
        String token_type = inputTokens.get(_index).getTokenType();
        if (token_type.equals("Variable") ||
                token_type.equals("Integer") ||
                token_type.equals("Real") ||
                token_type.equals("String") ||
                token_name.equals("(") ||
                token_name.equals("TRUE") || token_name.equals("FALSE")) {
            mult(_index, inputTokens, _tree);
        } else System.out.println("term : expected some Operation symb or Boolean|Variable|Integer|" +
                "Real|String|Brackets. ");
        while ((token_name.equals("*") || token_name.equals("AND")) &&
                (inputTokens.get(_index + 1).getTokenType().equals("Variable") ||
                        inputTokens.get(_index + 1).getTokenType().equals("Integer") ||
                        inputTokens.get(_index + 1).getTokenType().equals("Real") ||
                        inputTokens.get(_index + 1).getTokenType().equals("String") ||
                        inputTokens.get(_index + 1).getTokenName().equals("(")  ||
                        inputTokens.get(_index + 1).getTokenName().equals("TRUE") ||
                        inputTokens.get(_index + 1).getTokenName().equals("FALSE"))){
            _tree.addLeaf(inputTokens.get(_index).getTokenName());
            _index++;
            _tree.addLeaf(inputTokens.get(_index).getTokenName());
            _index++;
        }
    }

    private void mult(int _index, ArrayList<TokenParser> inputTokens, Tree<String> _tree){
        String token_name = inputTokens.get(_index).getTokenName();
        String token_type = inputTokens.get(_index).getTokenType();
        switch (token_type){
            case "Variable":
            case "Integer":
            case "Real":
            case "String":
                _tree.addLeaf(inputTokens.get(_index).getTokenName());
                _index++;
                break;
            default:
                if (token_name.equals("(")) {
                    expression(_index, inputTokens, _tree);
                } else System.out.println("mult : expected expression");
                break;
        }
    }

    private void operator_IF(int _index, ArrayList<TokenParser> inputTokens, Tree<String> _tree){
        _tree.addLeaf(inputTokens.get(_index).getTokenName());
        _index++;
        String token_name = inputTokens.get(_index).getTokenName();
        String token_type = inputTokens.get(_index).getTokenType();
        switch (token_type){
            case "Variable":
            case "Integer":
            case "Real":
            case "String":
            case "Brackets":
                expression(_index,inputTokens,_tree);
                break;
            default:
                if (token_name.equals("(")) {
                    expression(_index, inputTokens, _tree);
                } else System.out.println("operator_IF : expected expression");
                break;
        }
        token_name = inputTokens.get(_index).getTokenName();
// ----- if THEN here or BEGIN or some VARIABLE
        if (token_name.equals("THEN") && (inputTokens.get(_index+1).getTokenName().equals("BEGIN") ||
                inputTokens.get(_index+1).getTokenType().equals("Variable"))){
            _tree.addLeaf(inputTokens.get(_index).getTokenName());
            _index++;
            token_name = inputTokens.get(_index).getTokenName();
            token_type = inputTokens.get(_index).getTokenType();
            if (token_name.equals("BEGIN")){
//------составной оператор-----
                area_operators(_index,inputTokens,_tree);
            } else { // ----- if VARIABLE -----
//-----simple operator -----
                switch (token_type){
                    case "Variable":
                        operator_assign(_index, inputTokens, _tree);
                        if (inputTokens.get(_index).getTokenType().equals("End")) {
                            _tree.addLeaf(inputTokens.get(_index).getTokenName());
                            _index++;
                        } else System.out.println("operator_IF : expected ( ; )");
                        break;
//-----здесь разбираемся с тем какое именно слово к нам попало -----
                    case "Keyword":
                        switch (token_name){
                            case "IF":
                                operator_IF(_index, inputTokens, _tree);
                                break;
                            case "WHILE":
                                operator_WHILE(_index, inputTokens, _tree);
                                break;
                            case "FOR":
                                operator_FOR(_index, inputTokens, _tree);
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
            token_name = inputTokens.get(_index).getTokenName();

//-------- ELSE block --------
            if (token_name.equals("ELSE")) {
                _tree.addLeaf(inputTokens.get(_index).getTokenName());
                _index++;
                if (inputTokens.get(_index+1).getTokenName().equals("BEGIN") ||
                        inputTokens.get(_index+1).getTokenType().equals("Variable")) {
                    _tree.addLeaf(inputTokens.get(_index).getTokenName());
                    _index++;
                    token_name = inputTokens.get(_index).getTokenName();
                    token_type = inputTokens.get(_index).getTokenType();
                    if (token_name.equals("BEGIN")){
//------составной оператор-----
                        area_operators(_index,inputTokens,_tree);
                    } else { // ----- if VARIABLE -----
//-----simple operator -----
                        switch (token_type){
                            case "Variable":
                                operator_assign(_index, inputTokens, _tree);
                                if (inputTokens.get(_index).getTokenType().equals("End")) {
                                    _tree.addLeaf(inputTokens.get(_index).getTokenName());
                                    _index++;
                                } else System.out.println("operator_IF : expected ( ; )");
                                break;
//-----здесь разбираемся с тем какое именно слово к нам попало -----
                            case "Keyword":
                                switch (token_name){
                                    case "IF":
                                        operator_IF(_index, inputTokens, _tree);
                                        break;
                                    case "WHILE":
                                        operator_WHILE(_index, inputTokens, _tree);
                                        break;
                                    case "FOR":
                                        operator_FOR(_index, inputTokens, _tree);
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

    private void operator_WHILE(int _index, ArrayList<TokenParser> inputTokens, Tree<String> _tree){
//----- проверено наличие WHILE ------
        _tree.addLeaf(inputTokens.get(_index).getTokenName());
        _index++;
// ----- WHILE добавлен -----
        String token_name = inputTokens.get(_index).getTokenName();
        String token_type = inputTokens.get(_index).getTokenType();
        if ((inputTokens.get(_index+1).getTokenName().equals("DO")) && (token_type.equals("Variable") ||
                token_type.equals("Integer") ||
                token_type.equals("Real") ||
                token_type.equals("String") ||
                token_name.equals("(") ||
                token_name.equals("TRUE") || token_name.equals("FALSE"))) {
            expression(_index, inputTokens, _tree);
            _tree.addLeaf(inputTokens.get(_index).getTokenName());
            _index++;
// ----- we add DO in our tree -----
            token_name = inputTokens.get(_index).getTokenName();
            token_type = inputTokens.get(_index).getTokenType();

            if (token_name.equals("BEGIN") || token_type.equals("Variable")) {
                _tree.addLeaf(inputTokens.get(_index).getTokenName());
                _index++;
                token_name = inputTokens.get(_index).getTokenName();
                token_type = inputTokens.get(_index).getTokenType();
                if (token_name.equals("BEGIN")){
//------составной оператор-----
                    area_operators(_index,inputTokens,_tree);
                } else { // ----- if VARIABLE -----
//-----simple operator -----
                    switch (token_type){
                        case "Variable":
                            operator_assign(_index, inputTokens, _tree);
                            if (inputTokens.get(_index).getTokenType().equals("End")) {
                                _tree.addLeaf(inputTokens.get(_index).getTokenName());
                                _index++;
                            } else System.out.println("operator_WHILE : expected ( ; )");
                            break;
//-----здесь разбираемся с тем какое именно слово к нам попало -----
                        case "Keyword":
                            switch (token_name){
                                case "IF":
                                    operator_IF(_index, inputTokens, _tree);
                                    break;
                                case "WHILE":
                                    operator_WHILE(_index, inputTokens, _tree);
                                    break;
                                case "FOR":
                                    operator_FOR(_index, inputTokens, _tree);
                                    break;
                                default:
                                    System.out.println("operator_WHILE : expected some KEYWORD");
                                    break;
                            }
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

    private void operator_FOR(int _index, ArrayList<TokenParser> inputTokens, Tree<String> _tree){
//----- проверено наличие FOR ------
        _tree.addLeaf(inputTokens.get(_index).getTokenName());
        _index++;
// ----- FOR добавлен -----
        String token_name = inputTokens.get(_index).getTokenName();
        String token_type = inputTokens.get(_index).getTokenType();
        if (token_name.equals("FOR") && inputTokens.get(_index+1).getTokenType().equals("Variable") &&
                inputTokens.get(_index+2).getTokenType().equals("Assignment") &&
                inputTokens.get(_index+3).getTokenType().equals("Integer") &&
                inputTokens.get(_index+4).getTokenName().equals("TO") &&
                (inputTokens.get(_index+5).getTokenType().equals("Integer") ||
                        inputTokens.get(_index+3).getTokenType().equals("Variable")) &&
                inputTokens.get(_index+6).getTokenName().equals("DO")){
            for (int i = 1; i<=6; i++){
                _tree.addLeaf(inputTokens.get(_index).getTokenName());
                _index++;
            }

// ----- пытаем составной/простой оператор -----
            token_name = inputTokens.get(_index).getTokenName();
            token_type = inputTokens.get(_index).getTokenType();

            if (token_name.equals("BEGIN") || token_type.equals("Variable")) {
                _tree.addLeaf(inputTokens.get(_index).getTokenName());
                _index++;
                token_name = inputTokens.get(_index).getTokenName();
                token_type = inputTokens.get(_index).getTokenType();
                if (token_name.equals("BEGIN")){
//------составной оператор-----
                    area_operators(_index,inputTokens,_tree);
                } else { // ----- if VARIABLE -----
//-----simple operator -----
                    switch (token_type){
                        case "Variable":
                            operator_assign(_index, inputTokens, _tree);
                            if (inputTokens.get(_index).getTokenType().equals("End")) {
                                _tree.addLeaf(inputTokens.get(_index).getTokenName());
                                _index++;
                            } else System.out.println("operator_WHILE : expected ( ; )");
                            break;
//-----здесь разбираемся с тем какое именно слово к нам попало -----
                        case "Keyword":
                            switch (token_name){
                                case "IF":
                                    operator_IF(_index, inputTokens, _tree);
                                    break;
                                case "WHILE":
                                    operator_WHILE(_index, inputTokens, _tree);
                                    break;
                                case "FOR":
                                    operator_FOR(_index, inputTokens, _tree);
                                    break;
                                default:
                                    System.out.println("operator_WHILE : expected some KEYWORD");
                                    break;
                            }
                            break;
                        default:
                            System.out.println("operator_WHILE : expected Variable or END. or Keyword type");
                            break;
                    }
                }
            }
        } else System.out.println("operator_FOR : can not build operator FOR");
    }
}

