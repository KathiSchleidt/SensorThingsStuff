/*
 * Copyright (C) 2016 Hylke van der Schaaf.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library. If not, see <http://www.gnu.org/licenses/>.
 */
package de.fraunhofer.ilt.sta;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.dao.BaseDao;
import de.fraunhofer.iosb.ilt.sta.dao.ObservationDao;
import de.fraunhofer.iosb.ilt.sta.model.Datastream;
import de.fraunhofer.iosb.ilt.sta.model.Entity;
import de.fraunhofer.iosb.ilt.sta.model.Location;
import de.fraunhofer.iosb.ilt.sta.model.Observation;
import de.fraunhofer.iosb.ilt.sta.model.ObservedProperty;
import de.fraunhofer.iosb.ilt.sta.model.Sensor;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.model.TimeObject;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import de.fraunhofer.iosb.ilt.tests.Constants;
import de.fraunhofer.iosb.ilt.tests.Utils;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.geojson.Point;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.extra.Interval;

/**
 * Tests date and time functions.
 *
 * @author Hylke van der Schaaf
 */
public class DateTimeTests {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeTests.class);
    private static String rootUri;
    private static SensorThingsService service;
    private static final List<Thing> THINGS = new ArrayList<>();
    private static final List<Location> LOCATIONS = new ArrayList<>();
    private static final List<Sensor> SENSORS = new ArrayList<>();
    private static final List<ObservedProperty> O_PROPS = new ArrayList<>();
    private static final List<Datastream> DATASTREAMS = new ArrayList<>();
    private static final List<Observation> OBSERVATIONS = new ArrayList<>();
    private static ZonedDateTime T600;
    private static ZonedDateTime T659;
    private static ZonedDateTime T700;
    private static ZonedDateTime T701;
    private static ZonedDateTime T759;
    private static ZonedDateTime T800;
    private static ZonedDateTime T801;
    private static ZonedDateTime T900;
    private static Interval I600_659;
    private static Interval I600_700;
    private static Interval I600_701;
    private static Interval I700_800;
    private static Interval I701_759;
    private static Interval I759_900;
    private static Interval I800_900;
    private static Interval I801_900;
    private static Interval I659_801;
    private static Interval I700_759;
    private static Interval I700_801;
    private static Interval I659_800;
    private static Interval I701_800;

    public DateTimeTests() {
    }

    @BeforeClass
    public static void setUp() {
        rootUri = Constants.BASE_URL;
        rootUri = rootUri.trim();
        if (rootUri.lastIndexOf('/') == rootUri.length() - 1) {
            rootUri = rootUri.substring(0, rootUri.length() - 1);
        }
        URL url;
        try {
            url = new URL(rootUri);
            service = Constants.createService(url);
            createEntities();
        } catch (MalformedURLException | URISyntaxException ex) {
            LOGGER.error("Failed to create service uri.", ex);
        } catch (ServiceFailureException ex) {
            LOGGER.error("Failed to create entities.", ex);
        } catch (Exception ex) {
            LOGGER.error("Unknown Exception.", ex);
        }
    }

    @AfterClass
    public static void tearDown() {
        LOGGER.info("tearing down class.");
        try {
            Utils.deleteAll(service);
        } catch (ServiceFailureException ex) {
            LOGGER.error("Failed to clean database.", ex);
        }
    }

    private static void createEntities() throws ServiceFailureException, URISyntaxException {
        Thing thing = new Thing("Thing 1", "The first thing.");
        THINGS.add(thing);
        Location location = new Location("Location 1.0", "Location of Thing 1.", "application/vnd.geo+json", new Point(8, 51));
        LOCATIONS.add(location);
        thing.getLocations().add(location);
        service.create(thing);

        Sensor sensor = new Sensor("Sensor 1", "The first sensor.", "text", "Some metadata.");
        SENSORS.add(sensor);
        ObservedProperty obsProp = new ObservedProperty("Temperature", new URI("http://ucom.org/temperature"), "The temperature of the thing.");
        O_PROPS.add(obsProp);
        Datastream datastream = new Datastream("Datastream 1", "The temperature of thing 1, sensor 1.", "someType", new UnitOfMeasurement("degree celcius", "°C", "ucum:T"));
        DATASTREAMS.add(datastream);
        datastream.setThing(thing);
        datastream.setSensor(sensor);
        datastream.setObservedProperty(obsProp);
        service.create(datastream);

        T600 = ZonedDateTime.parse("2016-01-01T06:00:00.000Z");
        T659 = ZonedDateTime.parse("2016-01-01T06:59:00.000Z");
        T700 = ZonedDateTime.parse("2016-01-01T07:00:00.000Z");
        T701 = ZonedDateTime.parse("2016-01-01T07:01:00.000Z");
        T759 = ZonedDateTime.parse("2016-01-01T07:59:00.000Z");
        T800 = ZonedDateTime.parse("2016-01-01T08:00:00.000Z");
        T801 = ZonedDateTime.parse("2016-01-01T08:01:00.000Z");
        T900 = ZonedDateTime.parse("2016-01-01T09:00:00.000Z");

        I600_659 = Interval.of(T600.toInstant(), T659.toInstant());
        I600_700 = Interval.of(T600.toInstant(), T700.toInstant());
        I600_701 = Interval.of(T600.toInstant(), T701.toInstant());
        I700_800 = Interval.of(T700.toInstant(), T800.toInstant());
        I701_759 = Interval.of(T701.toInstant(), T759.toInstant());
        I759_900 = Interval.of(T759.toInstant(), T900.toInstant());
        I800_900 = Interval.of(T800.toInstant(), T900.toInstant());
        I801_900 = Interval.of(T801.toInstant(), T900.toInstant());
        I659_801 = Interval.of(T659.toInstant(), T801.toInstant());
        I700_759 = Interval.of(T700.toInstant(), T759.toInstant());
        I700_801 = Interval.of(T700.toInstant(), T801.toInstant());
        I659_800 = Interval.of(T659.toInstant(), T800.toInstant());
        I701_800 = Interval.of(T701.toInstant(), T800.toInstant());

        createObservation(0, datastream, T600, T600, null); // 0
        createObservation(1, datastream, T659, T659, null); // 1
        createObservation(2, datastream, T700, T700, null); // 2
        createObservation(3, datastream, T701, T701, null); // 3
        createObservation(4, datastream, T759, T759, null); // 4
        createObservation(5, datastream, T800, T800, null); // 5
        createObservation(6, datastream, T801, T801, null); // 6
        createObservation(7, datastream, T900, T900, null); // 7

        createObservation(8, datastream, I600_659, null, I600_659); // 8
        createObservation(9, datastream, I600_700, null, I600_700); // 9
        createObservation(10, datastream, I600_701, null, I600_701); // 10
        createObservation(11, datastream, I700_800, null, I700_800); // 11
        createObservation(12, datastream, I701_759, null, I701_759); // 12
        createObservation(13, datastream, I759_900, null, I759_900); // 13
        createObservation(14, datastream, I800_900, null, I800_900); // 14
        createObservation(15, datastream, I801_900, null, I801_900); // 15

        createObservation(16, datastream, I659_801, null, I659_801); // 16
        createObservation(17, datastream, I700_759, null, I700_759); // 17
        createObservation(18, datastream, I700_801, null, I700_801); // 18
        createObservation(19, datastream, I659_800, null, I659_800); // 19
        createObservation(20, datastream, I701_800, null, I701_800); // 20
    }

    private static void createObservation(double result, Datastream ds, Interval pt, ZonedDateTime rt, Interval vt) throws ServiceFailureException {
        createObservation(result, ds, new TimeObject(pt), rt, vt);
    }

    private static void createObservation(double result, Datastream ds, ZonedDateTime pt, ZonedDateTime rt, Interval vt) throws ServiceFailureException {
        createObservation(result, ds, new TimeObject(pt), rt, vt);
    }

    private static void createObservation(double result, Datastream ds, TimeObject pt, ZonedDateTime rt, Interval vt) throws ServiceFailureException {
        Observation o = new Observation(result, ds);
        o.setPhenomenonTime(pt);
        o.setResultTime(rt);
        o.setValidTime(vt);
        service.create(o);
        OBSERVATIONS.add(o);
    }

    public void filterAndCheck(BaseDao doa, String filter, List<? extends Entity> expected) {
        try {
            EntityList<Observation> result = doa.query().filter(filter).list();
            Utils.TestResult check = Utils.resultContains(result, expected);
            Assert.assertTrue("Failed on filter: " + filter + " Cause: " + check.message, check.testOk);
        } catch (ServiceFailureException ex) {
            Assert.fail("Failed to call service.");
        }
    }

    public void filterForException(BaseDao doa, String filter) {
        try {
            doa.query().filter(filter).list();
        } catch (IllegalArgumentException e) {
            return;
        } catch (ServiceFailureException ex) {
            Assert.fail("Failed to call service.");
        }
        Assert.fail("Filter " + filter + " did not respond with 400 Bad Request.");
    }

    @Test
    public void testLt() throws ServiceFailureException {
        ObservationDao doa = service.observations();
        filterAndCheck(doa, String.format("resultTime lt %s", T700), getFromList(OBSERVATIONS, 0, 1));
        filterAndCheck(doa, String.format("validTime lt %s", T700), getFromList(OBSERVATIONS, 8, 9));
        filterAndCheck(doa, String.format("phenomenonTime lt %s", T700), getFromList(OBSERVATIONS, 0, 1, 8, 9));

        filterAndCheck(doa, String.format("resultTime lt %s", I700_800), getFromList(OBSERVATIONS, 0, 1));
        filterAndCheck(doa, String.format("validTime lt %s", I700_800), getFromList(OBSERVATIONS, 8, 9));
        filterAndCheck(doa, String.format("phenomenonTime lt %s", I700_800), getFromList(OBSERVATIONS, 0, 1, 8, 9));

        filterAndCheck(doa, String.format("%s lt resultTime", T800), getFromList(OBSERVATIONS, 6, 7));
        filterAndCheck(doa, String.format("%s lt validTime", T800), getFromList(OBSERVATIONS, 15));
        filterAndCheck(doa, String.format("%s lt phenomenonTime", T800), getFromList(OBSERVATIONS, 6, 7, 15));

        filterAndCheck(doa, String.format("%s lt resultTime", I700_800), getFromList(OBSERVATIONS, 5, 6, 7));
        filterAndCheck(doa, String.format("%s lt validTime", I700_800), getFromList(OBSERVATIONS, 14, 15));
        filterAndCheck(doa, String.format("%s lt phenomenonTime", I700_800), getFromList(OBSERVATIONS, 5, 6, 7, 14, 15));
    }

    @Test
    public void testGt() throws ServiceFailureException {
        ObservationDao doa = service.observations();
        filterAndCheck(doa, String.format("resultTime gt %s", T800), getFromList(OBSERVATIONS, 6, 7));
        filterAndCheck(doa, String.format("validTime gt %s", T800), getFromList(OBSERVATIONS, 15));
        filterAndCheck(doa, String.format("phenomenonTime gt %s", T800), getFromList(OBSERVATIONS, 6, 7, 15));

        filterAndCheck(doa, String.format("resultTime gt %s", I700_800), getFromList(OBSERVATIONS, 5, 6, 7));
        filterAndCheck(doa, String.format("validTime gt %s", I700_800), getFromList(OBSERVATIONS, 14, 15));
        filterAndCheck(doa, String.format("phenomenonTime gt %s", I700_800), getFromList(OBSERVATIONS, 5, 6, 7, 14, 15));

        filterAndCheck(doa, String.format("%s gt resultTime", T700), getFromList(OBSERVATIONS, 0, 1));
        filterAndCheck(doa, String.format("%s gt validTime", T700), getFromList(OBSERVATIONS, 8, 9));
        filterAndCheck(doa, String.format("%s gt phenomenonTime", T700), getFromList(OBSERVATIONS, 0, 1, 8, 9));

        filterAndCheck(doa, String.format("%s gt resultTime", I700_800), getFromList(OBSERVATIONS, 0, 1));
        filterAndCheck(doa, String.format("%s gt validTime", I700_800), getFromList(OBSERVATIONS, 8, 9));
        filterAndCheck(doa, String.format("%s gt phenomenonTime", I700_800), getFromList(OBSERVATIONS, 0, 1, 8, 9));
    }

    @Test
    public void testLe() throws ServiceFailureException {
        ObservationDao doa = service.observations();
        filterAndCheck(doa, String.format("resultTime le %s", T700), getFromList(OBSERVATIONS, 0, 1, 2));
        filterAndCheck(doa, String.format("validTime le %s", T700), getFromList(OBSERVATIONS, 8, 9));
        filterAndCheck(doa, String.format("phenomenonTime le %s", T700), getFromList(OBSERVATIONS, 0, 1, 2, 8, 9));

        filterAndCheck(doa, String.format("resultTime le %s", I700_800), getFromList(OBSERVATIONS, 0, 1, 2));
        filterAndCheck(doa, String.format("validTime le %s", I700_800), getFromList(OBSERVATIONS, 8, 9, 10, 11, 17, 19));
        filterAndCheck(doa, String.format("phenomenonTime le %s", I700_800), getFromList(OBSERVATIONS, 0, 1, 2, 8, 9, 10, 11, 17, 19));

        filterAndCheck(doa, String.format("%s le resultTime", T800), getFromList(OBSERVATIONS, 5, 6, 7));
        filterAndCheck(doa, String.format("%s le validTime", T800), getFromList(OBSERVATIONS, 14, 15));
        filterAndCheck(doa, String.format("%s le phenomenonTime", T800), getFromList(OBSERVATIONS, 5, 6, 7, 14, 15));

        filterAndCheck(doa, String.format("%s le resultTime", I700_800), getFromList(OBSERVATIONS, 5, 6, 7));
        filterAndCheck(doa, String.format("%s le validTime", I700_800), getFromList(OBSERVATIONS, 11, 13, 14, 15, 18, 20));
        filterAndCheck(doa, String.format("%s le phenomenonTime", I700_800), getFromList(OBSERVATIONS, 5, 6, 7, 11, 13, 14, 15, 18, 20));
    }

    @Test
    public void testGe() throws ServiceFailureException {
        ObservationDao doa = service.observations();
        filterAndCheck(doa, String.format("resultTime ge %s", T800), getFromList(OBSERVATIONS, 5, 6, 7));
        filterAndCheck(doa, String.format("validTime ge %s", T800), getFromList(OBSERVATIONS, 14, 15));
        filterAndCheck(doa, String.format("phenomenonTime ge %s", T800), getFromList(OBSERVATIONS, 5, 6, 7, 14, 15));

        filterAndCheck(doa, String.format("resultTime ge %s", I700_800), getFromList(OBSERVATIONS, 5, 6, 7));
        filterAndCheck(doa, String.format("validTime ge %s", I700_800), getFromList(OBSERVATIONS, 11, 13, 14, 15, 18, 20));
        filterAndCheck(doa, String.format("phenomenonTime ge %s", I700_800), getFromList(OBSERVATIONS, 5, 6, 7, 11, 13, 14, 15, 18, 20));

        filterAndCheck(doa, String.format("%s ge resultTime", T700), getFromList(OBSERVATIONS, 0, 1, 2));
        filterAndCheck(doa, String.format("%s ge validTime", T700), getFromList(OBSERVATIONS, 8, 9));
        filterAndCheck(doa, String.format("%s ge phenomenonTime", T700), getFromList(OBSERVATIONS, 0, 1, 2, 8, 9));

        filterAndCheck(doa, String.format("%s ge resultTime", I700_800), getFromList(OBSERVATIONS, 0, 1, 2));
        filterAndCheck(doa, String.format("%s ge validTime", I700_800), getFromList(OBSERVATIONS, 8, 9, 10, 11, 17, 19));
        filterAndCheck(doa, String.format("%s ge phenomenonTime", I700_800), getFromList(OBSERVATIONS, 0, 1, 2, 8, 9, 10, 11, 17, 19));
    }

    @Test
    public void testEq() throws ServiceFailureException {
        ObservationDao doa = service.observations();
        filterAndCheck(doa, String.format("resultTime eq %s", T800), getFromList(OBSERVATIONS, 5));
        filterAndCheck(doa, String.format("validTime eq %s", T800), getFromList(OBSERVATIONS));
        filterAndCheck(doa, String.format("phenomenonTime eq %s", T800), getFromList(OBSERVATIONS, 5));

        filterAndCheck(doa, String.format("resultTime eq %s", I700_800), getFromList(OBSERVATIONS));
        filterAndCheck(doa, String.format("validTime eq %s", I700_800), getFromList(OBSERVATIONS, 11));
        filterAndCheck(doa, String.format("phenomenonTime eq %s", I700_800), getFromList(OBSERVATIONS, 11));

        filterAndCheck(doa, String.format("%s eq resultTime", T700), getFromList(OBSERVATIONS, 2));
        filterAndCheck(doa, String.format("%s eq validTime", T700), getFromList(OBSERVATIONS));
        filterAndCheck(doa, String.format("%s eq phenomenonTime", T700), getFromList(OBSERVATIONS, 2));

        filterAndCheck(doa, String.format("%s eq resultTime", I700_800), getFromList(OBSERVATIONS));
        filterAndCheck(doa, String.format("%s eq validTime", I700_800), getFromList(OBSERVATIONS, 11));
        filterAndCheck(doa, String.format("%s eq phenomenonTime", I700_800), getFromList(OBSERVATIONS, 11));
    }

    @Test
    public void testBefore() throws ServiceFailureException {
        ObservationDao doa = service.observations();
        filterAndCheck(doa, String.format("before(resultTime,%s)", T700), getFromList(OBSERVATIONS, 0, 1));
        filterAndCheck(doa, String.format("before(validTime,%s)", T700), getFromList(OBSERVATIONS, 8, 9));
        filterAndCheck(doa, String.format("before(phenomenonTime,%s)", T700), getFromList(OBSERVATIONS, 0, 1, 8, 9));

        filterAndCheck(doa, String.format("before(resultTime,%s)", I700_800), getFromList(OBSERVATIONS, 0, 1));
        filterAndCheck(doa, String.format("before(validTime,%s)", I700_800), getFromList(OBSERVATIONS, 8, 9));
        filterAndCheck(doa, String.format("before(phenomenonTime,%s)", I700_800), getFromList(OBSERVATIONS, 0, 1, 8, 9));

        filterAndCheck(doa, String.format("before(%s,resultTime)", T800), getFromList(OBSERVATIONS, 6, 7));
        filterAndCheck(doa, String.format("before(%s,validTime)", T800), getFromList(OBSERVATIONS, 15));
        filterAndCheck(doa, String.format("before(%s,phenomenonTime)", T800), getFromList(OBSERVATIONS, 6, 7, 15));

        filterAndCheck(doa, String.format("before(%s,resultTime)", I700_800), getFromList(OBSERVATIONS, 5, 6, 7));
        filterAndCheck(doa, String.format("before(%s,validTime)", I700_800), getFromList(OBSERVATIONS, 14, 15));
        filterAndCheck(doa, String.format("before(%s,phenomenonTime)", I700_800), getFromList(OBSERVATIONS, 5, 6, 7, 14, 15));
    }

    @Test
    public void testAfter() throws ServiceFailureException {
        ObservationDao doa = service.observations();
        filterAndCheck(doa, String.format("after(resultTime,%s)", T800), getFromList(OBSERVATIONS, 6, 7));
        filterAndCheck(doa, String.format("after(validTime,%s)", T800), getFromList(OBSERVATIONS, 15));
        filterAndCheck(doa, String.format("after(phenomenonTime,%s)", T800), getFromList(OBSERVATIONS, 6, 7, 15));

        filterAndCheck(doa, String.format("after(resultTime,%s)", I700_800), getFromList(OBSERVATIONS, 5, 6, 7));
        filterAndCheck(doa, String.format("after(validTime,%s)", I700_800), getFromList(OBSERVATIONS, 14, 15));
        filterAndCheck(doa, String.format("after(phenomenonTime,%s)", I700_800), getFromList(OBSERVATIONS, 5, 6, 7, 14, 15));

        filterAndCheck(doa, String.format("after(%s,resultTime)", T700), getFromList(OBSERVATIONS, 0, 1));
        filterAndCheck(doa, String.format("after(%s,validTime)", T700), getFromList(OBSERVATIONS, 8, 9));
        filterAndCheck(doa, String.format("after(%s,phenomenonTime)", T700), getFromList(OBSERVATIONS, 0, 1, 8, 9));

        filterAndCheck(doa, String.format("after(%s,resultTime)", I700_800), getFromList(OBSERVATIONS, 0, 1));
        filterAndCheck(doa, String.format("after(%s,validTime)", I700_800), getFromList(OBSERVATIONS, 8, 9));
        filterAndCheck(doa, String.format("after(%s,phenomenonTime)", I700_800), getFromList(OBSERVATIONS, 0, 1, 8, 9));
    }

    @Test
    public void testMeets() throws ServiceFailureException {
        ObservationDao doa = service.observations();
        filterAndCheck(doa, String.format("meets(resultTime,%s)", T700), getFromList(OBSERVATIONS, 2));
        filterAndCheck(doa, String.format("meets(validTime,%s)", T700), getFromList(OBSERVATIONS, 9, 11, 17, 18));
        filterAndCheck(doa, String.format("meets(phenomenonTime,%s)", T700), getFromList(OBSERVATIONS, 2, 9, 11, 17, 18));

        filterAndCheck(doa, String.format("meets(resultTime,%s)", I700_800), getFromList(OBSERVATIONS, 2, 5));
        filterAndCheck(doa, String.format("meets(validTime,%s)", I700_800), getFromList(OBSERVATIONS, 9, 14));
        filterAndCheck(doa, String.format("meets(phenomenonTime,%s)", I700_800), getFromList(OBSERVATIONS, 2, 5, 9, 14));

        filterAndCheck(doa, String.format("meets(%s,resultTime)", T700), getFromList(OBSERVATIONS, 2));
        filterAndCheck(doa, String.format("meets(%s,validTime)", T700), getFromList(OBSERVATIONS, 9, 11, 17, 18));
        filterAndCheck(doa, String.format("meets(%s,phenomenonTime)", T700), getFromList(OBSERVATIONS, 2, 9, 11, 17, 18));

        filterAndCheck(doa, String.format("meets(%s,resultTime)", I700_800), getFromList(OBSERVATIONS, 2, 5));
        filterAndCheck(doa, String.format("meets(%s,validTime)", I700_800), getFromList(OBSERVATIONS, 9, 14));
        filterAndCheck(doa, String.format("meets(%s,phenomenonTime)", I700_800), getFromList(OBSERVATIONS, 2, 5, 9, 14));
    }

    @Test
    public void testDuring() throws ServiceFailureException {
        ObservationDao doa = service.observations();
        filterForException(doa, String.format("during(resultTime,%s)", T700));
        filterForException(doa, String.format("during(validTime,%s)", T700));
        filterForException(doa, String.format("during(phenomenonTime,%s)", T700));

        filterAndCheck(doa, String.format("during(resultTime,%s)", I700_800), getFromList(OBSERVATIONS, 2, 3, 4));
        filterAndCheck(doa, String.format("during(validTime,%s)", I700_800), getFromList(OBSERVATIONS, 11, 12, 17, 20));
        filterAndCheck(doa, String.format("during(phenomenonTime,%s)", I700_800), getFromList(OBSERVATIONS, 2, 3, 4, 11, 12, 17, 20));

        filterForException(doa, String.format("during(%s,resultTime)", T700));
        filterAndCheck(doa, String.format("during(%s,validTime)", T700), getFromList(OBSERVATIONS, 10, 11, 16, 17, 18, 19));
        filterAndCheck(doa, String.format("during(%s,phenomenonTime)", T700), getFromList(OBSERVATIONS, 10, 11, 16, 17, 18, 19));

        filterForException(doa, String.format("during(%s,resultTime)", I700_800));
        filterAndCheck(doa, String.format("during(%s,validTime)", I700_800), getFromList(OBSERVATIONS, 11, 16, 18, 19));
        filterAndCheck(doa, String.format("during(%s,phenomenonTime)", I700_800), getFromList(OBSERVATIONS, 11, 16, 18, 19));
    }

    @Test
    public void testOverlaps() throws ServiceFailureException {
        ObservationDao doa = service.observations();
        filterAndCheck(doa, String.format("overlaps(resultTime,%s)", T700), getFromList(OBSERVATIONS, 2));
        filterAndCheck(doa, String.format("overlaps(validTime,%s)", T700), getFromList(OBSERVATIONS, 10, 11, 16, 17, 18, 19));
        filterAndCheck(doa, String.format("overlaps(phenomenonTime,%s)", T700), getFromList(OBSERVATIONS, 2, 10, 11, 16, 17, 18, 19));

        filterAndCheck(doa, String.format("overlaps(resultTime,%s)", I700_800), getFromList(OBSERVATIONS, 2, 3, 4));
        filterAndCheck(doa, String.format("overlaps(validTime,%s)", I700_800), getFromList(OBSERVATIONS, 10, 11, 12, 13, 16, 17, 18, 19, 20));
        filterAndCheck(doa, String.format("overlaps(phenomenonTime,%s)", I700_800), getFromList(OBSERVATIONS, 2, 3, 4, 10, 11, 12, 13, 16, 17, 18, 19, 20));

        filterAndCheck(doa, String.format("overlaps(%s,resultTime)", T700), getFromList(OBSERVATIONS, 2));
        filterAndCheck(doa, String.format("overlaps(%s,validTime)", T700), getFromList(OBSERVATIONS, 10, 11, 16, 17, 18, 19));
        filterAndCheck(doa, String.format("overlaps(%s,phenomenonTime)", T700), getFromList(OBSERVATIONS, 2, 10, 11, 16, 17, 18, 19));

        filterAndCheck(doa, String.format("overlaps(%s,resultTime)", I700_800), getFromList(OBSERVATIONS, 2, 3, 4));
        filterAndCheck(doa, String.format("overlaps(%s,validTime)", I700_800), getFromList(OBSERVATIONS, 10, 11, 12, 13, 16, 17, 18, 19, 20));
        filterAndCheck(doa, String.format("overlaps(%s,phenomenonTime)", I700_800), getFromList(OBSERVATIONS, 2, 3, 4, 10, 11, 12, 13, 16, 17, 18, 19, 20));
    }

    @Test
    public void testStarts() throws ServiceFailureException {
        ObservationDao doa = service.observations();
        filterAndCheck(doa, String.format("starts(resultTime,%s)", T700), getFromList(OBSERVATIONS, 2));
        filterAndCheck(doa, String.format("starts(validTime,%s)", T700), getFromList(OBSERVATIONS, 11, 17, 18));
        filterAndCheck(doa, String.format("starts(phenomenonTime,%s)", T700), getFromList(OBSERVATIONS, 2, 11, 17, 18));

        filterAndCheck(doa, String.format("starts(resultTime,%s)", I700_800), getFromList(OBSERVATIONS, 2));
        filterAndCheck(doa, String.format("starts(validTime,%s)", I700_800), getFromList(OBSERVATIONS, 11, 17, 18));
        filterAndCheck(doa, String.format("starts(phenomenonTime,%s)", I700_800), getFromList(OBSERVATIONS, 2, 11, 17, 18));

        filterAndCheck(doa, String.format("starts(%s,resultTime)", T700), getFromList(OBSERVATIONS, 2));
        filterAndCheck(doa, String.format("starts(%s,validTime)", T700), getFromList(OBSERVATIONS, 11, 17, 18));
        filterAndCheck(doa, String.format("starts(%s,phenomenonTime)", T700), getFromList(OBSERVATIONS, 2, 11, 17, 18));

        filterAndCheck(doa, String.format("starts(%s,resultTime)", I700_800), getFromList(OBSERVATIONS, 2));
        filterAndCheck(doa, String.format("starts(%s,validTime)", I700_800), getFromList(OBSERVATIONS, 11, 17, 18));
        filterAndCheck(doa, String.format("starts(%s,phenomenonTime)", I700_800), getFromList(OBSERVATIONS, 2, 11, 17, 18));
    }

    @Test
    public void testFinishes() throws ServiceFailureException {
        ObservationDao doa = service.observations();
        filterAndCheck(doa, String.format("finishes(resultTime,%s)", T800), getFromList(OBSERVATIONS, 5));
        filterAndCheck(doa, String.format("finishes(validTime,%s)", T800), getFromList(OBSERVATIONS, 11, 19, 20));
        filterAndCheck(doa, String.format("finishes(phenomenonTime,%s)", T800), getFromList(OBSERVATIONS, 5, 11, 19, 20));

        filterAndCheck(doa, String.format("finishes(resultTime,%s)", I700_800), getFromList(OBSERVATIONS, 5));
        filterAndCheck(doa, String.format("finishes(validTime,%s)", I700_800), getFromList(OBSERVATIONS, 11, 19, 20));
        filterAndCheck(doa, String.format("finishes(phenomenonTime,%s)", I700_800), getFromList(OBSERVATIONS, 5, 11, 19, 20));

        filterAndCheck(doa, String.format("finishes(%s,resultTime)", T800), getFromList(OBSERVATIONS, 5));
        filterAndCheck(doa, String.format("finishes(%s,validTime)", T800), getFromList(OBSERVATIONS, 11, 19, 20));
        filterAndCheck(doa, String.format("finishes(%s,phenomenonTime)", T800), getFromList(OBSERVATIONS, 5, 11, 19, 20));

        filterAndCheck(doa, String.format("finishes(%s,resultTime)", I700_800), getFromList(OBSERVATIONS, 5));
        filterAndCheck(doa, String.format("finishes(%s,validTime)", I700_800), getFromList(OBSERVATIONS, 11, 19, 20));
        filterAndCheck(doa, String.format("finishes(%s,phenomenonTime)", I700_800), getFromList(OBSERVATIONS, 5, 11, 19, 20));
    }

    public static <T extends Entity<T>> List<T> getFromList(List<T> list, int... ids) {
        List<T> result = new ArrayList<>();
        for (int i : ids) {
            result.add(list.get(i));
        }
        return result;
    }
}
