package com.inteliwallet.entity;

import lombok.Getter;

@Getter
public enum UserPlan {
    FREE("Free", 3, 0, 0.0),
    STANDARD("Standard", 6, 3, 5.0),
    PLUS("Plus", 10, 6, 20.0);

    private final String displayName;
    private final int maxGoals;
    private final int maxChallenges;
    private final double monthlyPrice;

    UserPlan(String displayName, int maxGoals, int maxChallenges, double monthlyPrice) {
        this.displayName = displayName;
        this.maxGoals = maxGoals;
        this.maxChallenges = maxChallenges;
        this.monthlyPrice = monthlyPrice;
    }

    public String getValue() {
        return this.name().toLowerCase();
    }

    public static UserPlan fromValue(String value) {
        for (UserPlan plan : values()) {
            if (plan.name().equalsIgnoreCase(value)) {
                return plan;
            }
        }
        throw new IllegalArgumentException("Invalid plan: " + value);
    }
}
