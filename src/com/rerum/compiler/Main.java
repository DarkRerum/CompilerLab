package com.rerum.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by Nikita on 18.11.2016.
 */
public class Main {
	public static void main(String[] args) {
		//System.out.println("test");
		Lexer lexer = new Lexer();
		File file = new File("tests/test1.pas");
		lexer.processFile(file);
		Token token = lexer.getNextToken();
		while (token != null) {
			System.out.println(token);
			token = lexer.getNextToken();
		}
	}

	static void readFile() {


	}
}