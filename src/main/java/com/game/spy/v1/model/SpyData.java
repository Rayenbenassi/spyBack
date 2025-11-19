package com.game.spy.v1.model;

import lombok.Data;

import java.util.List;

@Data
public class SpyData {

    private List<Long> spyIds;
    private Long spyId;
    private Boolean multiSpyMode;

    public boolean isMultiSpyMode() {
        return spyIds != null && !spyIds.isEmpty();
    }

    public List<Long> getAllSpyIds() {
        if (isMultiSpyMode()) {
            return spyIds;
        } else if (spyId != null) {
            return List.of(spyId);
        }
        return List.of();
    }


}
