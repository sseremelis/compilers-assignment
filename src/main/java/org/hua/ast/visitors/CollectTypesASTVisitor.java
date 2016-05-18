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
import org.hua.types.TypeException;
import org.hua.types.TypeUtils;
import org.objectweb.asm.Type;

/**
 * This code is part of the lab exercises for the Compilers course at Harokopio
 * University of Athens, Dept. of Informatics and Telematics.
 */
/**
 * Compute possible types for each node.
 */
public class CollectTypesASTVisitor implements ASTVisitor {

    private static final String CUSTOM_CLASSES = "Lorg/hua/customclasses/";

    public CollectTypesASTVisitor() {
    }

    @Override
    public void visit(AccessorExpression node) throws ASTVisitorException {
        node.getExpression().accept(this);
        node.getExpressions().accept(this);   
        SymTable<SymTableEntry> sTable = ASTUtils.getSafeEnv(node);
        SymTableEntry sEntry = sTable.lookup(node.getIdentifier());        
        Type exprClass = ASTUtils.getSafeType(node.getExpression());
        SymTable<SymTableEntry> classSymTable = Registry.getInstance().getExistingClass(exprClass);        
        SymTableEntry classFF = classSymTable.lookup(node.getIdentifier());
        
        if(classFF != null){
            
           ASTUtils.setType(node, classFF.getType());
        }
        else {
            ASTUtils.error(node, "This field or function is not defined in this class");
        }        
        
    }

    @Override
    public void visit(AssignmentStatement node) throws ASTVisitorException {
        node.getExpression1().accept(this);
        node.getExpression2().accept(this);
        Type type1 = ASTUtils.getSafeType(node.getExpression1());
        Type type2 = ASTUtils.getSafeType(node.getExpression2());
        System.out.println("type1: "+type1+" type2 " +type2);
        if (TypeUtils.isAssignable(type1, type1)) {
            ASTUtils.setType(node, TypeUtils.maxType(type1, type2));
        } else {
            ASTUtils.error(node, "Not assignable types");
        }
    }

    @Override
    public void visit(BinaryExpression node) throws ASTVisitorException {

        // 1. find type of expression1
        node.getExpression1().accept(this);
        // 2. find type of expression 2
        node.getExpression2().accept(this);
        // 3. Use TypeUtils.applyBinary to figure type of result
        // 4. set type of result

        try {
            Type type1 = ASTUtils.getSafeType(node.getExpression1());

            Type type2 = ASTUtils.getSafeType(node.getExpression2());
            ASTUtils.setType(node, TypeUtils.applyBinary(node.getOperator(), type1, type2));
        } // 5. error if TypeException
        catch (TypeException e) {
            ASTUtils.error(node, e.getMessage());
        }
        
    }

    @Override
    public void visit(BreakStatement node) throws ASTVisitorException {
        ASTUtils.setType(node, Type.VOID_TYPE);
    }

    @Override
    public void visit(ClassDefinition node) throws ASTVisitorException {
        node.getFfDefinitions().accept(this);
        ASTUtils.setType(node, Type.VOID_TYPE);
    }

    @Override
    public void visit(CompUnit node) throws ASTVisitorException {
        for (ClassDefinition cd : node.getClassDefinitions()) {
            cd.accept(this);
        }
        ASTUtils.setType(node, Type.VOID_TYPE);
    }

    @Override
    public void visit(CompoundStatement node) throws ASTVisitorException {

        node.getStatements().accept(this);
        ASTUtils.setType(node, Type.VOID_TYPE);
    }

    @Override
    public void visit(ContinueStatement node) throws ASTVisitorException {
        ASTUtils.setType(node, Type.VOID_TYPE);
    }

    @Override
    public void visit(DeclarationStatement node) throws ASTVisitorException {
        node.getType().accept(this);
        SymTable<SymTableEntry> sTable = ASTUtils.getSafeEnv(node);
        SymTableEntry sEntry = sTable.lookup(node.getIdentifier());
        if (sEntry == null) {
            ASTUtils.error(node, "This variable has not been defined");
        } else {
            ASTUtils.setType(node, sEntry.getType());
        }
    }

