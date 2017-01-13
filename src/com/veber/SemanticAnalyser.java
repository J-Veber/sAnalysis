package com.veber;

import java.util.ArrayList;

/**
 * Created by Veber on 24.11.2016.
 */
public class SemanticAnalyser {
    private String[] typeMas = {"INTEGER", "BOOLEAN", "STRING", "REAL"};
    private String[] variableMas = new String[150];
    private static int variableIndex; //двигаемся по массиву variableMas
    private static int index; //двигаемся по листьям дерева
    private static int variablecount = 0;
    private static boolean oncevar = false;
    public SemanticAnalyser(){
        index = 0;
        variableIndex = 0;
    }
    private static void analyse_table(ArrayList<DataForSemantAn> _variableList,Tree<String> _tree,
                                      ArrayList<TokenParser> _allTokens){
        for (int i = 1; i<_variableList.size(); i++){
            if (_variableList.get(i).getDeclaration() == false){
                System.out.println("sem_an_table: variable " + _variableList.get(i).getVarName() + " not declared");
                System.exit(1);
            }
            if (_variableList.get(i).getInitialization() == false){
                System.out.println("sem_an_table: variable " + _variableList.get(i).getVarName() + " not initialised");
                //System.exit(1);
            }
        }
    }
    private static void search_double_variable (ArrayList<DataForSemantAn> _variableList,Tree<String> _tree,
                                                ArrayList<TokenParser> _allTokens) {
        for (int q = 0; q<_variableList.size(); q++){
            if (q != _variableList.size() - 1) {
                for (int a = q + 1; a<_variableList.size()-1; q++){
                    if (_variableList.get(a).getVarName().equals(_variableList.get(a+1).getVarName())){
                        System.out.println("search_double_variable : variable " + _variableList.get(a).getVarName() +
                                " is already exist ");
                        System.exit(1);
                    }
                }
            }
        }
    }
    public void analyse(ArrayList<DataForSemantAn> _variableList,Tree<String> _tree,
                        ArrayList<TokenParser> _allTokens){
        Boolean Var = true;
        DataForSemantAn obj = new DataForSemantAn();
        for (String child : _tree.getSuccessors(_allTokens.get(index).getTokenName())) {
            if (Var){
                switch (child){
                    case "BEGIN":
                        Var = false; //start progblock
                        index++;
                        break;
                    case "INTEGER":
                        index++;
                        for (int q = 0; q<variableIndex; q++){
                            int indexInVariableMas = search(_variableList, variableMas[q]);
                            if (indexInVariableMas >= 0) {
                                _variableList.get(indexInVariableMas).setVarType(typeMas[0]);
                                _variableList.get(indexInVariableMas).setDeclaration(true);
                            } else System.out.println("ERROR");
                        }
                        variableIndex = 0;
                        break;
                    case "REAL":
                        index++;
                        for (int q = 0; q<variableIndex; q++){
                            int indexInVariableMas = search(_variableList, variableMas[q]);
                            if (indexInVariableMas >= 0) {
                                _variableList.get(indexInVariableMas).setVarType(typeMas[3]);
                                _variableList.get(indexInVariableMas).setDeclaration(true);
                            } else System.out.println("ERROR");
                        }
                        variableIndex = 0;
                        break;
                    case "STRING":
                        index++;
                        for (int q = 0; q<variableIndex; q++){
                            int indexInVariableMas = search(_variableList, variableMas[q]);
                            if (indexInVariableMas >= 0) {
                                _variableList.get(indexInVariableMas).setVarType(typeMas[2]);
                                _variableList.get(indexInVariableMas).setDeclaration(true);
                            } else System.out.println("ERROR");
                        }
                        variableIndex = 0;
                        break;
                    case "BOOLEAN":
                        index++;
                        for (int q = 0; q<variableIndex; q++){
                            int indexInVariableMas = search(_variableList, variableMas[q]);
                            if (indexInVariableMas >= 0) {
                                _variableList.get(indexInVariableMas).setVarType(typeMas[1]);
                                _variableList.get(indexInVariableMas).setDeclaration(true);
                            } else System.out.println("ERROR");
                        }
                        variableIndex = 0;
                        break;
                    case ":":
                    case ";":
                    case ",":
                    case "VAR":
                        index++;
                        break;
                    default: //если встретили переменную
                        if (index == 0) {
                            _variableList.get(0).setVarType("");
                            _variableList.get(0).setDeclaration(true);
                            _variableList.get(0).setInitialization(true);
                            variablecount++;
                            index++;
                            index++;
                        } else {
                            addVarInMas(child, variableIndex);
                            variableIndex++;
                            variablecount++;
                            index++;
                            break;
                        }
                }
            } else {

                if (oncevar = false) {
                    analyse_table(_variableList, _tree, _allTokens);
                    search_double_variable(_variableList, _tree, _allTokens);
                    oncevar = true;
                }

                switch (child){
                    case ":=":
                        //index++;
                        if (_allTokens.get(index-1).getTokenType().equals("Variable")){

                            int indexInVariableMas = search(_variableList, _allTokens.get(index-1).getTokenName());
                            String left_part = _variableList.get(indexInVariableMas).getVarType().toUpperCase();
                            _variableList.get(indexInVariableMas).setInitialization(true);
                            String right_part;
                            int int_right_part = search(_variableList, _allTokens.get(index+1).getTokenName());

                            if (int_right_part >= 0) {
                                right_part = _variableList.get(int_right_part).getVarType().toUpperCase();
                            } else {
                                right_part = _allTokens.get(index + 1).getTokenType().toUpperCase();
                                if (right_part.equals("BRACKET")) {
                                    //index++;
                                    //continue;
                                    //break;
                                }
                            }
                            if (!left_part.equals(right_part) && !right_part.equals("BRACKET")) {
                                System.out.println("can not match types " + left_part + " and " + right_part +
                                        " in operation " +  child + " in line " + _allTokens.get(index).getLine());
                                    System.exit(1);
                            }
                        } else {
                            System.out.println("in operation " + child + " " + "in line " + _allTokens.get(index).getLine() + " " +
                                    _allTokens.get(index-1).getTokenType() + " stay left. ERROR");
                            System.exit(1);
                        }
                        index++;
                        break;
                    case "+":
                        //index++;
                        String first = _allTokens.get(index-1).getTokenName();
                        int firstindexinvarmas = search(_variableList, first);
                        String second = _allTokens.get(index+1).getTokenName();
                        int secondindexinvarmas = search(_variableList, second);

                        if (firstindexinvarmas >= 0 && secondindexinvarmas>= 0) {
                            first = _variableList.get(firstindexinvarmas).getVarType();
                            second = _variableList.get(secondindexinvarmas).getVarType();
                        } else {
                            if (firstindexinvarmas < 0 && secondindexinvarmas < 0) {
                                switch (_allTokens.get(index+1).getTokenType()){
                                    case "Real":
                                        second = "REAL";
                                        break;
                                    case "Integer":
                                        second = "INTEGER";
                                        break;
                                    case "String":
                                        second = "STRING";
                                        break;
                                }
                                switch (_allTokens.get(index-1).getTokenType()){
                                    case "Real":
                                        first = "REAL";
                                        break;
                                    case "Integer":
                                        first = "INTEGER";
                                        break;
                                    case "String":
                                        first = "STRING";
                                        break;
                                }
                            }
                            if (secondindexinvarmas <= 0 && firstindexinvarmas >=0){
                                first = _variableList.get(firstindexinvarmas).getVarType();
                                switch (_allTokens.get(index+1).getTokenType()){
                                    case "Real":
                                        second = "REAL";
                                        break;
                                    case "Integer":
                                        second = "INTEGER";
                                        break;
                                    case "String":
                                        second = "STRING";
                                        break;
                                }
                            } else {
                                System.out.println("can not match types " + first + " and " + second + " in operation "
                                        + child + " in line " + _allTokens.get(index).getLine());
                                System.exit(1);
                            }
                        }
                        if ((first.equals(second) && !first.equals("Unknown")) && !first.equals("BOOLEAN") ||
                                (first.equals("REAL") && second.equals("INTEGER")) ||
                                (first.equals("INTEGER") && second.equals("REAL")) ||
                                (first.equals(second) && second.equals("STRING"))){
                        } else {
                            System.out.println("can not match types " + first + " and " + second + " in operation "
                                    + child + " in line " + _allTokens.get(index).getLine());
                            System.exit(1);
                        }
                        index++;
                        break;
                    case "*":
                    case "-":
                        //index++;
                        first = _allTokens.get(index-1).getTokenName();
                        firstindexinvarmas = search(_variableList, first);
                        second = _allTokens.get(index+1).getTokenName();
                        secondindexinvarmas = search(_variableList, second);

                        if (firstindexinvarmas >= 0 && secondindexinvarmas>= 0) {
                            first = _variableList.get(firstindexinvarmas).getVarType();
                            second = _variableList.get(secondindexinvarmas).getVarType();
                        } else if (secondindexinvarmas <= 0){
                            first = _variableList.get(firstindexinvarmas).getVarType();
                            switch (_allTokens.get(index+1).getTokenType()){
                                case "Real":
                                    second = "REAL";
                                    break;
                                case "Integer":
                                    second = "INTEGER";
                                    break;
                                case "String":
                                    second = "STRING";
                                    break;
                            }
                        } else {
                            System.out.println("can not match types " + first + " and " + second + " in operation "
                                    + child + " in line " + _allTokens.get(index).getLine());
                            System.exit(1);
                        }

                        if (first.equals(second) && !first.equals("Unknown") && !first.equals("STRING") ||
                                (!first.equals("BOOLEAN") && !second.equals("BOOLEAN")) ||
                                (!(first.equals("STRING") && !second.equals("STRING"))) ||
                                !(!first.equals("STRING") && second.equals("STRING"))){
                        } else {
                            System.out.println("can not match types " + first + " and " + second + " in operation "
                            + child + " in line " + _allTokens.get(index).getLine());
                            System.exit(1);
                        }
                        index++;
                        break;
                    case "=":
                    case "<>":
                        //index++;
                        first = _allTokens.get(index-1).getTokenName();
                        firstindexinvarmas = search(_variableList, first);
                        second = _allTokens.get(index+1).getTokenName();
                        secondindexinvarmas = search(_variableList, second);

                        if (firstindexinvarmas >= 0 && secondindexinvarmas>= 0) {
                            first = _variableList.get(firstindexinvarmas).getVarType();
                            second = _variableList.get(secondindexinvarmas).getVarType();
                        } else if (secondindexinvarmas <= 0){
                            first = _variableList.get(firstindexinvarmas).getVarType();
                            switch (_allTokens.get(index+1).getTokenType()){
                                case "Real":
                                    second = "REAL";
                                    break;
                                case "Integer":
                                    second = "INTEGER";
                                    break;
                                case "String":
                                    second = "STRING";
                                    break;
                            }
                        }
                        if ((first.equals(second) && !first.equals("Unknown")) ||
                                (first.equals("REAL") && second.equals("INTEGER")) ||
                                (first.equals("INTEGER") && second.equals("REAL")) ||
                                (first.equals("STRING") && second.equals("STRING"))){
                        } else {
                            System.out.println("can not match types " + first + " and " + second + " in operation " +
                                    child);
                            System.exit(1);
                        }
                        index++;
                        break;
                    case "<=":
                    case ">=":
                        //index++;
                        first = _allTokens.get(index-1).getTokenName();
                        firstindexinvarmas = search(_variableList, first);
                        second = _allTokens.get(index+1).getTokenName();
                        secondindexinvarmas = search(_variableList, second);

                        if (firstindexinvarmas >= 0 && secondindexinvarmas>= 0) {
                            first = _variableList.get(firstindexinvarmas).getVarType();
                            second = _variableList.get(secondindexinvarmas).getVarType();
                        } else if (secondindexinvarmas <= 0){
                            first = _variableList.get(firstindexinvarmas).getVarType();
                            switch (_allTokens.get(index+1).getTokenType()){
                                case "Real":
                                    second = "REAL";
                                    break;
                                case "Integer":
                                    second = "INTEGER";
                                    break;
                                case "String":
                                    second = "STRING";
                                    break;
                            }
                        }
                        if ((first.equals(second) && !first.equals("Unknown")) ||
                                (first.equals("REAL") && second.equals("INTEGER")) ||
                                (first.equals("INTEGER") && second.equals("REAL"))){
                        } else {
                            System.out.println("can not match types " + first + " and " + second + " in operation " +
                                child);
                            System.exit(1);
                        }
                        index++;
                        break;
                    case ")":
                    case "(":
                    case ";":
                    case "END":
                    case "END.":
                    case "BEGIN":
                    case "FOR":
                    case "TO":
                    case "DO":
                    case "IF":
                    case "THEN":
                    case "ELSE":
                        index++;
                        break;
                    default:
                        index++;
                        break;
                }
            }
        }
        int c = _variableList.size();
        for (int w = variablecount; w<c; w++){
            _variableList.remove(variablecount);
        }
        c = _variableList.size();
        for (int w = 0; w<c; w++){
            System.out.println(_variableList.get(w).getVarName() + " " + _variableList.get(w).getVarType() + " " +
            _variableList.get(w).getDeclaration() + " " + _variableList.get(w).getInitialization());
        }
        //-------analyse variableList -----

    }

    private void addVarInMas(String child, int _variableIndex) {
        variableMas[_variableIndex] = child;
    }
    private int search(ArrayList<DataForSemantAn> _variableList, String input){
        //возвращает номер строки где есть искомая переменная. считаем c 0
        int i = 0;
        if (_variableList.size()!=0){
            for (i = 0; i<_variableList.size(); i++){
                if (_variableList.get(i).getVarName().equals(input)){
                    return i;
                }
            }
            if (i == _variableList.size() && !_variableList.get(i-1).getVarName().equals(input)){
                return -1;
            }
        } else i = -1;
        return i;
    }
}
