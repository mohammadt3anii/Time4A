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
 * <p>Applies a functional calculation on chronological entities and
 * yields the result as changed copy. </p>
 *
 * <p>Technical note: This interface is only suitable in an
 * Java-pre8-environment (that is Java 6 + 7) if the type T is constrained
 * to a final type. Otherwise the compiler will fail when compiling
 * expressions like {@code entity.with(operator)} due to limited support
 * of type inference. This constraint does not exist with Java 8 and later. </p>
 *
 * @param   <T> generic type of entities this operator can be applied to
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Wendet auf chronologische Objekte eine funktionale Berechnung an und
 * liefert das ge&auml;nderte Objekt zur&uuml;ck. </p>
 *
 * <p>Technische Notiz: Dieses Interface ist in einer Java-pre-8-Umgebung
 * (also Java 6 + 7) nur dann geeignet, wenn der Typ T auf einen finalen Typ
 * eingeschr&auml;nkt wird. Sonst gibt es wegen der mangelnden Typinferenz bei
 * Ausdr&uuml;cken wie {@code entity.with(operator)} einen Compiler-Fehler. Ab
 * Java 8 besteht diese Einschr&auml;nkung nicht mehr. </p>
 *
 * @param   <T> generic type of entities this operator can be applied to
 * @author  Meno Hochschild
 */
public interface ChronoOperator<T> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Adjusts given entity and yields a changed copy of argument. </p>
     *
     * <p>Will be called by {@link ChronoEntity#with(ChronoOperator)}. </p>
     *
     * @param   entity      chronological entity to be adjusted
     * @return  adjusted copy of argument which itself remains unaffected
     * @throws  ChronoException if there is no element rule for adjusting
     * @throws  IllegalArgumentException if any invalid value is tried
     * @throws  ArithmeticException in case of numerical overflow
     */
    /*[deutsch]
     * <p>Passt die angegebene Entit&auml;t an und liefert eine ge&auml;nderte
     * Kopie des Arguments. </p>
     *
     * <p>Wird von {@link ChronoEntity#with(ChronoOperator)} aufgerufen. </p>
     *
     * @param   entity      chronological entity to be adjusted
     * @return  adjusted copy of argument which itself remains unaffected
     * @throws  ChronoException if there is no element rule for adjusting
     * @throws  IllegalArgumentException if any invalid value is tried
     * @throws  ArithmeticException in case of numerical overflow
     */
    T apply(T entity);

}
