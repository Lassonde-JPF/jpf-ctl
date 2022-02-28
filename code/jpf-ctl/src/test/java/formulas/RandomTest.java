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

package formulas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.RepeatedTest;

import formulas.ctl.CTLFormula;

/**
 * Generates a random abstract syntax tree, parses its textual representation,
 * and translates the parse tree into a abstract syntax tree. The resulting
 * abstract syntax tree should be the same as the original abstract syntax tree.
 * 
 * @author Franck van Breugel
 */
public class RandomTest extends BaseTest {

	/**
	 * Number of times each test is run.
	 */
	private static final int CASES = 1000;

	/**
	 * Tests a random formula can be parsed correctly
	 */
	@RepeatedTest(CASES)
	void test() {
		// generate a random abstract syntax tree
		CTLFormula randomFormula = CTLFormula.random();
		// obtain the parse tree of the textual representation of the abstract syntax
		// tree
		ParseTree tree = parse(randomFormula.toString());
		// generate an abstract syntax tree from the parse tree
		Formula formula = generator.visit(tree);
		assertNotNull(formula);
		assertEquals(randomFormula, formula);

	}
}
