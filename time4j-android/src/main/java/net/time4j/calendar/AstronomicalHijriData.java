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

import net.time4j.PlainDate;
import net.time4j.base.MathUtils;
import net.time4j.base.ResourceLoader;
import net.time4j.engine.CalendarEra;
import net.time4j.engine.EpochDays;
import net.time4j.format.expert.Iso8601Format;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;


/**
 * <p>Calendar system for astronomical hijri data of any variant. </p>
 *
 * <p>Users can easily take care of deviations (usually +/- 3 days) if they manage copies of the underlying data
 * and make their individual adjustments to lengths of months. The name of the copied change file is the new
 * variant name. Such a file has the extension &quot;.data&quot; and is located in the data-directory relative
 * to the class path. </p>
 *
 * @since   3.5/4.3
 */
final class AstronomicalHijriData
    implements EraYearMonthDaySystem<HijriCalendar> {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final AstronomicalHijriData UMALQURA;

    static {
        try {
            UMALQURA = new AstronomicalHijriData("islamic-umalqura"); // prefetch
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    //~ Instanzvariablen --------------------------------------------------

    private final String variant;
    private final int adjustment;
    private final String version;
    private final int minYear;
    private final int maxYear;
    private final long minUTC;
    private final long maxUTC;
    private final int[] lengthOfMonth;
    private final long[] firstOfMonth;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Creates a new instance for given variant loading its resource data. </p>
     *
     * @param   variant     name of calendar variant
     * @throws  net.time4j.engine.ChronoException if the variant contains an invalid day adjustment
     * @throws  IOException in case of any data inconsistencies
     */
    AstronomicalHijriData(String variant) throws IOException {
        super();

        HijriAdjustment ha = HijriAdjustment.from(variant);
        this.variant = variant;
        String baseVariant = ha.getBaseVariant();
        this.adjustment = ha.getValue();
        String name = "data/" + baseVariant.replace('-', '_') + ".data";
        URI uri = ResourceLoader.getInstance().locate("calendar", AstronomicalHijriData.class, name);
        InputStream is = ResourceLoader.getInstance().load(uri, true);

        if (is == null) {
            is = ResourceLoader.getInstance().load(AstronomicalHijriData.class, name, true);
        }

        try {
            Properties properties = new Properties();
            properties.load(is);
            String calendarType = properties.getProperty("type");
            if (!baseVariant.equals(calendarType)) {
                throw new IOException("Wrong hijri variant: expected=" + baseVariant + ", found=" + calendarType);
            }
            this.version = properties.getProperty("version", "1.0");

            String isoStart = properties.getProperty("iso-start", "");
            PlainDate startDate = Iso8601Format.EXTENDED_CALENDAR_DATE.parse(isoStart);
            this.minUTC = startDate.get(EpochDays.UTC);
            int min = Integer.parseInt(properties.getProperty("min", "1"));
            this.minYear = min;
            int max = Integer.parseInt(properties.getProperty("max", "0"));
            this.maxYear = max;
            int count = (max - min + 1) * 12;

            int[] mlen = new int[count];
            long[] mutc = new long[count];
            int i = 0;
            long v = this.minUTC;

            for (int year = min; year <= max; year++) {
                String row = properties.getProperty(String.valueOf(year));
                if (row == null) {
                    throw new IOException("Wrong file format: " + name + " (missing year=" + year + ")");
                }
                String[] monthLengths = row.split(" ");
                for (int m = 0; m < Math.min(monthLengths.length, 12); m++) {
                    mlen[i] = Integer.parseInt(monthLengths[m]);
                    mutc[i] = v;
                    v += mlen[i];
                    i++;
                }
                if (monthLengths.length < 12) {
                    int[] buf1 = new int[i];
                    long[] buf2 = new long[i];
                    System.arraycopy(mlen, 0, buf1, 0, i);
                    System.arraycopy(mutc, 0, buf2, 0, i);
                    mlen = buf1;
                    mutc = buf2;
                    break;
                }
            }

            this.maxUTC = v - 1;
            this.lengthOfMonth = mlen;
            this.firstOfMonth = mutc;

        } catch (ParseException pe) {
            throw new IOException("Wrong file format: " + name, pe);
        } catch (NumberFormatException nfe) {
            throw new IOException("Wrong file format: " + name, nfe);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                ioe.printStackTrace(System.err);
            }
        }

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public HijriCalendar transform(long utcDays) {

        long realDays = MathUtils.safeAdd(utcDays, this.adjustment);
        int monthStart = search(realDays, this.firstOfMonth);

        if (monthStart >= 0) {
            if (
                (monthStart < this.firstOfMonth.length - 1)
                || (this.firstOfMonth[monthStart] + this.lengthOfMonth[monthStart] > realDays)
            ) {
                int hyear = (monthStart / 12) + this.minYear;
                int hmonth = (monthStart % 12) + 1;
                int hdom = (int) (realDays - this.firstOfMonth[monthStart] + 1);
                return HijriCalendar.of(this.variant, hyear, hmonth, hdom);
            }
        }

        throw new IllegalArgumentException("Out of range: " + utcDays);

    }

    @Override
    public long transform(HijriCalendar date) {

        if (!date.getVariant().equals(this.variant)) {
            throw new IllegalArgumentException(
                "Given date does not belong to this calendar system: "
                + date
                + " (calendar variants are different).");
        }

        int index = (date.getYear() - this.minYear) * 12 + date.getMonth().getValue() - 1;
        return MathUtils.safeSubtract(this.firstOfMonth[index] + date.getDayOfMonth() - 1, this.adjustment);

    }

    @Override
    public long getMinimumSinceUTC() {

        return MathUtils.safeSubtract(this.minUTC, this.adjustment);

    }

    @Override
    public long getMaximumSinceUTC() {

        return MathUtils.safeSubtract(this.maxUTC, this.adjustment);

    }

    @Override
    public List<CalendarEra> getEras() {

        CalendarEra era = HijriEra.ANNO_HEGIRAE;
        return Collections.singletonList(era);

    }

    @Override
    public boolean isValid(
        CalendarEra era,
        int hyear,
        int hmonth,
        int hdom
    ) {

        if (
            (era != HijriEra.ANNO_HEGIRAE)
            || (hyear < this.minYear)
            || (hyear > this.maxYear)
            || (hmonth < 1)
            || (hmonth > 12)
            || (hdom < 1)
        ) {
            return false;
        }

        if ((hyear - this.minYear) * 12 + hmonth - 1 >= this.lengthOfMonth.length) {
            return false;
        }

        return (hdom <= this.getLengthOfMonth(era, hyear, hmonth));

    }

    @Override
    public int getLengthOfMonth(
        CalendarEra era,
        int hyear,
        int hmonth
    ) {

        if (era != HijriEra.ANNO_HEGIRAE) {
            throw new IllegalArgumentException("Wrong era: " + era);
        }

        int index = (hyear - this.minYear) * 12 + hmonth - 1;

        if (index < 0 || index >= this.lengthOfMonth.length) {
            throw new IllegalArgumentException("Out of bounds: year=" + hyear + ", month=" + hmonth);
        }

        return this.lengthOfMonth[index];

    }

    @Override
    public int getLengthOfYear(
        CalendarEra era,
        int hyear
    ) {

        if (era != HijriEra.ANNO_HEGIRAE) {
            throw new IllegalArgumentException("Wrong era: " + era);
        }

        if ((hyear < this.minYear) || (hyear > this.maxYear)) {
            throw new IllegalArgumentException("Out of bounds: yearOfEra=" + hyear);
        }

        int max = 0;

        for (int m = 1; m <= 12; m++) {
            int index = (hyear - this.minYear) * 12 + m - 1;
            if (index >= this.lengthOfMonth.length) {
                throw new IllegalArgumentException("Year range is not fully covered by underlying data: " + hyear);
            }
            max += this.lengthOfMonth[index];
        }

        return max;

    }

    /**
     * <p>Yields the version attribute of the underlying data. </p>
     *
     * @return  String
     */
    String getVersion() {

        return this.version;

    }

    // returns index of month-start associated with utcDays
    private static int search(
        long utcDays,
        long[] firstOfMonth
    ) {

        int low = 0;
        int high = firstOfMonth.length - 1;

        while (low <= high) {
            int middle = (low + high) / 2;

            if (firstOfMonth[middle] <= utcDays) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }

        return low - 1;

    }

}
