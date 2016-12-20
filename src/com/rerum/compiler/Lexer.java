package com.rerum.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Nikita on 18.11.2016.
 */

public class Lexer {
	private List<Symbol> m_symbolTable;
	private List<Token> m_tokenTable;

	private int m_currentLine;

	private boolean m_isInComment = false;

	private int m_tokenBeginPos = -1;

	private List<String> m_reservedWords = new ArrayList<String>() {{
			add("Var");
			add("REPEAT");
			add("UNTIL");
			add("Begin");
			add("End");
			add("not");
		}};

	public Lexer() {
		init();
	}

	public void processFile(File file) {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			m_currentLine = 0;
			while ((line = br.readLine()) != null) {
				m_currentLine++;
				parseLine(line);
			}
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public Token getNextToken() {
		if (m_tokenTable.isEmpty()) {
			return null;
		}
		return m_tokenTable.remove(0);
	}

	private void init() {
		m_currentLine = 0;
		m_symbolTable = new ArrayList<>();
		m_tokenTable = new ArrayList<>();
	}

	private void parseLine(String line) {
		int state = 0;
		int currentCharPos = 0;
		char currentChar;
		char nextChar;

		try {
			while (true) {
				currentChar = line.charAt(currentCharPos);

				try {
					nextChar = line.charAt(currentCharPos+1);
				}
				catch (IndexOutOfBoundsException e) {
					nextChar = '\0';
				}

				if (m_isInComment) {
					if (currentChar == '*' && nextChar == '/') {
						m_isInComment = false;
						currentCharPos++;
					}
					currentCharPos++;
					continue;
				}

				switch (state) {
					case 0:
						switch (currentChar) {
							case '-':
								state = 1;
								break;
							case '+':
								state = 2;
								break;
							case '*':
								state = 3;
								break;
							case '/':
								state = 4;
								break;
							case '=':
								state = 5;
								break;
							case '>':
								state = 8;
								break;
							case ';':
								state = 12;
								break;
							case ':':
								state = 17;
								break;
							case ',':
								state = 19;
								break;
							case '(':
							case ')':
								state = 20;
								break;
							default:
								if (isLetter(currentChar)) {
									m_tokenBeginPos = currentCharPos;
									state = 13;
									break;
								}
								if (isNumber(currentChar)) {
									m_tokenBeginPos = currentCharPos;
									state = 15;
									break;
								}

								currentCharPos++;

								if (isWhitespace(currentChar)) {
									break;
								}

								System.err.println("Line " + m_currentLine + ": unexpected char: '" +
										currentChar + "'");
								//System.exit(1);
								break;
						}
						break;
					case 1:
						m_tokenTable.add(new Token(TokenType.MINUS, m_currentLine));
						state = 0;
						currentCharPos++;
						break;
					case 2:
						m_tokenTable.add(new Token(TokenType.ADD, m_currentLine));
						state = 0;
						currentCharPos++;
						break;
					case 3:
						m_tokenTable.add(new Token(TokenType.MUL, m_currentLine));
						state = 0;
						currentCharPos++;
						break;
					case 4: {
						if (nextChar == '*') {
							currentCharPos++;
							//m_isInComment = true;
							state = 10;
						} else {
							state = 11;
						}
					}
						break;
					case 5: {
						try {
							if (nextChar == '<') {
								currentCharPos++;
								state = 6;
							} else {
								state = 7;
							}
						}
						catch (IndexOutOfBoundsException e) {
							m_tokenTable.add(new Token(TokenType.UNDEFINED, currentChar, m_currentLine));
						}
					}
					break;
					case 6:
						m_tokenTable.add(new Token(TokenType.LEQ, m_currentLine));
						currentCharPos++;
						state = 0;
						break;
					case 7:
						m_tokenTable.add(new Token(TokenType.EQ, m_currentLine));
						currentCharPos++;
						state = 0;
						break;
					case 8: {
						if (nextChar == '=') {
							currentCharPos++;
							state = 9;
						} else {
							m_tokenTable.add(new Token(TokenType.UNDEFINED, currentChar, m_currentLine));
							currentCharPos++;
							state = 0;
						}
					}
					break;
					case 9:
						m_tokenTable.add(new Token(TokenType.GEQ, m_currentLine));
						currentCharPos++;
						state = 0;
						break;
					case 10:
						m_isInComment = true;
						currentCharPos++;
						state = 0;
						break;
					case 11:
						m_tokenTable.add(new Token(TokenType.DIV, m_currentLine));
						currentCharPos++;
						state = 0;
						break;
					case 12:
						m_tokenTable.add(new Token(TokenType.SEMICOLON, m_currentLine));
						currentCharPos++;
						state = 0;
						break;
					case 13:
						if (!isLetter(nextChar)) {
							String identifier = line.substring(m_tokenBeginPos, currentCharPos+1);
							if (!isReservedWord(identifier)) {
								installId(identifier);
								m_tokenTable.add(new Token(TokenType.IDENTIFIER, identifier, m_currentLine));
							} else {
								switch (identifier) {
									case "Var":
										m_tokenTable.add(new Token(TokenType.VAR, m_currentLine));
										break;
									case "REPEAT":
										m_tokenTable.add(new Token(TokenType.REPEAT, m_currentLine));
										break;
									case "UNTIL":
										m_tokenTable.add(new Token(TokenType.UNTIL, m_currentLine));
										break;
									case "Begin":
										m_tokenTable.add(new Token(TokenType.BEGIN, m_currentLine));
										break;
									case "End":
										m_tokenTable.add(new Token(TokenType.END, m_currentLine));
										break;
									case "not":
										m_tokenTable.add(new Token(TokenType.NOT, m_currentLine));
										break;
								}
							}
							state = 0;
							m_tokenBeginPos = -1;

						}
						currentCharPos++;
						break;
					case 15:
						if (!isNumber(nextChar)) {
							String numberStr = line.substring(m_tokenBeginPos, currentCharPos+1);
							Integer i = Integer.parseInt(numberStr);
							//installId(line.substring();
							m_tokenTable.add(new Token(TokenType.NUMBER, i, m_currentLine));
							state = 0;
							m_tokenBeginPos = -1;
						}
						currentCharPos++;
						break;
					case 17:
						if (nextChar == '=') {
							currentCharPos++;
							state = 18;
						} else {
							m_tokenTable.add(new Token(TokenType.UNDEFINED, currentChar, m_currentLine));
							currentCharPos++;
							state = 0;
						}
						break;
					case 18:
						m_tokenTable.add(new Token(TokenType.ASSIGN, m_currentLine));
						currentCharPos++;
						state = 0;
						break;
					case 19:
						m_tokenTable.add(new Token(TokenType.COMMA, m_currentLine));
						currentCharPos++;
						state = 0;
						break;
					case 20:
						m_tokenTable.add(new Token(TokenType.BRACKET, currentChar, m_currentLine));
						currentCharPos++;
						state = 0;
						break;
				}
			}
		}
		catch (IndexOutOfBoundsException e) {

		}

	}

	private int processReservedWord(String word) {
		if (word.matches("/^Var[^A-Za-z]/g")) {
			m_tokenTable.add(new Token(TokenType.VAR, m_currentLine));
			return 3;
		}
		if (word.matches("^Begin[^A-Za-z]")) {
			m_tokenTable.add(new Token(TokenType.BEGIN, m_currentLine));
			return 5;
		}
		if (word.matches("^End[^A-Za-z]")) {
			m_tokenTable.add(new Token(TokenType.END, m_currentLine));
			return 3;
		}
		if (word.matches("^REPEAT[^A-Za-z]")) {
			m_tokenTable.add(new Token(TokenType.REPEAT, m_currentLine));
			return 6;
		}
		if (word.matches("^UNTIL[^A-Za-z]")) {
			m_tokenTable.add(new Token(TokenType.UNTIL, m_currentLine));
			return 5;
		}
		return 0;
	}

	private boolean isLetter(char ch) {
		if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z')
			return true;
		return false;
	}

	private boolean isNumber(char ch) {
		if (ch >= '0' && ch <= '9') {
			return true;
		}
		return false;
	}

	private boolean isWhitespace(char ch) {
		return ch == ' ' || ch == '\t';
	}

	private void installId(String identifier) {
		//System.out.println(identifier);
	}

	private boolean isReservedWord(String word) {
		return m_reservedWords.contains(word);
	}
}
