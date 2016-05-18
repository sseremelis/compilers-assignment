package org.hua.ast.visitors;

    /**
 * This code is part of the lab exercises for the Compilers course at Harokopio
 * University of Athens, Dept. of Informatics and Telematics.
 */


import org.hua.ast.ASTVisitor;
import org.hua.ast.ASTVisitorException;
import org.hua.ast.AccessorExpression;
import org.hua.ast.AssignmentStatement;
import org.hua.ast.BinaryExpression;
import org.hua.ast.BreakStatement;
import org.hua.ast.ClassDefinition;
import org.hua.ast.CompUnit;
import org.hua.ast.CompoundStatement;
import org.hua.ast.ContinueStatement;
import org.hua.ast.DeclarationStatement;
import org.hua.ast.FloatLiteralExpression;
import org.hua.ast.Expression;
import org.hua.ast.ExpressionList;
import org.hua.ast.ExpressionStatement;
import org.hua.ast.FFDefinition;
import org.hua.ast.FFDefinitionsList;
import org.hua.ast.FieldDefinition;
import org.hua.ast.FunctionDefinition;
import org.hua.ast.Identifier;
import org.hua.ast.IfElseStatement;
import org.hua.ast.IfStatement;
import org.hua.ast.IntegerLiteralExpression;
import org.hua.ast.NullExpression;
import org.hua.ast.ParameterDeclaration;
import org.hua.ast.ParameterList;
import org.hua.ast.ParenthesisExpression;
import org.hua.ast.ReturnStatement;
import org.hua.ast.Statement;
import org.hua.ast.StatementList;
import org.hua.ast.StorageSpecifier;
import org.hua.ast.StringLiteralExpression;
import org.hua.ast.ThisExpression;
import org.hua.ast.TypeSpecifier;
import org.hua.ast.UnaryExpression;
import org.hua.ast.WhileStatement;
import org.hua.ast.WriteStatement;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;
import org.objectweb.asm.Type;

public class PrintASTVisitor implements ASTVisitor {

    @Override
    public void visit(AccessorExpression node) throws ASTVisitorException {
        node.getExpression().accept(this);
        System.out.print("."+node.getIdentifier());
        node.getExpressions().accept(this);
    }

    @Override
    public void visit(AssignmentStatement node) throws ASTVisitorException {
        node.getExpression1().accept(this);
        System.out.print(" = ");
        node.getExpression2().accept(this);
        System.out.print(";");
    }

    @Override
    public void visit(BinaryExpression node) throws ASTVisitorException {
        node.getExpression1().accept(this);
        System.out.print(" ");
        System.out.print(node.getOperator());
        System.out.print(" ");
        node.getExpression2().accept(this);
    }

    @Override
    public void visit(BreakStatement node) throws ASTVisitorException {
        System.out.println("break;");
    }

    @Override
    public void visit(ClassDefinition node) throws ASTVisitorException {
        System.out.println("class "+node.getIdentifier()+" {");
        node.getFfDefinitions().accept(this);
        System.out.println("}");
    }

    @Override
    public void visit(CompUnit node) throws ASTVisitorException {        
        for (ClassDefinition cd : node.getClassDefinitions()) {
            cd.accept(this);
            System.out.println("");  //seperate class definitions from each other
        }
    }

    @Override
    public void visit(CompoundStatement node) throws ASTVisitorException {
        System.out.println("{");
        node.getStatements().accept(this);
        System.out.println("}");
    }

    @Override
    public void visit(ContinueStatement node) throws ASTVisitorException {
        System.out.println("continue");
    }

    @Override
    public void visit(DeclarationStatement node) throws ASTVisitorException {
        node.getType().accept(this);
        System.out.println(" "+node.getIdentifier()+";");
    }

    @Override
    public void visit(FloatLiteralExpression node) throws ASTVisitorException {
        System.out.println(node.getLiteral());
    }

    @Override
    public void visit(ExpressionList node) throws ASTVisitorException {
        if(!node.getExpressions().isEmpty()){ //if there is a list of expressions
            for(Expression e : node.getExpressions()){
                e.accept(this);
                System.out.print(", ");
            }
            System.out.println("");
        }
    }

    @Override
    public void visit(ExpressionStatement node) throws ASTVisitorException {
        node.getExpression().accept(this);
        System.out.print(" ;");
    }

