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

package net.time4j.format;

import net.time4j.engine.BasicElement;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * <p>Standard element which offers localized names for display purposes (in most cases). </p>
 *
 * <p>The implementation looks up the CLDR format symbol of the element and then associate it
 * with the suitable i18n-resources. Supported symbols are: G (era), u/y/Y (year), Q (quarter),
 * M (month), d (day-of-month), E (weekday), h/H/k/K (hour), m (minute), s (second). </p>
 *
 * @author  Meno Hochschild
 * @since   3.22/4.18
 */
/*[deutsch]
 * <p>Standardelement, das lokalisierte Anzeigenamen bietet (in den meisten F&auml;llen). </p>
 *
 * <p>Die Implementierung greift auf das CLDR-Formatsymbol des Elements zu und assoziiert es
 * mit den geeigneten i18n-Ressourcen. Unterst&uuml;tzte Symbole sind: G (&Auml;ra), u/y/Y (Jahr),
 * Q (Quartal), M (Monat), d (Monatstag), E (Wochentag), h/H/k/K (Stunde), m (Minute), s (Sekunde). </p>
 *
 * @author  Meno Hochschild
 * @since   3.22/4.18
 */
public abstract class DisplayElement<V extends Comparable<V>>
    extends BasicElement<V> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final Map<String, String> OTHER_DISPLAY_KEYS;

    static {
        Map<String, String> map = new HashMap<String, String>();
        map.put("YEAR_OF_DISPLAY", "L_year");
        map.put("MONTH_AS_NUMBER", "L_month");
        map.put("HOUR_FROM_0_TO_24", "L_hour");
        map.put("DAY_OF_MONTH", "L_day");
        OTHER_DISPLAY_KEYS = Collections.unmodifiableMap(map);
    }

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Called by subclasses which will usually assign an instance to
     * a static constant (creating a singleton). </p>
     *
     * @param   name            name of element
     * @throws  IllegalArgumentException if the name is empty or only
     *          contains <i>white space</i> (spaces, tabs etc.)
     */
    /*[deutsch]
     * <p>Konstruktor f&uuml;r Subklassen, die eine so erzeugte Instanz
     * in der Regel statischen Konstanten zuweisen und damit Singletons
     * erzeugen k&ouml;nnen. </p>
     *
     * @param   name            name of element
     * @throws  IllegalArgumentException if the name is empty or only
     *          contains <i>white space</i> (spaces, tabs etc.)
     */
    protected DisplayElement(String name) {
        super(name);

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public String getDisplayName(Locale language) {

        String key;

        switch (this.getSymbol()) {
            case 'G':
                key = "L_era";
                break;
            case 'u':
            case 'y':
            case 'Y':
                key = "L_year";
                break;
            case 'Q':
                key = "L_quarter";
                break;
            case 'M':
                key = "L_month";
                break;
            case 'w':
            case 'W':
                key = "L_week";
                break;
            case 'd':
                key = "L_day";
                break;
            case 'E':
            case 'e':
                key = "L_weekday";
                break;
            case 'H':
            case 'h':
            case 'K':
            case 'k':
                key = "L_hour";
                break;
            case 'm':
                key = "L_minute";
                break;
            case 's':
                key = "L_second";
                break;
            default:
                String n = this.name();
                key = OTHER_DISPLAY_KEYS.get(n);
                if (key == null) {
                    return n;
                }
        }

        String lname = CalendarText.getIsoInstance(language).getTextForms().get(key);
        return ((lname == null) ? this.name() : lname);

    }

}
