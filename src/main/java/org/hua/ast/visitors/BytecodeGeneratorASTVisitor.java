/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hua.ast.visitors;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hua.Registry;
import org.hua.ast.ASTNode;
import org.hua.ast.ASTUtils;
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
import org.hua.ast.Expression;
import org.hua.ast.ExpressionList;
import org.hua.ast.ExpressionStatement;
import org.hua.ast.FFDefinition;
import org.hua.ast.FFDefinitionsList;
import org.hua.ast.FieldDefinition;
import org.hua.ast.FloatLiteralExpression;
import org.hua.ast.FunctionDefinition;
import org.hua.ast.IdentifierExpression;
import org.hua.ast.IfElseStatement;
import org.hua.ast.IfStatement;
import org.hua.ast.IntegerLiteralExpression;
import org.hua.ast.NewIdentifierExpression;
import org.hua.ast.NullExpression;
import org.hua.ast.Operator;
import org.hua.ast.ParameterDeclaration;
import org.hua.ast.ParameterList;
import org.hua.ast.ParenthesisExpression;
import org.hua.ast.ReturnStatement;
import org.hua.ast.Statement;
import org.hua.ast.StatementList;
import org.hua.ast.StringLiteralExpression;
import org.hua.ast.ThisExpression;
import org.hua.ast.TypeSpecifier;
import org.hua.ast.UnaryExpression;
import org.hua.ast.WhileStatement;
import org.hua.ast.WriteStatement;
import org.hua.symbol.LocalIndexPool;
import org.hua.symbol.SymTable;
import org.hua.symbol.SymTableEntry;
import org.hua.types.TypeUtils;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ParameterNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 *
 * @author sssotiris22
 */
public class BytecodeGeneratorASTVisitor implements ASTVisitor {

    private ClassNode cn;
    private MethodNode mn;

    public BytecodeGeneratorASTVisitor() {
        // create class
//        cn = new ClassNode();
//        cn.access = Opcodes.ACC_PUBLIC;
//        cn.version = Opcodes.V1_5;
//        cn.name = "Assignment";
//        cn.sourceFile = "Assignment.in";
//        cn.superName = "java/lang/Object";

//        // create constructor
//        mn = new MethodNode(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
//        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
//        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V"));
//        mn.instructions.add(new InsnNode(Opcodes.RETURN));
//        mn.maxLocals = 1;
//        mn.maxStack = 1;
//        cn.methods.add(mn);
    }

    private void backpatch(List<JumpInsnNode> list, LabelNode labelNode) {
        if (list == null) {
            return;
        }
        for (JumpInsnNode instr : list) {
            instr.label = labelNode;
        }
    }

    public ClassNode getClassNode() {
        return cn;
    }

