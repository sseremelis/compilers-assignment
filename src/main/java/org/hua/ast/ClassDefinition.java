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
public class ClassDefinition extends ASTNode{
    
    private String identifier;
    private FFDefinitionsList ffDefinitions;

    public ClassDefinition(String identifier,FFDefinitionsList ffDefinitions) {
        this.identifier = identifier;
        this.ffDefinitions = ffDefinitions;
    }
    
    public ClassDefinition(){
        this.ffDefinitions = new FFDefinitionsList();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    
    public FFDefinitionsList getFfDefinitions() {
        return ffDefinitions;
    }

    public void setFfDefinitions(FFDefinitionsList ffDefinitions) {
        this.ffDefinitions = ffDefinitions;
    }   
    

    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }
    
}
