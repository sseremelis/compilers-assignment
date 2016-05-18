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
public class ParameterList extends ASTNode{
    
    private List<ParameterDeclaration> parameters;

    public ParameterList(List<ParameterDeclaration> parameters) {
        this.parameters = parameters;
    }

    public ParameterList() {
        this.parameters = new ArrayList<ParameterDeclaration>();
    }

    public List<ParameterDeclaration> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParameterDeclaration> parameters) {
        this.parameters = parameters;
    }
    
    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }
    
    
    
}
