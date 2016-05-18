package org.hua.ast.visitors;

import org.hua.Registry;
import org.hua.ast.ASTUtils;
import org.hua.ast.ASTVisitor;
import org.hua.ast.ASTVisitorException;
import org.hua.ast.AccessorExpression;
import org.hua.ast.AssignmentStatement;
import org.hua.ast.BinaryExpression;
import org.hua.ast.BreakStatement;
import org.hua.ast.ClassDefinition;
import org.hua.ast.CompUnit;
import org.hua.ast.CompoundStatement;
import org.hua.ast.ContinueStatement;
import org.hua.ast.DeclarationStatement;
import org.hua.ast.Expression;
import org.hua.ast.ExpressionList;
import org.hua.ast.ExpressionStatement;
import org.hua.ast.FFDefinition;
import org.hua.ast.FFDefinitionsList;
import org.hua.ast.FieldDefinition;
import org.hua.ast.FloatLiteralExpression;
import org.hua.ast.FunctionDefinition;
import org.hua.ast.Identifier;
import org.hua.ast.IfElseStatement;
import org.hua.ast.IfStatement;
import org.hua.ast.IntegerLiteralExpression;
import org.hua.ast.NullExpression;
import org.hua.ast.ParameterDeclaration;
import org.hua.ast.ParameterList;
import org.hua.ast.ParenthesisExpression;
import org.hua.ast.ReturnStatement;
import org.hua.ast.Statement;
import org.hua.ast.StatementList;
import org.hua.ast.StringLiteralExpression;
import org.hua.ast.ThisExpression;
import org.hua.ast.TypeSpecifier;
import org.hua.ast.UnaryExpression;
import org.hua.ast.WhileStatement;
import org.hua.ast.WriteStatement;
import org.hua.symbol.SymTable;
import org.hua.symbol.SymTableEntry;
import org.objectweb.asm.Type;

/**
 * This code is part of the lab exercises for the Compilers course at Harokopio
 * University of Athens, Dept. of Informatics and Telematics.
 */
/**
 * Collect all symbols such as variables, methods, etc in symbol table.
 */
public class CollectSymbolsASTVisitor implements ASTVisitor {
    
    private String curClass = "";
    private CompoundStatement curFunction;

    private static final String CUSTOM_CLASSES = "Lorg/hua/customclasses/";

    public CollectSymbolsASTVisitor() {
    }

    @Override
    public void visit(AccessorExpression node) throws ASTVisitorException {
//        SymTable<SymTableEntry> sTable = ASTUtils.getSafeEnv(node);
//        SymTableEntry sEntry = sTable.lookupOnlyInTop(node.getIdentifier());
//        System.out.println("node id: "+node.getIdentifier());
//        if(sEntry==null){
//            SymTableEntry s = new SymTableEntry(node.getIdentifier());
//            sTable.put(node.getIdentifier(), s);
//        } else {
//            ASTUtils.error(node, "A field with the same name has already been defined.");
//        }
        node.getExpression().accept(this);
        node.getExpressions().accept(this);
    }

    @Override
    public void visit(AssignmentStatement node) throws ASTVisitorException {
        node.getExpression1().accept(this);
        node.getExpression2().accept(this);
    }

    @Override
    public void visit(BinaryExpression node) throws ASTVisitorException {
        node.getExpression1().accept(this);
        node.getExpression2().accept(this);
    }

    @Override
    public void visit(BreakStatement node) throws ASTVisitorException {
        //do nothing here
    }

    @Override
    public void visit(ClassDefinition node) throws ASTVisitorException {
        SymTable<SymTableEntry> symTable = ASTUtils.getSafeEnv(node);
        Type classType = Type.getType(CUSTOM_CLASSES + node.getIdentifier() + ";");
        curClass = node.getIdentifier();
        //register class in Registry
        if (!Registry.getInstance().classExists(classType)) {
            Registry.getInstance().addClass(classType, symTable);
        } else {
            ASTUtils.error(node, "A class with the same name has already been defined (registry)");
        }

//        put class in symTable
//        if (symTable.lookupOnlyInTop(classType.toString()) == null) {
//            SymTableEntry sym = new SymTableEntry(node.getIdentifier());
//            symTable.put(node.getIdentifier(), sym);
//        } else {
//            ASTUtils.error(node, "A class with the same name has already been defined (symbol table)");
//        }
        node.getFfDefinitions().accept(this);
    }

    @Override
    public void visit(CompUnit node) throws ASTVisitorException {
        for (ClassDefinition cd : node.getClassDefinitions()) {
            cd.accept(this);
        }
    }

    @Override
    public void visit(CompoundStatement node) throws ASTVisitorException {
        node.getStatements().accept(this);
    }

    @Override
    public void visit(ContinueStatement node) throws ASTVisitorException {
        //nothing to do here
    }

