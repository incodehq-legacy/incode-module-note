/*
 *  Copyright 2014~2015 Dan Haywood
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
package org.incode.module.note.integtests.note;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Iterables;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.incode.module.note.api.notable.Notable;
import org.incode.module.note.dom.note.Note;
import org.incode.module.note.dom.note.NoteContributionsOnNotable;
import org.incode.module.note.dom.note.NoteRepository;
import org.incode.module.note.fixture.dom.calendarname.CalendarNameRepositoryForDemo;
import org.incode.module.note.fixture.dom.notedemoobject.NoteDemoObject;
import org.incode.module.note.fixture.dom.notedemoobject.NoteDemoObjectMenu;
import org.incode.module.note.fixture.scripts.teardown.NoteDemoObjectsTearDownFixture;
import org.incode.module.note.integtests.NoteModuleIntegTest;

import static org.assertj.core.api.Assertions.assertThat;

public class NoteRepositoryIntegTest extends NoteModuleIntegTest {

    @Inject
    CalendarNameRepositoryForDemo calendarNameRepository;

    @Inject
    NoteDemoObjectMenu noteDemoObjectMenu;

    @Inject
    NoteContributionsOnNotable noteContributionsOnNotable;

    @Inject
    NoteRepository noteRepository;

    Notable notable1;
    Notable notable2;

    @Before
    public void setUpData() throws Exception {
        fixtureScripts.runFixtureScript(new NoteDemoObjectsTearDownFixture(), null);

        notable1 = wrap(noteDemoObjectMenu).create("Foo");
        notable2 = wrap(noteDemoObjectMenu).create("Bar");

        calendarNameRepository.setCalendarNames(NoteDemoObject.class, "BLUE", "GREEN", "RED");
    }

    public static class FindByNotableIntegTest extends NoteRepositoryIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            wrap(noteContributionsOnNotable).addNote(notable1, "note A", fakeData.jodaLocalDates().any(), "GREEN");
            wrap(noteContributionsOnNotable).addNote(notable1, "note B", fakeData.jodaLocalDates().any(), "BLUE");

            wrap(noteContributionsOnNotable).addNote(notable2, "note C", fakeData.jodaLocalDates().any(), "GREEN");
            wrap(noteContributionsOnNotable).addNote(notable2, "note D", fakeData.jodaLocalDates().any(), "BLUE");

            final List<Note> notable2Notes = wrap(noteContributionsOnNotable).notes(notable2);

            // when
            final List<Note> noteList = noteRepository.findByNotable(notable2);

            // then
            assertThat(noteList).hasSize(2);
            for (Note note : noteList) {
                assertThat(note.getNotable()).isEqualTo(notable2);
                assertThat(notable2Notes).contains(note);
            }
        }
    }

    public static class FindByNotableAndCalendarNameIntegTest extends NoteRepositoryIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            wrap(noteContributionsOnNotable).addNote(notable1, "note A", fakeData.jodaLocalDates().any(), "BLUE");
            wrap(noteContributionsOnNotable).addNote(notable1, "note B", fakeData.jodaLocalDates().any(), "GREEN");
            wrap(noteContributionsOnNotable).addNote(notable1, "note C", fakeData.jodaLocalDates().any(), "RED");

            wrap(noteContributionsOnNotable).addNote(notable2, "note D", fakeData.jodaLocalDates().any(), "GREEN");

            final List<Note> notable1Notes = wrap(noteContributionsOnNotable).notes(notable1);
            final List<Note> notable2Notes = wrap(noteContributionsOnNotable).notes(notable2);

            // when
            final Note note1 = noteRepository.findByNotableAndCalendarName(notable1, "GREEN");

            // then
            assertThat(note1).isNotNull();
            assertThat(note1.getNotable()).isEqualTo(notable1);
            assertThat(notable1Notes).contains(note1);

            // when
            final Note note2 = noteRepository.findByNotableAndCalendarName(notable2, "GREEN");

            // then
            assertThat(note2).isNotNull();
            assertThat(note2.getNotable()).isEqualTo(notable2);
            assertThat(notable2Notes).contains(note2);
        }
    }

    public static class FindByNotableInDateRangeIntegTest extends NoteRepositoryIntegTest {

        private LocalDate someDate;

        private List<Note> notable1Notes;
        private List<Note> notable2Notes;

        @Before
        public void setUp() throws Exception {

            // given
            someDate = fakeData.jodaLocalDates().any();

            wrap(noteContributionsOnNotable).addNote(notable1, "note A", someDate, "BLUE");
            wrap(noteContributionsOnNotable).addNote(notable1, "note B", someDate.plusDays(2), "GREEN");
            wrap(noteContributionsOnNotable).addNote(notable1, "note C", someDate.plusDays(-2), "RED");

            wrap(noteContributionsOnNotable).addNote(notable2, "note D", someDate, "GREEN");

            notable1Notes = wrap(noteContributionsOnNotable).notes(notable1);
            notable2Notes = wrap(noteContributionsOnNotable).notes(notable2);
        }

        @Test
        public void single() throws Exception {

            // when
            final List<Note> notes = asList(noteRepository.findByNotableInDateRange(notable1, someDate, someDate));
            final Note noteA = Iterables.find(notable1Notes, x -> x.getNotes().equals("note A"));

            // then
            assertThat(notes).hasSize(1);
            assertThat(notes.get(0)).isSameAs(noteA);
            assertThat(notes.get(0).getNotable()).isEqualTo(notable1);
        }

        @Test
        public void multiple() throws Exception {

            // when
            final List<Note> notes = asList(noteRepository.findByNotableInDateRange(notable1, someDate, someDate.plusDays(2)));
            Note noteA = Iterables.find(notable1Notes, x -> x.getNotes().equals("note A"));
            Note noteB = Iterables.find(notable1Notes, x -> x.getNotes().equals("note B"));

            // then
            assertThat(notes).hasSize(2);
            assertThat(notes.get(0)).isSameAs(noteA);
            assertThat(notes.get(0).getNotable()).isEqualTo(notable1);
            assertThat(notes.get(1)).isSameAs(noteB);
            assertThat(notes.get(1).getNotable()).isEqualTo(notable1);
        }
    }

    public static class FindInDateRangeIntegTest extends NoteRepositoryIntegTest {

        private LocalDate someDate;

        private List<Note> notable1Notes;
        private List<Note> notable2Notes;

        @Before
        public void setUp() throws Exception {

            // given
            someDate = fakeData.jodaLocalDates().any();

            wrap(noteContributionsOnNotable).addNote(notable1, "note A", someDate, "BLUE");
            wrap(noteContributionsOnNotable).addNote(notable1, "note B", someDate.plusDays(2), "GREEN");
            wrap(noteContributionsOnNotable).addNote(notable1, "note C", someDate.plusDays(-2), "RED");

            wrap(noteContributionsOnNotable).addNote(notable2, "note D", someDate, "GREEN");

            notable1Notes = wrap(noteContributionsOnNotable).notes(notable1);
            notable2Notes = wrap(noteContributionsOnNotable).notes(notable2);
        }

        @Test
        public void single() throws Exception {

            // when
            final List<Note> notes = asList(noteRepository.findInDateRange(someDate.plusDays(1), someDate.plusDays(2)));
            Note noteB = Iterables.find(notable1Notes, x -> x.getNotes().equals("note B"));

            // then
            assertThat(notes).hasSize(1);
            assertThat(notes.get(0)).isSameAs(noteB);
            assertThat(notes.get(0).getNotable()).isEqualTo(notable1);
        }

        @Test
        public void multiple() throws Exception {

            // when
            final List<Note> notes = asList(noteRepository.findInDateRange(someDate.plusDays(-1), someDate.plusDays(+1)));
            Note noteA = Iterables.find(notable1Notes, x -> x.getNotes().equals("note A"));
            Note noteD = Iterables.find(notable2Notes, x -> x.getNotes().equals("note D"));

            // then
            assertThat(notes).hasSize(2);
            assertThat(notes.get(0)).isSameAs(noteA);
            assertThat(notes.get(0).getNotable()).isEqualTo(notable1);
            assertThat(notes.get(1)).isSameAs(noteD);
            assertThat(notes.get(1).getNotable()).isEqualTo(notable2);
        }

    }

}