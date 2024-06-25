package com.t2pellet.strawgolem.entity;

import java.util.Optional;

public interface StrawGolemOrderer {

    Optional<StrawGolem> getOrderedGolem();
    void setOrderedGolem(StrawGolem golem);
}
