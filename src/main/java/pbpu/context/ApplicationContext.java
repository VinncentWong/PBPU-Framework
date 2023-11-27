package pbpu.context;

import java.util.LinkedHashMap;
import java.util.Map;

/***
 * ApplicationContext is root class that is used for holder of object
 */
public class ApplicationContext {

    // type to operate, type of the database, instance
    private Map<Class<?>, Map<Class<?>, Object>> map;

    private static ApplicationContext INSTANCE;

    private ApplicationContext(){
        this.map = new LinkedHashMap<>();
    }

    public static ApplicationContext getInstance(){
        if(INSTANCE != null){
            return INSTANCE;
        } else {
            INSTANCE = new ApplicationContext();
            return INSTANCE;
        }
    }

    public void add(Class<?> clazz, Map<Class<?>, Object> obj){
        System.out.println("Put key " + clazz + " with value " + obj);
        this.map.put(clazz, obj);
    }

    public Map<Class<?>, Object> getComponent(Class<?> clazz) {
        return this.map.get(clazz);
    }
    
}
