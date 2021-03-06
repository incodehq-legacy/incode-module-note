/*
 *
 *  Copyright 2015 incode.org
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

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.note.dom.NoteModule;

public abstract class T_notes<T> {

    //region > constructor
    private final T notable;
    public T_notes(final T notable) {
        this.notable = notable;
    }

    public T getNotable() {
        return notable;
    }
    //endregion

    //region > $$
    public static class DomainEvent extends NoteModule.ActionDomainEvent<T_notes> { } { }
    @Action(
            domainEvent = DomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            contributed = Contributed.AS_ASSOCIATION
    )
    @CollectionLayout(
            defaultView = "table",
            named = "Notes" // might be required, was a regression in 1.11.x
    )
    public List<Note> $$() {
        return noteRepository.findByNotable(this.notable);
    }

    //endregion

    //region  > (injected)
    @Inject
    NoteRepository noteRepository;
    //endregion


}
