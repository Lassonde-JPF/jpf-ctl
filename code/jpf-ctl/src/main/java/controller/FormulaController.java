package controller;

import java.util.Set;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.ctl.CTLLexer;
import org.ctl.CTLParser;

import error.LabelChecker;
import error.LabelDoesNotExistException;
import formulas.Formula;
import formulas.ctl.Generator;
import model.LogicType;

public class FormulaController {
	
	public static Formula parseFormula(Set<String> labels, String input, LogicType type) throws LabelDoesNotExistException {
		switch (type) {
			case CTL:
				return parseCTLFormula(labels, input);
			default:
				return null; // TODO shouldn't happen
		}
	}
	
	private static Formula parseCTLFormula(Set<String> labels, String input) {
		CharStream inputStream = CharStreams.fromString(input);
		ParseTree pT = new CTLParser(new CommonTokenStream(new CTLLexer(inputStream))).formula();
		
		LabelChecker.checkLabelsExist(labels, pT);
		
		return new Generator().visit(pT);
	}
	
}
