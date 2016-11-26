package com.veber;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static Tree<String> sTree;
    public static void main(String[] args) {

        String wayToFile = "C:\\Users\\Veiber\\IdeaProjects\\sAnalysis\\test\\ListLexem4.txt";
        try (FileInputStream fin = new FileInputStream(wayToFile)){

            ArrayList<TokenParser> allTokens = new ArrayList<TokenParser>();
            int i = 0;
            int q = 0;
            List<String> lines = Files.readAllLines(Paths.get(wayToFile), StandardCharsets.UTF_8);
            ArrayList<DataForSemantAn> dataForSemantAnList = new ArrayList<DataForSemantAn>();
            if (lines.size() == 0 ){
                System.out.println("FILE IS EMPTY");
            } else {
                for(String line: lines){
                    //System.out.println(line);
                    if (line != "") {
                        TokenParser curObj = new TokenParser();
                        curObj.setTokenName(line.substring(0,21).replaceAll(" ", ""));
                        curObj.setTokenType(line.substring(26,42).replaceAll(" ", ""));
                        curObj.setLine(Integer.parseInt(line.substring(42,44).replaceAll(" ", "")));
                        curObj.setCurrentPosition(Integer.parseInt(line.substring(44,line.length()).replaceAll(" ", "")));
                        //curObj.print();
                        allTokens.add(i, curObj);

                        if (curObj.getTokenType().equals("Variable")){
                            DataForSemantAn obj = new DataForSemantAn();
                            obj.setVarName(curObj.getTokenName());
                            obj.setLine(curObj.getLine());
                            obj.setVarType("Unknown");
                            obj.setInitialization(false);
                            obj.setDeclaration(false);
                            //obj.print();

                            dataForSemantAnList.add(q, obj);
                            System.out.println(dataForSemantAnList.get(q).getVarName() + " " +
                                    dataForSemantAnList.get(q).getVarType()
                                    + " " +
                                    dataForSemantAnList.get(q).getDeclaration().toString() + " " +
                                    dataForSemantAnList.get(q).getInitialization().toString());
                            q++;
                        }
                        i++;
                    }
                }
//create tree ans syntax parse Julia's lexems
                TokenParser Pascal = new TokenParser();
                if (allTokens.get(0).getTokenName().equals("PROGRAM")){
                    sTree = new Tree<>(allTokens.get(0).getTokenName());
                    Pascal.init(sTree, allTokens);
                }

//Semantic analyse
                int ii = 0;
                SemanticAnalyser analyser = new SemanticAnalyser();
                analyser.analyse(dataForSemantAnList, sTree, allTokens);

            }

        } catch (IOException ex) { System.out.println(ex.getMessage()); }

    }
}
