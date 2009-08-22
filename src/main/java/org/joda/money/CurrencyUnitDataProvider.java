/*
 *  Copyright 2009 Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.joda.money;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provider for available currencies.
 *
 * @author Stephen Colebourne
 */
public abstract class CurrencyUnitDataProvider {

    /**
     * Registers all the currencies known by this provider.
     * 
     * @throws Exception if an error occurs
     */
    protected abstract void registerCurrencies() throws Exception;

    /**
     * Registers a currency allowing it to be used.
     * <p>
     * This method is called by {@link #registerCurrencies()} to perform the
     * actual creation of a currency.
     *
     * @param currencyCode  the currency code, not null
     * @param numericCurrencyCode  the numeric currency code, -1 if none
     * @param decimalPlaces  the number of decimal places that the currency
     *  normally has, from 0 to 3, or -1 for a pseudo-currency
     * @return the new instance, never null
     */
    protected final void registerCurrency(String currencyCode, int numericCurrencyCode, int decimalPlaces) {
        CurrencyUnit.registerCurrency(currencyCode, numericCurrencyCode, decimalPlaces);
    }

    //-----------------------------------------------------------------------
    /**
     * Provider for available currencies using a file.
     * <p>
     * This reads the first resource named '/org/joda/money/MoneyData.csv' on the classpath.
     */
    static class ResourceDataProvider extends CurrencyUnitDataProvider {
        /** Regex format for the csv line. */
        private static final Pattern REGEX_LINE = Pattern.compile("([A-Z]{3}),(-1|[0-9]{1,3}),(-1|0|1|2|3),([A-Z]*)#?.*");

        /**
         * Registers all the currencies known by this provider.
         * <p>
         * This reads the first resource named '/org/joda/money/MoneyData.csv' on the classpath.
         * 
         * @throws Exception if an error occurs
         */
        protected void registerCurrencies() throws Exception {
            InputStream in = getClass().getResourceAsStream("/org/joda/money/MoneyData.csv");
            if (in == null) {
                throw new FileNotFoundException("Data file /org/joda/money/MoneyData.csv not found");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = REGEX_LINE.matcher(line);
                if (matcher.matches()) {
                    registerCurrency(matcher.group(1), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
                }
            }
        }
    }
}
