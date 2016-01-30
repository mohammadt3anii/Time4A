/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (NewYearStrategy.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j.history;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * <p>Determines the begin of a given historic year. </p>
 *
 * @author  Meno Hochschild
 * @since   3.14/4.11
 */
/*[deutsch]
 * <p>Bestimmt den Beginn eines historischen Jahres. </p>
 *
 * @author  Meno Hochschild
 * @since   3.14/4.11
 */
public final class NewYearStrategy {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final NewYearStrategy DEFAULT = new NewYearStrategy(NewYearRule.BEGIN_OF_JANUARY, Integer.MAX_VALUE);

    private static final Comparator<NewYearStrategy> STD_ORDER = new NYSComparator();

    //~ Instanzvariablen --------------------------------------------------

    private final List<NewYearStrategy> strategies;
    private final NewYearRule lastRule;
    private final int lastAnnoDomini;

    //~ Konstruktoren -----------------------------------------------------

    NewYearStrategy(
        NewYearRule lastRule,
        int lastAnnoDomini
    ) {
        super();

        this.strategies = Collections.emptyList();
        this.lastRule = lastRule;
        this.lastAnnoDomini = lastAnnoDomini;

    }

    NewYearStrategy(List<NewYearStrategy> strategies) {
        super();

        Collections.sort(strategies, STD_ORDER);
        NewYearStrategy prev = null;

        for (NewYearStrategy nys : strategies) {
            if ((prev != null) && (nys.lastAnnoDomini == prev.lastAnnoDomini)) {
                throw new IllegalArgumentException(
                    "Multiple strategies with overlapping validity range: " + strategies);
            } else {
                prev = nys;
            }
        }

        assert (strategies.size() >= 2);

        this.strategies = Collections.unmodifiableList(strategies);
        this.lastRule = NewYearRule.BEGIN_OF_JANUARY;
        this.lastAnnoDomini = Integer.MAX_VALUE;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Combines this and given strategy to a new strategy. </p>
     *
     * @param   next    strategy which chronologically follows after this strategy
     * @return  combined stragegy
     * @throws  IllegalArgumentException in case of multiple overlapping rules
     * @since   3.14/4.11
     */
    /*[deutsch]
     * <p>Kombiniert diese und die angegebene Strategie zu einer neuen Strategie. </p>
     *
     * @param   next    strategy which chronologically follows after this strategy
     * @return  combined stragegy
     * @throws  IllegalArgumentException in case of multiple overlapping rules
     * @since   3.14/4.11
     */
    public NewYearStrategy and(NewYearStrategy next) {

        List<NewYearStrategy> list = new ArrayList<NewYearStrategy>();
        list.addAll(this.strategies);

        if (list.isEmpty()) {
            list.add(this);
        }

        if (next.strategies.isEmpty()) {
            list.add(next);
        } else {
            list.addAll(next.strategies);
        }

        return new NewYearStrategy(list);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof NewYearStrategy) {
            NewYearStrategy that = (NewYearStrategy) obj;
            return (
                this.strategies.equals(that.strategies)
                && (this.lastRule == that.lastRule)
                && (this.lastAnnoDomini == that.lastAnnoDomini));
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return 17 * this.strategies.hashCode() + 37 * this.lastRule.hashCode() + this.lastAnnoDomini;

    }

    /**
     * <p>For debugging purposes. </p>
     *
     * @return  description of content
     */
    /*[deutsch]
     * <p>F&uuml;r Debugging-Zwecke. </p>
     *
     * @return  description of content
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        if (this.strategies.isEmpty()) {
            sb.append("[new-year-rule=");
            sb.append(this.lastRule);
        } else {
            boolean first = true;
            for (NewYearStrategy nys : this.strategies) {
                if (first) {
                    sb.append('[');
                    first = false;
                } else {
                    sb.append(',');
                }
                sb.append(nys.lastRule);
                sb.append("->");
                sb.append(nys.lastAnnoDomini);
            }
        }
        sb.append(']');
        return sb.toString();

    }

    /**
     * <p>Determines the date of New Year. </p>
     *
     * @param   era         historic era
     * @param   yearOfEra   historic year of era
     * @return  historic date of New Year
     * @since   3.14/4.11
     */
    /*[deutsch]
     * <p>Determines the date of New Year. </p>
     *
     * @param   era         historic era
     * @param   yearOfEra   historic year of era
     * @return  historic date of New Year
     * @since   3.14/4.11
     */
    HistoricDate newYear(
        HistoricEra era,
        int yearOfEra
    ) {

        int annoDomini = era.annoDomini(yearOfEra);
        int previous = Integer.MIN_VALUE;

        for (int i = 0, n = this.strategies.size(); i < n; i++) {
            NewYearStrategy strategy = this.strategies.get(i);
            if ((annoDomini >= previous) && (annoDomini < strategy.lastAnnoDomini)) {
                return strategy.lastRule.newYear(era, yearOfEra);
            }
            previous = strategy.lastAnnoDomini;
        }

        return this.lastRule.newYear(era, yearOfEra);

    }

    /**
     * <p>Determines the displayed year for given historic date. </p>
     *
     * <p>The displayed year can deviate from year of era depending on the concrete new year strategy. </p>
     *
     * @param   date    historic date as reference for the calculation of the displayed year
     * @return  displayed historic year
     * @throws  IllegalArgumentException if given date is earlier than AD 8 and defined either in era BC or AD
     * @since   3.14/4.11
     */
    /*[deutsch]
     * <p>Determines the displayed year for given historic date. </p>
     *
     * <p>Das Anzeigejahr kann in Abh&auml;ngigkeit vom Beginn des Jahres vom Jahr der &Auml;ra abweichen. </p>
     *
     * @param   date    historic date as reference for the calculation of the displayed year
     * @return  displayed historic year
     * @throws  IllegalArgumentException if given date is earlier than AD 8 and defined either in era BC or AD
     * @since   3.14/4.11
     */
    int displayedYear(HistoricDate date) {

        HistoricEra era = date.getEra();
        int yearOfEra = date.getYearOfEra();
        int annoDomini = era.annoDomini(yearOfEra);

        if ((annoDomini < 8) && (era.compareTo(HistoricEra.AD) <= 0)) {
            throw new IllegalArgumentException("Cannot determine displayed year in non-proleptic era: " + date);
        }

        int previous = Integer.MIN_VALUE;

        for (int i = 0, n = this.strategies.size(); i < n; i++) {
            NewYearStrategy strategy = this.strategies.get(i);
            if ((annoDomini >= previous) && (annoDomini < strategy.lastAnnoDomini)) {
                return strategy.lastRule.displayedYear(this, date);
            }
            previous = strategy.lastAnnoDomini;
        }

        return this.lastRule.displayedYear(this, date);

    }

    // used in serialization
    void writeToStream(DataOutput out) throws IOException {

        int n = this.strategies.size();
        out.writeInt(n);

        if (n == 0) {
            out.writeUTF(this.lastRule.name());
            out.writeInt(this.lastAnnoDomini);
            return;
        }

        for (int i = 0; i < n; i++) {
            NewYearStrategy strategy = this.strategies.get(i);
            out.writeUTF(strategy.lastRule.name());
            out.writeInt(strategy.lastAnnoDomini);
        }

    }

    // used in deserialization
    static NewYearStrategy readFromStream(DataInput in) throws IOException {

        int n = in.readInt();

        if (n == 0) {
            NewYearRule rule = NewYearRule.valueOf(in.readUTF());
            int annoDomini = in.readInt();

            if ((annoDomini == Integer.MAX_VALUE) && (rule == NewYearRule.BEGIN_OF_JANUARY)) {
                return NewYearStrategy.DEFAULT;
            } else {
                return new NewYearStrategy(rule, annoDomini);
            }
        }

        List<NewYearStrategy> strategies = new ArrayList<NewYearStrategy>(n);

        for (int i = 0; i < n; i++) {
            NewYearRule rule = NewYearRule.valueOf(in.readUTF());
            int annoDomini = in.readInt();
            strategies.add(new NewYearStrategy(rule, annoDomini));
        }

        return new NewYearStrategy(strategies);

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class NYSComparator
        implements Comparator<NewYearStrategy> {

        //~ Methoden ------------------------------------------------------

        @Override
        public int compare(
            NewYearStrategy o1,
            NewYearStrategy o2
        ) {
            return (
                (o1.lastAnnoDomini < o2.lastAnnoDomini)
                ? -1
                : (o1.lastAnnoDomini > o2.lastAnnoDomini ? 1 : 0)
            );
        }

    }

}