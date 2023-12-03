package pbpu.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import pbpu.database.CoreDatabase;
import pbpu.util.LineUtil;

// Proxy pattern is used to apply transactional operation!
public class DatabaseProxy<T> implements InvocationHandler {

    private final CoreDatabase<T> target;

    private final LineUtil lineUtil;

    public DatabaseProxy(CoreDatabase<T> target){
        this.target = target;
        lineUtil = LineUtil.getInstance();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        this.lineUtil.printLine();
        System.out.printf("Starting invocation on method %s\n", method.getName());
        System.out.println("Begin Operation");
        try{
            var result = method.invoke(target, args);
            this.lineUtil.printLine();
            System.out.printf("Invocation on method %s finish\n", method.getName());
            this.lineUtil.printLine();
            return result;
        } catch(Exception ex){
            this.lineUtil.printLine();
            System.out.printf("Error occurred with message %s\n", ex.getMessage());

            // if u need the real implementation to do rollback, 
            // add the implementation on below
            System.out.println("Doing rollback!");
            this.lineUtil.printLine();
        }

        return null;

    }

}
