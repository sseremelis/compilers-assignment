/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hua.ast;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sssotiris22
 */
public class StatementList extends Statement{
    
    private List<Statement> statements;

    public StatementList(List<Statement> statements) {
        this.statements = statements;
    }

    public StatementList() {
        this.statements = new ArrayList<Statement>();
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }
    
}
