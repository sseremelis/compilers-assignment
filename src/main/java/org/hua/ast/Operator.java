/**
 * This code is part of the lab exercises for the Compilers course at Harokopio
 * University of Athens, Dept. of Informatics and Telematics.
 */
package org.hua.ast;

public enum Operator {

    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    MOD("%"),
    DIVISION("/"),
    EQUAL("=="),
    NOT_EQUAL("!="),
    LESS("<"),
    GREATER(">"),
    LESS_EQUAL("<="),
    GREATER_EQUAL(">="),
    NOT("!"),
    AND("&&"),
    OR("||");

    private String type;

    private Operator(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }
    
    public boolean isUnary() {
        return this.equals(Operator.MINUS);
    }

    public boolean isRelational() {
        return this.equals(Operator.EQUAL) || this.equals(Operator.NOT_EQUAL)
                || this.equals(Operator.GREATER) || this.equals(Operator.GREATER_EQUAL)
                || this.equals(Operator.LESS) || this.equals(Operator.LESS_EQUAL);
    }

}
