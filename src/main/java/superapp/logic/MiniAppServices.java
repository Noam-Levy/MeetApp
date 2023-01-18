package superapp.logic;

import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.user.UserIdBoundary;
import superapp.data.SuperAppObjectEntity;
import superapp.data.UserPK;
import superapp.util.wrappers.SuperAppObjectIdWrapper;

public interface MiniAppServices {
   public void handleObjectByType(SuperAppObjectBoundary object);
   public Object runCommand(String miniapp, SuperAppObjectIdWrapper targetObject,
                          UserIdBoundary invokedBy, String commandCase);
   public void checkValidBinding(SuperAppObjectEntity parent, SuperAppObjectEntity child, UserPK userId);

}
