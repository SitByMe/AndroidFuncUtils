package ptv.example.zoulinheng.androidutils.jtest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by zoulinheng on 2020/3/23.
 * desc:动态代理测试
 */
public class ProxyTest {
    public static void main(String[] args) {
        //1.创建目标对象
        RealSubject realSubject = new RealSubject();
        //2.创建调用处理器对象
        ProxyHandler proxyHandler = new ProxyHandler(realSubject);
        //3.动态生成代理对象
        Subject proxySubject = (Subject) Proxy.newProxyInstance(RealSubject.class.getClassLoader(), RealSubject.class.getInterfaces(), proxyHandler);
        //4.通过代理对象调用方法
        proxySubject.request();
    }

    /**
     * 主题接口
     */
    interface Subject {
        void request();
    }

    /**
     * 目标对象类
     */
    static class RealSubject implements Subject {
        @Override
        public void request() {
            System.out.println("====RealSubject Request====");
        }
    }

    /**
     * 代理类的调用处理器
     */
    static class ProxyHandler implements InvocationHandler {
        private Subject subject;

        ProxyHandler(Subject subject) {
            this.subject = subject;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //定义预处理的工作，当然你也可以根据 method 的不同进行不同的预处理工作
            System.out.println("====before====");
            Object result = method.invoke(subject, args);
            System.out.println("====after====");
            return result;
        }
    }
}
