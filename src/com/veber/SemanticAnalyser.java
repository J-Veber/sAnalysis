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

    public void analyse(ArrayList<DataForSemantAn> _variableList,Tree<String> _tree,
                        ArrayList<TokenParser> _allTokens){
        Boolean Var = true;
        DataForSemantAn obj = new DataForSemantAn();
        for (String child : _tree.getSuccessors(_allTokens.get(index).getTokenName())) {
            if (Var){
                switch (child){
                    case "BEGIN":
                        Var = false; //start progblock
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
                //analyse_table(_variableList, _tree, _allTokens);
                switch (child){
                    case ":=":
                        switch (child){
                            case "(":
                                index++;
                                break;
                            default:
                                int indexInVariableMas = search(_variableList, _allTokens.get(index).getTokenName());
                                if (indexInVariableMas >= 0) {
                                    _variableList.get(indexInVariableMas).setInitialization(true);
                                    index++;
                                } else index++;
                                break;
                        }
                        break;
                    case "+":
                        index++;
                        String first = _allTokens.get(index-1).getTokenName();
                        int firstindexinvarmas = search(_variableList, first);
                        String second = _allTokens.get(index+1).getTokenName();
                        int secondindexinvarmas = search(_variableList, second);

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
                                    + child);
                            System.exit(1);
                        }

                        if ((first.equals(second) && !first.equals("Unknown")) && !first.equals("BOOLEAN") ||
                                (first.equals("REAL") && second.equals("INTEGER")) ||
                                (first.equals("INTEGER") && second.equals("REAL")) ||
                                (first.equals(second) && second.equals("STRING"))){
                        } else {
                            System.out.println("can not match types " + first + " and " + second + " in operation "
                                    + child);
                            System.exit(1);
                        }
                        break;
                    case "*":
                    case "-":
                        index++;
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
                                    + child);
                            System.exit(1);
                        }

                        if (first.equals(second) && !first.equals("Unknown") &&
                                !first.equals("BOOLEAN") && !second.equals("BOOLEAN") &&
                                !(first.equals("STRING") && !second.equals("STRING")) &&
                                !(!first.equals("STRING") && second.equals("STRING"))){
                        } else {
                            System.out.println("can not match types " + first + " and " + second + " in operation "
                            + child);
                            System.exit(1);
                        }
                        break;
                    case "=":
                    case "<=":
                    case ">=":
                    case "!=":
                        index++;
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
                        break;
                    case "AND":
                    case "OR":
                    case ")":
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
