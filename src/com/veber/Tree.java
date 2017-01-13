package com.veber;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Veiber on 23.10.2016.
 */
public class Tree<T> {
    private T head;
    private ArrayList<Tree<T>> leafs = new ArrayList<Tree<T>>();
    private Tree<T> parent = null;
    private HashMap<T, Tree<T>> locate = new HashMap<T, Tree<T>>();


    public Tree(T head) {
        this.head = head;
        locate.put(head, this);
    }

    public void addLeaf(T root, T leaf) {
        if (locate.containsKey(root)) {
            locate.get(root).addLeaf(leaf);
        } else {
            addLeaf(root).addLeaf(leaf);
        }
    }

    public Tree<T> addLeaf(T leaf) {
        Tree<T> t = new Tree<T>(leaf);
        leafs.add(t);
        t.parent = this;
        t.locate = this.locate;
        locate.put(leaf, t);
        return t;
    }

    public T getHead() {
        return head;
    }
    public Tree<T> getTree(T element) {
        return locate.get(element);
    }

    public Tree<T> getParent() {
        return parent;
    }

    public ArrayList<T> getSuccessors(T root) {
        ArrayList<T> successors = new ArrayList<T>();
        Tree<T> tree = getTree(root);
        if (null != tree) {
            for (Tree<T> leaf : tree.leafs) {
                successors.add(leaf.head);
            }
        }
        return successors;
    }

    public Collection<Tree<T>> getSubTrees() {
        return leafs;
    }

    public static <T> Collection<T> getSuccessors(T of, Collection<Tree<T>> in) {
        for (Tree<T> tree : in) {
            if (tree.locate.containsKey(of)) {
                return tree.getSuccessors(of);
            }
        }
        return new ArrayList<T>();
    }

    @Override
    public String toString() {
        return printTree(0);
    }

    private static final int indent = 2;

    public String printTree(int increment) {
        String s = "";
        String inc = "";
        for (int i = 0; i < increment; ++i) {
            inc = inc + " ";
        }
        s = inc + head;
        for (Tree<T> child : leafs) {
            s += "\n" + child.printTree(increment + indent);
        }
        return s;
    }

    //печать для генератора кода (фигня какая-то короче)
    public String printTreeGen(){
        String s = "";
        s = s + head;
        for (Tree<T> child : leafs) {
            s += "\n" + child.printTreeGen();
        }
        return s;
    }

    private int minElemOfArray(String[] _array){
        int min =_array.length;
        for(int i = 0; i != _array.length; i ++){
            if(_array[i]!=null && i<min)
                min = i;
        }
        return min;
    };
    private int minElemOfArrayMoreThenOne(String[] _array){
        int min =_array.length;
        for(int i = 0; i != _array.length; i ++){
            if(_array[i]!=null && i<min&&i>1)
                min = i;
        }
        return min;
    };

    private int maxElemOfIntArray(int[] _array){
        int max =0;
        for(int i = 0; i != _array.length; i ++){
            if(_array[i]>max)
                max = _array[i];
        }
        return max;
    }

