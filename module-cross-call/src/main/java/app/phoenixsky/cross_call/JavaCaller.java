package app.phoenixsky.cross_call;

/**
 * @author: Rocky
 * @date: 2021/11/25
 * @Git phoenixsky
 */
public class JavaCaller {


    public static void main(String[] args) {

    }

    public static void testSuspend() {
        KotlinCaller.INSTANCE.testSuspendAsync();
    }



}
