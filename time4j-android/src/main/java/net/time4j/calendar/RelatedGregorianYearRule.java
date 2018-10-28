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

import net.time4j.base.MathUtils;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.CalendarVariant;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ElementRule;
import net.time4j.engine.EpochDays;

import java.util.Collections;
import java.util.Map;


/**
 * <p>Defines a rule for the related gregorian year. </p>
 *
 * @author  Meno Hochschild
 * @since   3.20/4.16
 */
final class RelatedGregorianYearRule<T extends ChronoEntity<T>>
    implements ElementRule<T, Integer> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final String KEY_CALENDRICAL = "calendrical";

    //~ Instanzvariablen --------------------------------------------------

    private final Map<String, ? extends CalendarSystem<T>> map;
    private final ChronoElement<Integer> dayOfYear;

    //~ Konstruktoren -----------------------------------------------------

    RelatedGregorianYearRule(
        CalendarSystem<T> calsys,
        ChronoElement<Integer> dayOfYear
    ) {
        super();

        this.map = Collections.singletonMap(KEY_CALENDRICAL, calsys);
        this.dayOfYear = dayOfYear;

    }

    RelatedGregorianYearRule(
        Map<String, ? extends CalendarSystem<T>> map,
        ChronoElement<Integer> dayOfYear
    ) {
        super();

        this.map = map;
        this.dayOfYear = dayOfYear;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Integer getValue(T context) {

        CalendarSystem<T> calsys = this.getCalendarSystem(context);
        T start = context.with(this.dayOfYear, 1);
        return toGregorianYear(calsys.transform(start));

    }

    @Override
    public Integer getMinimum(T context) {

        CalendarSystem<T> calsys = this.getCalendarSystem(context);
        long utc = calsys.getMinimumSinceUTC();
        T start = calsys.transform(utc).with(this.dayOfYear, 1);
        return toGregorianYear(calsys.transform(start));

    }

    @Override
    public Integer getMaximum(T context) {

        CalendarSystem<T> calsys = this.getCalendarSystem(context);
        long utc = calsys.getMaximumSinceUTC();
        T start = calsys.transform(utc).with(this.dayOfYear, 1);
        return toGregorianYear(calsys.transform(start));

    }

    @Override
    public boolean isValid(
        T context,
        Integer value
    ) {

        return this.getValue(context).equals(value);

    }

    @Override
    public T withValue(
        T context,
        Integer value,
        boolean lenient
    ) {

        if (this.isValid(context, value)) {
            return context;
        } else {
            throw new IllegalArgumentException("The related gregorian year is read-only.");
        }

    }

    @Override
    public ChronoElement<?> getChildAtFloor(T context) {

        return null;

    }

    @Override
    public ChronoElement<?> getChildAtCeiling(T context) {

        return null;

    }

    private CalendarSystem<T> getCalendarSystem(T context) {

        if (context instanceof CalendarVariant) {
            return this.map.get(CalendarVariant.class.cast(context).getVariant());
        } else {
            return this.map.get(KEY_CALENDRICAL);
        }

    }

    private static Integer toGregorianYear(long utc) {

        long mjd = EpochDays.MODIFIED_JULIAN_DATE.transform(utc, EpochDays.UTC);
        long days = MathUtils.safeAdd(mjd, 719468 - 40587);

        long q400 = MathUtils.floorDivide(days, 146097);
        int r400 = MathUtils.floorModulo(days, 146097);

        long y;

        if (r400 == 146096) {
            y = (q400 + 1) * 400;
        } else {
            int q100 = (r400 / 36524);
            int r100 = (r400 % 36524);

            int q4 = (r100 / 1461);
            int r4 = (r100 % 1461);

            if (r4 == 1460) {
                y = (q400 * 400 + q100 * 100 + (q4 + 1) * 4);
            } else {
                int q1 = (r4 / 365);
                int r1 = (r4 % 365);

                y = (q400 * 400 + q100 * 100 + q4 * 4 + q1);
                int m = (((r1 + 31) * 5) / 153) + 2;

                if (m > 12) {
                    y++;
                }
            }
        }

        return Integer.valueOf(MathUtils.safeCast(y));

    }

}
