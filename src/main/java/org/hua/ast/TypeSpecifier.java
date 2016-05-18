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
public class TypeSpecifier extends ASTNode{
    
    private static final String CUSTOM_CLASSES = "Lorg/hua/customclasses/";

    
    private Type type;
    private Identifier identifier;

    public TypeSpecifier(Type type) {
        this.type = type;
    }

    public TypeSpecifier(Identifier id) {
        this.identifier = id;
    }
    
    public TypeSpecifier(String idString){
        this.identifier = new Identifier(idString);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }
    
    public Type getTypeSpecifier(){
        if(type!=null){
            return type;
        }
        else{
            return Type.getType(CUSTOM_CLASSES+identifier.getIdentifier()+";");
        }
    }
    
    public boolean isClassIdentifier(){
        return (identifier!=null)?true:false;
    }
    
    
}
