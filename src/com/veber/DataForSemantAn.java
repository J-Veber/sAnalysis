package com.veber;


import com.sun.org.apache.xpath.internal.operations.Bool;

/**
 * Created by Veiber on 01.11.2016.
 */
public class DataForSemantAn {
    private String varName;
    private String varType;
    private Boolean declaration;
    private Boolean initialization;
    private int line;

    public DataForSemantAn() {
        varName = "";
        varType = "";
        declaration = false;
        initialization = false;
        line = 0;
    }

    public void setVarName(String input){
        varName = input;
    }
    public void setVarType(String input){
        varType = input;
    }
    public void setDeclaration(Boolean input){
        declaration = input;
    }
    public void setInitialization(Boolean input){
        initialization = input;
    }
    public void setLine(int input){
        line = input;
    }
    public int getLine(){
        return line;
    }
    public String getVarName(){
        return varName;
    }
    public String getVarType(){
        return varType;
    }
    public Boolean getDeclaration(){
        return declaration;
    }
    public Boolean getInitialization(){
        return initialization;
    }
    public void print(){
        System.out.println(varName + " ; " + varType + " ; " + declaration + " ; " + initialization + " ; " + line);
    }
}
