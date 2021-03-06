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

import net.time4j.Moment;
import net.time4j.PlainTimestamp;
import net.time4j.base.MathUtils;

import java.util.concurrent.TimeUnit;


/**
 * <p>Enumeration of the four most important moon phases. </p>
 *
 * @author 	Meno Hochschild
 * @since 	3.36/4.31
 */
/*[deutsch]
 * <p>Aufz&auml;hlung der vier wichtigsten Mondphasen. </p>
 *
 * @author 	Meno Hochschild
 * @since 	3.36/4.31
 */
public enum MoonPhase {

	//~ Statische Felder/Initialisierungen --------------------------------

	/**
	 * <p>Phase of new moon. </p>
	 */
	/*[deutsch]
	 * <p>Markiert den Neumond. </p>
	 */
	NEW_MOON(0),

	/**
	 * <p>Marks the first quarter moon. </p>
	 */
	/*[deutsch]
	 * <p>Markiert das erste Viertel. </p>
	 */
	FIRST_QUARTER(90),

	/**
	 * <p>Marks the full moon. </p>
	 */
	/*[deutsch]
	 * <p>Markiert den Vollmond. </p>
	 */
	FULL_MOON(180),

	/**
	 * <p>Marks the last quarter moon. </p>
	 */
	/*[deutsch]
	 * <p>Markiert das letzte Viertel. </p>
	 */
	LAST_QUARTER(270);

	private static final int[] FACTORS = {100, 1000, 10000, 100000};
	private static final double MEAN_SYNODIC_MONTH = 29.530588861;
	private static final Moment ZERO_REF = PlainTimestamp.of(2000, 1, 6, 18, 13, 42).atUTC(); // NEW_MOON.atLunation(0)

	private static final int[] W_NEW_FULL = {
		0, 1, 0, 0, 1, 1, 2, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	};
	private static final int[] W_QUARTER = {
		0, 1, 1, 0, 0, 1, 2, 0, 0, 0, 1, 1, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0
	};

	private static final int[] X_NEW_FULL = {
		0, 1, 0, 0, -1, 1, 2, 0, 0, 1, 0, 1, 1, -1, 2, 0, 3, 1, 0, 1, -1, -1, 1, 0
	};
	private static final int[] X_QUARTER = {
		0, 1, 1, 0, 0, -1, 2, 0, 0, 0, -1, 1, 1, 2, 1, -1, 0, 1, -2, 1, 3, 0, -1, 1
	};

	private static final int[] Y_NEW_FULL = {
		1, 0, 2, 0, 1, 1, 0, 1, 1, 2, 3, 0, 0, 2, 1, 2, 0, 1, 2, 1, 1, 1, 3, 4
	};
	private static final int[] Y_QUARTER = {
		1, 0, 1, 2, 0, 1, 0, 1, 1, 3, 2, 0, 0, 1, 2, 1, 2, 1, 1, 1, 0, 2, 1, 3
	};

	private static final int[] Z_NEW_FULL = {
		0, 0, 0, 2, 0, 0, 0, -2, 2, 0, 0, 2, -2, 0, 0, -2, 0, -2, 2, 2, 2, -2, 0, 0
	};
	private static final int[] Z_QUARTER = {
		0, 0, 0, 0, 2, 0, 0, -2, 2, 0, 0, 2, -2, 0, 0, -2, 2, 2, 0, -2, 0, -2, 2, 0
	};

	private static final double[] V_NEW = {
		-0.40720, 0.17241, 0.01608, 0.01039, 0.00739, -0.00514, 0.00208, -0.00111, -0.00057, 0.00056, -0.00042, 0.00042,
		0.00038, -0.00024, -0.00007, 0.00004, 0.00004, 0.00003, 0.00003, -0.00003, 0.00003, -0.00002, -0.00002, 0.00002
	};
	private static final double[] V_FULL = {
		-0.40614, 0.17302, 0.01614, 0.01043, 0.00734, -0.00515, 0.00209, -0.00111, -0.00057, 0.00056, -0.00042, 0.00042,
		0.00038, -0.00024, -0.00007, 0.00004, 0.00004, 0.00003, 0.00003, -0.00003, 0.00003, -0.00002, -0.00002, 0.00002
	};
	private static final double[] V_QUARTER = {
		-0.62801, 0.17172, -0.01183, 0.00862, 0.00804, 0.00454, 0.00204, -0.0018, -0.0007, -0.0004, -0.00034, 0.00032,
		0.00032, -0.00028, 0.00027, -0.00005, 0.00004, -0.00004, 0.00004, 0.00003, 0.00003, 0.00002, 0.00002, -0.00002
	};

