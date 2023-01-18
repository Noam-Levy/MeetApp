package superapp.boundaries.split;

import superapp.boundaries.user.UserIdBoundary;

public class SplitDebtBoundary {

    private UserIdBoundary userId;
    private float debt;

    public SplitDebtBoundary() {}

    public SplitDebtBoundary(UserIdBoundary userId, float debt) {
        this.userId = userId;
        this.debt = debt;
    }

    public UserIdBoundary getUserId() { return userId; }

    public void setUserId(UserIdBoundary userId) { this.userId = userId; }

    public float getDebt() { return debt; }

    public void setDebt(float debt) { this.debt = debt; }

    @Override
    public String toString() {
        return "SplitDebtBoundary{" +
                "userId=" + userId +
                ", debt=" + debt +
                '}';
    }
}
