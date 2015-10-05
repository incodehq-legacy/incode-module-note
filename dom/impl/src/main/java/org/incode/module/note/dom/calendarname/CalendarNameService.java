/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.incode.module.note.dom.calendarname;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.note.api.calendarname.CalendarNameRepository;
import org.incode.module.note.api.notable.Notable;
import org.incode.module.note.dom.note.Note;

/**
 * Simple wrapper around {@link CalendarNameRepository}, that always returns a non-null list (including
 * the {@link #DEFAULT_CALENDAR_NAME default name} if necessary).
 */
@DomainService(
        nature = NatureOfService.DOMAIN
)
public class CalendarNameService {

    public static final String DEFAULT_CALENDAR_NAME = "(default)";

    /**
     * Return the list of objects to act as calendars for the {@link Note}s to attach to the specified {@link Notable},
     * as per {@link CalendarNameRepository}, or a default name otherwise.
     *
     * <p>
     *     May return null if there are none (in which case a default name will be used).
     * </p>
     */
    @Programmatic
    public Collection<String> calendarNamesFor(final Notable notable) {
        final Collection<String> calendarNames = calendarNameRepository.calendarNamesFor(notable);
        return calendarNames != null? calendarNames: Collections.singleton(DEFAULT_CALENDAR_NAME);
    }

    @Inject
    CalendarNameRepository calendarNameRepository;

}