	//~ Instanzvariablen --------------------------------------------------

	private transient final int phase;

	//~ Konstruktoren -----------------------------------------------------

	private MoonPhase(int phase) {
		this.phase = phase;
	}

	//~ Methoden ----------------------------------------------------------

	/**
	 * <p>Obtains the first moon phase which is after given moment. </p>
	 *
	 * @param 	moment	the moment to be compared with
	 * @return	first time of this phase after given moment
	 * @throws  IllegalArgumentException if the associated year is not in the range {@code -2000 <= year <= 3000}
	 */
	/*[deutsch]
	 * <p>Liefert die erste Mondphase, die nach dem angegebenen Zeitpunkt liegt. </p>
	 *
	 * @param 	moment	the moment to be compared with
	 * @return	first time of this phase after given moment
	 * @throws  IllegalArgumentException if the associated year is not in the range {@code -2000 <= year <= 3000}
	 */
	public Moment after(Moment moment) {

		int estimation = this.getEstimatedLunations(moment);
		Moment m = this.atLunation(estimation);
		int n = estimation;

		while (!m.isAfter(moment)) {
			n++;
			m = this.atLunation(n);
		}

		if (n <= estimation) {
			while (true) {
				n--;
				Moment test = this.atLunation(n);
				if (test.isAfter(moment)) {
					m = test;
				} else {
					break;
				}
			}
		}

		return m;

	}

	/**
	 * <p>Obtains the first moon phase which is at or after given moment. </p>
	 *
	 * @param 	moment	the moment to be compared with
	 * @return	first time of this phase at or after given moment
	 * @throws  IllegalArgumentException if the associated year is not in the range {@code -2000 <= year <= 3000}
	 * @since 	3.39/4.34
	 */
	/*[deutsch]
	 * <p>Liefert die erste Mondphase, die gleich oder nach dem angegebenen Zeitpunkt liegt. </p>
	 *
	 * @param 	moment	the moment to be compared with
	 * @return	first time of this phase at or after given moment
	 * @throws  IllegalArgumentException if the associated year is not in the range {@code -2000 <= year <= 3000}
	 * @since 	3.39/4.34
	 */
	public Moment atOrAfter(Moment moment) {

		int estimation = this.getEstimatedLunations(moment);
		Moment m = this.atLunation(estimation);
		int n = estimation;

		while (m.isBefore(moment)) {
			n++;
			m = this.atLunation(n);
		}

		if (n <= estimation) {
			while (true) {
				n--;
				Moment test = this.atLunation(n);
				if (!test.isBefore(moment)) {
					m = test;
				} else {
					break;
				}
			}
		}

		return m;

	}

	/**
	 * <p>Obtains the last moon phase which is still before given moment. </p>
	 *
	 * @param 	moment	the moment to be compared with
	 * @return	last time of this phase before given moment
	 * @throws  IllegalArgumentException if the associated year is not in the range {@code -2000 <= year <= 3000}
	 */
	/*[deutsch]
	 * <p>Liefert die letzte Mondphase, die vor dem angegebenen Zeitpunkt liegt. </p>
	 *
	 * @param 	moment	the moment to be compared with
	 * @return	last time of this phase before given moment
	 * @throws  IllegalArgumentException if the associated year is not in the range {@code -2000 <= year <= 3000}
	 */
	public Moment before(Moment moment) {

		int estimation = this.getEstimatedLunations(moment);
		Moment m = this.atLunation(estimation);
		int n = estimation;

		while (!m.isBefore(moment)) {
			n--;
			m = this.atLunation(n);
		}

		if (n >= estimation) {
			while (m.plus(29, TimeUnit.DAYS).isBefore(moment)) { // optimization
				n++;
				Moment test = this.atLunation(n);
				if (test.isBefore(moment)) {
					m = test;
				} else {
					break;
				}
			}
		}

		return m;

	}