    @Override
    public void visit(FloatLiteralExpression node) throws ASTVisitorException {
        ASTUtils.setType(node, Type.FLOAT_TYPE);
    }

    @Override
    public void visit(ExpressionList node) throws ASTVisitorException {
        if (!node.getExpressions().isEmpty()) { //if there is a list of expressions
            for (Expression e : node.getExpressions()) {
                e.accept(this);
            }
        }
        ASTUtils.setType(node, Type.VOID_TYPE);
    }

    @Override
    public void visit(ExpressionStatement node) throws ASTVisitorException {

        node.getExpression().accept(this);
        ASTUtils.setType(node, Type.VOID_TYPE);
    }

    @Override
    public void visit(FFDefinitionsList node) throws ASTVisitorException {
        if (!node.getFfDefinitons().isEmpty()) {
            for (FFDefinition ffd : node.getFfDefinitons()) {
                ffd.accept(this);
            }
        }
        ASTUtils.setType(node, Type.VOID_TYPE);
    }

    @Override
    public void visit(FieldDefinition node) throws ASTVisitorException {
        node.getType().accept(this);
        SymTable<SymTableEntry> symTable = ASTUtils.getSafeEnv(node);
        SymTableEntry entry = symTable.lookup(node.getIdentifier());
        if (entry == null) {

            ASTUtils.error(node, "The type of the variable has already been defined.");
        } else {
            ASTUtils.setType(node, entry.getType());
        }
    }

    @Override
    public void visit(FunctionDefinition node) throws ASTVisitorException {

        node.getCompoundStatement().accept(this);
        node.getParameters().accept(this);
        node.getType().accept(this);
//        why???
        SymTable<SymTableEntry> sTable = ASTUtils.getSafeEnv(node);
        SymTableEntry seEntry = sTable.lookup(node.getIdentifier());
        ASTUtils.setType(node, node.getType().getTypeSpecifier());
    }

    @Override
    public void visit(Identifier node) throws ASTVisitorException {
        if (node.getExpressions() != null) {
            node.getExpressions().accept(this);
        }
        SymTable<SymTableEntry> sTable = ASTUtils.getSafeEnv(node);        
        SymTableEntry sEntry = sTable.lookup(node.getIdentifier());
        if (sEntry == null) {
            SymTable<SymTableEntry> existingClass = Registry.getInstance().getExistingClass(Type.getType(CUSTOM_CLASSES+node.getIdentifier()+";"));
                if(existingClass !=null){
                    ASTUtils.setType(node, Type.getType(CUSTOM_CLASSES+node.getIdentifier()+";"));
                }
                else{
                    ASTUtils.error(node, "A class of this type has not been declared");
                }
//            ASTUtils.error(node, "This variable could not be found in the symbol table 1");
        } else {
            if(sEntry.getType()!=null){
                ASTUtils.setType(node, sEntry.getType());
            }
            else{
                SymTable<SymTableEntry> existingClass = Registry.getInstance().getExistingClass(Type.getType(CUSTOM_CLASSES+node.getIdentifier()+";"));
                if(existingClass !=null){
                    ASTUtils.setType(node, Type.getType(CUSTOM_CLASSES+node.getIdentifier()+";"));
                }
                else{
                    ASTUtils.error(node, "A class of this type has not been declared");
                }
            }
        }
    }

    @Override
    public void visit(IfElseStatement node) throws ASTVisitorException {
        node.getExpression().accept(this);
        if (!ASTUtils.getSafeType(node.getExpression()).equals(Type.BOOLEAN_TYPE)) {
            ASTUtils.error(node.getExpression(), "Invalid expression, should be boolean");
        }
        node.getStatement1().accept(this);
        node.getStatement2().accept(this);
        ASTUtils.setType(node, Type.VOID_TYPE);
    }

