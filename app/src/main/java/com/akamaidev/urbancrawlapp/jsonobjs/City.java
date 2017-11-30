package com.akamaidev.urbancrawlapp.jsonobjs;

import java.util.List;

/*
 * Copyright 2017 Akamai Technologies, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class City {

    String name, countryname, thumburl, description, heroimage, budget, besttime, language, population, traveladvice, currency;
    int id;
    double lat, lng;

    List<Place> places;

    public String getName() {
        return name;
    }

    public String getCountryname() {
        return countryname;
    }

    public String getThumburl() {
        return thumburl;
    }

    public String getDescription() {
        return description;
    }

    public String getHeroimage() {
        return heroimage;
    }

    public String getBudget() {
        return budget;
    }

    public String getBesttime() {
        return besttime;
    }

    public String getLanguage() {
        return language;
    }

    public String getPopulation() {
        return population;
    }

    public String getTraveladvice() {
        return traveladvice;
    }

    public String getCurrency() {
        return currency;
    }

    public int getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public List<Place> getPlaces() {
        return places;
    }
}