    //обход дерева
    public String passByDeep(ArrayList<TokenParser> _allTokens) {
        int counter = 0;
        String sygn = " ";
        String str = " ";
        boolean flagBegin = false;
        int size = _allTokens.size();
        String strTokens = " ";
        int i = 0;
        int j = 0;
        String[] StrTokensVariable = new String[size];
        String[] StrTokensComma = new String[size];
        String[] StrTokensEnd = new String[size];
        String[] StrTokensString = new String[size];
        String[] StrTokensInteger = new String[size];
        String[] StrTokensBoolean = new String[size];
        String[] StrTokensSymbol = new String[size];
        String[] StrTokensBegin = new String[size];

        String[] StrAll = new String[size];

        int countTabIfElse = 0;
        int countTabWhile = 0;
        int countTabFor = 0;
        // int count = 0;
        // int count = 0;
        // int count = 0;
        // int count = 0;
        // int count = 0;



        while (i < size && j < size) {

            String VarStrType = _allTokens.get(i).getTokenType().toString();
            String VarStrName = _allTokens.get(i).getTokenName().toString();
            // for (int j = 0; j<size;j++){

            //создаем массив под позиции переменных
            if (VarStrType.equals("Variable")) {
                StrTokensVariable[j] = VarStrName;
            }

            //создаем массив под позиции запятых
            if (VarStrType.equals("Comma")) {
                StrTokensComma[j] = VarStrName;
            }

            //создаем массив под позиции строк
            if (VarStrType.equals("String")) {
                StrTokensString[j] = VarStrName;
            }

            //создаем массив под позиции эндов
            if (VarStrType.equals("End")) {
                StrTokensEnd[j] = VarStrName;
            }

            //создаем массив под позиции двоеточий
            if (VarStrType.equals("Simbol")) {
                StrTokensSymbol[j] = VarStrName;
            }

            //создаем массив под позиции целых
            if (VarStrType.equals("Integer")) {
                StrTokensInteger[j] = VarStrName;
            }

            //создаем массив под позиции первого бегина
            if (VarStrType.equals("Keyword") && VarStrName.equals("BEGIN"))
                StrTokensBegin[j] = VarStrName;

            i++;
            j++;

        }
        str = "int main(){";//+"\n";

        int minForInt = minElemOfArray(StrTokensInteger);
        int minForVar = minElemOfArray(StrTokensVariable);

        int minForComma = minElemOfArray(StrTokensComma);
        int minForStr = minElemOfArray(StrTokensString);
        int minForBegin = minElemOfArray(StrTokensBegin);
        int minForSym = minElemOfArray(StrTokensSymbol);

        for (Tree<T> child : leafs) {
            sygn = child.head.toString();
            T var = child.getHead();


            //убираем переменную с названием программы
            if (sygn.equals(StrTokensVariable[minForVar])) {
                if (minForVar <= 1) {
                    sygn = "";
                    // StrTokensVariable[minForVar]=;
                    minForVar = minElemOfArrayMoreThenOne(StrTokensVariable);
                }
            }

            //убираем первый бегин

            if (sygn.equals(StrTokensBegin[minForBegin]) && counter < 17 && flagBegin == false) {
                sygn = "";
                flagBegin = true;
            }

            //вставляем запятые
//            if (sygn.equals(StrTokensComma[minForComma]) && counter < 20)//==4)
            //              sygn = ",";

            //убираем двоеточия
            if (sygn.equals(StrTokensSymbol[minForSym])/*&&counter<15*/)
                sygn = "";

            //вставляет end и переход на новую строку
            if (sygn.equals(StrTokensEnd[2]) && counter == 1)
                sygn = "" + "\n";

            //перемещение названия типа переменной и самой переменной

            switch (sygn) {
                //начало и конец программы
                case "BEGIN":
                    sygn = "{ " + "\n" + " ";
                    //str = str + sygn+"\n";
                    break;
                case "END":
                    sygn = "}";// +"\n";
                    //str = str + sygn+"\n";
                    break;
                case "END.":
                    sygn = "} ";// +"\n";
                    break;

                //съедаемые слова
                case "VAR":
                    sygn = "";
                    str = str + sygn;
                    break;

                //условия и циклы
                case "IF":
                    sygn = "if" + " (";
                    countTabIfElse++;
                    break;

                case "THEN":
                    sygn = ")" + "\n";
                    break;
                case "ELSE":
                    sygn = " else ";
                    break;

                case "FOR":

                    //String varI = "";
                    // varI = child.getSuccessors(var).toString();
                    sygn = "for (";//+ varI;
                    countTabFor++;
                    break;
                case "TO":
                    sygn = ", " + StrTokensVariable[minForVar] + "< ";
                    break;
                case "DO":
                    sygn = ", " + StrTokensVariable[minForVar] + "++) " + "\n";
                    break;

                case "WHILE":
                    sygn = "while (";
                    countTabWhile++;
                    break;

                // переменные
                case "INTEGER":
                    sygn = " int ";
                    break;
                case "BOOLEAN":
                    sygn = "bool";
                    break;
                case "STRING":
                    sygn = "string";
                    break;

                //знаки
                case ":=":
                    sygn = " = ";
                    break;
                case ";":
                    sygn = ";" + "\n";
                    break;
                case "=":
                    sygn = "==";
                    break;
                case "<=":
                    sygn = "<=";
                    break;
                case ">=":
                    sygn = ">=";
                    break;
                case "<>":
                    sygn = "!=";
                    break;

            }
            StrAll[counter] = sygn;
            //str = str+StrAll[counter];

            counter++;
        }

        int count = 0;
        int markForString=0;


        for (count = 0; count < counter; count++) {
            if (StrAll[count] == " int ") {
                StrAll[2] = "int ";
                StrAll[count] = "";
                markForString=count+2;
            }
            if (StrAll[count] == "string")
            {
                if (markForString == 0) {
                    StrAll[markForString]="\n" + " string "+StrAll[markForString];
                    StrAll[markForString+1]= "";
                    StrAll[count]="";
                }
                else{
                    StrAll[markForString]="string "+StrAll[markForString];
                    StrAll[count]="";
                }

            }
        }
//ТАБУЛЯЦИЯ

        boolean flagWhile=false;
        int FlagWhile[]= new int[10];

        boolean flagIf=false;
        int FlagIf[]= new int[10];

        boolean flagFor=false;
        int FlagFor[]=new int[10];
        int Flag =0;
        String STRALL[] = new String[counter];

        for (count=0;count<counter; count++){
            STRALL[count]=StrAll[count];
        }

        while (Flag<10){
            for (count = 0; count<counter; count++){
                if (STRALL[count].contains("if")){
                    FlagIf[Flag]=count;
                    STRALL[count]="";
                    break;
                }
                if (STRALL[count].contains("while")) {
                    FlagWhile[Flag] = count;
                    STRALL[count]="";
                    break;
                }
                if (STRALL[count].contains("for")){
                    FlagFor[Flag]=count;
                    STRALL[count]="";
                    break;
                }
            }


            Flag++;
        }

        int maxSpaceForBeg=maxElemOfIntArray(FlagFor);

        for (count =0; count<counter-2; count++)
            if (StrAll[count].contains("\n"))
                StrAll[count+1]=" "+StrAll[count+1];

        //for (count=maxSpaceForBeg;)

        for (Flag=0;Flag<10;Flag++) {
            if (FlagIf[Flag]!=0) {
                for (count = FlagIf[Flag]; count < counter-3; count++) {
                    if (StrAll[count].contains("\n"))
                        StrAll[count + 1] = " " + StrAll[count + 1];
                }
            }
            if (FlagFor[Flag]!=0)
                for (count = FlagFor[Flag]; count < counter-6; count++) {
                    if (StrAll[count].contains("\n"))
                        StrAll[count + 1] = " " + StrAll[count + 1];
                }
            if (FlagWhile[Flag]!=0)
                for (count = FlagFor[Flag]; count < counter-6; count++) {
                    if (StrAll[count].contains("\n"))
                        StrAll[count + 1] = " " + StrAll[count + 1];
                }
        }

        for (count =0; count<counter-3; count++)
            if (StrAll[count].contains("while")){
                StrAll[count+4]=")";
                int countWhile=count+5;
                StrAll[count]=" "+StrAll[count];
                for (count = countWhile; count<counter-6; count++)
                    // if (StrAll[count].contains("{ \n"))
                    //  StrAll[count]=" " +StrAll[count];
                    if (StrAll[count].contains("\n")) {
                        StrAll[count + 1] = " " + StrAll[count+ 1];
                        for (count =countWhile; count<counter-3; count++) {
                            if (StrAll[count].contains("if")) {
                                int countIf = count;
                                StrAll[count] = " " + StrAll[count];
                                for (count = countIf; count < counter - 6; count++)
                                    if (StrAll[count].contains("\n"))
                                        StrAll[count + 1] = "  " + StrAll[count + 1];
                            }

                        }
                        if (StrAll[count].contains("for (")){
                            int countFor=count;
                            for (count = countFor; count<counter-4; count++)
                                if (StrAll[count].contains("\n")) {
                                    StrAll[count + 1] = " " + StrAll[count + 1];
                                    for (count =0; count<counter-3; count++)
                                        if (StrAll[count].contains("if")){
                                            // int countIf=count;
                                            for (count = countFor; count<counter-4; count++)
                                                if (StrAll[count].contains("\n"))
                                                    StrAll[count+1]=" "+StrAll[count+1];
                                        }
                                }
                        }
                        for (count =0; count<counter-5; count++)

                            if (StrAll[count].contains("if")){
                                int countIf=count;
                                for (count = countIf; count<counter-4; count++)
                                    if (StrAll[count].contains("\n"))
                                        StrAll[count+1]=" "+StrAll[count+1];
                            }
                    }
            }

        for (count =0; count<counter; count++)
            str = str + StrAll[count];

        return str;
    }
}
