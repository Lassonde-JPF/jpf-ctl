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
//import parser.CTLParser.RootContext;
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
	 * Visits a bracket node in the parse tree.
	 * 
	 * @return The formula node contained within the brackets
	 */
	
	/**
	 * Visits the given Bracket node in the parse tree and returns the abstract syntax
	 * tree corresponding to the subtree of the parse tree rooted at the Bracket node.
	 * 
	 * @param context a node in the syntax tree that corresponds to the Bracket
	 * @return A {@code Formula} instance that represents abstract syntax tree corresponding 
	 * to the formula within the brackets 
	 */
	@Override
	public Formula visitBracket(BracketContext context) {
		return visit(context.formula());
	}

	/**
	 * Visits the given ForAllAlways node in the parse tree and returns the abstract syntax
	 * tree corresponding to the subtree of the parse tree rooted at the ForAllAlways node.
	 * 
	 * @param context a node in the syntax tree that corresponds to the ForAllAlways alternative
	 * @return A {@code ForAllAlways} instance that represents abstract syntax tree corresponding 
	 * to the subtree of the parse tree rooted {@code context}
	 */
	@Override
	public Formula visitForAllAlways(ForAllAlwaysContext context) {
		Formula formula = (Formula) visit(context.formula());
		return new ForAllAlways(formula);
	}

	
	/**
	 * Visits the left and right sub trees of the given Or node in the parse tree and returns
	 *  an Or instance containing the left and right abstract syntax trees 
	 * 
	 * @param context a node in the syntax tree that corresponds to the Or 
	 * @return A {@code Or} instance that represents abstract syntax tree corresponding 
	 * to the left and right subtree of the parse tree rooted {@code context}
	 */	
	@Override
	public Formula visitOr(OrContext context) {
		Formula left = (Formula) visit(context.formula(0));
		Formula right = (Formula) visit(context.formula(1));
		return new Or(left, right);
	}


	/**
	 * Visits the left and right subtrees of the given Iff node in the parse tree 
	 * and returns an Iff object containing the abstract syntax trees corresponding 
	 * to the left and right subtrees of the parse tree rooted at the Iff node.
	 * 
	 * @param context a node in the syntax tree that corresponds to the Iff alternative
	 * @return A {@code Iff} instance that represents the abstract syntax tree corresponding 
	 * to the left and right subtrees of the parse tree rooted {@code context}
	 */
	@Override
	public Formula visitIff(IffContext context) {
		Formula left = (Formula) visit(context.formula(0));
		Formula right = (Formula) visit(context.formula(1));	
		return new Iff(left, right);
	}


	/**
	 * Visits the given True Terminal node in the parse tree 
	 * 
	 * @param context a node in the syntax tree that corresponds to the True alternative
	 * @return A {@code True} instance 
	 */
	@Override
	public Formula visitTrue(TrueContext context) {
		return new True();
	}

	/**
	 * Visits the given False Terminal node in the parse tree 
	 * 
	 * @param context a node in the syntax tree that corresponds to the False alternative
	 * @return A {@code False} instance 
	 */	
	@Override
	public Formula visitFalse(FalseContext context) {
		return new False();
	}


	/**
	 * Visits the given ExistsEventually node in the parse tree and returns the abstract syntax
	 * tree corresponding to the subtree of the parse tree rooted at the ExistsEventually node.
	 * 
	 * @param context a node in the syntax tree that corresponds to the ExistsEventually alternative
	 * @return A {@code ExistsEventually} instance that represents abstract syntax tree corresponding 
	 * to the subtree of the parse tree rooted {@code context}
	 */
	@Override
	public Formula visitExistsEventually(ExistsEventuallyContext context) {
		Formula formula = (Formula) visit(context.formula());
		return new ExistsEventually(formula);
	}


	/**
	 * Visits the given AtomicProposition Terminal node in the parse tree and return the context of atomic proposition 
	 * 
	 * @param context a node in the syntax tree that corresponds to the AtomicProposition alternative
	 * @return A {@code AtomicProposition} instance containing a string representation of an atomic proposition as defined by the grammar
	 */	
	@Override
	public Formula visitAtomicProposition(AtomicPropositionContext context) {
		return new AtomicProposition(context.ATOMIC_PROPOSITION().toString());
	}

	
	/**
	 * Visits the given ForAllEventually node in the parse tree and returns the abstract syntax
	 * tree corresponding to the subtree of the parse tree rooted at the ForAllEventually node.
	 * 
	 * @param context a node in the syntax tree that corresponds to the ForAllEventually alternative
	 * @return A {@code ForAllEventually} instance that represents abstract syntax tree corresponding 
	 * to the subtree of the parse tree rooted {@code context}
	 */
	@Override
	public Formula visitForAllEventually(ForAllEventuallyContext context) {
		Formula formula = (Formula) visit(context.formula());
		return new ForAllEventually(formula);
	}

	/**
	 * Visits the given Not node in the parse tree and returns the abstract syntax tree corresponding 
	 * to the subtree of the parse tree rooted at the Not Node.
	 * 
	 * @param context a node in the syntax tree that corresponds to the Not alternative
	 * @return	A {@code Not} instance that represents the abstract syntax tree corresponding to the subtree
	 * of the parse tree rooted {@code context}
	 */
	@Override
	public Formula visitNot(NotContext context) {
		Formula formula = (Formula) visit(context.formula());
		return new Not(formula);
	}

	//stop here
	/**
	 * Visits the left and right sub trees of the given ForAllUntil node in the parse tree and returns
	 *  an ForAllUntil instance containing the left and right abstract syntax trees 
	 *  
	 * @param context a node in the syntax tree that corresponds to the ForAllUntil formula 
	 * @return A {@code ForAllUntil} instance that represents abstract syntax tree corresponding 
	 * to the left and right subtree of the parse tree rooted {@code context}
	 */
	@Override
	public Formula visitForAllUntil(ForAllUntilContext context) {
	
		Formula right = (Formula) visit(context.formula(1));
		Formula left = (Formula) visit(context.formula(0));
		return new ForAllUntil(left, right);
	}

	/**
	 * Visits the left and right sub trees of the given Implies node in the parse tree and returns
	 *  an Implies instance containing the left and right abstract syntax trees 
	 *  
	 * @param context a node in the syntax tree that corresponds to the Implies formula 
	 * @return A {@code Implies} instance that represents abstract syntax tree corresponding 
	 * to the left and right subtree of the parse tree rooted {@code context}
	 */
	@Override
	public Formula visitImplies(ImpliesContext context) {
		
		Formula right = (Formula) visit(context.formula(1));
		Formula left = (Formula) visit(context.formula(0));
		return new Implies(left, right);
	}

	/**
	 * Visits the given ForAllNext node in the parse tree and returns the abstract syntax
	 * tree corresponding to the subtree of the parse tree rooted at the ForAllNext node.
	 * 
	 * @param context a node in the syntax tree that corresponds to the ForAllNext alternative
	 * @return A {@code ForAllNext} instance that represents abstract syntax tree corresponding 
	 * to the subtree of the parse tree rooted {@code context}
	 */
	@Override
	public Formula visitForAllNext(ForAllNextContext context) {
		Formula formula = (Formula) visit(context.formula());
		return new ForAllNext(formula);
	}

	/**
	 * Visits the left and right sub trees of the given Implies node in the parse tree and returns
	 *  an And instance containing the left and right abstract syntax trees 
	 *  
	 * @param context a node in the syntax tree that corresponds to the And formula 
	 * @return A {@code And} instance that represents abstract syntax tree corresponding 
	 * to the left and right subtree of the parse tree rooted {@code context}
	 */
	@Override
	public Formula visitAnd(AndContext context) {
		Formula left = (Formula) visit(context.formula(0));
		Formula right = (Formula) visit(context.formula(1));
		return new And(left, right);
	}

	/**
	 * Visits the given ExistsAlways node in the parse tree and returns the abstract syntax
	 * tree corresponding to the subtree of the parse tree rooted at the ExistsAlways node.
	 * 
	 * @param context a node in the syntax tree that corresponds to the ExistsAlways alternative
	 * @return A {@code ExistsAlways} instance that represents abstract syntax tree corresponding 
	 * to the subtree of the parse tree rooted {@code context}
	 */
	@Override
	public Formula visitExistsAlways(ExistsAlwaysContext context) {
		Formula formula = (Formula) visit(context.formula());
		return new ExistsAlways(formula);		
	}

	/**
	 * Visits the left and right sub trees of the given ExistsUntil node in the parse tree and returns
	 *  an ExistsUntil instance containing the left and right abstract syntax trees 
	 *  
	 * @param context a node in the syntax tree that corresponds to the ExistUntil formula 
	 * @return A {@code ExistsUntil} instance that represents abstract syntax tree corresponding 
	 * to the left and right subtree of the parse tree rooted {@code context}
	 */
	@Override
	public Formula visitExistsUntil(ExistsUntilContext context) {
		
		Formula right = (Formula) visit(context.formula(1));
		Formula left = (Formula) visit(context.formula(0));
		return new ExistsUntil(left, right);
	}

	/** 
	 * Visits the given ExistsNext node in the parse tree and returns the abstract syntax
	 * tree corresponding to the subtree of the parse tree rooted at the ExistsNext node.
	 * 
	 * @param context a node in the syntax tree that corresponds to the ExistsNext alternative
	 * @return A {@code ExistsNext} instance that represents abstract syntax tree corresponding 
	 * to the subtree of the parse tree rooted {@code context}
	 */
	@Override
	public Formula visitExistsNext(ExistsNextContext context) {
		Formula formula = (Formula) visit(context.formula());
		return new ExistsNext(formula);
	}
}