    @Override
    public void visit(DeclarationStatement node) throws ASTVisitorException {
        node.getType().accept(this);

        // 1. find symbol table
        SymTable<SymTableEntry> symTable = ASTUtils.getSafeEnv(node);

        // 2. lookup identifier only in top scope
        //    to make sure it is redefined
        if (symTable.lookupOnlyInTop(node.getIdentifier()) == null) {
            // 3. register identifier with type in symbol table
            //    use org.objectweb.asm.Type for the type
            SymTableEntry symbol = new SymTableEntry(node.getIdentifier(),node.getType().getTypeSpecifier());
            symTable.put(node.getIdentifier(), symbol);
        } else {
            ASTUtils.error(node, "A variable with the same name has already been defined.");
        }

    }

    @Override
    public void visit(FloatLiteralExpression node) throws ASTVisitorException {
        //nothing here
    }

    @Override
    public void visit(ExpressionList node) throws ASTVisitorException {
        if (!node.getExpressions().isEmpty()) { //if there is a list of expressions
            for (Expression e : node.getExpressions()) {
                e.accept(this);
            }
        }
    }

    @Override
    public void visit(ExpressionStatement node) throws ASTVisitorException {
        node.getExpression().accept(this);
    }

    @Override
    public void visit(FFDefinitionsList node) throws ASTVisitorException {
        if (!node.getFfDefinitons().isEmpty()) {
            for (FFDefinition ffd : node.getFfDefinitons()) {
                ffd.accept(this);
            }
        }
    }

    @Override
    public void visit(FieldDefinition node) throws ASTVisitorException {
        SymTable<SymTableEntry> symTable = ASTUtils.getSafeEnv(node);
        if (symTable.lookupOnlyInTop(node.getIdentifier()) == null) {
            SymTableEntry s = new SymTableEntry(node.getIdentifier(),node.getType().getTypeSpecifier());
            symTable.put(node.getIdentifier(), s);
        } else {
            ASTUtils.error(node, "A field with the same name has already been defined.");
        }        
        node.getType().accept(this);
    }

    @Override
    public void visit(FunctionDefinition node) throws ASTVisitorException {        
        SymTable<SymTableEntry> symTable = ASTUtils.getSafeEnv(node);
        if (symTable.lookupOnlyInTop(node.getIdentifier()) == null) {
            SymTableEntry s = new SymTableEntry(node.getIdentifier(),node.getType().getTypeSpecifier());
            symTable.put(node.getIdentifier(), s);
        } else {
            ASTUtils.error(node, "A function with the same name has already been defined.");
        }

        node.getCompoundStatement().accept(this);
        curFunction = node.getCompoundStatement();
        node.getParameters().accept(this);
        
    }

    @Override
    public void visit(Identifier node) throws ASTVisitorException {       
        if (node.getExpressions() != null) {
            node.getExpressions().accept(this);
        }
    }

    @Override
    public void visit(IfElseStatement node) throws ASTVisitorException {
        node.getExpression().accept(this);
        node.getStatement1().accept(this);
        node.getStatement2().accept(this);
    }

    @Override
    public void visit(IfStatement node) throws ASTVisitorException {
        node.getExpression().accept(this);
        node.getStatement().accept(this);
    }

    @Override
    public void visit(IntegerLiteralExpression node) throws ASTVisitorException {
        // do nothing
    }

    @Override
    public void visit(NullExpression node) throws ASTVisitorException {
        // do nothing
    }

    @Override
    public void visit(ParameterDeclaration node) throws ASTVisitorException {
        
        SymTable<SymTableEntry> sTable = ASTUtils.getSafeEnv(curFunction);
        SymTableEntry sEntry = sTable.lookup(node.getIdentifier());
        if (sEntry == null) {
            SymTableEntry s = new SymTableEntry(node.getIdentifier(),node.getType().getTypeSpecifier());
            sTable.put(node.getIdentifier(), s);
        } else {
            ASTUtils.error(node, "A parameter with the same name has already been defined for this function");
        }
        
        node.getType().accept(this);
    }

    @Override
    public void visit(ParameterList node) throws ASTVisitorException {
        if (!node.getParameters().isEmpty()) {
            for (ParameterDeclaration pd : node.getParameters()) {
                pd.accept(this);
            }
        }
    }

    @Override
    public void visit(ParenthesisExpression node) throws ASTVisitorException {
        node.getExpression().accept(this);
    }

    @Override
    public void visit(ReturnStatement node) throws ASTVisitorException {
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
        }
    }

    @Override
    public void visit(StatementList node) throws ASTVisitorException {
        if (!node.getStatements().isEmpty()) {
            for (Statement s : node.getStatements()) {
                s.accept(this);
            }
        }
    }

    @Override
    public void visit(StringLiteralExpression node) throws ASTVisitorException {
        //do nothing
    }

    @Override
    public void visit(ThisExpression node) throws ASTVisitorException {
        //do nothing
    }

    @Override
    public void visit(UnaryExpression node) throws ASTVisitorException {
        node.getExpression().accept(this);
    }

    @Override
    public void visit(WhileStatement node) throws ASTVisitorException {
        node.getExpression().accept(this);
        node.getStatement().accept(this);
    }

    @Override
    public void visit(WriteStatement node) throws ASTVisitorException {
        node.getExpression().accept(this);
    }

    @Override
    public void visit(TypeSpecifier node) throws ASTVisitorException {
        if (node.getIdentifier() != null) {
            node.getIdentifier().accept(this);
        }
    }
}