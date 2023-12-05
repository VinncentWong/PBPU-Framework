package pbpu.injector;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import pbpu.annotation.Crud;
import pbpu.annotation.CsvDatabase;
import pbpu.annotation.Entity;
import pbpu.annotation.Inject;
import pbpu.annotation.JsonDatabase;
import pbpu.context.ApplicationContext;
import pbpu.database.CoreDatabase;
import pbpu.database.CsvDatabaseImplementation;
import pbpu.database.JsonDatabaseImplementation;
import pbpu.proxy.DatabaseProxy;

@Builder
@Slf4j
public class DatabaseInjector {

    private List<Class<?>> databases;

    /***
     * This method is used to create Json Proxy Instance
     * @param <T>   The type of entity to be inserted into proxy
     * @param clazz Class type of the entity to be inserted into proxy
     * @return the instance of the proxy
     */
    @SuppressWarnings("unchecked")
    private <T> CoreDatabase<T> createJsonProxyInstance(Class<?> type, String clazz) {
        System.out.println("proxy entity name = " + clazz);
        var database = new JsonDatabaseImplementation<>(clazz, type);
        return (CoreDatabase<T>) Proxy.newProxyInstance(
                database.getClass().getClassLoader(),
                new Class[] { CoreDatabase.class },
                new DatabaseProxy<>(database));
    }

    /***
     * This method is used to create Csv Proxy Instance
     * @param <T>   The type of entity to be inserted into proxy
     * @param clazz Class type of the entity to be inserted into proxy
     * @return the instance of the proxy
     */
    @SuppressWarnings("unchecked")
    private <T> CoreDatabase<T> createCsvProxyInstance(Class<?> type,String clazz) {
        System.out.println("proxy entity name = " + clazz);
        var database = new CsvDatabaseImplementation<>(clazz, type);
        return (CoreDatabase<T>) Proxy.newProxyInstance(
                database.getClass().getClassLoader(),
                new Class[] { CoreDatabase.class },
                new DatabaseProxy<>(database));
    }
    
    public void injectProxy(){
        databases.forEach((c) -> {
            if (c.isAnnotationPresent(Crud.class)) {

                var instance = getInstance(c);

                for (var field : c.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Inject.class)) {

                        System.out.println("inject class exist");

                        // determine the entity type to operate
                        var entityType = ((ParameterizedType)field.getGenericType())
                        .getActualTypeArguments()[0];

                        var inject = field.getAnnotation(Inject.class);
                        var repositoryType = inject.type();

                        System.out.println("metadata repository type = " +repositoryType);

                        // checking the value assigned into @JsonDatabase
                        if (repositoryType == JsonDatabase.class) {
                            System.out.println("Instance JsonDatabase");
                            // then inject the object proxy that implements JsonDatabase
                            try {
                                // make it public on runtime, be careful and just make sure to set it to private
                                // again
                                field.setAccessible(true);

                                // get the generic type of the Field
                                // for example CoreDatabase<String> means that genericType will be String
                                var genericType = ((Class<?>)entityType);

                                var name = genericType.getSimpleName();

                                if(genericType.isAnnotationPresent(Entity.class)){
                                    var entity = genericType.getAnnotation(Entity.class);
                                    if(!(entity.name().isBlank() || entity.name().isEmpty())){
                                        name = entity.name();
                                    }
                                }

                                var proxy = createJsonProxyInstance(genericType, name);

                                field.set(instance, proxy);

                                ApplicationContext
                                    .getInstance()
                                    .add((Class<?>)entityType, Map.of(JsonDatabase.class, instance));

                                field.setAccessible(false);
                            } catch (IllegalArgumentException ex) {
                                System.out.printf("Error when trying to access field %s with err %s\n", field.getName(),
                                        ex.getMessage());
                                return;
                            } catch (IllegalAccessException ex) {
                                System.out.printf("Error when trying to set instance %s with err %s\n", instance.getClass(),
                                        ex.getMessage());
                                return;
                            }
                        } else if(repositoryType == CsvDatabase.class){
                            System.out.println("Instance CsvDatabase");
                            try {
                                // make it public on runtime, be careful and just make sure to set it to private
                                // again
                                field.setAccessible(true);

                                var genericType = ((Class<?>)entityType);

                                var name = genericType.getSimpleName();

                                if(genericType.isAnnotationPresent(Entity.class)){
                                    var entity = genericType.getAnnotation(Entity.class);
                                    if(!(entity.name().isBlank() || entity.name().isEmpty())){
                                        name = entity.name();
                                    }
                                }

                                var proxy = createCsvProxyInstance(genericType,name);

                                field.set(instance, proxy);

                                ApplicationContext
                                    .getInstance()
                                    .add((Class<?>)entityType, Map.of(CsvDatabase.class, instance));

                                field.setAccessible(false);
                            } catch (IllegalArgumentException ex) {
                                System.out.printf("Error when trying to access field %s with err %s\n", field.getName(),
                                        ex.getMessage());
                                return;
                            } catch (IllegalAccessException ex) {
                                System.out.printf("Error when trying to set instance %s with err %s\n", instance.getClass(),
                                        ex.getMessage());
                                return;
                            }
                        } else {
                            log.error("You can only use type of JsonDatabase or CsvDatabase");
                        }
                    }
                }
            } else {
                System.out.println("Please use @Crud on your class");
            }
        });
    }

    @SneakyThrows
    private Object getInstance(Class<?> clazz){
        return clazz.getDeclaredConstructor().newInstance();
    }
}
