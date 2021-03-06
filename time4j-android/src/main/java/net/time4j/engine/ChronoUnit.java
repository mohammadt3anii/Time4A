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

package net.time4j.engine;


/**
 * <p>External time units which are not registered on any chronology (time axis)
 * can implement this interface in order to support standard calculations in
 * time spans and symbol formatting. </p>
 *
 * <p><strong>Naming convention:</strong> Time units have Java-names in
 * plural form. </p>
 *
 * <p><strong>Specification:</strong>
 * All implementations must be immutable. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Externe Zeiteinheiten, die nicht in einer Chronologie (Zeitachse)
 * registriert sind, k&ouml;nnen dieses Interface implementieren, um
 * Standardberechnungen in Zeitspannen und Symbolformatierungen zu
 * unterst&uuml;tzen. </p>
 *
 * <p><strong>Namenskonvention:</strong> Zeiteinheiten haben Java-Namen in
 * Pluralform. </p>
 *
 * <p><strong>Specification:</strong>
 * All implementations must be immutable. </p>
 *
 * @author  Meno Hochschild
 */
public interface ChronoUnit {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Defines the typical length of this time unit in seconds without
     * taking into account anomalies like timezone effects or leap seconds. </p>
     *
     * <p><strong>Important note:</strong> This method can only yield an
     * estimated value and is not intended to assist in calculations of
     * durations, but only in sorting of units. </p>
     *
     * @return  estimated decimal value in seconds
     */
    /*[deutsch]
     * <p>Definiert die typische L&auml;nge dieser Zeiteinheit in Sekunden
     * ohne Ber&uuml;cksichtigung von Anomalien wie Zeitzoneneffekten oder
     * Schaltsekunden. </p>
     *
     * <p><strong>Wichtiger Hinweis:</strong> Diese Methode kann nur einen
     * Sch&auml;tzwert liefern und ist f&uuml;r die Berechnung einer jedweden
     * Dauer ungeeignet. Stattdessen darf und soll die Methode zum Sortieren
     * von Zeiteinheiten verwendet werden. </p>
     *
     * @return  estimated decimal value in seconds
     */
    double getLength();

    /**
     * <p>Queries if this time unit is calendrical respective is at least
     * as long as a calendar day. </p>
     *
     * <p>Implementation note: The method must be consistent with the typical
     * length of the unit. The expression
     * {@code Double.compare(unit.getLength(), 86400.0) >= 0} is
     * equivalent to {@code unit.isCalendrical()}. </p>
     *
     * @return  {@code true} if at least as long as a day else {@code false}
     */
    /*[deutsch]
    /**
     * <p>Ist diese Zeiteinheit kalendarisch beziehungsweise mindestens
     * so lange wie ein Kalendertag? </p>
     *
     * <p>Implementierungshinweis: Die Methode mu&szlig; konsistent
     * mit der typischen Standardl&auml;nge sein. Der Ausdruck
     * {@code Double.compare(unit.getLength(), 86400.0) >= 0} ist
     * &auml;quivalent zu {@code unit.isCalendrical()}. </p>
     *
     * @return  {@code true} if at least as long as a day else {@code false}
     */
    boolean isCalendrical(); // TODO: Ab Java 8 (Time4J-2.0) default-Methode

}
