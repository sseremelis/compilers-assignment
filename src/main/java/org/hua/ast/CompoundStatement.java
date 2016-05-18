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
public class CompoundStatement extends Statement{
    
    private StatementList statements;

    public CompoundStatement(StatementList statements) {
        this.statements = statements;
    }
    
    public CompoundStatement(){
        this.statements = new StatementList();
    }

    public StatementList getStatements() {
        return statements;
    }

    public void setStatements(StatementList statements) {
        this.statements = statements;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }    
}
