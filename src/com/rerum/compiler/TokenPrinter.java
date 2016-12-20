package com.rerum.compiler;

/**
 * Created by Nikita on 20.12.2016.
 */
public class TokenPrinter {
	public void println(Token token) {
		System.out.println(token.getLineNumber() + ' ' + token.getType().toString());
	}
}
