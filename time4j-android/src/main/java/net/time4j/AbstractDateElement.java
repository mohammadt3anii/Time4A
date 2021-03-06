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

package net.time4j;

import net.time4j.engine.ChronoFunction;
import net.time4j.format.DisplayElement;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;


/**
 * <p>Abstrakte Basisklasse f&uuml;r Datumselemente, die bereits alle
 * Methoden des Interface {@code AdjustableElement} vordefiniert. </p>
 *
 * @param   <V> generic type of element values
 * @author  Meno Hochschild
 */
abstract class AbstractDateElement<V extends Comparable<V>>
    extends DisplayElement<V>
    implements AdjustableElement<V, PlainDate> {

    //~ Instanzvariablen --------------------------------------------------

    private transient final ElementOperator<PlainDate> minimizer;
    private transient final ElementOperator<PlainDate> maximizer;

    //~ Konstruktoren -----------------------------------------------------

    AbstractDateElement(String name) {
        super(name);

        this.minimizer = new DateOperator(this, ElementOperator.OP_MINIMIZE);
        this.maximizer = new DateOperator(this, ElementOperator.OP_MAXIMIZE);

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public ElementOperator<PlainDate> newValue(V value) {

        return new DateOperator(this, ElementOperator.OP_NEW_VALUE, value);

    }

    @Override
    public ElementOperator<PlainDate> minimized() {

        return this.minimizer;

    }

    @Override
    public ElementOperator<PlainDate> maximized() {

        return this.maximizer;

    }

    @Override
    public ElementOperator<PlainDate> decremented() {

        return new DateOperator(this, ElementOperator.OP_DECREMENT);

    }

    @Override
    public ElementOperator<PlainDate> incremented() {

        return new DateOperator(this, ElementOperator.OP_INCREMENT);

    }

    @Override
    public ElementOperator<PlainDate> atFloor() {

        return new DateOperator(this, ElementOperator.OP_FLOOR);

    }

    @Override
    public ElementOperator<PlainDate> atCeiling() {

        return new DateOperator(this, ElementOperator.OP_CEILING);

    }

    public ElementOperator<PlainDate> setLenient(V value) {

        return new DateOperator(this, ElementOperator.OP_LENIENT, value);

    }

    @Override
    public ChronoFunction<Moment, V> inStdTimezone() {

        return this.in(Timezone.ofSystem());

    }

    @Override
    public ChronoFunction<Moment, V> inTimezone(TZID tzid) {

        return this.in(Timezone.of(tzid));

    }

    @Override
    public ChronoFunction<Moment, V> in(Timezone tz) {

        return new ZonalQuery<V>(this, tz);

    }

    @Override
    public ChronoFunction<Moment, V> atUTC() {

        return this.at(ZonalOffset.UTC);

    }

    @Override
    public ChronoFunction<Moment, V> at(ZonalOffset offset) {

        return new ZonalQuery<V>(this, offset);

    }

}
