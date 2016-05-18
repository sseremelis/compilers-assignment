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
public enum StorageSpecifier{
    
    STATIC("static");
    
    private String type;
    
    private StorageSpecifier(String type){
        this.type = type;
    }
    
    public String getType(){
        return type;
    }
    
    @Override
    public String toString(){
        return type;
    }
}
