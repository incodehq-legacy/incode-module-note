package org.incode.module.note.dom.api.note;

import org.joda.time.LocalDate;

import org.apache.isis.applib.Identifier;

import org.incode.module.note.dom.api.NoteApiModule;
import org.incode.module.note.dom.api.notable.Notable;

public interface Note {

    int NOTES_ABBREVIATED_TO = 40;

    abstract class PropertyDomainEvent<S,T> extends NoteApiModule.PropertyDomainEvent<S, T> {
        public PropertyDomainEvent(final S source, final Identifier identifier, final T oldValue, final T newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    abstract class CollectionDomainEvent<S,T> extends NoteApiModule.CollectionDomainEvent<S, T> {
        public CollectionDomainEvent(
                final S source,
                final Identifier identifier,
                final Of of, final T value) {
            super(source, identifier, of, value);
        }
    }

    abstract class ActionDomainEvent<S> extends NoteApiModule.ActionDomainEvent<S> {
        public ActionDomainEvent(final S source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }
    }


    class NotableDomainEvent extends PropertyDomainEvent<Note,Notable> {
        public NotableDomainEvent(
                final Note source,
                final Identifier identifier,
                final Notable oldValue,
                final Notable newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    class NotesAbbreviatedDomainEvent extends PropertyDomainEvent<Note,String> {
        public NotesAbbreviatedDomainEvent(
                final Note source,
                final Identifier identifier,
                final String oldValue,
                final String newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    class NotesDomainEvent extends PropertyDomainEvent<Note,String> {
        public NotesDomainEvent(
                final Note source,
                final Identifier identifier,
                final String oldValue,
                final String newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    class DateDomainEvent extends PropertyDomainEvent<Note,LocalDate> {
        public DateDomainEvent(
                final Note source,
                final Identifier identifier,
                final LocalDate oldValue,
                final LocalDate newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    class CalendarNameDomainEvent extends PropertyDomainEvent<Note,String> {
        public CalendarNameDomainEvent(
                final Note source,
                final Identifier identifier,
                final String oldValue,
                final String newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

}
