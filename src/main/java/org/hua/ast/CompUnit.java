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
public class CompUnit extends ASTNode{
    
    private List<ClassDefinition> classDefinitions;

    public CompUnit() {
        classDefinitions = new ArrayList<ClassDefinition>();
    }

    public CompUnit(List<ClassDefinition> statements) {
        this.classDefinitions = statements;
    }

    public List<ClassDefinition> getClassDefinitions() {
        return classDefinitions;
    }

    public void setClassDefinitions(List<ClassDefinition> classDefinitions) {
        this.classDefinitions = classDefinitions;
    }    

    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }
    
}
