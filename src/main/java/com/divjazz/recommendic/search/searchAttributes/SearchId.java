package com.divjazz.recommendic.search.searchAttributes;

import io.github.wimdeblauwe.jpearl.AbstractEntityId;

import java.util.UUID;

public class SearchId extends AbstractEntityId<UUID> {
    private SearchId(){}
    public SearchId(UUID id){super(id);}
}
