package org.incode.module.note.dom.note;

import java.util.Locale;

import javax.inject.Inject;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Function;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.i18n.LocaleProvider;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.util.TitleBuffer;

import org.isisaddons.wicket.fullcalendar2.cpt.applib.CalendarEvent;
import org.isisaddons.wicket.fullcalendar2.cpt.applib.CalendarEventable;

import org.incode.module.note.api.NoteApiModule;
import org.incode.module.note.api.notable.Notable;
import org.incode.module.note.api.note.NoteDomainEvent;
import org.incode.module.note.dom.notablelink.NotableLink;
import org.incode.module.note.dom.notablelink.NotableLinkRepository;

/**
 * An event that has or is scheduled to occur at some point in time, pertaining
 * to an {@link Notable}.
 */
@javax.jdo.annotations.PersistenceCapable(
        schema = "incodeNote",
        table = "Note",
        identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findInDateRange", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.note.dom.impl.note.Note "
                        + "WHERE date >= :startDate "
                        + "   && date <= :endDate")
})
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "Note_date_idx",
                members = { "date" })
})
@DomainObject(
        editing = Editing.DISABLED
)
public class Note implements CalendarEventable, Comparable<Note> {

    //region > title
    public String title() {
        final TitleBuffer buf = new TitleBuffer();
        buf.append(container.titleOf(getNotable()));
        if(getDate() != null) {
            // final String dateStr = container.titleOf(getDate()); // broken in isis 1.9.0
            final Locale locale = localeProvider.getLocale();
            final String dateStr = DateTimeFormat.forStyle("M-").withLocale(locale).print(getDate());
            buf.append(" @").append(dateStr);
        }
        buf.append(": ").append(getNotesAbbreviated());
        return buf.toString();
    }

    //endregion

    //region > notable (property)

    /**
     * Polymorphic association to (any implementation of) {@link Notable}.
     */
    @Property(
            domainEvent = NoteDomainEvent.NotableDomainEvent.class,
            editing = Editing.DISABLED,
            hidden = Where.PARENTED_TABLES,
            notPersisted = true
    )
    public Notable getNotable() {
        final NotableLink link = getNotableLink();
        return link != null? link.getPolymorphicReference(): null;
    }

    @Programmatic
    public void setNotable(final Notable notable) {
        removeNotableLink();
        notableLinkRepository.createLink(this, notable);
    }

    private void removeNotableLink() {
        final NotableLink notableLink = getNotableLink();
        if(notableLink != null) {
            container.remove(notableLink);
        }
    }

    private NotableLink getNotableLink() {
        if (!container.isPersistent(this)) {
            return null;
        }
        return notableLinkRepository.findByNote(this);
    }
    //endregion

    //region > notesAbbreviated (property)

    public static int NOTES_ABBREVIATED_TO = 40;

    /**
     * Derived from {@link #getNotes()}, solely for use in title and in tables.
     */
    @javax.jdo.annotations.NotPersistent
    @Property(
            domainEvent = NoteDomainEvent.NotesAbbreviatedDomainEvent.class,
            hidden = Where.OBJECT_FORMS
    )
    @PropertyLayout(
            named = "Notes"
    )
    public String getNotesAbbreviated() {
        return trim(getNotes(), "...", NOTES_ABBREVIATED_TO);
    }

    static String trim(final String notes, final String ending, final int length) {
        if(notes == null || notes.length() <= length) {
            return notes;
        }
        return notes.substring(0, length-ending.length()) + ending ;
    }
    //endregion

    //region > notes (property)

    private String notes;

    /**
     * Hidden in tables, instead the derived {@link #getNotesAbbreviated()} is shown.
     */
    @javax.jdo.annotations.Column(allowsNull = "true", length = NoteApiModule.JdoColumnLength.NOTES)
    @Property(
            domainEvent = NoteDomainEvent.NotesDomainEvent.class,
            hidden = Where.ALL_TABLES
    )
    @PropertyLayout(
            multiLine = NoteApiModule.MultiLine.NOTES
    )
    public String getNotes() {
        return notes;
    }

    public void setNotes(final String description) {
        this.notes = description;
    }
    //endregion

    //region > date (property)

    private LocalDate date;

    @javax.jdo.annotations.Column(allowsNull = "true")
    @Property(
            domainEvent = NoteDomainEvent.DateDomainEvent.class
    )
    public LocalDate getDate() {
        return date;
    }

    public void setDate(final LocalDate startDate) {
        this.date = startDate;
    }

    //endregion

    //region > calendarName (property)

    private String calendarName;

    /**
     * The name of the &quot;calendar&quot; (if any) to which this note belongs.
     * 
     * <p>
     * The &quot;calendar&quot; is a string identifier that indicates the nature
     * of a note.  These are expected to be uniquely identifiable for all and
     * any notes that might be created. They therefore typically
     * include information relating to the type/class of the note's
     * {@link #getNotable() subject}.
     * 
     * <p>
     * For example, a note on a lease's
     * <tt>FixedBreakOption</tt> has three dates: the <i>break date</i>, the
     * <i>exercise date</i> and the <i>reminder date</i>. These therefore
     * correspond to three different calendar names, respectively <i>Fixed
     * break</i>, <i>Fixed break exercise</i> and <i>Fixed break exercise
     * reminder</i>.
     */
    @javax.jdo.annotations.Column(allowsNull = "true", length = NoteApiModule.JdoColumnLength.CALENDAR_NAME)
    @Property(
            domainEvent = NoteDomainEvent.CalendarNameDomainEvent.class,
            editing = Editing.DISABLED
    )
    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(final String calendarName) {
        this.calendarName = calendarName;
    }


    //endregion

    //region > CalendarEventable impl
    @Programmatic
    public CalendarEvent toCalendarEvent() {
        if(getDate() == null || getCalendarName() == null) {
            return null;
        }
        final String eventTitle = container.titleOf(getNotable()) + ": " + getNotesAbbreviated();
        return new CalendarEvent(getDate().toDateTimeAtStartOfDay(), getCalendarName(), eventTitle);
    }
    //endregion

    //region > Functions

    public final static class Functions {
        private Functions() {}
        public final static Function<Note, CalendarEvent> TO_CALENDAR_EVENT = input -> input.toCalendarEvent();
        public final static Function<Note, String> GET_CALENDAR_NAME = input -> input.getCalendarName();
    }
    //endregion

    //region > toString, compareTo

    @Override
    public String toString() {
        return ObjectContracts.toString(this, "date", "calendarName");
    }

    @Override
    public int compareTo(final Note other) {
        return ObjectContracts.compare(this, other, "date", "source", "calendarName");
    }

    //endregion

    //region > injected

    @Inject
    NotableLinkRepository notableLinkRepository;
    @Inject
    DomainObjectContainer container;

    @Inject
    LocaleProvider localeProvider;
    //endregion

}