    @Override
    public void visit(IfStatement node) throws ASTVisitorException {
        node.getExpression().accept(this);
        if (!ASTUtils.getSafeType(node.getExpression()).equals(Type.BOOLEAN_TYPE)) {
            ASTUtils.error(node.getExpression(), "Invalid expression, should be boolean");
        }
        node.getStatement().accept(this);
        ASTUtils.setType(node, Type.VOID_TYPE);
    }

    @Override
    public void visit(IntegerLiteralExpression node) throws ASTVisitorException {
        ASTUtils.setType(node, Type.INT_TYPE);
    }

    @Override
    public void visit(NullExpression node) throws ASTVisitorException {
        ASTUtils.setType(node, Type.VOID_TYPE);
    }

    @Override
    public void visit(ParameterDeclaration node) throws ASTVisitorException {
        SymTable<SymTableEntry> sTable = ASTUtils.getSafeEnv(node);
        SymTableEntry sEntry = sTable.lookup(node.getIdentifier());
        if (sEntry == null) {
            ASTUtils.setType(node, node.getType().getTypeSpecifier());
        } else {            
            ASTUtils.error(node, "This parameter could not be found in the symbol table");
        }
    }

    @Override
    public void visit(ParameterList node) throws ASTVisitorException {
        if (!node.getParameters().isEmpty()) {
            for (ParameterDeclaration pd : node.getParameters()) {
                pd.accept(this);
            }
        }
        ASTUtils.setType(node, Type.VOID_TYPE);
    }

    @Override
    public void visit(ParenthesisExpression node) throws ASTVisitorException {
        node.getExpression().accept(this);
        ASTUtils.setType(node, Type.VOID_TYPE);
    }

    @Override
    public void visit(ReturnStatement node) throws ASTVisitorException {
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
        }
        SymTable<SymTableEntry> sTable = ASTUtils.getSafeEnv(node);
        System.out.println("fix me!!");
    }

    @Override
    public void visit(StatementList node) throws ASTVisitorException {
        if (!node.getStatements().isEmpty()) {
            for (Statement s : node.getStatements()) {
                s.accept(this);
            }
        }
        ASTUtils.setType(node, Type.VOID_TYPE);
    }

    @Override
    public void visit(StringLiteralExpression node) throws ASTVisitorException {
        ASTUtils.setType(node, Type.getType(String.class));
    }

    @Override
    public void visit(ThisExpression node) throws ASTVisitorException {
        ASTUtils.setType(node, Type.VOID_TYPE);
    }

    @Override
    public void visit(UnaryExpression node) throws ASTVisitorException {
        node.getExpression().accept(this);
        try {
            ASTUtils.setType(node, TypeUtils.applyUnary(node.getOperator(), ASTUtils.getSafeType(node.getExpression())));
        } catch (TypeException e) {
            ASTUtils.error(node, e.getMessage());
        }
    }

    @Override
    public void visit(WhileStatement node) throws ASTVisitorException {
        node.getExpression().accept(this);
        if (!ASTUtils.getSafeType(node.getExpression()).equals(Type.BOOLEAN_TYPE)) {
            ASTUtils.error(node.getExpression(), "Invalid expression, should be boolean");
        }
        node.getStatement().accept(this);
        ASTUtils.setType(node, Type.VOID_TYPE);
    }

    @Override
    public void visit(WriteStatement node) throws ASTVisitorException {
        node.getExpression().accept(this);
        ASTUtils.setType(node, Type.VOID_TYPE);
    }

    @Override
    public void visit(TypeSpecifier node) throws ASTVisitorException {
        SymTable<SymTableEntry> sTable = ASTUtils.getSafeEnv(node);    
        if (node.isClassIdentifier()) {
            ASTUtils.setType(node, Type.getType(CUSTOM_CLASSES+node.getIdentifier().getIdentifier()+";"));
        }
        else{
            ASTUtils.setType(node, node.getType());            
        }        
    }
}
