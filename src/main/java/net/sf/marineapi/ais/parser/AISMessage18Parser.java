/*
 * AISMessage18Parser.java
 * Copyright (C) 2015 Lázár József
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
package net.sf.marineapi.ais.parser;

import net.sf.marineapi.ais.message.AISMessage18;
import net.sf.marineapi.ais.util.Sixbit;

/**
 * AIS message 18 parser, Standard Class B CS Position Report.
 *
 * @author Lázár József
 */
class AISMessage18Parser extends AISPositionReportBParser implements AISMessage18 {

	/**
	 * Constructor.
	 *
	 * @param content Six-bit message content.
	 */
	public AISMessage18Parser(Sixbit content) {
		super(content);
	}
}
