/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.incode.module.note.dom.impl.notablelink;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import org.joda.time.LocalDate;

import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;

import org.isisaddons.module.poly.dom.PolymorphicAssociationLink;
import org.isisaddons.wicket.fullcalendar2.cpt.applib.CalendarEventable;
import org.isisaddons.wicket.fullcalendar2.cpt.applib.Calendarable;

import org.incode.module.note.dom.api.NoteApiModule;
import org.incode.module.note.dom.api.notable.Notable;
import org.incode.module.note.dom.impl.calendarname.CalendarNameService;
import org.incode.module.note.dom.impl.note.NoteImpl;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "incodeNote",
        table = "NotableLink"
)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByNote", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.note.dom.impl.notablelink.NotableLink "
                        + "WHERE note == :note"),
        @javax.jdo.annotations.Query(
                name = "findByNotable", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.note.dom.impl.notablelink.NotableLink "
                        + "WHERE notableObjectType == :notableObjectType "
                        + "   && notableIdentifier == :notableIdentifier "),
        @javax.jdo.annotations.Query(
                name = "findByNotableAndCalendarName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.note.dom.impl.notablelink.NotableLink "
                        + "WHERE notableObjectType == :notableObjectType "
                        + "   && notableIdentifier == :notableIdentifier "
                        + "   && calendarName == :calendarName"),
        @javax.jdo.annotations.Query(
                name = "findByNotableInDateRange", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.note.dom.impl.notablelink.NotableLink "
                        + "WHERE notableObjectType == :notableObjectType "
                        + "   && notableIdentifier == :notableIdentifier "
                        + "   && date >= :startDate "
                        + "   && date <= :endDate")
})
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "NotableLink_notable_IDX",
                members = { "notableObjectType", "notableIdentifier" })
})
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name="NotableLink_note_UNQ",
                members = {"note"})
})
@DomainObject(
        objectType = "note.NotableLink"
)
public abstract class NotableLink
        extends PolymorphicAssociationLink<NoteImpl, Notable, NotableLink>
        implements Calendarable {

    //region > event classes
    public static abstract class PropertyDomainEvent<T> extends NoteApiModule.PropertyDomainEvent<NotableLink, T> {
        public PropertyDomainEvent(final NotableLink source, final Identifier identifier) {
            super(source, identifier);
        }

        public PropertyDomainEvent(final NotableLink source, final Identifier identifier, final T oldValue, final T newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    public static abstract class CollectionDomainEvent<T> extends NoteApiModule.CollectionDomainEvent<NotableLink, T> {
        public CollectionDomainEvent(final NotableLink source, final Identifier identifier, final org.apache.isis.applib.services.eventbus.CollectionDomainEvent.Of of) {
            super(source, identifier, of);
        }

        public CollectionDomainEvent(final NotableLink source, final Identifier identifier, final org.apache.isis.applib.services.eventbus.CollectionDomainEvent.Of of, final T value) {
            super(source, identifier, of, value);
        }
    }

    public static abstract class ActionDomainEvent extends NoteApiModule.ActionDomainEvent<NotableLink> {
        public ActionDomainEvent(final NotableLink source, final Identifier identifier) {
            super(source, identifier);
        }

        public ActionDomainEvent(final NotableLink source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }

        public ActionDomainEvent(final NotableLink source, final Identifier identifier, final List<Object> arguments) {
            super(source, identifier, arguments);
        }
    }
    //endregion

    //region > instantiateEvent (poly pattern)
    public static class InstantiateEvent
            extends PolymorphicAssociationLink.InstantiateEvent<NoteImpl, Notable, NotableLink> {

        public InstantiateEvent(final Object source, final NoteImpl subject, final Notable owner) {
            super(NotableLink.class, source, subject, owner);
        }
    }
    //endregion

    //region > constructor
    public NotableLink() {
        super("{polymorphicReference} has {subject}");
    }
    //endregion

    //region > SubjectPolymorphicReferenceLink API

    /**
     * The subject of the pattern, which (perhaps confusingly in this instance) is actually the
     * {@link #getNote() event}.
     */
    @Override
    @Programmatic
    public NoteImpl getSubject() {
        return getNote();
    }

    @Override
    @Programmatic
    public void setSubject(final NoteImpl subject) {
        setNote(subject);
    }

    @Override
    @Programmatic
    public String getPolymorphicObjectType() {
        return getNotableObjectType();
    }

    @Override
    @Programmatic
    public void setPolymorphicObjectType(final String polymorphicObjectType) {
        setNotableObjectType(polymorphicObjectType);
    }

    @Override
    @Programmatic
    public String getPolymorphicIdentifier() {
        return getNotableIdentifier();
    }

    @Override
    @Programmatic
    public void setPolymorphicIdentifier(final String polymorphicIdentifier) {
        setNotableIdentifier(polymorphicIdentifier);
    }
    //endregion

    //region > note (property)

    public static class EventDomainEvent extends PropertyDomainEvent<NoteImpl> {
        public EventDomainEvent(final NotableLink source, final Identifier identifier) {
            super(source, identifier);
        }
        public EventDomainEvent(final NotableLink source, final Identifier identifier, final NoteImpl oldValue, final NoteImpl newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    private NoteImpl note;
    @javax.jdo.annotations.Column(allowsNull = "false", name = "eventId")
    @Property(
            domainEvent = EventDomainEvent.class,
            editing = Editing.DISABLED
    )
    public NoteImpl getNote() {
        return note;
    }

    public void setNote(final NoteImpl note) {
        this.note = note;
    }
    //endregion

    //region > notableObjectType (property)

    public static class SourceObjectTypeDomainEvent extends PropertyDomainEvent<String> {
        public SourceObjectTypeDomainEvent(final NotableLink source, final Identifier identifier, final String oldValue, final String newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    private String notableObjectType;

    @javax.jdo.annotations.Column(allowsNull = "false", length = 255)
    @Property(
            domainEvent = SourceObjectTypeDomainEvent.class,
            editing = Editing.DISABLED
    )
    public String getNotableObjectType() {
        return notableObjectType;
    }

    public void setNotableObjectType(final String notableObjectType) {
        this.notableObjectType = notableObjectType;
    }
    //endregion

    //region > notableIdentifier (property)

    public static class SourceIdentifierDomainEvent extends PropertyDomainEvent<String> {
        public SourceIdentifierDomainEvent(final NotableLink source, final Identifier identifier, final String oldValue, final String newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    private String notableIdentifier;

    @javax.jdo.annotations.Column(allowsNull = "false", length = 255)
    @Property(
            domainEvent = SourceIdentifierDomainEvent.class,
            editing = Editing.DISABLED
    )
    public String getNotableIdentifier() {
        return notableIdentifier;
    }

    public void setNotableIdentifier(final String notableIdentifier) {
        this.notableIdentifier = notableIdentifier;
    }
    //endregion

    //region > notable (derived property)
    /**
     * Simply returns the {@link #getPolymorphicReference()}.
     */
    @Programmatic
    public Notable getNotable() {
        return getPolymorphicReference();
    }
    //endregion
    
    //region > date (property)

    public static class DateDomainEvent extends PropertyDomainEvent<LocalDate> {
        public DateDomainEvent(final NotableLink source, final Identifier identifier, final LocalDate oldValue, final LocalDate newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    private LocalDate date;

    /**
     * Copy of the {@link #getNote() note}'s {@link NoteImpl#getDate() date}, to support querying.
     *
     * <p>
     *     If the {@link NoteImpl#getDate()} is changed, then this derived property is also updated.
     * </p>
     */
    @javax.jdo.annotations.Column(allowsNull = "true")
    @Property(
            domainEvent = DateDomainEvent.class
    )
    public LocalDate getDate() {
        return date;
    }

    public void setDate(final LocalDate startDate) {
        this.date = startDate;
    }

    //endregion

    //region > calendarName (property)

    public static class CalendarNameDomainEvent extends PropertyDomainEvent<String> {
        public CalendarNameDomainEvent(final NotableLink source, final Identifier identifier) {
            super(source, identifier);
        }
        public CalendarNameDomainEvent(final NotableLink source, final Identifier identifier, final String oldValue, final String newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    private String calendarName;

    /**
     * Copy of the {@link #getNote() note}'s {@link NoteImpl#getCalendarName() calendar name}, to support querying.
     *
     * <p>
     *     If the {@link NoteImpl#getCalendarName()} is changed, then this derived property is also updated.
     * </p>
     */
    @javax.jdo.annotations.Column(allowsNull = "true", length= NoteApiModule.JdoColumnLength.CALENDAR_NAME)
    @Property(
            domainEvent = CalendarNameDomainEvent.class,
            editing = Editing.DISABLED
    )
    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(final String calendarName) {
        this.calendarName = calendarName;
    }
    //endregion

    //region > eventSource impl

    /**
     * Can add to all calendars
     */
    @Programmatic
    @Override
    public Set<String> getCalendarNames() {
        return Sets.newTreeSet(calendarNameService.calendarNamesFor(getSubject()));
    }

    /**
     * to display in fullcalendar2
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Programmatic
    @Override
    public ImmutableMap<String, CalendarEventable> getCalendarEvents() {
        return ImmutableMap.<String, CalendarEventable>of(getCalendarName(), getNote());
    }

    //endregion

    //region > Functions
    public static class Functions {
        public static Function<NotableLink, NoteImpl> note() {
            return note(NoteImpl.class);
        }
        public static <T extends NoteImpl> Function<NotableLink, T> note(Class<T> cls) {
            return input -> input != null
                                ? (T)input.getNote()
                                : null;
        }
        public static Function<NotableLink, Notable> notable() {
            return notable(Notable.class);
        }

        public static <T extends Notable> Function<NotableLink, T> notable(final Class<T> cls) {
            return input -> input != null
                                ? (T)input.getNotable()
                                : null;
        }
    }
    //endregion

    //region  >  (injected)
    @Inject
    CalendarNameService calendarNameService;
    //endregion
    
}
