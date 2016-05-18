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
public class FFDefinitionsList extends ASTNode{
    
    private List<FFDefinition> ffDefinitons;
    
    public FFDefinitionsList(){
        this.ffDefinitons = new ArrayList<FFDefinition>();
    }
    
    public FFDefinitionsList(List<FFDefinition> ffDefinitons){
        this.ffDefinitons = ffDefinitons;
    }

    public List<FFDefinition> getFfDefinitons() {
        return ffDefinitons;
    }

    public void setFfDefinitons(List<FFDefinition> ffDefinitons) {
        this.ffDefinitons = ffDefinitons;
    }
    
    public void addFFDefinition(FFDefinition ffDef){
        this.ffDefinitons.add(ffDef);
    }

    @Override
    public void accept(ASTVisitor visitor) throws ASTVisitorException {
        visitor.visit(this);
    }
    
}
