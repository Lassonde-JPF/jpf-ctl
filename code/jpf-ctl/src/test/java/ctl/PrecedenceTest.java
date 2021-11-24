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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.RepeatedTest;

/**
 * Tests that the precedence of operators.
 *
 * @author Jinho Hwang
 * @author Minh Cu (Huy Cu)
 * @author Alberto Mastrofrancesco
 * @author Qasim Ahmed
 * @author Parssa Khazra
 * @author Hongru Wang
 */
public class PrecedenceTest extends BaseTest {

    /**
     * Tests that the and operator has a higher precedence than the or operator.
     */
    @RepeatedTest(TIMES)
    public void testAndOr() {
            // generate three random abstract syntax trees
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula third = Formula.random();
            // combine the three
            Formula expected = new Or(first, new And(second, third));
            // create its string representation without parentheses
            String formula = first + " || " + second + " && " + third;
            // obtain the abstract syntax tree
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);

            // combine the three (in another way)
            expected = new Or(new And(first, second), third);
            // create its string representation without parentheses
            formula = first + " && " + second + " || " + third;
            // obtain the abstract syntax tree
            actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the and operator has a higher precedence than the implies operator.
     */
    @RepeatedTest(TIMES)
    public void testAndImplies() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula third = Formula.random();
            Formula expected = new Implies(new And(first, second), third);
            String formula = first + " && " + second + " -> " + third;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);

            expected = new Implies(first, new And(second, third));
            formula = first + " -> " + second + " && " + third;
            actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the and operator has a higher precedence than the iff operator.
     */
    @RepeatedTest(TIMES)
    public void testAndIff() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula third = Formula.random();
            Formula expected = new Iff(new And(first, second), third);
            String formula = first + " && " + second + " <-> " + third;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);

            expected = new Iff(first, new And(second, third));
            formula = first + " <-> " + second + " && " + third;
            actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the not operator has a higher precedence than the and operator.
     */
    @RepeatedTest(TIMES)
    public void testAndNot() {
             Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new And(new Not(first), second);
            String formula = "! " + first + " && " + second;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
     }

    /**
     * Tests that the AG operator has a higher precedence than the and operator.
     */
    @RepeatedTest(TIMES)
    public void testAndForAllAlways() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new And(new ForAllAlways(first), second);
            String formula = "AG " + first + " && " + second;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the AF operator has a higher precedence than the and operator.
     */
    @RepeatedTest(TIMES)
    public void testAndForAllEventually() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new And(new ForAllEventually(first), second);
            String formula = "AF " + first + " && " + second;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the EG operator has a higher precedence than the and operator.
     */
    @RepeatedTest(TIMES)
    public void testAndExistsAlways() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new And(new ExistsAlways(first), second);
            String formula = "EG " + first + " && " + second;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the EF operator has a higher precedence than the and operator.
     */
    @RepeatedTest(TIMES)
    public void testAndExistsEventually() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new And(new ExistsEventually(first), second);
            String formula = "EF " + first + " && " + second;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the AU operator has a higher precedence than the and operator.
     */
    @RepeatedTest(TIMES)
    public void testAndForAllUntil() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula third = Formula.random();
            Formula expected = new And(new ForAllUntil(first, second), third);
            String formula = first + " AU " + second + " && " + third;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);

            expected = new And(first, new ForAllUntil(second, third));
            formula = first + " && " + second + " AU " + third;
            actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the EU operator has a higher precedence than the and operator.
     */
    @RepeatedTest(TIMES)
    public void testAndExistsUntil() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula third = Formula.random();
            Formula expected = new And(new ExistsUntil(first, second), third);
            String formula = first + " EU " + second + " && " + third;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);

            expected = new And(first, new ExistsUntil(second, third));
            formula = first + " && " + second + " EU " + third;
            actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the or operator has a higher precedence than the implies operator.
     */
    @RepeatedTest(TIMES)
    public void testImpliesOr() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula third = Formula.random();
            Formula expected = new Implies(first, new Or(second, third));
            String formula = first + " -> " + second + " || " + third;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);

            expected = new Implies(new Or(first, second), third);
            formula = first + " || " + second + " -> " + third;
            actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the implies operator has a higher precedence than the iff operator.
     */
    @RepeatedTest(TIMES)
    public void testImpliesIff() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula third = Formula.random();
            Formula expected = new Iff(new Implies(first, second), third);
            String formula = first + " -> " + second + " <-> " + third;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);

            expected = new Iff(first, new Implies(second, third));
            formula = first + " <-> " + second + " -> " + third;
            actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the not operator has a higher precedence than the implies operator.
     */
    @RepeatedTest(TIMES)
    public void testImpliesNot() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new Implies(new Not(first), second);
            String formula = "! "+first + " -> " + second;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the AG operator has a higher precedence than the implies operator.
     */
    @RepeatedTest(TIMES)
    public void testImpliesForAllAlways() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new Implies(new ForAllAlways(first), second);
            String formula = "AG "+first + " -> " + second;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the AF operator has a higher precedence than the implies operator.
     */
    @RepeatedTest(TIMES)
    public void testImpliesForAllEventually() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new Implies( new ForAllEventually(first), second);
            String formula = "AF " + first + " -> " + second;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the EG operator has a higher precedence than the implies operator.
     */
    @RepeatedTest(TIMES)
    public void testImpliesExistsAlways() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new Implies(new ExistsAlways(first), second);
            String formula = "EG " + first + " -> " + second;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the EF operator has a higher precedence than the implies operator.
     */
    @RepeatedTest(TIMES)
    public void testImpliesExistsEventually() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new Implies(new ExistsEventually(first), second);
            String formula = "EF " + first + " -> " + second;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the AU operator has a higher precedence than the implies operator.
     */
    @RepeatedTest(TIMES)
    public void testImpliesForAllUntil() {
             Formula first = Formula.random();
            Formula second = Formula.random();
            Formula third = Formula.random();
            Formula expected = new Implies(first, new ForAllUntil(second, third));
            String formula = first + " -> " + second + " AU " + third;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);

            expected = new Implies(new ForAllUntil(first, second), third);
            formula = first + " AU " + second + " -> " + third;
            actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the EU operator has a higher precedence than the implies operator.
     */
    @RepeatedTest(TIMES)
    public void testImpliesExistsUntil() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula third = Formula.random();
            Formula expected = new Implies(first, new ExistsUntil(second, third));
            String formula = first + " -> " + second + " EU " + third;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);

            expected = new Implies(new ExistsUntil(first, second), third);
            formula = first + " EU " + second + " -> " + third;
            actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the or operator has a higher precedence than the iff operator.
     */
    @RepeatedTest(TIMES)
    public void testIffOr() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula third = Formula.random();
            Formula expected = new Iff(first, new Or(second, third));
            String formula = first + " <-> " + second + " || " + third;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);

            expected = new Iff(new Or(first, second), third);
            formula = first + " || " + second + " <-> " + third;
            actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the not operator has a higher precedence than the iff operator.
     */
    @RepeatedTest(TIMES)
    public void testIffNot() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula third = Formula.random();
            Formula expected = new Iff(new Not(first), second);
            String formula = "! " + first + " <-> " + second;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the AG operator has a higher precedence than the iff operator.
     */
    @RepeatedTest(TIMES)
    public void testIffForAllAlways() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula third = Formula.random();
            Formula expected = new Iff(new ForAllAlways(first), second);
            String formula = "AG "+first + " <-> " + second;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the AF operator has a higher precedence than the iff operator.
     */
    @RepeatedTest(TIMES)
    public void testIffForAllEventually() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new Iff(new ForAllEventually(first), second);
            String formula = "AF " + first + " <-> " + second;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the EG operator has a higher precedence than the iff operator.
     */
    @RepeatedTest(TIMES)
    public void testIffExistsAlways() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new Iff(new ExistsAlways(first), second);
            String formula = "EG " + first + " <-> " + second;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the EF operator has a higher precedence than the iff operator.
     */
    @RepeatedTest(TIMES)
    public void testIffExistsEventually() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new Iff(new ExistsEventually(first), second);
            String formula = "EF " + first + " <-> " + second;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the AU operator has a higher precedence than the iff operator.
     */
    @RepeatedTest(TIMES)
    public void testIffForAllUntil() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula third = Formula.random();
            Formula expected = new Iff(first, new ForAllUntil(second, third));
            String formula = first + " <-> " + second + " AU " + third;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);

            expected = new Iff(new ForAllUntil(first, second), third);
            formula = first + " AU " + second + " <-> " + third;
            actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the EU operator has a higher precedence than the iff operator.
     */
    @RepeatedTest(TIMES)
    public void testIffExistsUntil() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula third = Formula.random();
            Formula expected = new Iff(first, new ExistsUntil(second, third));
            String formula = first + " <-> " + second + " EU " + third;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);

            expected = new Iff(new ExistsUntil(first, second), third);
            formula = first + " EU " + second + " <-> " + third;
            actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the not operator has a higher precedence than the or operator.
     */
    @RepeatedTest(TIMES)
    public void testOrNot() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new Or(new Not(first), second);
            String formula = "! " + first  +" || " + second;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the AG operator has a higher precedence than the or operator.
     */
    @RepeatedTest(TIMES)
    public void testOrForAllAlways() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new Or(new ForAllAlways(first), second);
            String formula = "AG " + first + " || " + second;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the AF operator has a higher precedence than the or operator.
     */
    @RepeatedTest(TIMES)
    public void testOrForAllEventually() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new Or(new ForAllEventually(first), second);
            String formula = "AF " + first + " || " + second;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the EG operator has a higher precedence than the or operator.
     */
    @RepeatedTest(TIMES)
    public void testOrExistsAlways() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new Or(new ExistsAlways(first), second);
            String formula = "EG " + first + " || " + second;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the EF operator has a higher precedence than the or operator.
     */
    @RepeatedTest(TIMES)
    public void testOrExistsEventually() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new Or(new ExistsEventually(first), second);
            String formula = "EF " + first + " || " + second;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the AU operator has a higher precedence than the or operator.
     */
    @RepeatedTest(TIMES)
    public void testOrForAllUntil() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula third = Formula.random();
            Formula expected = new Or(new ForAllUntil(first, second), third);
            String formula = first + " AU " + second + " || " + third;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);

            expected = new Or(first, new ForAllUntil(second, third));
            formula = first + " || " + second + " AU " + third;
            actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the EU operator has a higher precedence than the or operator.
     */
    @RepeatedTest(TIMES)
    public void testOrExistsUntil() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula third = Formula.random();
            Formula expected = new Or(new ExistsUntil(first, second), third);
            String formula = first + " EU " + second + " || " + third;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);

            expected = new Or(first, new ExistsUntil(second, third));
            formula = first + " || " + second + " EU " + third;
            actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the AU operator has a higher precedence than the EU operator.
     */
    @RepeatedTest(TIMES)
    public void testForAllUntilExistsUntil() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula third = Formula.random();
            Formula expected = new ExistsUntil(first, new ForAllUntil(second,third));
            String formula = first + " EU " + second + " AU " + third;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);

            expected = new ExistsUntil(new ForAllUntil(first, second), third);
            formula = first + " AU " + second + " EU " + third;
            actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the not operator has a higher precedence than the AU operator.
     */
    @RepeatedTest(TIMES)
    public void testForAllUntilNot() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new ForAllUntil(new Not(first), second);
            String formula = "! " + first + " AU " + second ;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the AG operator has a higher precedence than the AU operator.
     */
    @RepeatedTest(TIMES)
    public void testForAllUntilForAllAlways() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new ForAllUntil(new ForAllAlways(first), second);
            String formula = "AG "+ first + " AU " + second ;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the AF operator has a higher precedence than the AU operator.
     */
    @RepeatedTest(TIMES)
    public void testForAllUntilForAllEventually() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new ForAllUntil(new ForAllEventually(first), second);
            String formula = "AF " + first + " AU " + second ;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the EG operator has a higher precedence than the AU operator.
     */
    @RepeatedTest(TIMES)
    public void testForAllUntilExistsAlways() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new ForAllUntil(new ExistsAlways(first), second);
            String formula = "EG " + first + " AU " + second ;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the EF operator has a higher precedence than the AU operator.
     */
    @RepeatedTest(TIMES)
    public void testForAllUntilExistsEventually() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new ForAllUntil(new ExistsEventually(first), second);
            String formula = "EF " + first + " AU " + second ;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the not operator has a higher precedence than the EU operator.
     */
    @RepeatedTest(TIMES)
    public void testExistsUntilNot() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new ExistsUntil(new Not(first), second);
            String formula = "! " + first + " EU " + second ;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the AG operator has a higher precedence than the EU operator.
     */
    @RepeatedTest(TIMES)
    public void testExistsUntilForAllAlways() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new ExistsUntil(new ForAllAlways(first), second);
            String formula = "AG " + first + " EU " + second ;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the AF operator has a higher precedence than the EU operator.
     */
    @RepeatedTest(TIMES)
    public void testExistsUntilForAllEventually() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new ExistsUntil(new ForAllEventually(first), second);
            String formula = "AF "+ first + " EU " + second ;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the EG operator has a higher precedence than the EU operator.
     */
    @RepeatedTest(TIMES)
    public void testExistsUntilExistsAlways() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new ExistsUntil(new ExistsAlways(first), second);
            String formula = "EG "+ first + " EU " + second ;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }

    /**
     * Tests that the EF operator has a higher precedence than the EU operator.
     */
    @RepeatedTest(TIMES)
    public void testExistsUntilExistsEventually() {
            Formula first = Formula.random();
            Formula second = Formula.random();
            Formula expected = new ExistsUntil(new ExistsEventually(first), second);
            String formula = "EF " + first + " EU " + second ;
            Formula actual = parse(formula);
            assertNotNull(actual);
            assertEquals(expected, actual);
    }
}
