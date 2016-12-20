/**
 * Created by Nikita on 18.11.2016.
 */

package com.rerum.compiler;

import com.rerum.compiler.*;

public class Token {
	private TokenType m_tokenType;
	private Object m_attribute;
	private int m_lineNumber;

	public Token(TokenType tokenType, Object attribute, int lineNumber) {
		m_tokenType = tokenType;
		m_attribute = attribute;
		m_lineNumber = lineNumber;
	}

	public Token(TokenType tokenType, int lineNumber) {
		m_tokenType = tokenType;
		m_attribute = null;
		m_lineNumber = lineNumber;
	}

	public TokenType getType() {
		return m_tokenType;
	}

	public Object getAttribute() {
		return m_attribute;
	}

	public int getLineNumber() {
		return m_lineNumber;
	}

	public String toString() {
		String string;
		if (m_attribute != null)
			string = m_tokenType.toString() + ' ' + m_attribute.toString();
		else
			string = m_tokenType.toString();
		return string;
	}
}
