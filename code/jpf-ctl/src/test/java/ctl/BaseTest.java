/*
 * Copyright (C)  2021
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ctl;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import org.ctl.CTLLexer;
import org.ctl.CTLParser;

/**
 * Base for all the tests.
 * 
 * @author Jinho Hwang
 * @author Minh Cu (Huy Cu)
 * @author Alberto Mastrofrancesco
 * @author Qasim Ahmed
 * @author Parssa Khazra
 * @author Hongru Wang
 */
public abstract class BaseTest {

	/**
	 * Generates abstract syntax tree from parse tree.
	 */
	private static final Generator generator = new Generator();

	/**
	 * Number of times tests that involve randomness are repeated.
	 */
	protected static final int TIMES = 1000;

	/**
	 * Translates a syntactically correct CTL formula from its string representation to an
	 * abstract syntax tree.
	 * 
	 * @param formula CTL formula
	 * @return the abstract syntax corresponding to the given CTL formula
	 */
	public static Formula parse(String formula) {
		CharStream input = CharStreams.fromString(formula);
		CTLLexer lexer = new CTLLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CTLParser parser = new CTLParser(tokens);
		ParseTree tree = parser.formula();
		return generator.visit(tree);
	}
}
