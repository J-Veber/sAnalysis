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
    public SemanticAnalyser(){
        index = 0;
        variableIndex = 0;
    }

    public void analyse(ArrayList<DataForSemantAn> _variableList,Tree<String> _tree, ArrayList<TokenParser> _allTokens){
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
                            index++;
                            index++;
                        } else {
                            addVarInMas(child, variableIndex);
                            variableIndex++;
                            index++;
                            break;
                        }
                }
            } else {
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
                                    //indexInVariableMas++;
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
                        }
                        if (first.equals(second)){

                        } else {
                            System.out.println("can not match types " + first + " and " + second + " in operation +");
                        }
                        break;
                    case "-":
                    case "*":
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
