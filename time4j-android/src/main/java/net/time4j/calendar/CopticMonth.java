/*
 * Licensed by the author of Time4J-project.
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership. The copyright owner
 * licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.time4j.calendar;

import net.time4j.engine.ChronoCondition;
import net.time4j.format.CalendarText;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.util.Locale;


/**
 * <p>The Coptic calendar defines 13 Coptic months. </p>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
/*[deutsch]
 * <p>Der koptische Kalender definiert 13 koptische Monate. </p>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
public enum CopticMonth
    implements ChronoCondition<CopticCalendar> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * The 1st month of Coptic calendar with 30 days.
     */
    /*[deutsch]
     * Der erste Monat des koptischen Kalenders mit 30 Tagen.
     */
    TOUT,

    /**
     * The 2nd month of Coptic calendar with 30 days.
     */
    /*[deutsch]
     * Der zweite Monat des koptischen Kalenders mit 30 Tagen.
     */
    BABA,

    /**
     * The 3rd month of Coptic calendar with 30 days.
     */
    /*[deutsch]
     * Der dritte Monat des koptischen Kalenders mit 30 Tagen.
     */
    HATOR,

    /**
     * The 4th month of Coptic calendar with 30 days.
     */
    /*[deutsch]
     * Der vierte Monat des koptischen Kalenders mit 30 Tagen.
     */
    KIAHK,

    /**
     * The 5th month of Coptic calendar with 30 days.
     */
    /*[deutsch]
     * Der f&uuml;nfte Monat des koptischen Kalenders mit 30 Tagen.
     */
    TOBA,

    /**
     * The 6th month of Coptic calendar with 30 days.
     */
    /*[deutsch]
     * Der sechste Monat des koptischen Kalenders mit 30 Tagen.
     */
    AMSHIR,

    /**
     * The 7th month of Coptic calendar with 30 days.
     */
    /*[deutsch]
     * Der siebente Monat des koptischen Kalenders mit 30 Tagen.
     */
    BARAMHAT,

    /**
     * The 8th month of Coptic calendar with 30 days.
     */
    /*[deutsch]
     * Der achte Monat des koptischen Kalenders mit 30 Tagen.
     */
    BARAMOUDA,

    /**
     * The 9th month of Coptic calendar with 30 days.
     */
    /*[deutsch]
     * Der neunte Monat des koptischen Kalenders mit 30 Tagen.
     */
    BASHANS,

    /**
     * The 10th month of Coptic calendar with 30 days.
     */
    /*[deutsch]
     * Der zehnte Monat des koptischen Kalenders mit 30 Tagen.
     */
    PAONA,

    /**
     * The 11th month of Coptic calendar with 30 days.
     */
    /*[deutsch]
     * Der elfte Monat des koptischen Kalenders mit 30 Tagen.
     */
    EPEP,

    /**
     * The 12th month of Coptic calendar with 30 days.
     */
    /*[deutsch]
     * Der zw&ouml;lfte Monat des koptischen Kalenders mit 30 Tagen.
     */
    MESRA,

    /**
     * The 13th month of Coptic calendar with 5 or 6 days (if in leap year).
     */
    /*[deutsch]
     * Der dreizehnte Monat des koptischen Kalenders mit 5 oder 6 Tagen (wenn im Schaltjahr).
     */
    NASIE;

    private static final CopticMonth[] ENUMS = CopticMonth.values(); // Cache

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets the enum-constant which corresponds to the given numerical value. </p>
     *
     * @param   month   coptic month in the range [1-13]
     * @return  coptic month of year as enum
     * @throws  IllegalArgumentException if given argument is out of range
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert die zum kalendarischen Integer-Wert passende Enum-Konstante. </p>
     *
     * @param   month   coptic month in the range [1-13]
     * @return  coptic month of year as enum
     * @throws  IllegalArgumentException if given argument is out of range
     * @since   3.11/4.8
     */
    public static CopticMonth valueOf(int month) {

        if ((month < 1) || (month > 13)) {
            throw new IllegalArgumentException("Out of range: " + month);
        }

        return ENUMS[month - 1];

    }

    /**
     * <p>Gets the corresponding numerical value. </p>
     *
     * @return  number of month in the range [1-13]
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert den korrespondierenden kalendarischen Integer-Wert. </p>
     *
     * @return  number of month in the range [1-13]
     * @since   3.11/4.8
     */
    public int getValue() {

        return (this.ordinal() + 1);

    }

    /**
     * <p>Equivalent to the expression
     * {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT)}. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth, OutputContext)
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Entspricht dem Ausdruck
     * {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT)}. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth, OutputContext)
     * @since   3.11/4.8
     */
    public String getDisplayName(Locale locale) {

        return this.getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT);

    }

    /**
     * <p>Gets the description text dependent on the locale and style
     * parameters. </p>
     *
     * <p>The second argument controls the width of description while the
     * third argument is only relevant for languages which make a difference
     * between stand-alone forms and embedded text forms (does not matter in
     * English). </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @param   context     output context
     * @return  descriptive text for given locale and style (never {@code null})
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert den sprachabh&auml;ngigen Beschreibungstext. </p>
     *
     * <p>&Uuml;ber das zweite Argument kann gesteuert werden, ob eine kurze
     * oder eine lange Form des Beschreibungstexts ausgegeben werden soll. Das
     * ist besonders sinnvoll in Benutzeroberfl&auml;chen, wo zwischen der
     * Beschriftung und der detaillierten Erl&auml;uterung einer graphischen
     * Komponente unterschieden wird. Das dritte Argument ist in Sprachen von
     * Belang, die verschiedene grammatikalische Formen f&uuml;r die Ausgabe
     * als alleinstehend oder eingebettet in formatierten Text kennen. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @param   context     output context
     * @return  descriptive text for given locale and style (never {@code null})
     * @since   3.11/4.8
     */
    public String getDisplayName(
        Locale locale,
        TextWidth width,
        OutputContext context
    ) {

        CalendarText names = CalendarText.getInstance("coptic", locale);
        return names.getStdMonths(width, context).print(this);

    }

    @Override
    public boolean test(CopticCalendar context) {

        return (context.getMonth() == this);

    }

}
