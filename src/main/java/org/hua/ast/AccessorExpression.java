/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hua.ast;

/**
 *
 * @author sssotiris22
 */
public class AccessorExpression extends Expression{
    
    private Expression expression;
    private String identifier;
    private ExpressionList expressions;

    public AccessorExpression(Expression expression, String identifier) {
        this.identifier = identifier;
        this.expression = expression;
        this.expressions = new ExpressionList();
    }

    public AccessorExpression(Expression expression, String identifier, ExpressionList expressions) {
        this.expression = expression;
        this.identifier = identifier;
        this.expressions = expressions;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public ExpressionList getExpressions() {
        return expressions;
    }

    public void setExpressions(ExpressionList expressions) {
        this.expressions = expressions;
    }

    
    
    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }    
}
