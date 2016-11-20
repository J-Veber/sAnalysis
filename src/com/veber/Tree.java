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

    Tree() {
        throw new UnsupportedOperationException("Not supported yet.");
        //To change body of generated methods, choose Tools | Templates.
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

    public Tree<T> setAsParent(T parentRoot) {
        Tree<T> t = new Tree<T>(parentRoot);
        t.leafs.add(this);
        this.parent = t;
        t.locate = this.locate;
        t.locate.put(head, this);
        t.locate.put(parentRoot, t);
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
    public Collection<T> getSuccessors(T root) {
        Collection<T> successors = new ArrayList<T>();
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
//    private Boolean search_in_tree(T inputString){
//        Collection<T> search = getSuccessors(inputString);
//        while (search.size() != 0)
//        if (search.size() != 0) return true;
//        else return false;
//    }
    public void sem_analyse(Tree<String> _tree, ArrayList<DataForSemantAn> _inputArrayList) {
        int counter_for_inputArraylist = 0;
        for (String child : _tree.getSuccessors("PROGRAM")) {
            //int counter_for_inputArraylist = 1;
            if (counter_for_inputArraylist == 0) {
                _inputArrayList.get(counter_for_inputArraylist).setVarType("Keyword");
                counter_for_inputArraylist++;
            } else {
                //-----add datatypes for all variable ------
                String[] mas_for_var = new String[15];
                int i = 0;
                if (!child.equals("BEGIN")){
                    mas_for_var[i] = child;
                    i++;
                } else { //----- if met DATATYPE we should assign mas_for_var
                    for (int q = 0; q<i; q++) {
                        switch (child){
                            case "STRING":
                                _inputArrayList.get(counter_for_inputArraylist).setVarType("String");
                                counter_for_inputArraylist++;
                                break;
                            case "INTEGER":
                                _inputArrayList.get(counter_for_inputArraylist).setVarType("Integer");
                                counter_for_inputArraylist++;
                                break;
                            case "BOOLEAN":
                                _inputArrayList.get(counter_for_inputArraylist).setVarType("Boolean");
                                counter_for_inputArraylist++;
                                break;
                            case "REAL":
                                _inputArrayList.get(counter_for_inputArraylist).setVarType("Real");
                                counter_for_inputArraylist++;
                                break;
                            default:
                                break;
                        }
                    }
                    i = 0;
                }
            }


        }
    }
}
