package com.veber;

/**
 * Created by Александра on 06.01.2017.
 */

import java.util.ArrayList;
import java.io.*;

public class CodeGenerator {

    private String CText;
    protected ArrayList<TokenParser> tokenList = null;
    private Tree <TokenParser> parseTree = null;
    protected ArrayList<TokenParser> allTokens = new ArrayList<TokenParser>();
    public void generate(Tree<String> _tree, ArrayList<TokenParser> _allTokens){

        Tree<String> tree = _tree;
        String s = _tree.passByDeep(_allTokens);

        //Определяем файл
        File file = new File("second.txt");
        //File file = new File("first.txt");


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