	/**
	 * <p>Obtains the minimum supported lunation. </p>
	 *
	 * @return	int
	 * @see 	#atLunation(int)
	 * @since 	3.38/4.33
	 */
	/*[deutsch]
	 * <p>Liefert die minimal unterst&uuml;tzte Lunation. </p>
	 *
	 * @return	int
	 * @see 	#atLunation(int)
	 * @since 	3.38/4.33
	 */
	public static int minLunation() {

		return -49473;

	}

	/**
	 * <p>Obtains the maximum supported lunation. </p>
	 *
	 * @return	int
	 * @see 	#atLunation(int)
	 * @since 	3.38/4.33
	 */
	/*[deutsch]
	 * <p>Liefert die maximal unterst&uuml;tzte Lunation. </p>
	 *
	 * @return	int
	 * @see 	#atLunation(int)
	 * @since 	3.38/4.33
	 */
	public static int maxLunation() {

		return 12379;

	}

	/**
	 * <p>Obtains the time of n-th lunation based on this type of phase. </p>
	 *
	 * <p>The parameter value {@code n = 0} will determine the first phase after the calendar date 2000-01-01.
	 * For example, the expression {@code NEW_MOON.atLunation(0)} calculates the first new moon in year 2000
	 * which is on the date 2000-01-06 (UTC). The lunation parameter should be chosen within the range
	 * {@code minLunation() <= n <= maxLunation()}. </p>
	 *
	 * @param 	n	count of lunations (distance between two consecutive moon phases of same type)
	 * @return	moment of this phase after given lunations
	 * @throws  IllegalArgumentException if the associated year is not in the range {@code -2000 <= year <= 3000}
	 * @see 	#minLunation()
	 * @see 	#maxLunation()
	 */
	/*[deutsch]
	 * <p>Liefert den Zeitpunkt der n-ten Lunation basierend auf diesem Phasentyp. </p>
	 *
	 * <p>Der Parameterwert {@code n = 0} bestimmt die erste Phase nach dem Kalenderdatum 2000-01-01.
	 * Zum Beispiel wird der Ausdruck {@code NEW_MOON.atLunation(0)} den ersten Neumond im Jahre 2000
	 * berechnen, und zwar zum Datum 2000-01-06 (UTC). Die Lunation sollte innerhalb des Bereichs
	 * {@code minLunation() <= n <= maxLunation()} liegen. </p>
	 *
	 * @param 	n	count of lunations (distance between two consecutive moon phases of same type)
	 * @return	moment of this phase after given lunations
	 * @throws  IllegalArgumentException if the associated year is not in the range {@code -2000 <= year <= 3000}
	 * @see 	#minLunation()
	 * @see 	#maxLunation()
	 */
	public Moment atLunation(int n) {

		// Meeus (Chapter 49)
		double k = n + this.phase / 360.0;
		double jct = k / 1236.85;
		double sqJ = jct * jct;

		double jde =
			2451550.09766
				+ MEAN_SYNODIC_MONTH * k
				+ (0.00015437 + (-0.00000015 + 0.00000000073 * jct) * jct) * sqJ;

		double omega =
			124.7746 - 1.56375588 * k + (0.0020672 + 0.00000215 * jct) * sqJ;
		jde -= 0.00017 * Math.sin(Math.toRadians(omega));

		double excentricity = // symbol E
			1 - (0.002516 + 0.0000074 * jct) * jct;
		double solarAnomaly = // symbol M
			2.5534
				+ 29.1053567 * k
				- (0.0000014 + 0.00000011 * jct) * sqJ;
		double lunarAnomaly = // symbol M'
			201.5643
				+ 385.81693528 * k
				+ (0.0107582 + (0.00001238 - 0.000000058 * jct) * jct) * sqJ;
		double moonArgument = // symbol F
			160.7108
				+ 390.67050284 * k
				+ (-0.0016118 + (- 0.00000227 + 0.000000011 * jct) * jct) * sqJ;

		jde += this.periodic24(excentricity, solarAnomaly, lunarAnomaly, moonArgument);

		if (this == FIRST_QUARTER) {
			jde += corrQuarter(excentricity, solarAnomaly, lunarAnomaly, moonArgument);
		} else if (this == LAST_QUARTER) {
			jde -= corrQuarter(excentricity, solarAnomaly, lunarAnomaly, moonArgument);
		}

		double[] planetaryArgs = new double[28];
		planetaryArgs[0] = 299.77 + 0.107408 * k - 0.009173 * sqJ;
		planetaryArgs[1] = 0.000325;
		planetaryArgs[2] = 251.88 + 0.016321 * k;
		planetaryArgs[3] = 0.000165;
		planetaryArgs[4] = 251.83 + 26.651886 * k;
		planetaryArgs[5] = 0.000164;
		planetaryArgs[6] = 349.42 + 36.412478 * k;
		planetaryArgs[7] = 0.000126;
		planetaryArgs[8] = 84.66 + 18.206239 * k;
		planetaryArgs[9] = 0.00011;
		planetaryArgs[10] = 141.74 + 53.303771 * k;
		planetaryArgs[11] = 0.000062;
		planetaryArgs[12] = 207.14 + 2.453732 * k;
		planetaryArgs[13] = 0.00006;
		planetaryArgs[14] = 154.84 + 7.306860 * k;
		planetaryArgs[15] = 0.000056;
		planetaryArgs[16] = 34.52 + 27.261239 * k;
		planetaryArgs[17] = 0.000047;
		planetaryArgs[18] = 207.19 + 0.121824 * k;
		planetaryArgs[19] = 0.000042;
		planetaryArgs[20] = 291.34 + 1.844379 * k;
		planetaryArgs[21] = 0.00004;
		planetaryArgs[22] = 161.72 + 24.198154 * k;
		planetaryArgs[23] = 0.000037;
		planetaryArgs[24] = 239.56 + 25.513099 * k;
		planetaryArgs[25] = 0.000035;
		planetaryArgs[26] = 331.55 + 3.592518 * k;
		planetaryArgs[27] = 0.000023;

		for (int i = 0; i < 28; i += 2) {
			jde = jde + planetaryArgs[i + 1] * Math.sin(Math.toRadians(planetaryArgs[i]));
		}

		return JulianDay.ofEphemerisTime(jde).toMoment().with(Moment.PRECISION, TimeUnit.SECONDS);

	}

