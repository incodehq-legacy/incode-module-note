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
package org.incode.module.note.dom.impl.note;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;

import org.incode.module.note.dom.api.notable.Notable;
import org.incode.module.note.dom.impl.notablelink.NotableLink;
import org.incode.module.note.dom.impl.notablelink.NotableLinkRepository;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = NoteImpl.class
)
public class NoteRepository {

    //region > findByNotable (programmatic)
    @Programmatic
    public List<NoteImpl> findByNotable(final Notable notable) {
        final List<NotableLink> links = notableLinkRepository.findByNotable(notable);
        return Lists.newArrayList(
                Iterables.transform(links, NotableLink.Functions.note()));
    }
    //endregion

    //region > findByNotableAndCalendarName (programmatic)
    @Programmatic
    public NoteImpl findByNotableAndCalendarName(
            final Notable notable,
            final String calendarName) {
        final NotableLink link = notableLinkRepository
                .findByNotableAndCalendarName(notable, calendarName);
        return NotableLink.Functions.note().apply(link);
    }
    //endregion

    //region > findInDateRange (programmatic)
    @Programmatic
    public List<NoteImpl> findInDateRange(
            final LocalDate startDate,
            final LocalDate endDate) {
        return container.allMatches(
                new QueryDefault<>(
                        NoteImpl.class,
                        "findInDateRange",
                        "startDate", startDate,
                        "endDate", endDate));
    }
    //endregion

    //region > findByNotableInDateRange (programmatic)
    @Programmatic
    public Iterable<NoteImpl> findByNotableInDateRange(
            final Notable notable,
            final LocalDate startDate,
            final LocalDate endDate) {
        final List<NotableLink> link = notableLinkRepository
                .findByNotableInDateRange(notable, startDate, endDate);
        return Iterables.transform(link, NotableLink.Functions.note());
    }
    //endregion

    //region > add (programmatic)
    @Programmatic
    public NoteImpl add(
            final Notable notable,
            final String noteText,
            final LocalDate date,
            final String calendarName) {
        final NoteImpl note = container.newTransientInstance(NoteImpl.class);
        note.setDate(date);
        note.setCalendarName(calendarName);
        note.setNotable(notable);
        note.setNotes(noteText);
        container.persistIfNotAlready(note);

        return note;
    }
    //endregion

    //region > remove (programmatic)
    @Programmatic
    public void remove(NoteImpl note) {
        final NotableLink link = notableLinkRepository.findByNote(note);
        container.removeIfNotAlready(link);
        container.flush();
        container.removeIfNotAlready(note);
        container.flush();
    }
    //endregion

    //region > allNotes (programmatic)

    @Programmatic
    public List<NoteImpl> allNotes() {
        return container.allInstances(NoteImpl.class);
    }
    //endregion

    //region > injected
    @Inject
    NotableLinkRepository notableLinkRepository;
    @Inject
    DomainObjectContainer container;
    //endregion

}
