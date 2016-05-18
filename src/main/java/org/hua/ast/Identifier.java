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
public class Identifier extends Expression{
    
    private String identifier;
    private ExpressionList expressions;
    private boolean isClassId;

    public boolean isIsClassId() {
        return isClassId;
    }

    public void setIsClassId(boolean isClassId) {
        this.isClassId = isClassId;
    }

    public Identifier(String identifier) {
        this.identifier = identifier;
    }
    
    public Identifier(String identifier, ExpressionList expr){
        this.identifier = identifier;
        this.expressions = expr;
    }
    
    public Identifier(String identifier, ExpressionList expr,boolean isClassId){
        this.identifier = identifier;
        this.expressions = expr;
        this.isClassId = isClassId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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
