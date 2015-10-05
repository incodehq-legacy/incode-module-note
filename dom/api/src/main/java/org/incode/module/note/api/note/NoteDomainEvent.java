package org.incode.module.note.api.note;

import org.joda.time.LocalDate;

import org.apache.isis.applib.Identifier;

import org.incode.module.note.api.NoteApiModule;
import org.incode.module.note.api.notable.Notable;

public final class NoteDomainEvent {

    private NoteDomainEvent(){}

    public static abstract class Property<S,T> extends NoteApiModule.PropertyDomainEvent<S, T> {
        public Property(final S source, final Identifier identifier, final T oldValue, final T newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    public static abstract class Collection<S,T> extends NoteApiModule.CollectionDomainEvent<S, T> {
        public Collection(
                final S source,
                final Identifier identifier,
                final Of of, final T value) {
            super(source, identifier, of, value);
        }
    }

    public static abstract class Action<S> extends NoteApiModule.ActionDomainEvent<S> {
        public Action(final S source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }
    }


    public static class NotableDomainEvent extends Property<NoteDomainEvent,Notable> {
        public NotableDomainEvent(
                final NoteDomainEvent source,
                final Identifier identifier,
                final Notable oldValue,
                final Notable newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    public static class NotesAbbreviatedDomainEvent extends Property<NoteDomainEvent,String> {
        public NotesAbbreviatedDomainEvent(
                final NoteDomainEvent source,
                final Identifier identifier,
                final String oldValue,
                final String newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    public static class NotesDomainEvent extends Property<NoteDomainEvent,String> {
        public NotesDomainEvent(
                final NoteDomainEvent source,
                final Identifier identifier,
                final String oldValue,
                final String newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    public static class DateDomainEvent extends Property<NoteDomainEvent,LocalDate> {
        public DateDomainEvent(
                final NoteDomainEvent source,
                final Identifier identifier,
                final LocalDate oldValue,
                final LocalDate newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    public static class CalendarNameDomainEvent extends Property<NoteDomainEvent,String> {
        public CalendarNameDomainEvent(
                final NoteDomainEvent source,
                final Identifier identifier,
                final String oldValue,
                final String newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

}
