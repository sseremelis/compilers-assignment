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
public class FieldDefinition extends FFDefinition{
    
    private TypeSpecifier type;
    private String identifier;

    public FieldDefinition(TypeSpecifier type, String identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    public TypeSpecifier getType() {
        return type;
    }

    public void setType(TypeSpecifier type) {
        this.type = type;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }

    
    
    
}