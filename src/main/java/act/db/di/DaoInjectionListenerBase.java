package act.db.di;

import act.app.DbServiceManager;
import act.db.DB;
import act.di.DependencyInjectionListener;
import act.util.AnnotationUtil;
import act.util.DestroyableBase;
import org.osgl.$;
import org.osgl.logging.LogManager;
import org.osgl.logging.Logger;
import org.osgl.util.C;

import java.lang.reflect.Type;
import java.util.Map;

public abstract class DaoInjectionListenerBase extends DestroyableBase implements DependencyInjectionListener {

    protected Logger logger = LogManager.get(DaoInjectionListenerBase.class);

    // Map type parameter array to (Model type, service ID) pair
    private Map<Type[], $.T2<Class, String>> svcIdCache = C.newMap();

    @Override
    protected void releaseResources() {
        svcIdCache.clear();
    }

    protected $.T2<Class, String> resolve(Type[] typeParameters) {
        $.T2<Class, String> resolved = svcIdCache.get(typeParameters);
        if (null == resolved) {
            resolved = findSvcId(typeParameters);
            svcIdCache.put(typeParameters, resolved);
        }
        return resolved;
    }

    private $.T2<Class, String> findSvcId(Type[] typeParameters) {
        // the EbeanDao<Long, User> and MorphiaDao<User> case
        Type modelType = (typeParameters.length > 1) ? typeParameters[1] : typeParameters[0];
        DB db = AnnotationUtil.declaredAnnotation((Class)modelType, DB.class);
        return $.T2((Class) modelType, null == db ? DbServiceManager.DEFAULT : db.value());
    }

}