    private void handleBooleanOperator(Expression node, Operator op, Type type) throws ASTVisitorException {
        List<JumpInsnNode> trueList = new ArrayList<JumpInsnNode>();
        System.out.println("***** handle boolean " + op);
        if (type.equals(TypeUtils.STRING_TYPE)) {
            mn.instructions.add(new InsnNode(Opcodes.SWAP));
            JumpInsnNode jmp = null;
            mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false));
            switch (op) {
                case EQUAL:
                    jmp = new JumpInsnNode(Opcodes.IFNE, null);
                    break;
                case NOT_EQUAL:
                    jmp = new JumpInsnNode(Opcodes.IFEQ, null);
                    break;
                default:
                    ASTUtils.error(node, "Operator not supported on strings");
                    break;
            }
            mn.instructions.add(jmp);
            trueList.add(jmp);
        }
        else if (type.equals(Type.DOUBLE_TYPE)) {

            // FIXME: add DCMPG instruction
            // FIXME: add a JumpInsnNode with null label based on the operation
            //        IFEQ, IFNE, IFGT, IFGE, IFLT, IFLE
            // FIXME: add the jmp instruction into trueList
            mn.instructions.add(new InsnNode(Opcodes.DCMPG));
            JumpInsnNode jmp = null;
            switch (op) {
                case EQUAL:
                    jmp = new JumpInsnNode(Opcodes.IFEQ, null);
                    mn.instructions.add(jmp);
                    break;
                case NOT_EQUAL:
                    jmp = new JumpInsnNode(Opcodes.IFNE, null);
                    mn.instructions.add(jmp);
                    break;
                case GREATER:
                    jmp = new JumpInsnNode(Opcodes.IFGT, null);
                    mn.instructions.add(jmp);
                    break;
                case GREATER_EQUAL:
                    jmp = new JumpInsnNode(Opcodes.IFGE, null);
                    mn.instructions.add(jmp);
                    break;
                case LESS:
                    jmp = new JumpInsnNode(Opcodes.IFLT, null);
                    mn.instructions.add(jmp);
                    break;
                case LESS_EQUAL:
                    jmp = new JumpInsnNode(Opcodes.IFLE, null);
                    mn.instructions.add(jmp);
                    break;
            }
            trueList.add(jmp);

        }
        else {
            System.out.println("here");
            JumpInsnNode jmp = null;
            switch (op) {
                case EQUAL:
                    jmp = new JumpInsnNode(Opcodes.IF_ICMPEQ, null);
                    mn.instructions.add(jmp);
                    break;
                case NOT_EQUAL:
                    jmp = new JumpInsnNode(Opcodes.IF_ICMPNE, null);
                    mn.instructions.add(jmp);
                    break;
                case GREATER:
                    System.out.println("----- greater");
                    jmp = new JumpInsnNode(Opcodes.IF_ICMPGT, null);
                    mn.instructions.add(jmp);
                    break;
                case GREATER_EQUAL:
                    jmp = new JumpInsnNode(Opcodes.IF_ICMPGE, null);
                    mn.instructions.add(jmp);
                    break;
                case LESS:
                    jmp = new JumpInsnNode(Opcodes.IF_ICMPLT, null);
                    mn.instructions.add(jmp);
                    break;
                case LESS_EQUAL:
                    jmp = new JumpInsnNode(Opcodes.IF_ICMPLE, null);
                    mn.instructions.add(jmp);
                    break;
                default:
                    ASTUtils.error(node, "Operator not supported");
                    break;
            }
            trueList.add(jmp);
        }
        ASTUtils.setTrueList(node, trueList);
        List<JumpInsnNode> falseList = new ArrayList<JumpInsnNode>();
        JumpInsnNode jmp = new JumpInsnNode(Opcodes.GOTO, null);
        mn.instructions.add(jmp);
        falseList.add(jmp);
        ASTUtils.setFalseList(node, falseList);
    }

    private void handleNumberOperator(ASTNode node, Operator op, Type type) throws ASTVisitorException {
        if (op.equals(Operator.PLUS)) {

            // FIXME: IADD or DADD, etc.
            //        use type.getOpcode(Opcodes.IADD) to avoid if-then
            mn.instructions.add(new InsnNode(type.getOpcode(Opcodes.IADD)));

        }
        else if (op.equals(Operator.MINUS)) {

            // FIXME: ISUB or DSUB, etc.
            //        use type.getOpcode() to avoid if-then
            mn.instructions.add(new InsnNode(type.getOpcode(Opcodes.ISUB)));

        }
        else if (op.equals(Operator.MULTIPLY)) {

            // FIXME: IMUL or DMUL, etc.
            //        use type.getOpcode() to avoid if-then
            mn.instructions.add(new InsnNode(type.getOpcode(Opcodes.IMUL)));

        }
        else if (op.equals(Operator.DIVISION)) {

            // FIXME: IDIV or DDIV, etc.
            //        use type.getOpcode() to avoid if-then
            mn.instructions.add(new InsnNode(type.getOpcode(Opcodes.IDIV)));

        }
        else if (op.isRelational()) {
            if (type.equals(Type.DOUBLE_TYPE)) {
                mn.instructions.add(new InsnNode(Opcodes.DCMPG));
                JumpInsnNode jmp = null;
                switch (op) {
                    case EQUAL:
                        jmp = new JumpInsnNode(Opcodes.IFEQ, null);
                        mn.instructions.add(jmp);
                        break;
                    case NOT_EQUAL:
                        jmp = new JumpInsnNode(Opcodes.IFNE, null);
                        mn.instructions.add(jmp);
                        break;
                    case GREATER:
                        jmp = new JumpInsnNode(Opcodes.IFGT, null);
                        mn.instructions.add(jmp);
                        break;
                    case GREATER_EQUAL:
                        jmp = new JumpInsnNode(Opcodes.IFGE, null);
                        mn.instructions.add(jmp);
                        break;
                    case LESS:
                        jmp = new JumpInsnNode(Opcodes.IFLT, null);
                        mn.instructions.add(jmp);
                        break;
                    case LESS_EQUAL:
                        jmp = new JumpInsnNode(Opcodes.IFLE, null);
                        mn.instructions.add(jmp);
                        break;
                    default:
                        ASTUtils.error(node, "Operator not supported");
                        break;
                }
                mn.instructions.add(new InsnNode(Opcodes.ICONST_0));
                LabelNode endLabelNode = new LabelNode();
                mn.instructions.add(new JumpInsnNode(Opcodes.GOTO, endLabelNode));
                LabelNode trueLabelNode = new LabelNode();
                jmp.label = trueLabelNode;
                mn.instructions.add(trueLabelNode);
                mn.instructions.add(new InsnNode(Opcodes.ICONST_1));
                mn.instructions.add(endLabelNode);
            }
            else if (type.equals(Type.INT_TYPE)) {
                LabelNode trueLabelNode = new LabelNode();
                switch (op) {
                    case EQUAL:
                        mn.instructions.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, trueLabelNode));
                        break;
                    case NOT_EQUAL:
                        mn.instructions.add(new JumpInsnNode(Opcodes.IF_ICMPNE, trueLabelNode));
                        break;
                    case GREATER:
                        mn.instructions.add(new JumpInsnNode(Opcodes.IF_ICMPGT, trueLabelNode));
                        break;
                    case GREATER_EQUAL:
                        mn.instructions.add(new JumpInsnNode(Opcodes.IF_ICMPGE, trueLabelNode));
                        break;
                    case LESS:
                        mn.instructions.add(new JumpInsnNode(Opcodes.IF_ICMPLT, trueLabelNode));
                        break;
                    case LESS_EQUAL:
                        mn.instructions.add(new JumpInsnNode(Opcodes.IF_ICMPLE, trueLabelNode));
                        break;
                    default:
                        break;
                }
                mn.instructions.add(new InsnNode(Opcodes.ICONST_0));
                LabelNode endLabelNode = new LabelNode();
                mn.instructions.add(new JumpInsnNode(Opcodes.GOTO, endLabelNode));
                mn.instructions.add(trueLabelNode);
                mn.instructions.add(new InsnNode(Opcodes.ICONST_1));
                mn.instructions.add(endLabelNode);
            }
            else {
                ASTUtils.error(node, "Cannot compare such types.");
            }
        }
        else {
            ASTUtils.error(node, "Operator not recognized.");
        }
    }

    /**
     * Assumes top of stack contains two strings
     */
    private void handleStringOperator(ASTNode node, Operator op) throws ASTVisitorException {
        if (op.equals(Operator.PLUS)) {
            mn.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/lang/StringBuilder"));
            mn.instructions.add(new InsnNode(Opcodes.DUP));
            mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false));
            mn.instructions.add(new InsnNode(Opcodes.SWAP));
            mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
            mn.instructions.add(new InsnNode(Opcodes.SWAP));
            mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
            mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false));
        }
        else if (op.isRelational()) {
            LabelNode trueLabelNode = new LabelNode();
            switch (op) {
                case EQUAL:
                    mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false));
                    mn.instructions.add(new JumpInsnNode(Opcodes.IFNE, trueLabelNode));
                    break;
                case NOT_EQUAL:
                    mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false));
                    mn.instructions.add(new JumpInsnNode(Opcodes.IFEQ, trueLabelNode));
                    break;
                default:
                    ASTUtils.error(node, "Operator not supported on strings");
                    break;
            }
            mn.instructions.add(new InsnNode(Opcodes.ICONST_0));
            LabelNode endLabelNode = new LabelNode();
            mn.instructions.add(new JumpInsnNode(Opcodes.GOTO, endLabelNode));
            mn.instructions.add(trueLabelNode);
            mn.instructions.add(new InsnNode(Opcodes.ICONST_1));
            mn.instructions.add(endLabelNode);
        }
        else {
            ASTUtils.error(node, "Operator not recognized");
        }
    }

    @Override
    public void visit(AccessorExpression node) throws ASTVisitorException {
        node.getExpression().accept(this);
        //function access
        if (node.getExpressions() != null) {

            node.getExpressions().accept(this);

            //if expressionS length is 0, no args privided
            Type returnType;
            Type exprClass = ASTUtils.getSafeType(node.getExpression());
            SymTable<SymTableEntry> sTable = ASTUtils.getSafeEnv(node);
            SymTableEntry lookup = sTable.lookup(node.getIdentifier());
            if (lookup != null) {
                mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                        exprClass.getDescriptor(),
                        node.getIdentifier(),
                        Type.getMethodDescriptor(lookup.getType(), lookup.getParametersTypes()),
                        true));
            }
        }
        //field access
        else {

            mn.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, cn.name, node.getIdentifier(), ASTUtils.getSafeType(node).getDescriptor()));

        }
    }

    @Override
    public void visit(AssignmentStatement node) throws ASTVisitorException {
        node.getExpression1().accept(this);
        Type exprType1 = ASTUtils.getSafeType(node.getExpression1());

        node.getExpression2().accept(this);
        Type exprType2 = ASTUtils.getSafeType(node.getExpression2());

        Type type = ASTUtils.getSafeType(node.getExpression2());

        LocalIndexPool lip = ASTUtils.getSafeLocalIndexPool(node);
        int li = lip.getLocalIndex(type);
        //tests
        SymTable<SymTableEntry> sTable = ASTUtils.getSafeEnv(node.getExpression1());
        Integer index = ASTUtils.getIndex(node.getExpression1());
        System.out.println("***** class: " + node.getExpression1().getClass() + " index: " + index + " old index: " + li);
        widen(exprType1, exprType2);
        mn.instructions.add(new VarInsnNode(exprType2.getOpcode(Opcodes.ISTORE), li));
        lip.freeLocalIndex(li, type);
    }

    @Override
    public void visit(BinaryExpression node) throws ASTVisitorException {
        node.getExpression1().accept(this);
        Type expr1Type = ASTUtils.getSafeType(node.getExpression1());

        node.getExpression2().accept(this);
        Type expr2Type = ASTUtils.getSafeType(node.getExpression2());

        Type maxType = TypeUtils.maxType(expr1Type, expr2Type);

        // cast top of stack to max
        if (!maxType.equals(expr2Type)) {
            widen(maxType, expr2Type);
        }
        System.out.println("^^^^^^^^^^^^^^^^^^^ binary op: " + node.getOperator());
        System.out.println("             type1: " + expr1Type + " type2: " + expr2Type);
        System.out.println("                max type: " + !maxType.equals(expr1Type));
        // cast second from top to max
        if (!maxType.equals(expr1Type)) {
            System.out.println("not to be dispalyed");
            LocalIndexPool lip = ASTUtils.getSafeLocalIndexPool(node);
            int localIndex = -1;
            if (expr2Type.equals(Type.DOUBLE_TYPE) || expr1Type.equals(Type.DOUBLE_TYPE)) {
                localIndex = lip.getLocalIndex(expr2Type);
                mn.instructions.add(new VarInsnNode(expr2Type.getOpcode(Opcodes.ISTORE), localIndex));
            }
            else {
                mn.instructions.add(new InsnNode(Opcodes.SWAP));
            }
            widen(maxType, expr1Type);
            if (expr2Type.equals(Type.DOUBLE_TYPE) || expr1Type.equals(Type.DOUBLE_TYPE)) {

                mn.instructions.add(new VarInsnNode(expr2Type.getOpcode(Opcodes.ILOAD), localIndex));
                lip.freeLocalIndex(localIndex, expr2Type);
            }
            else {
                mn.instructions.add(new InsnNode(Opcodes.SWAP));
            }
        }

        // 
        if (ASTUtils.isBooleanExpression(node)) {
            handleBooleanOperator(node, node.getOperator(), maxType);
        }
        else if (maxType.equals(TypeUtils.STRING_TYPE)) {
            mn.instructions.add(new InsnNode(Opcodes.SWAP));
            handleStringOperator(node, node.getOperator());
        }
        else {
            handleNumberOperator(node, node.getOperator(), maxType);
        }
    }

    @Override
    public void visit(BreakStatement node) throws ASTVisitorException {
        JumpInsnNode jmp = new JumpInsnNode(Opcodes.GOTO, null);
        mn.instructions.add(jmp);
        ASTUtils.getBreakList(node).add(jmp);
    }

    @Override
    public void visit(ClassDefinition node) throws ASTVisitorException {
        ClassNode classNode = new ClassNode();
        cn = classNode;

        cn.version = Opcodes.V1_5;
        cn.superName = "java/lang/Object";
        cn.name = node.getIdentifier();
        cn.access = Opcodes.ACC_PUBLIC;

        // create constructor
        mn = new MethodNode(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false));
        mn.instructions.add(new InsnNode(Opcodes.RETURN));

        mn.maxLocals = 1;
        mn.maxStack = 1;
        classNode.methods.add(mn);

        node.getFfDefinitions().accept(this);

        // IMPORTANT: this should be dynamically calculated
        // use COMPUTE_MAXS when computing the ClassWriter,
        // e.g. new ClassWriter(ClassWriter.COMPUTE_MAXS)
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
//         ClassVisitor ca = new CheckClassAdapter(cw);
//         cn.accept(new CheckClassAdapter(ca));
        TraceClassVisitor cv = new TraceClassVisitor(cw, new PrintWriter(System.out));

        cn.accept(cv);
        //get code
        byte code[] = cw.toByteArray();

        Logger.getLogger(BytecodeGeneratorASTVisitor.class.getName()).info("Writing " + node.getIdentifier() + " class to .class file");
        // update to file
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(node.getIdentifier() + ".class");
            fos.write(code);
            fos.close();
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(BytecodeGeneratorASTVisitor.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(BytecodeGeneratorASTVisitor.class.getName()).log(Level.SEVERE, null, ex);
        }

        cn = null;
        System.out.println("##### finished writing to file");

    }

    @Override
    public void visit(CompUnit node) throws ASTVisitorException {
        for (ClassDefinition cd : node.getClassDefinitions()) {
            cd.accept(this);
        }
    }

    @Override
    public void visit(CompoundStatement node) throws ASTVisitorException {
        node.getStatements().accept(this);
    }

    @Override
    public void visit(ContinueStatement node) throws ASTVisitorException {
        JumpInsnNode jmp = new JumpInsnNode(Opcodes.GOTO, null);
        mn.instructions.add(jmp);
        ASTUtils.getContinueList(node).add(jmp);
    }

    @Override
    public void visit(DeclarationStatement node) throws ASTVisitorException {
        SymTable<SymTableEntry> sTable = ASTUtils.getSafeEnv(node);
        SymTableEntry lookup = sTable.lookup(node.getIdentifier());
        node.getType().accept(this);
        //nothing??
    }

    @Override
    public void visit(FloatLiteralExpression node) throws ASTVisitorException {
        if (ASTUtils.isBooleanExpression(node)) {
            JumpInsnNode i = new JumpInsnNode(Opcodes.GOTO, null);
            mn.instructions.add(i);
            if (node.getLiteral() != 0) {
                ASTUtils.getTrueList(node).add(i);
            }
            else {
                ASTUtils.getFalseList(node).add(i);
            }
        }
        else {
            Float d = node.getLiteral();
            mn.instructions.add(new LdcInsnNode((d)));
        }
    }

    @Override
    public void visit(ExpressionList node) throws ASTVisitorException {
        if (!node.getExpressions().isEmpty()) { //if there is a list of expressions
            for (Expression e : node.getExpressions()) {
                e.accept(this);
            }
        }
    }

    @Override
    public void visit(ExpressionStatement node) throws ASTVisitorException {
        node.getExpression().accept(this);
    }

    @Override
    public void visit(FFDefinitionsList node) throws ASTVisitorException {
        if (!node.getFfDefinitons().isEmpty()) {
            for (FFDefinition ffd : node.getFfDefinitons()) {
                ffd.accept(this);
            }
        }
    }

    @Override
    public void visit(FieldDefinition node) throws ASTVisitorException {
        cn.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, node.getIdentifier(), node.getType().getTypeSpecifier().getDescriptor(), null, null));
    }

    @Override
    public void visit(FunctionDefinition node) throws ASTVisitorException {
        //fix the signature       
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~ " + node.getIdentifier() + " ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        LocalIndexPool safeLocalIndexPool = ASTUtils.getSafeLocalIndexPool(node);
        String methodType = Type.getMethodDescriptor(node.getType().getTypeSpecifier(), node.getParameters().getParameterTypes());
        int accessor = Opcodes.ACC_PUBLIC;
        if (ASTUtils.getIsStatic(node)) {
            accessor = accessor + Opcodes.ACC_STATIC;
        }
        MethodNode methodNode = new MethodNode(accessor, node.getIdentifier(), methodType, null, null);
        mn = methodNode;

        node.getParameters().accept(this);
        node.getCompoundStatement().accept(this);

        mn.instructions.add(new InsnNode(Opcodes.RETURN));
//            mn.maxLocals = 30;
//        mn.maxStack = 30;

        cn.methods.add(mn);

        mn = null;
    }

    @Override
    public void visit(IdentifierExpression node) throws ASTVisitorException {
        if (node.getExpressions() != null) {
            node.getExpressions().accept(this);
            SymTable<SymTableEntry> symbols = Registry.getInstance().getExistingClass(Type.getType("Lorg/hua/customclasses/" + cn.name + ";"));
            SymTableEntry sEntry = symbols.lookup(node.getIdentifier());
            if (sEntry != null) {
                mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                        Type.getType("Lorg/hua/customclasses/" + cn.name + ";").getDescriptor(),
                        node.getIdentifier(),
                        Type.getMethodDescriptor(Type.VOID_TYPE, Type.INT_TYPE),
                        false));
            }
            else {
                //search all classes
                //if the function is static and defined in another class, ok
                //else error
                String nodeId = node.getIdentifier();
                boolean foundStaticOtherClass = false;
                String classFound;
                Map<Type, SymTable<SymTableEntry>> classes = Registry.getInstance().getClasses();
                Iterator<Map.Entry<Type, SymTable<SymTableEntry>>> entries = classes.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry<Type, SymTable<SymTableEntry>> entry = entries.next();
                    if (entry.getValue().lookup(nodeId) != null) {
                        if (entry.getValue().lookup(nodeId).isIsStatic()) {
                            foundStaticOtherClass = true;
                            classFound = entry.getKey().toString();
                            break;
                        }
                    }
                }
                if (!foundStaticOtherClass) {
                    ASTUtils.error(node, "This static(?) method could not be found");
                }
            }
