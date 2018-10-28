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
package net.time4j.android;

import android.app.Application;
import android.content.Context;


/**
 * <p>Serves as super class for any time-based android application using Time4A. </p>
 *
 * <p>Uses {@link ApplicationStarter#initialize(Context, boolean)
 * ApplicationStarter.initialize(this, false)}. </p>
 *
 * @author      Meno Hochschild
 * @since       3.2
 */
/*[deutsch]
 * <p>Dient als Superklasse f&uuml;r eine beliebige zeitbasierte Android-App, die Time4A nutzt. </p>
 *
 * <p>Nutzt {@link ApplicationStarter#initialize(Context, boolean)
 * ApplicationStarter.initialize(this, false)}. </p>
 *
 * @author      Meno Hochschild
 * @since       3.2
 */
public abstract class TimeApplication
    extends Application {

    //~ Methoden ----------------------------------------------------------

    @Override
    public void onCreate() {
        super.onCreate();

        ApplicationStarter.initialize(this, false);

    }

}
