/*
 * OSDParser.java
 * Copyright (C) 2020 Joshua Sweaney
 *
 * This file is part of Java Marine API.
 * <http://ktuukkan.github.io/marine-api/>
 *
 * Java Marine API is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Java Marine API is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Java Marine API. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.marineapi.nmea.parser;

import java.util.Arrays;

import net.sf.marineapi.nmea.sentence.OSDSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.util.DataStatus;
import net.sf.marineapi.nmea.util.ReferenceSystem;
import net.sf.marineapi.nmea.util.Units;

/**
 * OSD sentence parser
 *
 * @author Joshua Sweaney
 */
class OSDParser extends SentenceParser implements OSDSentence {

    private static final int HEADING = 0;
    private static final int HEADING_STATUS = 1;
    private static final int COURSE = 2;
    private static final int COURSE_REFERENCE = 3;
    private static final int SPEED = 4;
    private static final int SPEED_REFERENCE = 5;
    private static final int VESSEL_SET = 6;
    private static final int VESSEL_DRIFT = 7;
    private static final int SPEED_UNITS = 8;

    private static final Units[] VALID_SPEED_UNITS = {Units.KILOMETERS, Units.NAUTICAL_MILES, Units.STATUTE_MILES};

    /**
	 * Creates a new instance of OSD parser
	 *
	 * @param nmea OSD sentence string.
	 */
	public OSDParser(String nmea) {
        super(nmea, SentenceId.OSD);
    }

    /**
	 * Creates OSD parser with empty sentence.
	 *
	 * @param talker TalkerId to set
	 */
	public OSDParser(TalkerId talker) {
		super(talker, SentenceId.OSD, 9);
    }

    /**
     * @see OSDSentence#getHeading()
     */
    public double getHeading() {
        return getDoubleValue(HEADING);
    }

    /**
     * @see OSDSentence#getHeadingStatus()
     */
    public DataStatus getHeadingStatus() {
        return DataStatus.valueOf(getCharValue(HEADING_STATUS));
    }

    /**
     * @see OSDSentence#getCourse()
     */
    public double getCourse() {
        return getDoubleValue(COURSE);
    }

    /**
     * @see OSDSentence#getCourseReference()
     */
    public ReferenceSystem getCourseReference() {
        return ReferenceSystem.valueOf(getCharValue(COURSE_REFERENCE));
    }

    /**
     * @see OSDSentence#getSpeed()
     */
    public double getSpeed() {
        return getDoubleValue(SPEED);
    }

    /**
     * @see OSDSentence#getSpeedReference()
     */
    public ReferenceSystem getSpeedReference() {
        return ReferenceSystem.valueOf(getCharValue(SPEED_REFERENCE));
    }

    /**
     * @see OSDSentence#getVesselSet()
     */
    public double getVesselSet() {
        return getDoubleValue(VESSEL_SET);
    }

    /**
     * @see OSDSentence#getVesselDrift()
     */
    public double getVesselDrift() {
        return getDoubleValue(VESSEL_DRIFT);
    }

    /**
     * @see OSDSentence#getSpeedUnits()
     */
    public Units getSpeedUnits() {
        return Units.valueOf(getCharValue(SPEED_UNITS));
    }

    /**
     * @see OSDSentence#setHeading(double)
     */
    public void setHeading(double heading) {
        setDoubleValue(HEADING, heading);
    }

    /**
     * @see OSDSentence#setHeadingStatus(DataStatus)
     */
    public void setHeadingStatus(DataStatus status) {
        setCharValue(HEADING_STATUS, status.toChar());
    }

    /**
     * @see OSDSentence#setCourse(double)
     */
    public void setCourse(double course) {
        setDoubleValue(COURSE, course);
    }

    /**
     * @see OSDSentence#setCourseReference(ReferenceSystem)
     */
    public void setCourseReference(ReferenceSystem reference) {
        setCharValue(COURSE_REFERENCE, reference.toChar());
    }

    /**
     * @see OSDSentence#setSpeed(double)
     */
    public void setSpeed(double speed) {
        setDoubleValue(SPEED, speed);
    }

    /**
     * @see OSDSentence#setSpeedReference(ReferenceSystem)
     */
    public void setSpeedReference(ReferenceSystem reference) {
        setCharValue(SPEED_REFERENCE, reference.toChar());
    }

    /**
     * @see OSDSentence#setVesselSet(double)
     */
    public void setVesselSet(double set) {
        setDoubleValue(VESSEL_SET, set);
    }

    /**
     * @see OSDSentence#setVesselDrift(double)
     */
    public void setVesselDrift(double drift) {
        setDoubleValue(VESSEL_DRIFT, drift);
    }

    /**
     * @see OSDSentence#setSpeedUnits(Units)
     */
    public void setSpeedUnits(Units units) {
        if (Arrays.asList(VALID_SPEED_UNITS).contains(units)) {
            setCharValue(SPEED_UNITS, units.toChar());
        } else {
            String err = "Speed units must be ";
            for (int i = 0; i<VALID_SPEED_UNITS.length; i++) {
                Units u = VALID_SPEED_UNITS[i];
                err += u.name() + "(" + u.toChar() + ")";
                if (i != VALID_SPEED_UNITS.length-1) {
                    err += ", ";
                }
            }
            throw new IllegalArgumentException(err);
        }
    }

}