//            Registry.getInstance().getExistingClass(Type.getType("Lorg/hua/customclasses/"+cn.name));
//            mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, string, string1, string2, true));
        }
        else {

            Type type = ASTUtils.getSafeType(node);
            SymTable<SymTableEntry> symbols = Registry.getInstance().getExistingClass(Type.getType("Lorg/hua/customclasses/" + cn.name + ";"));
            SymTableEntry csEntry = symbols.lookup(node.getIdentifier());
            if (csEntry != null) {

                mn.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, cn.name, node.getIdentifier(), csEntry.getType().getDescriptor()));
                return;
            }
            SymTable<SymTableEntry> sTable = ASTUtils.getSafeEnv(node);
            SymTableEntry sEntry = sTable.lookup(node.getIdentifier());

            mn.instructions.add(new VarInsnNode(type.getOpcode(Opcodes.ILOAD), sEntry.getIndex()));
        }
    }

    @Override
    public void visit(IfElseStatement node) throws ASTVisitorException {
        ASTUtils.setBooleanExpression(node.getExpression(), true);
        node.getExpression().accept(this);

        LabelNode stmt1StartLabelNode = new LabelNode();
        mn.instructions.add(stmt1StartLabelNode);
        node.getStatement1().accept(this);

        JumpInsnNode skipGoto = new JumpInsnNode(Opcodes.GOTO, null);
        mn.instructions.add(skipGoto);

        LabelNode stmt2StartLabelNode = new LabelNode();
        mn.instructions.add(stmt2StartLabelNode);
        node.getStatement2().accept(this);

        backpatch(ASTUtils.getTrueList(node.getExpression()), stmt1StartLabelNode);
        backpatch(ASTUtils.getFalseList(node.getExpression()), stmt2StartLabelNode);

        ASTUtils.getNextList(node).addAll(ASTUtils.getNextList(node.getStatement1()));
        ASTUtils.getNextList(node).addAll(ASTUtils.getNextList(node.getStatement2()));
        ASTUtils.getNextList(node).add(skipGoto);

        ASTUtils.getBreakList(node).addAll(ASTUtils.getBreakList(node.getStatement1()));
        ASTUtils.getBreakList(node).addAll(ASTUtils.getBreakList(node.getStatement2()));

        ASTUtils.getContinueList(node).addAll(ASTUtils.getContinueList(node.getStatement1()));
        ASTUtils.getContinueList(node).addAll(ASTUtils.getContinueList(node.getStatement2()));
    }

    @Override
    public void visit(IfStatement node) throws ASTVisitorException {
        ASTUtils.setBooleanExpression(node.getExpression(), true);

        node.getExpression().accept(this);

        LabelNode labelNode = new LabelNode();
        mn.instructions.add(labelNode);
        backpatch(ASTUtils.getTrueList(node.getExpression()), labelNode);

        node.getStatement().accept(this);

        ASTUtils.getBreakList(node).addAll(ASTUtils.getBreakList(node.getStatement()));
        ASTUtils.getContinueList(node).addAll(ASTUtils.getContinueList(node.getStatement()));

        ASTUtils.getNextList(node).addAll(ASTUtils.getFalseList(node.getExpression()));
        ASTUtils.getNextList(node).addAll(ASTUtils.getNextList(node.getStatement()));
    }

    @Override
    public void visit(IntegerLiteralExpression node) throws ASTVisitorException {
        if (ASTUtils.isBooleanExpression(node)) {
            JumpInsnNode i = new JumpInsnNode(Opcodes.GOTO, null);
            mn.instructions.add(i);
            if (node.getLiteral() != 0) {
                ASTUtils.getTrueList(node).add(i);
            }
            else {
                ASTUtils.getFalseList(node).add(i);
            }
        }
        else {
            Integer d = node.getLiteral();
            mn.instructions.add(new LdcInsnNode(d));
        }
    }

    @Override
    public void visit(NullExpression node) throws ASTVisitorException {
        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, Opcodes.NULL));
    }

    @Override
    public void visit(ParameterDeclaration node) throws ASTVisitorException {
        node.getType().accept(this);
        //is the access flag private?

        mn.parameters.add(new ParameterNode(node.getIdentifier(), Opcodes.ACC_PRIVATE));
        System.out.println("hereeeee " + node.getIdentifier());
    }

    @Override
    public void visit(ParameterList node) throws ASTVisitorException {
        if (!node.getParameters().isEmpty()) {
            mn.parameters = new ArrayList<ParameterNode>();
            for (ParameterDeclaration pd : node.getParameters()) {
                System.out.println("~~~~ param " + pd.getClass());
                pd.accept(this);

            }
        }
    }

    @Override
    public void visit(ParenthesisExpression node) throws ASTVisitorException {
        node.getExpression().accept(this);
    }

    @Override
    public void visit(ReturnStatement node) throws ASTVisitorException {
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
            SymTable<SymTableEntry> sTable = ASTUtils.getSafeEnv(node);
            mn.instructions.add(new VarInsnNode(Opcodes.RETURN, 0));
        }
        else {
            mn.instructions.add(new InsnNode(Opcodes.RETURN));
        }
    }

    @Override
    public void visit(StatementList node) throws ASTVisitorException {
        Statement s = null, ps;
        Iterator<Statement> it = node.getStatements().iterator();
        while (it.hasNext()) {

            ps = s;
            s = it.next();

            if (ps != null && !ASTUtils.getNextList(ps).isEmpty()) {
                LabelNode labelNode = new LabelNode();
                mn.instructions.add(labelNode);
                backpatch(ASTUtils.getNextList(ps), labelNode);
            }
            s.accept(this);
            if (!ASTUtils.getBreakList(s).isEmpty()) {
                ASTUtils.error(s, "Break detected without a loop.");
            }

            if (!ASTUtils.getContinueList(s).isEmpty()) {
                ASTUtils.error(s, "Continue detected without a loop.");
            }

        }

        if (s != null && !ASTUtils.getNextList(s).isEmpty()) {
            LabelNode labelNode = new LabelNode();
            mn.instructions.add(labelNode);
            backpatch(ASTUtils.getNextList(s), labelNode);
        }

    }

    @Override
    public void visit(StringLiteralExpression node) throws ASTVisitorException {
        String s = node.getLiteral();
        mn.instructions.add(new LdcInsnNode(s));
    }

    @Override
    public void visit(ThisExpression node) throws ASTVisitorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(UnaryExpression node) throws ASTVisitorException {
        node.getExpression().accept(this);

        Type type = ASTUtils.getSafeType(node.getExpression());

        if (node.getOperator().equals(Operator.MINUS)) {
            mn.instructions.add(new InsnNode(type.getOpcode(Opcodes.INEG)));
        }
        else {
            ASTUtils.error(node, "Operator not recognized.");
        }
    }

    @Override
    public void visit(WhileStatement node) throws ASTVisitorException {
        ASTUtils.setBooleanExpression(node.getExpression(), true);

        LabelNode beginLabelNode = new LabelNode();
        mn.instructions.add(beginLabelNode);

        node.getExpression().accept(this);

        LabelNode trueLabelNode = new LabelNode();
        mn.instructions.add(trueLabelNode);
        backpatch(ASTUtils.getTrueList(node.getExpression()), trueLabelNode);

        node.getStatement().accept(this);

        backpatch(ASTUtils.getNextList(node.getStatement()), beginLabelNode);
        backpatch(ASTUtils.getContinueList(node.getStatement()), beginLabelNode);

        mn.instructions.add(new JumpInsnNode(Opcodes.GOTO, beginLabelNode));

        ASTUtils.getNextList(node).addAll(ASTUtils.getFalseList(node.getExpression()));
        ASTUtils.getNextList(node).addAll(ASTUtils.getBreakList(node.getStatement()));
    }

    @Override
    public void visit(WriteStatement node) throws ASTVisitorException {

        Type type = ASTUtils.getSafeType(node.getExpression());
        LocalIndexPool lip = ASTUtils.getSafeLocalIndexPool(node);
        int li = lip.getLocalIndex(type);
        mn.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        mn.instructions.add(new VarInsnNode(type.getOpcode(Opcodes.ILOAD), li));
        node.getExpression().accept(this);
        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "print", Type.getMethodType(Type.VOID_TYPE, type).toString(), false));
        lip.freeLocalIndex(li, type);
    }

    @Override
    public void visit(TypeSpecifier node) throws ASTVisitorException {
//        System.out.println("im here "+node.getTypeSpecifier());
    }

    @Override
    public void visit(NewIdentifierExpression node) throws ASTVisitorException {
        if (node.getExpressions() != null) {
            node.getExpressions().accept(this);
        }
        mn.instructions.add(new TypeInsnNode(Opcodes.NEW, node.getIdentifier()));
        mn.instructions.add(new InsnNode(Opcodes.DUP));
        //@TODO: fix calling parameters
        if (node.getExpressions() != null) {
            if (node.getExpressions().getExpressions().isEmpty() && Registry.getInstance().classExists(Type.getType("Lorg/hua/customclasses/" + node.getIdentifier() + ";"))) {
                System.out.println("222222222222222222222 it's in");
                mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, ASTUtils.getSafeType(node).getDescriptor(), "<init>", "()V", false));
            }
            else //get parameter types and then give that as the signature
            if (Registry.getInstance().classExists(Type.getType("Lorg/hua/customclasses/" + node.getIdentifier() + ";"))) {
                SymTable<SymTableEntry> existingClass = Registry.getInstance().getExistingClass(Type.getType("Lorg/hua/customclasses/" + node.getIdentifier() + ";"));
                SymTableEntry lookup = existingClass.lookup(node.getIdentifier());
                if (lookup != null) {
                    Type[] types = lookup.getParametersTypes();
                    mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                            ASTUtils.getSafeType(node).getDescriptor(),
                            node.getIdentifier(), Type.getMethodDescriptor(Type.VOID_TYPE, types),
                            false));
                }
                else {
                    ASTUtils.error(node, "Constructor not found");
                }
            }
            else {
                ASTUtils.error(node, "Problem with called constructor");
            }
        }
