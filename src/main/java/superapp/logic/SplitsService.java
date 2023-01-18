package superapp.logic;

import superapp.boundaries.split.SplitDebtBoundary;
import superapp.boundaries.user.UserIdBoundary;
import superapp.data.SuperAppObjectEntity;
import superapp.data.SuperappObjectPK;

public interface SplitsService {
 public void settleGroupDebts(SuperAppObjectEntity group);
 public SplitDebtBoundary showDebt(SuperappObjectPK group, UserIdBoundary user);
 public Object showAllDebts(SuperAppObjectEntity group);

}