    @Override
    public void visit(FFDefinitionsList node) throws ASTVisitorException {        
        if(!node.getFfDefinitons().isEmpty()){
            for(FFDefinition ffd : node.getFfDefinitons()){
                ffd.accept(this);
                System.out.println(""); //print a new line to seperate each definition from the other
            }
        }
    }

    @Override
    public void visit(FieldDefinition node) throws ASTVisitorException {
        node.getType().accept(this);
        System.err.println(" "+node.getIdentifier()+";");
    }

    @Override
    public void visit(FunctionDefinition node) throws ASTVisitorException {
        if(node.getStorageSpecifier()!=null){
            System.out.print(node.getStorageSpecifier()+" ");
        }
        node.getType().accept(this);
        System.out.print(" ");
        System.out.print(node.getIdentifier()+" ( ");
        node.getParameters().accept(this);
        System.out.print(" ) ");
        node.getCompoundStatement().accept(this);
    }

    @Override
    public void visit(Identifier node) throws ASTVisitorException {
        System.out.print(node.getIdentifier());
        if(node.getExpressions()!=null){
            node.getExpressions().accept(this);
        }
    }

    @Override
    public void visit(IfElseStatement node) throws ASTVisitorException {
        System.out.print("if( ");
        node.getExpression().accept(this);
        System.out.println(")");
        node.getStatement1().accept(this);
        System.out.println("\nelse");
        node.getStatement2().accept(this);
        System.out.println("");
    }

    @Override
    public void visit(IfStatement node) throws ASTVisitorException {
        System.out.print("if (");
        node.getExpression().accept(this);
        System.out.println(")");
        node.getStatement().accept(this);
        System.out.println("");    }

    @Override
    public void visit(IntegerLiteralExpression node) throws ASTVisitorException {
        System.out.print(node.getLiteral());
    }

    @Override
    public void visit(NullExpression node) throws ASTVisitorException {
        System.out.println("null");
    }

    @Override
    public void visit(ParameterDeclaration node) throws ASTVisitorException {
        node.getType().accept(this);
        System.out.println(" " + node.getIdentifier() + ";");
    }

    @Override
    public void visit(ParameterList node) throws ASTVisitorException {
        if(!node.getParameters().isEmpty()){
            for(ParameterDeclaration pd : node.getParameters()){
                pd.accept(this);
                System.out.print(", ");
            }
        }
    }

    @Override
    public void visit(ParenthesisExpression node) throws ASTVisitorException {
        System.out.print("( ");
        node.getExpression().accept(this);
        System.out.print(" )");
    }

    @Override
    public void visit(ReturnStatement node) throws ASTVisitorException {
        
        if(node.getExpression()==null){ //if there is no return expression
            System.out.println("return;");
        }
        else{
            System.out.println("return ");
            node.getExpression().accept(this);
        }
    }

    @Override
    public void visit(StatementList node) throws ASTVisitorException {
        if(!node.getStatements().isEmpty()){
            for(Statement s : node.getStatements()){
                s.accept(this);
                System.out.println("");  //seperate statements from each other
            }
        }
    }

    @Override
    public void visit(StringLiteralExpression node) throws ASTVisitorException {
        System.out.print("\"");
        System.out.print(StringEscapeUtils.escapeJava(node.getLiteral()));
        System.out.print("\"");
    }

    @Override
    public void visit(ThisExpression node) throws ASTVisitorException {
        System.out.println("this");
    }

    @Override
    public void visit(UnaryExpression node) throws ASTVisitorException {
        System.out.print(node.getOperator());
        System.out.print(" ");
        node.getExpression().accept(this);
    }

    @Override
    public void visit(WhileStatement node) throws ASTVisitorException {
        System.out.print("while (");
        node.getExpression().accept(this);
        System.out.println(")");
        node.getStatement().accept(this);
        System.out.println("");
    }

    @Override
    public void visit(WriteStatement node) throws ASTVisitorException {
        System.out.print("print( ");
        node.getExpression().accept(this);
        System.out.println(" );");
    }
    
    @Override
    public void visit(TypeSpecifier node) throws ASTVisitorException{
        if(node.getIdentifier()!=null){
            node.getIdentifier().accept(this);
        }
        else{
            System.out.print(node.getTypeSpecifier());
        }
    }

    

}
