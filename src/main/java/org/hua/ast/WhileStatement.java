/**
 * This code is part of the lab exercises for the Compilers course at Harokopio
 * University of Athens, Dept. of Informatics and Telematics.
 */
package org.hua.ast;

public class WhileStatement extends Statement {

    private Expression expression;
    private Statement statement;

    public WhileStatement(Expression expression, Statement cstatement) {
        this.expression = expression;
        this.statement = cstatement;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement cstatement) {
        this.statement = cstatement;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }

}
