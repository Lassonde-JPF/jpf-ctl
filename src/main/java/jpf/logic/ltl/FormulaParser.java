/*
 * Copyright (C)  2022
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
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package jpf.logic.ltl;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * Parses LTL formulas.
 * 
 * @author Franck van Breugel
 */
public class FormulaParser implements jpf.logic.FormulaParser {

	@Override
	public Formula parse(String formula) {
		CharStream input = CharStreams.fromString(formula);
		LTLLexer lexer = new LTLLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		LTLParser parser = new LTLParser(tokens);
		ParseTree tree = parser.formula();
		Visitor visitor = new Visitor();
		return visitor.visit(tree);
	}
}
