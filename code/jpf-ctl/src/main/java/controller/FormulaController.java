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
import error.LogicDoesNotExistException;
import formulas.Formula;
import formulas.ctl.Generator;
import model.LogicType;

/**
 * Formula controller.
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public class FormulaController {
	
	/**
	 * Parses a formula string into a Formula object using a particular logic type and set of predefined labels. 
	 * 
	 * @param labels - labels previously defined by user
	 * @param input - string representation of a formula
	 * @param type - type of logic to consider this formula
	 * @return Formula - a Formula object 
	 * @throws LabelDoesNotExistException
	 * @throws LogicDoesNotExistException 
	 */
	public static Formula parseFormula(Set<String> labels, String input, LogicType type) throws LabelDoesNotExistException {
		switch (type) {
			case CTL:
				return parseCTLFormula(labels, input);
			default:
				return null;
		}
	}
	
	// private method for parsing CTL formulas
	private static Formula parseCTLFormula(Set<String> labels, String input) {
		CharStream inputStream = CharStreams.fromString(input);
		ParseTree pT = new CTLParser(new CommonTokenStream(new CTLLexer(inputStream))).formula();
		
		LabelChecker.checkLabelsExist(labels, pT);
		
		return new Generator().visit(pT);
	}
	
}