	/**
	 * <p>Determines the degree of illumination of the moon at given moment. </p>
	 *
	 * <p>The precision is limited to percent values (two digits after decimal point). </p>
	 *
	 * @param 	moment	    universal time
	 * @return	degree of illumination in range {@code 0.00 <= i <= 1.00}
	 */
	/*[deutsch]
	 * <p>Ermittelt den Beleuchtungsgrad des Mondes zur angegebenen Zeit. </p>
	 *
	 * <p>Die Genauigkeit ist auf Prozentwerte beschr&auml;nkt (zwei Nachkommastellen). </p>
	 *
	 * @param 	moment	    universal time
	 * @return	degree of illumination in range {@code 0.00 <= i <= 1.00}
	 */
	public static double getIllumination(Moment moment) {

		return getIllumination(moment, 0);

	}

	/**
	 * <p>Determines the degree of illumination of the moon at given moment. </p>
	 *
	 * <p>The accuracy is limited to percent values (two digits after decimal point)
	 * if specified as {@code 0}. For promille values, specify {@code 1} etc. However,
     * an increased precision does not imply more accuracy but is only suitable to
     * show some vague tendencies. Percent precision is usually the best choice. </p>
	 *
	 * @param 	moment		universal time
	 * @param 	precision	desired count of fractional digits of percent values (in range {@code 0-3})
	 * @return	degree of illumination in range {@code 0.00 <= i <= 1.00}
	 * @throws 	IndexOutOfBoundsException if the precision is out of range
	 * @since 	3.40/4.35
	 */
	/*[deutsch]
	 * <p>Ermittelt den Beleuchtungsgrad des Mondes zur angegebenen Zeit. </p>
	 *
	 * <p>Die Genauigkeit ist auf Prozentwerte beschr&auml;nkt (zwei Nachkommastellen), wenn
	 * als {@code 0} angegeben. F&uuml;r Promillewerte setze {@code 1} usw. Zu beachten ist
	 * aber, da&szlig; h&ouml;here Genauigkeiten nicht notwendig real sind und lediglich
	 * geeignet sind, ungef&auml;hre Tendenzen anzuzeigen. Meistens ist eine reine
	 * Prozentgenauigkeit sinnvoll. </p>
	 *
	 * @param 	moment		universal time
	 * @param 	precision	desired count of fractional digits of percent values (in range {@code 0-3})
	 * @return	degree of illumination in range {@code 0.00 <= i <= 1.00}
	 * @throws 	IndexOutOfBoundsException if the precision is out of range
	 * @since 	3.40/4.35
	 */
	public static double getIllumination(
		Moment moment,
		int precision
	) {

		double jct = JulianDay.ofEphemerisTime(moment).getCenturyJ2000();

		// Meeus (47.2)
		double meanElongation =
			297.8501921 + (445267.1114034 + (-0.0018819 + (1.0 / 545868 + (1.0 / 113065000) * jct) * jct) * jct) * jct;

		// Meeus (47.3)
		double meanAnomalySun =
			357.5291092 + (35999.0502909 + (-0.0001536 + (1.0 / 24490000) * jct) * jct) * jct;

		// Meeus (47.4)
		double meanAnomalyMoon =
			134.9633964 + (477198.8675055 + (0.0087414 + ((1.0 / 69699) + (1.0 / 14712000) * jct) * jct) * jct) * jct;

		double i = // phase angle of moon for a geocentric observer, Meeus (48.4)
			180 - meanElongation
				- 6.289 * sin(meanAnomalyMoon)
				+ 2.1 * sin(meanAnomalySun)
				- 1.274 * sin(2 * meanElongation - meanAnomalyMoon)
				- 0.658 * sin(2 * meanElongation)
				- 0.214 * sin(2 * meanAnomalyMoon)
				- 0.11 * sin(meanElongation);

		double k = (cos(i) + 1) / 2; // Meeus (48.1)
		int factor = FACTORS[precision];

		if (factor - k * factor <= 0.5) {
			return 1.0;
		} else {
			return Math.floor(k * factor) / factor; // rounding
		}

	}

