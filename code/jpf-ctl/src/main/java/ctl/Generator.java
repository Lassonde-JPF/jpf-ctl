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

import parser.CTLBaseVisitor;
import parser.CTLParser.AndContext;
import parser.CTLParser.AtomicPropositionContext;
import parser.CTLParser.BracketContext;
import parser.CTLParser.ExistsAlwaysContext;
import parser.CTLParser.ExistsEventuallyContext;
import parser.CTLParser.ExistsNextContext;
import parser.CTLParser.ExistsUntilContext;
import parser.CTLParser.FalseContext;
import parser.CTLParser.ForAllAlwaysContext;
import parser.CTLParser.ForAllEventuallyContext;
import parser.CTLParser.ForAllNextContext;
import parser.CTLParser.ForAllUntilContext;
import parser.CTLParser.IffContext;
import parser.CTLParser.ImpliesContext;
import parser.CTLParser.NotContext;
import parser.CTLParser.OrContext;
import parser.CTLParser.RootContext;
import parser.CTLParser.FormulaContext;
import parser.CTLParser.TrueContext;

/**
 * Generates an abstract syntax tree from a parse tree.
 * 
 * @author Neena Govindhan
 * @author Jonas Laya
 * @author Jessie Leung
 * @author Paul Sison
 * @author Franck van Breugel
 */

public class Generator extends CTLBaseVisitor<Formula> {

	/**
	 * Visits a root node of the parse tree.
	 * @return	The first formula node of the parse tree
	 */
	@Override
	public Formula visitRoot(RootContext context) {
		return visit(context.formula());
	}

	/**
	 * Visits a bracket node in the parse tree.
	 * 
	 * @return The formula node contained within the brackets
	 */
	@Override
	public Formula visitBracket(BracketContext context) {
		return visit(context.formula());
	}

	/**
	 * Visits the subformula node of a ForAllAlways node in the parse tree. 
	 * 
	 * @return	A {@code ForAll} instance of an {@code Always} instance of the
	 * 			inner subformula
	 */
	@Override
	public Formula visitForAllAlways(ForAllAlwaysContext context) {
		Formula formula = (Formula) visit(context.formula());
		return new ForAllAlways(formula);
	}

	/**
	 * Visits the left and right subformula nodes of an Or node in the parse tree. 
	 * 
	 * @return	An {@code Or} instance containing the left and right subformulas
	 */
	@Override
	public Formula visitOr(OrContext context) {
		Formula left = (Formula) visit(context.formula(0));
		Formula right = (Formula) visit(context.formula(1));
		return new Or(left, right);
	}

	/**
	 * Visits the left and right subformula nodes of an Iff node in the parse tree.
	 *  
	 * @return	An {@code Iff} instance containing the left and right subformulas
	 */
	@Override
	public Formula visitIff(IffContext context) {
		Formula left = (Formula) visit(context.formula(0));
		Formula right = (Formula) visit(context.formula(1));	
		return new Iff(left, right);
	}

	/**
	 * Visits a True terminal node in the parse tree.
	 * 
	 * @return	A {@code True} instance
	 */
	@Override
	public Formula visitTrue(TrueContext context) {
		return new True();
	}

	/**
	 * Visits a False terminal node in the parse tree.
	 * 
	 * @return	A {@code False} instance
	 */
	@Override
	public Formula visitFalse(FalseContext context) {
		return new False();
	}

	/**
	 * Visits the subformula node of an ExistsEventually node in the parse tree.
	 *  
	 * @return	An {@code Exists} instance of an {@code Eventually} instance of the
	 * 			inner subformula
	 */
	@Override
	public Formula visitExistsEventually(ExistsEventuallyContext context) {
		Formula formula = (Formula) visit(context.formula());
		return new ExistsEventually(formula);
	}

	/**
	 * Visits an AtomicProposition terminal node in the parse tree.
	 * 
	 * @return	An {@code AtomicProposition} instance containing a string representation
	 * 			of an atomic proposition as defined by the grammar
	 */
	@Override
	public Formula visitAtomicProposition(AtomicPropositionContext context) {
		return new AtomicProposition(context.ATOMIC_PROPOSITION().toString());
	}

	/**
	 * Visits the subformula node of a ForAllEventually node in the parse tree.
	 *  
	 * @return	A {@code ForAll} instance of an {@code Eventually} instance of the
	 * 			inner subformula
	 */
	@Override
	public Formula visitForAllEventually(ForAllEventuallyContext context) {
		Formula formula = (Formula) visit(context.formula());
		return new ForAllEventually(formula);
	}

	/**
	 * Visits the subformula node of a Not node in the parse tree.
	 * 
	 * @return	A {@code Not} instance of the inner subformula
	 */
	@Override
	public Formula visitNot(NotContext context) {
		Formula formula = (Formula) visit(context.formula());
		return new Not(formula);
	}

	/**
	 * Visits the left and right subformula nodes of a ForAllUntil node in the parse tree. 
	 * 
	 * @return	A {@code ForAll} instance of an {@code Until} instance containing the 
	 * 			left and right subformulas
	 */
	@Override
	public Formula visitForAllUntil(ForAllUntilContext context) {
		Formula left = (Formula) visit(context.formula(0));
		Formula right = (Formula) visit(context.formula(1));	
		return new ForAllUntil(left, right);
	}

	/**
	 * Visits the left and right subformula nodes of an Implies node in the parse tree. 
	 * 
	 * @return	An {@code Implies} instance containing the left and right subformulas
	 */
	@Override
	public Formula visitImplies(ImpliesContext context) {
		Formula left = (Formula) visit(context.formula(0));
		Formula right = (Formula) visit(context.formula(1));
		return new Implies(left, right);
	}

	/**
	 * Visits the subformula node of a ForAllNext node in the parse tree. 
	 * 
	 * @return	A {@code ForAll} instance of a {@code Next} instance of the
	 * 			inner subformula
	 */
	@Override
	public Formula visitForAllNext(ForAllNextContext context) {
		Formula formula = (Formula) visit(context.formula());
		return new ForAllNext(formula);
	}

	/**
	 * Visits the left and right subformula nodes of an And node in the parse tree. 
	 * 
	 * @return	An {@code And} instance containing the left and right subformulas
	 */
	@Override
	public Formula visitAnd(AndContext context) {
		Formula left = (Formula) visit(context.formula(0));
		Formula right = (Formula) visit(context.formula(1));
		return new And(left, right);
	}

	/**
	 * Visits the subformula node of an ExistsAlways node in the parse tree. 
	 * 
	 * @return	An {@code Exists} instance of an {@code Always} instance of the
	 * 			inner subformula
	 */
	@Override
	public Formula visitExistsAlways(ExistsAlwaysContext context) {
		Formula formula = (Formula) visit(context.formula());
		return new ExistsAlways(formula);		
	}

	/**
	 * Visits the left and right subformula nodes of an ExistsUntil node in the parse tree.
	 *  
	 * @return	An {@code Exists} instance of an {@code Until} instance containing the
	 * 			left and right subformulas
	 */
	@Override
	public Formula visitExistsUntil(ExistsUntilContext context) {
		Formula left = (Formula) visit(context.formula(0));
		Formula right = (Formula) visit(context.formula(1));
		return new ExistsUntil(left, right);
	}

	/**
	 * Visits the subformula node of an ExistsNext node in the parse tree. 
	 * 
	 * @return	An {@code Exists} instance of an {@code Next} instance of the
	 * 			inner subformula
	 */
	@Override
	public Formula visitExistsNext(ExistsNextContext context) {
		Formula formula = (Formula) visit(context.formula());
		return new ExistsNext(formula);
	}
}
