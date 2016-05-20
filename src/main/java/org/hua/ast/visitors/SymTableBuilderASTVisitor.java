package org.hua.ast.visitors;


/**
 * This code is part of the lab exercises for the Compilers course at Harokopio
 * University of Athens, Dept. of Informatics and Telematics.
 */
import org.hua.ast.ASTUtils;
import java.util.ArrayDeque;
import java.util.Deque;

import org.hua.symbol.SymTable;
import org.hua.symbol.SymTableEntry;
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
import org.hua.ast.IdentifierExpression;
import org.hua.ast.IfElseStatement;
import org.hua.ast.IfStatement;
import org.hua.ast.IntegerLiteralExpression;
import org.hua.ast.NewIdentifierExpression;
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
import org.hua.symbol.HashSymTable;

/**
 * Build symbol tables for each node of the AST.
 */
public class SymTableBuilderASTVisitor implements ASTVisitor {

    private final Deque<SymTable<SymTableEntry>> env;

    public SymTableBuilderASTVisitor() {
        env = new ArrayDeque<SymTable<SymTableEntry>>();
    }
    
    private void pushEnvironment() {
        SymTable<SymTableEntry> oldSymTable = env.peek();
        SymTable<SymTableEntry> symTable = new HashSymTable<SymTableEntry>(oldSymTable);
        env.push(symTable);
    }

    private void popEnvironment() {
        env.pop();
    }

    @Override
    public void visit(AccessorExpression node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
        node.getExpression().accept(this);
        node.getExpressions().accept(this);
    }

    @Override
    public void visit(AssignmentStatement node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
        node.getExpression1().accept(this);
        node.getExpression2().accept(this);
    }

    @Override
    public void visit(BinaryExpression node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
        node.getExpression1().accept(this);
        node.getExpression2().accept(this);
    }

    @Override
    public void visit(BreakStatement node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
    }

    @Override
    public void visit(ClassDefinition node) throws ASTVisitorException {
        //maybe remove push pop because there is no field declaration outside classes
        pushEnvironment();  
        ASTUtils.setEnv(node, env.element());        
        node.getFfDefinitions().accept(this);
        popEnvironment();
    }

    @Override
    public void visit(CompUnit node) throws ASTVisitorException {        
        pushEnvironment();        
        ASTUtils.setEnv(node, env.element());
        for (ClassDefinition cd : node.getClassDefinitions()) {
            cd.accept(this);
        }        
        popEnvironment();
    }

    @Override
    public void visit(CompoundStatement node) throws ASTVisitorException {
        pushEnvironment();
        ASTUtils.setEnv(node, env.element());
        node.getStatements().accept(this);
        popEnvironment();
    }

    @Override
    public void visit(ContinueStatement node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
    }

    @Override
    public void visit(DeclarationStatement node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
        node.getType().accept(this);
    }

    @Override
    public void visit(FloatLiteralExpression node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
    }

    @Override
    public void visit(ExpressionList node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
        if(!node.getExpressions().isEmpty()){ //if there is a list of expressions
            for(Expression e : node.getExpressions()){
                e.accept(this);
            }
        }
    }

    @Override
    public void visit(ExpressionStatement node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
        node.getExpression().accept(this);
    }

    @Override
    public void visit(FFDefinitionsList node) throws ASTVisitorException {        
        ASTUtils.setEnv(node, env.element());
        if(!node.getFfDefinitons().isEmpty()){
            for(FFDefinition ffd : node.getFfDefinitons()){
                ffd.accept(this);
            }
        }
    }

    @Override
    public void visit(FieldDefinition node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
        node.getType().accept(this);
    }

    @Override
    public void visit(FunctionDefinition node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
        node.getType().accept(this);
        node.getParameters().accept(this);
        node.getCompoundStatement().accept(this);
    }

    @Override
    public void visit(IdentifierExpression node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
        if(node.getExpressions()!=null){
            node.getExpressions().accept(this);
        }
    }
    
    @Override
    public void visit(NewIdentifierExpression node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
        if(node.getExpressions()!=null){
            node.getExpressions().accept(this);
        }
    }

    @Override
    public void visit(IfElseStatement node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
        node.getExpression().accept(this);
        node.getStatement1().accept(this);
        node.getStatement2().accept(this);
    }

    @Override
    public void visit(IfStatement node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
        node.getExpression().accept(this);
        node.getStatement().accept(this);
    }

    @Override
    public void visit(IntegerLiteralExpression node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
    }

    @Override
    public void visit(NullExpression node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
    }

    @Override
    public void visit(ParameterDeclaration node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
        node.getType().accept(this);
    }

    @Override
    public void visit(ParameterList node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
        if(!node.getParameters().isEmpty()){
            for(ParameterDeclaration pd : node.getParameters()){
                pd.accept(this);
            }
        }
    }

    @Override
    public void visit(ParenthesisExpression node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
        node.getExpression().accept(this);
    }

    @Override
    public void visit(ReturnStatement node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
        if(node.getExpression()!=null){
            node.getExpression().accept(this);
        }
    }

    @Override
    public void visit(StatementList node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
        if(!node.getStatements().isEmpty()){
            for(Statement s : node.getStatements()){
                s.accept(this);
            }
        }
    }

    @Override
    public void visit(StringLiteralExpression node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
    }

    @Override
    public void visit(ThisExpression node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
    }

    @Override
    public void visit(UnaryExpression node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
        node.getExpression().accept(this);
    }

    @Override
    public void visit(WhileStatement node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
        node.getExpression().accept(this);
        node.getStatement().accept(this);
    }

    @Override
    public void visit(WriteStatement node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
        node.getExpression().accept(this);
    }

    @Override
    public void visit(TypeSpecifier node) throws ASTVisitorException {
        ASTUtils.setEnv(node, env.element());
        if(node.getIdentifier()!=null){
            node.getIdentifier().accept(this);
        }
    }

    

}
