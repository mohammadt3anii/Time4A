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

package net.time4j.calendar.astro;


/**
 * Helper class for various astronomical calculations.
 *
 * @author  Meno Hochschild
 * @since   3.38/4.33
 */
class AstroUtils {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Obtains the mean sidereal time of Greenwich in radians. </p>
     *
     * @param   mjd     modified julian date on the time scale of UT
     * @return  mean sidereal time of Greenwich in radians
     */
    static double gmst(double mjd) {

        double mjd0 = Math.floor(mjd);
        double ut = 86400 * (mjd - mjd0);
        double jct0 = (mjd0 - 51544.5) / 36525;
        double jct = (mjd - 51544.5) / 36525;
        double gmstInSecs =
            24110.54841 + 8640184.812866 * jct0 + 1.0027379093 * ut + (0.093104 - 0.0000062 * jct) * jct * jct;
        double gmstInDays = gmstInSecs / 86400;
        return (gmstInDays - Math.floor(gmstInDays)) * 2 * Math.PI;

    }

    /**
     * <p>Obtains the refraction on given calculated elevation. </p>
     *
     * @param   elevation   true elevation in degrees
     * @return  mean refraction in arc minutes
     */
    static double getRefraction(double elevation) {

        // formula by Saemundsson (Meeus, 16.4)
        return (1.02 / Math.tan(Math.toRadians((10.3 / (elevation + 5.11)) + elevation))) + 0.0019279;

    }

    /**
     * <p>Approximation assuming standard atmosphere below tropopause. </p>
     *
     * @param   altitude    altitude of observer in meters
     * @return  multiplication factor for correcting refraction depending on mean pressure and temperature
     */
    static double refractionFactorOfStdAtmosphere(int altitude) {

        // https://de.wikipedia.org/wiki/Normatmosph%C3%A4re
        // https://de.wikipedia.org/wiki/Barometrische_H%C3%B6henformel#Atmosph.C3.A4re_mit_linearem_Temperaturverlauf
        double temperature = 1 - ((0.0065 * altitude) / 288.15);

        // we neglect that the underlying bennett term is rather valid for T=10K and p = 1010hPa - Meeus p.107
        return Math.pow(temperature, 4.255);

    }

    /**
     * <p>Adjusts given angle to circular range {@code [0.0, 360.0)}. </p>
     *
     * @param   value   the angle to be adjusted
     * @return  the adjusted angle
     * @since   4.37
     */
    static double toRange_0_360(double value) {

        while (Double.compare(0.0, value) > 0) {
            value += 360;
        }
        while (Double.compare(value, 360.0) >= 0) {
            value -= 360;
        }
        return value;

    }

    /**
     * <p>Corresponds to Java-8-implementation. </p>
     *
     * @param   value   double-value
     * @return  hash value
     */
    static int hashCode(double value) {

        long bits = Double.doubleToLongBits(value);
        return (int)(bits ^ (bits >>> 32));

    }

}
