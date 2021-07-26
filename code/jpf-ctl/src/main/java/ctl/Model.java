package ctl;
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

import java.util.Set;

import ctl.False;
import ctl.Formula;
import ctl.True;

/**
 * 
 * 
 * @author Franck van Breugel
 */
public class Model {

    /**
     * This class represents a pair of sets of states: 
     */
    public static class StateSets {
        private Set<Integer> sat;
        private Set<Integer> unsat;

        /**
         * 
         * 
         * @param sat
         * @param unsat
         */
        public StateSets(Set<Integer> sat, Set<Integer> unsat) {
            this.sat = sat;
            this.unsat = unsat;
        }

        @Override
        public String toString() {
            return "sat = " + sat + "\nunsat = " + unsat;
        }
    }

    /**
     * Returns the 
     * 
     * @param system
     * @param formula
     * @return
     */
    public StateSets check(LabelledPartialTransitionSystem system, Formula formula) {
        if (formula instanceof True) {
            return null;
        } else if (formula instanceof False) {
            return null;
        } else if (formula instanceof And) {
        	And a = (And)formula;
        	
        			
        			
      
            return null;
        } else {
            return null;
        }
    }
}