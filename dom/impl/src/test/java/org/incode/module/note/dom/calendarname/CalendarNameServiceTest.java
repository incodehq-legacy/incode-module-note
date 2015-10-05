package org.incode.module.note.dom.calendarname;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.note.api.calendarname.CalendarNameRepository;
import org.incode.module.note.api.notable.Notable;

public class CalendarNameServiceTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    CalendarNameRepository mockCalendarNameRepository;

    CalendarNameService calendarNameService;

    @Before
    public void setUp() throws Exception {
        calendarNameService = new CalendarNameService();
        calendarNameService.calendarNameRepository = mockCalendarNameRepository;
    }

    public static class CalendarNamesForTest extends CalendarNameServiceTest {

        @Test
        public void when_repository_returns_values() throws Exception {

            // given
            final Notable notable = new Notable(){};
            final List<String> calendarNames = Lists.newArrayList("a", "b", "c");

            // expecting
            context.checking(new Expectations() {{
                oneOf(mockCalendarNameRepository).calendarNamesFor(notable);
                will(returnValue(calendarNames));
            }});

            // when
            final Collection<String> returnedCalendarNames = calendarNameService.calendarNamesFor(notable);

            // then
            Assertions.assertThat(returnedCalendarNames).isEqualTo(calendarNames);
        }


        @Test
        public void when_repository_returns_null() throws Exception {

            // given
            final Notable notable = new Notable(){};

            // expecting
            context.checking(new Expectations() {{
                oneOf(mockCalendarNameRepository).calendarNamesFor(notable);
                will(returnValue(null));
            }});

            // when
            final Collection<String> returnedCalendarNames = calendarNameService.calendarNamesFor(notable);

            // then
            Assertions.assertThat(returnedCalendarNames).hasSize(1);
            Assertions.assertThat(returnedCalendarNames).containsExactly(CalendarNameService.DEFAULT_CALENDAR_NAME);
        }

    }

}