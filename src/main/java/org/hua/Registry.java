package org.hua;

/**
 * This code is part of the lab exercises for the Compilers course at Harokopio
 * University of Athens, Dept. of Informatics and Telematics.
 */


import java.util.HashMap;
import java.util.Map;
import org.hua.ast.ASTNode;
import org.hua.symbol.SymTable;
import org.hua.symbol.SymTableEntry;
import org.objectweb.asm.Type;

/**
 * Global registry (Singleton pattern)
 */
public class Registry {

    ASTNode root;
    Map<Type,SymTable<SymTableEntry>> classes = new HashMap<Type, SymTable<SymTableEntry>>();

    public Map<Type, SymTable<SymTableEntry>> getClasses() {
        return classes;
    }

    public void setClasses(Map<Type, SymTable<SymTableEntry>> classes) {
        this.classes = classes;
    }
    
    public void addClass(Type type, SymTable<SymTableEntry> symTable){
        classes.put(type, symTable);
    }
    
    public SymTable<SymTableEntry> getExistingClass(Type type){
        return classes.get(type);
    }
    
    public boolean classExists(Type type){
        return classes.containsKey(type);
    }

    private Registry() {
        root = null;
    }

    private static class SingletonHolder {

        public static final Registry instance = new Registry();

    }

    public static Registry getInstance() {
        return SingletonHolder.instance;
    }

    public ASTNode getRoot() {
        return root;
    }

    public void setRoot(ASTNode root) {
        this.root = root;
    }

}
