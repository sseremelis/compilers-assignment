package org.hua.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.tree.JumpInsnNode;
import org.hua.Registry;
import org.hua.symbol.HashSymTable;
import org.hua.symbol.LocalIndexPool;
import org.objectweb.asm.Type;

import org.hua.symbol.SymTable;
import org.hua.symbol.SymTableEntry;

/**
 * Class with static helper methods for AST handling
 */
public class ASTUtils {

    public static final String SYMTABLE_PROPERTY = "SYMTABLE_PROPERTY";
    public static final String IS_BOOLEAN_EXPR_PROPERTY = "IS_BOOLEAN_EXPR_PROPERTY";
    public static final String TYPE_PROPERTY = "TYPE_PROPERTY";
    public static final String IS_FUNCTION = "IS_FUNCTION_PROPERTY";
    public static final String PARAMETERS_PROPERTY = "PARAMETERS_PROPERTY";
    public static final String IS_STATIC = "IS_STATIC";
    public static final String LOCAL_INDEX_POOL_PROPERTY = "LOCAL_INDEX_POOL_PROPERTY";
    public static final String NEXT_LIST_PROPERTY = "NEXT_LIST_PROPERTY";
    public static final String BREAK_LIST_PROPERTY = "BREAK_LIST_PROPERTY";
    public static final String CONTINUE_LIST_PROPERTY = "CONTINUE_LIST_PROPERTY";
    public static final String TRUE_LIST_PROPERTY = "TRUE_LIST_PROPERTY";
    public static final String FALSE_LIST_PROPERTY = "FALSE_LIST_PROPERTY";

    private ASTUtils() {
    }

    @SuppressWarnings("unchecked")
    public static SymTable<SymTableEntry> getEnv(ASTNode node) {
        return (SymTable<SymTableEntry>) node.getProperty(SYMTABLE_PROPERTY);
    }

    @SuppressWarnings("unchecked")
    public static SymTable<SymTableEntry> getSafeEnv(ASTNode node)
            throws ASTVisitorException {
        SymTable<SymTableEntry> symTable = (SymTable<SymTableEntry>) node
                .getProperty(SYMTABLE_PROPERTY);
        if (symTable == null) {
            ASTUtils.error(node, "Symbol table not found.");
        }
        return symTable;
    }

    public static void setEnv(ASTNode node, SymTable<SymTableEntry> env) {
        node.setProperty(SYMTABLE_PROPERTY, env);
    }

    public static boolean isBooleanExpression(Expression node) {
        Boolean b = (Boolean) node.getProperty(IS_BOOLEAN_EXPR_PROPERTY);
        if (b == null) {
            return false;
        }
        return b;
    }

    public static void setBooleanExpression(Expression node, boolean value) {
        node.setProperty(IS_BOOLEAN_EXPR_PROPERTY, value);
    }

    public static Type getType(ASTNode node) {
        return (Type) node.getProperty(TYPE_PROPERTY);
    }

    public static Type getSafeType(ASTNode node) throws ASTVisitorException {
        Type type = (Type) node.getProperty(TYPE_PROPERTY);
        if (type == null) {
            ASTUtils.error(node, "Type not found.");
        }
        return type;
    }

    public static void setType(ASTNode node, Type type) {
        node.setProperty(TYPE_PROPERTY, type);
    }

    public static void error(ASTNode node, String message)
            throws ASTVisitorException {
        throw new ASTVisitorException(node.getLine() + ":" + node.getColumn()
                + ": " + message);
    }

    public static void setIsFunction(ASTNode node, boolean is) {
        node.setProperty(IS_FUNCTION, is);
    }

