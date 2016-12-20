package com.veber;

import java.util.ArrayList;

/**
 * Created by Лавров on 10.12.2016
 */
public class Optimizator {

    @SuppressWarnings("Convert2streamapi")
    static Tree optimizator(Tree<String> tree, ArrayList<DataForSemantAn> list) {
        Tree<String> newTree = new Tree<>(tree.getHead());
        ArrayList<DataForSemantAn> newList = new ArrayList<>();
        for (int i = 1; i < list.size(); i++) {
            DataForSemantAn item = list.get(i);
            if (!item.getInitialization() && item.getDeclaration()) {
                newList.add(item);
            }
        }
        ArrayList<String> arrayList = tree.getSuccessors(tree.getHead());
        for (int i = 0; i < arrayList.size(); i++) {
            String item = arrayList.get(i);
            if (isUselessVar(newList, item)) {
                i++;
                continue;
            }
            newTree.addLeaf(item);
            if (isIncrement(arrayList, i, item)) {
                newTree.addLeaf("++");
                //TODO: Achtung!
                i = i + 4;
                continue;
            }
            if (isDecrement(arrayList, i, item)) {
                newTree.addLeaf("--");
                i = i + 4;
            }
        }
        return newTree;
    }

    private static boolean isUselessVar(ArrayList<DataForSemantAn> list, String s) {
        for (DataForSemantAn item : list) {
            if (item.getVarName().equals(s)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isIncrement(ArrayList<String> list, int i, String s) {
        if (i + 1 < list.size() && list.get(i + 1).equals(":=")) {
            if (i + 2 < list.size() && list.get(i + 2).equals(s)) {
                if (i + 3 < list.size() && list.get(i + 3).equals("+")) {
                    if (i + 4 < list.size() && list.get(i + 4).equals("1")) {
                        if (i + 5 < list.size() && list.get(i + 5).equals(";")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    private static boolean isDecrement(ArrayList<String> list, int i, String s) {
        if (i + 1 < list.size() && list.get(i + 1).equals(":=")) {
            if (i + 2 < list.size() && list.get(i + 2).equals(s)) {
                if (i + 3 < list.size() && list.get(i + 3).equals("-")) {
                    if (i + 4 < list.size() && list.get(i + 4).equals("1")) {
                        if (i + 5 < list.size() && list.get(i + 5).equals(";")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}