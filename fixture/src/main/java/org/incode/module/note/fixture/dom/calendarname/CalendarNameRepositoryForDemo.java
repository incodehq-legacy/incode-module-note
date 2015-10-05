package org.incode.module.note.fixture.dom.calendarname;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.note.api.calendarname.CalendarNameRepository;
import org.incode.module.note.api.notable.Notable;
import org.incode.module.note.fixture.dom.notedemoobject.NoteDemoObject;

@DomainService(
    nature = NatureOfService.DOMAIN
)
public class CalendarNameRepositoryForDemo implements CalendarNameRepository {

    private final Map<Class<?>, List<String>> namesByClass = Maps.newHashMap();

    public CalendarNameRepositoryForDemo() {
        setCalendarNames(NoteDemoObject.class, "BLUE", "GREEN", "RED");
    }

    @Programmatic
    public void setCalendarNames(final Class<?> cls, final String... names) {
        namesByClass.put(cls, Lists.newArrayList(names));
    }

    @Override
    public Collection<String> calendarNamesFor(final Notable notable) {
        return namesByClass.get(notable.getClass());
    }
}