    public static void setParameters(String nodeId, SymTable<SymTableEntry> params) {
        boolean paramsSet = false;
        Map<Type, SymTable<SymTableEntry>> classes = Registry.getInstance().getClasses();
        Iterator<Map.Entry<Type, SymTable<SymTableEntry>>> entries = classes.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Type, SymTable<SymTableEntry>> entry = entries.next();
            if (entry.getValue().lookup(nodeId) != null) {
                entry.getValue().lookup(nodeId).setParameters(params);
                paramsSet = true;
            }
        }
        if (!paramsSet) {
            System.out.println("No function with the name " + nodeId + " was found to set its parameters.");
        }
    }

    public static void setParameter(String nodeId, SymTableEntry param) {
        boolean paramsSet = false;
        Map<Type, SymTable<SymTableEntry>> classes = Registry.getInstance().getClasses();
        Iterator<Map.Entry<Type, SymTable<SymTableEntry>>> entries = classes.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Type, SymTable<SymTableEntry>> entry = entries.next();
            SymTableEntry sEntry = entry.getValue().lookup(nodeId);//sEntry: function sym table
            if (sEntry != null) {
                if (sEntry.getParameters() == null) {
                    sEntry.setParameters(new HashSymTable<SymTableEntry>());
                }
                sEntry.setParameter(param.getId(), param);
                paramsSet = true;
            }
        }
        if (!paramsSet) {
            System.out.println("No function with the name " + nodeId + " was found to set its parameter.");
        }
    }

    public static SymTable<SymTableEntry> getParameters(ASTNode node) throws ASTVisitorException {
        return (SymTable<SymTableEntry>) node.getProperty(PARAMETERS_PROPERTY);
    }

    public static SymTable<SymTableEntry> getParameters(String nodeId) throws ASTVisitorException {
        Map<Type, SymTable<SymTableEntry>> classes = Registry.getInstance().getClasses();
        Iterator<Map.Entry<Type, SymTable<SymTableEntry>>> entries = classes.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Type, SymTable<SymTableEntry>> entry = entries.next();
            if (entry.getValue().lookup(nodeId) != null) {
                return entry.getValue().lookup(nodeId).getParameters();
            }
        }
        return null;
    }

    public static void setParameterType(ASTNode node, String id, Type type) throws ASTVisitorException {
        SymTable<SymTableEntry> params = getParameters(node);

        params.lookup(id).setType(type);
    }

    public static void setParameterType(String nodeId, String id, Type type) throws ASTVisitorException {
        boolean paramsSet = false;
        Map<Type, SymTable<SymTableEntry>> classes = Registry.getInstance().getClasses();
        Iterator<Map.Entry<Type, SymTable<SymTableEntry>>> entries = classes.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Type, SymTable<SymTableEntry>> entry = entries.next();
            SymTableEntry sEntry = entry.getValue().lookup(nodeId); //function
            if (sEntry != null) {
                SymTable<SymTableEntry> params = sEntry.getParameters();
                SymTableEntry parameter = params.lookup(id); //parameter

                if (parameter != null) {
                    parameter.setType(type);
                    paramsSet = true;
                }
            }
        }
        if (!paramsSet) {
            throw new ASTVisitorException("@@@ No parameter with the name " + id + " was found to set its type.");
        }
    }

    public static void setIsStatic(ASTNode node, String bool) throws ASTVisitorException {
        node.setProperty(IS_STATIC, bool);
    }

    public static boolean getIsStatic(ASTNode node) throws ASTVisitorException {
        if ((String) node.getProperty(IS_STATIC) == "yes") {
            return true;
        }
        return false;
    }

    public static void setLocalIndexPool(ASTNode node, LocalIndexPool pool) {
        node.setProperty(LOCAL_INDEX_POOL_PROPERTY, pool);
    }
    
    @SuppressWarnings("unchecked")
    public static LocalIndexPool getSafeLocalIndexPool(ASTNode node)
            throws ASTVisitorException {
        LocalIndexPool lip = (LocalIndexPool) node.getProperty(LOCAL_INDEX_POOL_PROPERTY);
        if (lip == null) {
            ASTUtils.error(node, "Local index pool not found.");
        }
        return lip;
    }
    
    @SuppressWarnings("unchecked")
    public static List<JumpInsnNode> getTrueList(Expression node) {
        List<JumpInsnNode> l = (List<JumpInsnNode>) node.getProperty(TRUE_LIST_PROPERTY);
        if (l == null) {
            l = new ArrayList<JumpInsnNode>();
            node.setProperty(TRUE_LIST_PROPERTY, l);
        }
        return l;
    }

    public static void setTrueList(Expression node, List<JumpInsnNode> list) {
        node.setProperty(TRUE_LIST_PROPERTY, list);
    }

    @SuppressWarnings("unchecked")
    public static List<JumpInsnNode> getFalseList(Expression node) {
        List<JumpInsnNode> l = (List<JumpInsnNode>) node.getProperty(FALSE_LIST_PROPERTY);
        if (l == null) {
            l = new ArrayList<JumpInsnNode>();
            node.setProperty(FALSE_LIST_PROPERTY, l);
        }
        return l;
    }

    public static void setFalseList(Expression node, List<JumpInsnNode> list) {
        node.setProperty(FALSE_LIST_PROPERTY, list);
    }

    @SuppressWarnings("unchecked")
    public static List<JumpInsnNode> getNextList(Statement node) {
        List<JumpInsnNode> l = (List<JumpInsnNode>) node.getProperty(NEXT_LIST_PROPERTY);
        if (l == null) {
            l = new ArrayList<JumpInsnNode>();
            node.setProperty(NEXT_LIST_PROPERTY, l);
        }
        return l;
    }

    public static void setNextList(Statement node, List<JumpInsnNode> list) {
        node.setProperty(NEXT_LIST_PROPERTY, list);
    }

    @SuppressWarnings("unchecked")
    public static List<JumpInsnNode> getBreakList(Statement node) {
        List<JumpInsnNode> l = (List<JumpInsnNode>) node.getProperty(BREAK_LIST_PROPERTY);
        if (l == null) {
            l = new ArrayList<JumpInsnNode>();
            node.setProperty(BREAK_LIST_PROPERTY, l);
        }
        return l;
    }

    public static void setBreakList(Statement node, List<JumpInsnNode> list) {
        node.setProperty(BREAK_LIST_PROPERTY, list);
    }

    @SuppressWarnings("unchecked")
    public static List<JumpInsnNode> getContinueList(Statement node) {
        List<JumpInsnNode> l = (List<JumpInsnNode>) node.getProperty(CONTINUE_LIST_PROPERTY);
        if (l == null) {
            l = new ArrayList<JumpInsnNode>();
            node.setProperty(CONTINUE_LIST_PROPERTY, l);
        }
        return l;
    }

    public static void setContinueList(Statement node, List<JumpInsnNode> list) {
        node.setProperty(CONTINUE_LIST_PROPERTY, list);
    }

}
