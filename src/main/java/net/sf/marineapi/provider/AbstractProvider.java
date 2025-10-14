/*
 * AbstractProvider.java
 * Copyright (C) 2011 Kimmo Tuukkanen
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
package net.sf.marineapi.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.event.SentenceListener;
import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.provider.event.ProviderEvent;
import net.sf.marineapi.provider.event.ProviderListener;

/**
 * <p>Abstract base class for providers. Defines methods that all providers must
 * implement and provides general services for capturing and validating the
 * required sentences.</p>
 * <p>When constructing {@link net.sf.marineapi.provider.event.PositionEvent},
 * the maximum age for all captured sentences is 1000 ms, i.e. all sentences are
 * from within the default NMEA update rate (1/s).</p>
 *
 * @author Kimmo Tuukkanen
 * @param <T> The {@link ProviderEvent} to be dispatched.
 */
public abstract class AbstractProvider<T extends ProviderEvent> implements
		SentenceListener {

	/** The default timeout for receicing a burst of sentences. */
	public static int DEFAULT_TIMEOUT = 1000;

	private SentenceReader reader;
	private List<SentenceEvent> events = new ArrayList<SentenceEvent>();
	private List<ProviderListener<T>> listeners = new ArrayList<ProviderListener<T>>();
	private int timeout = DEFAULT_TIMEOUT;

	/**
	 * Creates a new instance of AbstractProvider.
	 *
	 * @param reader Sentence reader to be used as data source
	 * @param ids Types of sentences to capture for creating provider events
	 */
	public AbstractProvider(SentenceReader reader, String... ids) {
		this.reader = reader;
		for (String id : ids) {
			reader.addSentenceListener(this, id);
		}
	}

	/**
	 * Creates a new instance of AbstractProvider.
	 *
	 * @param reader Sentence reader to be used as data source
	 * @param ids Types of sentences to capture for creating provider events
	 */
	public AbstractProvider(SentenceReader reader, SentenceId... ids) {
		this.reader = reader;
		for (SentenceId id : ids) {
			reader.addSentenceListener(this, id);
		}
	}

	/**
	 * Inserts a listener to provider.
	 *
	 * @param listener Listener to add
	 */
	public void addListener(ProviderListener<T> listener) {
		listeners.add(listener);
	}

	/**
	 * Creates a {@code ProviderEvent} of type {@code T}.
	 *
	 * @return Created event, or null if failed.
	 */
	protected abstract T createProviderEvent();

	/**
	 * Dispatch the TPV event to all listeners.
	 *
	 * @param event TPVUpdateEvent to dispatch
	 */
	private void fireProviderEvent(T event) {
		for (ProviderListener<T> listener : listeners) {
			listener.providerUpdate(event);
		}
	}

	/**
	 * Returns the collected sentences.
	 *
	 * @return List of sentences.
	 */
	protected final List<Sentence> getSentences() {
		List<Sentence> s = new ArrayList<Sentence>();
		for (SentenceEvent e : events) {
			s.add(e.getSentence());
		}
		return s;
	}

	/**
	 * Tells if the provider has captured all the specified sentences.
	 *
	 * @param id Sentence type IDs to look for.
	 * @return True if all specified IDs match the captured sentences.
	 */
	protected final boolean hasAll(String... id) {
		for (String s : id) {
			if (!hasOne(s)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tells if the provider has captured at least one of the specified
	 * sentences.
	 *
	 * @param id Sentence type IDs to look for, in prioritized order.
	 * @return True if any of the specified IDs matches the type of at least one
	 *         captured sentences.
	 */
	protected final boolean hasOne(String... id) {
		List<String> ids = Arrays.asList(id);
		for (Sentence s : getSentences()) {
			if (ids.contains(s.getSentenceId())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tells if provider has captured the required sentences for creating new
	 * ProviderEvent.
	 *
	 * @return true if ready to create ProviderEvent, otherwise false.
	 */
	protected abstract boolean isReady();

	/**
	 * Tells if the captured sentence events contain valid data to be dispatched
	 * to ProviderListeners.
	 *
	 * @return true if valid, otherwise false.
	 */
	protected abstract boolean isValid();

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.event.SentenceListener#readingPaused()
	 */
	public void readingPaused() {
		// nothing
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.event.SentenceListener#readingStarted()
	 */
	public void readingStarted() {
		reset();
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.event.SentenceListener#readingStopped()
	 */
	public void readingStopped() {
		reset();
		reader.removeSentenceListener(this);
	}

	/**
	 * Removes the specified listener from provider.
	 *
	 * @param listener Listener to remove
	 */
	public void removeListener(ProviderListener<T> listener) {
		listeners.remove(listener);
	}

	/**
	 * Clears the list of collected events.
	 */
	private void reset() {
		events.clear();
	}

	/**
	 * Expunge collected events that are older than {@code timeout} to prevent
	 * the {@code events} list growing when sentences are being delivered
	 * without valid data (e.g. during device warm-up or offline state).
	 */
	private void expunge() {
		long now = System.currentTimeMillis();
		List<SentenceEvent> expired = this.events.stream()
			.filter(event -> now - event.getTimeStamp() > this.timeout)
			.collect(Collectors.toList());
		this.events.removeAll(expired);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.marineapi.nmea.event.SentenceListener#sentenceRead(
	 * net.sf.marineapi.nmea.event.SentenceEvent)
	 */
	public void sentenceRead(SentenceEvent event) {
		events.add(event);
		if (isReady()) {
			if (validate()) {
				T pEvent = createProviderEvent();
				fireProviderEvent(pEvent);
			}
			reset();
		} else {
			expunge();
		}
	}

	/**
	 * <p>Sets the timeout for receiving a burst of sentences. The default value
	 * is 1000 ms as per default update rate of NMEA 0183 (1/s). However, the
	 * length of data bursts may vary depending on the device. If the timeout is
	 * exceeded before receiving all needed sentences, the sentences are
	 * discarded and waiting period for next burst of data is started. Use this
	 * method to increase the timeout if the bursts are constantly being
	 * discarded and thus no events are dispatched by the provider.</p>
	 *
	 * @param millis Timeout to set, in milliseconds.
	 * @see #DEFAULT_TIMEOUT
	 */
	public void setTimeout(int millis) {
		if (millis < 1) {
			throw new IllegalArgumentException("Timeout value must be > 0");
		}
		this.timeout = millis;
	}

	/**
	 * Validates the collected sentences by checking the ages of each sentence
	 * and then by calling {@link #isValid()}. If extending implementation has
	 * no validation criteria, it should return always {@code true}.
	 *
	 * @return true if valid, otherwise false
	 */
	private boolean validate() {
		long now = System.currentTimeMillis();
		for (SentenceEvent se : events) {
			long age = now - se.getTimeStamp();
			if (age > this.timeout) {
				return false;
			}
		}
		return isValid();
	}
}
