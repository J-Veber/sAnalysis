package com.veber;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class CodeGenerator {
    private String CText;
    protected ArrayList<TokenParser> tokenList = null;
    private Tree <TokenParser> parseTree = null;
    protected ArrayList<TokenParser> allTokens = new ArrayList<TokenParser>();
    public void generate(Tree<String> _tree, ArrayList<TokenParser> _allTokens){

        Tree<String> tree = _tree;
        String s = _tree.passByDeep(_allTokens);

        //Определяем файл
        //File file = new File("test\\second.txt");
        File file = new File("C:\\Users\\Юлия\\Desktop\\ConsoleApplication1\\ConsoleApplication1\\bin\\Release\\FINAL_PROGRAM.txt");


        try {
            //проверяем, что если файл не существует то создаем его
            if(!file.exists()){
                file.createNewFile();
            }

            //PrintWriter обеспечит возможности записи в файл
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());

            try {
                //Записываем текст в файл

                out.print(s);
                // out.print(strTokens);
                //   out.print(strTokens);

                // out.println(_tree.getSubTrees());
            } finally {
                //После чего мы должны закрыть файл
                //Иначе файл не запишется
                out.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
