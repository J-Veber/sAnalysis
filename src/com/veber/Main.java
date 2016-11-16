package com.veber;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        String wayToFile = "C:\\Users\\Veiber\\IdeaProjects\\sAnalysis\\test\\ListLexem2.txt";
        try (FileInputStream fin = new FileInputStream(wayToFile)){

            ArrayList<TokenParser> allTokens = new ArrayList<TokenParser>();
            int i = 0;
            List<String> lines = Files.readAllLines(Paths.get(wayToFile), StandardCharsets.UTF_8);
            ArrayList<DataForSemantAn> dataForSemantAnList = new ArrayList<DataForSemantAn>();

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
                    //System.out.print(allTokens.get(i).getTokenName());
                    if (curObj.getTokenType().equals("Variable")){
                        DataForSemantAn obj = new DataForSemantAn();
                        obj.setVarName(curObj.getTokenName());
                        //obj.print();
                        dataForSemantAnList.add(obj);
                    }
                    i++;
                    //curObj.clearCurrentData();
                }
            }
            //create tree
            TokenParser Pascal = new TokenParser();
            Pascal.init(allTokens);


        } catch (IOException ex) { System.out.println(ex.getMessage()); }

    }
}