//        System.out.println("~~~~ "+ASTUtils.getSafeType(node).getDescriptor());

//        mn.instructions.add(new VarInsnNode(Opcodes.ASTORE, 0));
    }

    /**
     * Cast the top of the stack to a particular type
     */
    private void widen(Type target, Type source) {
        if (source.equals(target)) {
            return;
        }

        if (source.equals(Type.BOOLEAN_TYPE)) {
            if (target.equals(Type.INT_TYPE)) {
                // nothing
            }
            else if (target.equals(Type.DOUBLE_TYPE)) {
                mn.instructions.add(new InsnNode(Opcodes.I2D));
            }
            else if (target.equals(TypeUtils.STRING_TYPE)) {
                mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Boolean", "toString", "(Z)Ljava/lang/String;", false));
            }
        }
        else if (source.equals(Type.INT_TYPE)) {
            if (target.equals(Type.DOUBLE_TYPE)) {
                mn.instructions.add(new InsnNode(Opcodes.I2D));
            }
            else if (target.equals(TypeUtils.STRING_TYPE)) {
                mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "toString", "(I)Ljava/lang/String;", false));
            }
        }
        else if (source.equals(Type.DOUBLE_TYPE)) {
            if (target.equals(TypeUtils.STRING_TYPE)) {
                mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Double", "toString", "(D)Ljava/lang/String;", false));
            }
        }
    }

}
