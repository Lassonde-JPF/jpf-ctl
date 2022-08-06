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

package jpf.logic.ctl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Tests a range of aliases.
 * 
 * @author Jinho Hwang
 * @author Minh Cu (Huy Cu)
 * @author Alberto Mastrofrancesco
 * @author Qasim Ahmed
 */
public class AliasTest {
	
	/**
	 * Tests an alias that starts with a number.
	 */
	@Test
	public void testNumberFirst() {
		String alias = "1java.awt";
		CTLFormula expected = new Alias(alias);
		CTLFormulaParser parser = new CTLFormulaParser();
		CTLFormula actual = parser.parse(alias);
		assertNotNull(actual);
		assertNotEquals(expected, actual);
	}
	
	/**
	 * Tests an alias that starts with a special character.
	 */
	@Test
	public void testSpecialCharacterFirst() {
		String alias = "#java.awt";
		CTLFormula expected = new Alias(alias);
		CTLFormulaParser parser = new CTLFormulaParser();
		CTLFormula actual = parser.parse(alias);
		assertNotNull(actual);
		assertNotEquals(expected, actual);
	}
	
	/**
	 * Tests a very long alias.
	 */
	@Test
	public void testVeryLongPackageNames() {
		String alias = "java.a.b.c.d.e.f.g.h.i.j.k.l.m.n.o.p.q.r.s.t.u.v.w.x.y.z";
		CTLFormula expected = new Alias(alias);
		CTLFormulaParser parser = new CTLFormulaParser();
		CTLFormula actual = parser.parse(alias);
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	/**
	 * Tests an alias that is the name of a class.
	 */
	@Test
	public void testJDKName() {
		String alias = "java.lang.Object";
		CTLFormula expected = new Alias(alias);
		CTLFormulaParser parser = new CTLFormulaParser();
		CTLFormula actual = parser.parse(alias);
		assertNotNull(actual);
		assertEquals(expected, actual);

		alias = "java.util.AbstractCollection";
		expected = new Alias(alias);
		parser = new CTLFormulaParser();
		actual = parser.parse(alias);
		assertNotNull(actual);
		assertEquals(expected, actual);

		alias = "java.util.AbstractList";
		expected = new Alias(alias);
		parser = new CTLFormulaParser();
		actual = parser.parse(alias);
		assertNotNull(actual);
		assertEquals(expected, actual);

		alias = "java.util.ArrayList";
		expected = new Alias(alias);
		parser = new CTLFormulaParser();
		actual = parser.parse(alias);
		assertNotNull(actual);
		assertEquals(expected, actual);

		alias = "java.util.AbstractQueue";
		expected = new Alias(alias);
		parser = new CTLFormulaParser();
		actual = parser.parse(alias);
		assertNotNull(actual);
		assertEquals(expected, actual);

		alias = "java.util.concurrent.PriorityBlockingQueue";
		expected = new Alias(alias);
		parser = new CTLFormulaParser();
		actual = parser.parse(alias);
		assertNotNull(actual);
		assertEquals(expected, actual);

		alias = "java.util.concurrent.LinkedBlockingDeque";
		expected = new Alias(alias);
		parser = new CTLFormulaParser();
		actual = parser.parse(alias);
		assertNotNull(actual);
		assertEquals(expected, actual);

		alias = "java.security.spec.ECPoint";
		expected = new Alias(alias);
		parser = new CTLFormulaParser();
		actual = parser.parse(alias);
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
}
