/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hua.ast;

import org.objectweb.asm.Type;

/**
 *
 * @author sssotiris22
 */
public class IdentifierExpression extends Expression{
    
    private String identifier;
    private ExpressionList expressions;

    public Type getType() {
        return null;
    }

    public boolean isFunction() {
        return expressions!=null;
    }

    public IdentifierExpression(String identifier) {
        this.identifier = identifier;
    }
    
    public IdentifierExpression(String identifier, ExpressionList expr){
        this.identifier = identifier;
        this.expressions = expr;
    }
    
//    public Identifier(String identifier, ExpressionList expr,boolean isClassId){
//        this.identifier = identifier;
//        this.expressions = expr;
//        this.isFunction = isClassId;
//    }

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
