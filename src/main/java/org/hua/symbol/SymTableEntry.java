package org.hua.symbol;

import org.objectweb.asm.Type;

public class SymTableEntry {

    private String id;
    private Type type;
    private Integer index;
    private boolean isStatic;

    public boolean isIsStatic() {
        return isStatic;
    }

    public void setIsStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
    private SymTable<SymTableEntry> parameters;

    public SymTableEntry(String id) {
        this(id, null);
    }

    public SymTableEntry(String id, Type type) {
        this.id = id;
        this.type = type;
        this.parameters = null;
    }
    
    public SymTableEntry(String id, Type type, SymTable<SymTableEntry> parameters){
        this.id = id;
        this.type = type;
        this.parameters  = parameters;
    }

    public boolean isFunction(){
        return parameters!=null;
    }

    public SymTable<SymTableEntry> getParameters() {
        return parameters;
    }

    public void setParameters(SymTable<SymTableEntry> parameters) {
        this.parameters = parameters;
    }
    
    public void setParameter(String id,SymTableEntry param){
        this.parameters.put(id, param);
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 97 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SymTableEntry other = (SymTableEntry) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if (this.type != other.type && (this.type == null || !this.type.equals(other.type))) {
            return false;
        }
        return true;
    }

}
