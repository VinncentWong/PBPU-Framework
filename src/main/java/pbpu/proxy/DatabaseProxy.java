package pbpu.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import pbpu.database.CoreDatabase;

// Proxy pattern is used to apply transactional operation!
public class DatabaseProxy<T> implements InvocationHandler {

    private final CoreDatabase<T> target;

    public DatabaseProxy(CoreDatabase<T> target){
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.printf("Starting invocation on method %s\n", method.getName());
        System.out.println("Begin Operation");
        try{
            var result = method.invoke(target, args);
            System.out.printf("Invocation on method %s finish\n", method.getName());
            return result;
        } catch(Exception ex){
            System.out.printf("Error occurred with message %s\n", ex.getMessage());
            ex.printStackTrace();
            // if u need the real implementation to do rollback, 
            // add the implementation on below
            System.out.println("Doing rollback!");
        }

        return null;

    }

}
