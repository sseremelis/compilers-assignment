package org.hua;

/**
 * This code is part of the lab exercises for the Compilers course at Harokopio
 * University of Athens, Dept. of Informatics and Telematics.
 */
import org.hua.ast.visitors.CollectTypesASTVisitor;
import org.hua.ast.visitors.PrintASTVisitor;
import org.hua.ast.visitors.CollectSymbolsASTVisitor;
import org.hua.ast.visitors.SymTableBuilderASTVisitor;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.hua.ast.ASTNode;
import org.hua.ast.ASTUtils;
import org.hua.ast.ASTVisitor;
import org.hua.symbol.SymTable;
import org.hua.symbol.SymTableEntry;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Compiler1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Compiler1.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            LOGGER.info("Usage : java Compiler [ --encoding <name> ] <inputfile(s)>");
        }
        else {
            int firstFilePos = 0;
            String encodingName = "UTF-8";
            if (args[0].equals("--encoding")) {
                firstFilePos = 2;
                encodingName = args[1];
                try {
                    java.nio.charset.Charset.forName(encodingName); // Side-effect: is encodingName valid? 
                }
                catch (Exception e) {
                    LOGGER.error("Invalid encoding '" + encodingName + "'");
                    return;
                }
            }
            for (int i = firstFilePos; i < args.length; i++) {
                Lexer scanner = null;
                try {
                    java.io.FileInputStream stream = new java.io.FileInputStream(args[i]);
                    LOGGER.info("-----> Scanning file " + args[i]);
                    java.io.Reader reader = new java.io.InputStreamReader(stream, encodingName);
                    scanner = new Lexer(reader);

                    // parse
                    parser p = new parser(scanner);
                    ASTNode compUnit = (ASTNode) p.parse().value;
                    LOGGER.info("-----> Constructed AST");

                    // keep global instance of program
                    Registry.getInstance().setRoot(compUnit);

                    // build symbol table
                    LOGGER.info("-----> Building system table");
                    compUnit.accept(new SymTableBuilderASTVisitor());

                    // construct types
                    LOGGER.info("-----> Semantic check");
                    LOGGER.info("-----> CollectSymbols");
                    compUnit.accept(new CollectSymbolsASTVisitor());

                    LOGGER.info("-----> CollectTypes");
                    compUnit.accept(new CollectTypesASTVisitor());
                    
                    //print all classes, theis fields+functions, and functions parameters
                    Map<Type, SymTable<SymTableEntry>> classes = Registry.getInstance().getClasses();
                    Iterator<Map.Entry<Type, SymTable<SymTableEntry>>> entries = classes.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry<Type, SymTable<SymTableEntry>> classEntry = entries.next();
                        System.out.println("class: " + classEntry.getKey());
                        SymTable<SymTableEntry> classSymTable = classEntry.getValue();
                        for (SymTableEntry e : classSymTable.getSymbols()) {
                            System.out.println("|---> id: " + e.getId() + " type: " + e.getType());
                            if (e.getParameters() != null) {
                                SymTable<SymTableEntry> prms = e.getParameters();
                                for (SymTableEntry param : prms.getSymbols()) {
                                    System.out.println("|---|---> param: " + param.getId() + " type: " + param.getType());
                                }
                            }
                        }
                    }

                    // print program
                    LOGGER.info("Input:");
                    ASTVisitor printVisitor = new PrintASTVisitor();
                    compUnit.accept(printVisitor);

                    LOGGER.info("Compilation done");
                }
                catch (java.io.FileNotFoundException e) {
                    LOGGER.error("File not found : \"" + args[i] + "\"");
                }
                catch (java.io.IOException e) {
                    LOGGER.error("IO error scanning file \"" + args[i] + "\"");
                    LOGGER.error(e.toString());
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage());
                    //e.printStackTrace();
                }
            }
        }
    }

}
