/**
 * This code is part of the lab exercises for the Compilers course at Harokopio
 * University of Athens, Dept. of Informatics and Telematics.
 */
package org.hua.ast;

/**
 * Abstract syntax tree visitor.
 */
public interface ASTVisitor {    
    
    void visit(AccessorExpression node) throws ASTVisitorException;
    
    void visit(AssignmentStatement node) throws ASTVisitorException;
    
    void visit(BinaryExpression node) throws ASTVisitorException;
    
    void visit(BreakStatement node) throws ASTVisitorException;
    
    void visit(ClassDefinition node) throws ASTVisitorException;
    
    void visit(CompUnit node) throws ASTVisitorException;
    
    void visit(CompoundStatement node) throws ASTVisitorException;
    
    void visit(ContinueStatement node) throws ASTVisitorException;
    
    void visit(DeclarationStatement node) throws ASTVisitorException;    
    
    void visit(FloatLiteralExpression node) throws ASTVisitorException;
    
    void visit(ExpressionList node) throws ASTVisitorException;
    
    void visit(ExpressionStatement node) throws ASTVisitorException;
    
    void visit(FFDefinitionsList node) throws ASTVisitorException;
    
    void visit(FieldDefinition node) throws ASTVisitorException;
    
    void visit(FunctionDefinition node) throws ASTVisitorException;
    
    void visit(IdentifierExpression node) throws ASTVisitorException;
    
    void visit(IfElseStatement node) throws ASTVisitorException;
    
    void visit(IfStatement node) throws ASTVisitorException;
        
    void visit(IntegerLiteralExpression node) throws ASTVisitorException;
    
    void visit(NullExpression node) throws ASTVisitorException;
    
    void visit(ParameterDeclaration node) throws ASTVisitorException;
    
    void visit(ParameterList node) throws ASTVisitorException;
    
    void visit(ParenthesisExpression node) throws ASTVisitorException;
    
    void visit(ReturnStatement node) throws ASTVisitorException;
    
    void visit(StatementList node) throws ASTVisitorException;
        
    void visit(StringLiteralExpression node) throws ASTVisitorException;
    
    void visit(ThisExpression node) throws ASTVisitorException;
    
    void visit(UnaryExpression node) throws ASTVisitorException;

    void visit(WhileStatement node) throws ASTVisitorException;
    
    void visit(WriteStatement node) throws ASTVisitorException;

    void visit(TypeSpecifier node) throws ASTVisitorException;

    public void visit(NewIdentifierExpression node) throws ASTVisitorException;
}