	private int getEstimatedLunations(Moment moment) {

		return MathUtils.safeCast(
			Math.round(ZERO_REF.until(moment, TimeUnit.DAYS) / MEAN_SYNODIC_MONTH - this.phase / 360.0));

	}

	private double periodic24(
		double excentricity,
		double solarAnomaly,
		double lunarAnomaly,
		double moonArgument
	) {

		double[] v = (this == NEW_MOON ? V_NEW : (this == FULL_MOON ? V_FULL : V_QUARTER));
		int[] w = ((this == NEW_MOON || this == FULL_MOON) ? W_NEW_FULL : W_QUARTER);
		int[] x = ((this == NEW_MOON || this == FULL_MOON) ? X_NEW_FULL : X_QUARTER);
		int[] y = ((this == NEW_MOON || this == FULL_MOON) ? Y_NEW_FULL : Y_QUARTER);
		int[] z = ((this == NEW_MOON || this == FULL_MOON) ? Z_NEW_FULL : Z_QUARTER);

		double s = 0;
		for (int i = 23; i >= 0; i--) {
			double p = v[i];
			if (w[i] == 1) {
				p *= excentricity;
			} else if (w[i] == 2) {
				p = p * excentricity * excentricity;
			}
			p *= sin(x[i] * solarAnomaly + y[i] * lunarAnomaly + z[i] * moonArgument);
			s += p;
		}
		return s;

	}

	private static double corrQuarter(
		double excentricity,
		double solarAnomaly,
		double lunarAnomaly,
		double moonArgument
	) {

		return 0.00306
			- 0.00038 * excentricity * cos(solarAnomaly)
			+ 0.00026 * cos(lunarAnomaly)
			- 0.00002 * cos(lunarAnomaly - solarAnomaly)
			+ 0.00002 * cos(lunarAnomaly + solarAnomaly)
			+ 0.00002 * cos(2 * moonArgument);

	}

	private static double sin(double deg) {

		return Math.sin(deg * Math.PI / 180);

	}

	private static double cos(double deg) {

		return Math.cos(deg * Math.PI / 180);

	}

}
