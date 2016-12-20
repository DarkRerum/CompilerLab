package com.rerum.compiler;

/**
 * Created by Nikita on 20.12.2016.
 */
public class TokenPrinter {
	public void println(Token token) {
		//System.out.println(token.getLineNumber() + ' ' + token.getType().toString());
		Object attr = token.getAttribute();
		if (attr != null)
			System.out.format("%4s %-15s%-30s\n", token.getLineNumber(), token.getType(), attr);
		else
			System.out.format("%4s %-15s\n", token.getLineNumber(), token.getType());
	}
}
