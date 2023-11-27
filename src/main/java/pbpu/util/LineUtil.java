package pbpu.util;

public class LineUtil {
    
    public static LineUtil getInstance(){
        return new LineUtil();
    }

    public void printLine(){
        System.out.println("====================");
    }
}
