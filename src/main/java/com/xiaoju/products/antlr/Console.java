package com.xiaoju.products.antlr;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;

public class Console {
	 public static void run(String expr) throws Exception { 
	 ANTLRStringStream in = new ANTLRStringStream(expr); 
	 ExprLexer lexer = new ExprLexer(in); 
		 CommonTokenStream tokens = new CommonTokenStream(lexer); 
		 ExprParser parser = new ExprParser(tokens); 
		 parser.prog(); 	
	 }
	 
	 
	 public static void main(String[] args) {
		try {
			run(args[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

