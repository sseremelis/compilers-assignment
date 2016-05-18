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
public class FunctionDefinition extends FFDefinition{
    
    private StorageSpecifier storageSpecifier;
    private TypeSpecifier type;
    private String identifier;
    private ParameterList parameters;
    private CompoundStatement compoundStatement;

    public FunctionDefinition(StorageSpecifier storageSpecifier, TypeSpecifier type, String identifier, ParameterList parameters, CompoundStatement compoundStatement) {
        this.storageSpecifier = storageSpecifier;
        this.type = type;
        this.identifier = identifier;
        this.parameters = parameters;
        this.compoundStatement = compoundStatement;
    }

    public FunctionDefinition(TypeSpecifier type, String identifier, ParameterList parameters, CompoundStatement compoundStatement) {
        this.type = type;
        this.identifier = identifier;
        this.parameters = parameters;
        this.compoundStatement = compoundStatement;
        this.storageSpecifier = null;
    }

    public StorageSpecifier getStorageSpecifier() {
        return storageSpecifier;
    }

    public void setStorageSpecifier(StorageSpecifier storageSpecifier) {
        this.storageSpecifier = storageSpecifier;
    }

    public TypeSpecifier getType() {
        return type;
    }

    public void setType(TypeSpecifier type) {
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public ParameterList getParameters() {
        return parameters;
    }

    public void setParameters(ParameterList parameters) {
        this.parameters = parameters;
    }

    public CompoundStatement getCompoundStatement() {
        return compoundStatement;
    }

    public void setCompoundStatement(CompoundStatement compoundStatement) {
        this.compoundStatement = compoundStatement;
    }
    
    

    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
       visitor.visit(this);
    }

    
}
