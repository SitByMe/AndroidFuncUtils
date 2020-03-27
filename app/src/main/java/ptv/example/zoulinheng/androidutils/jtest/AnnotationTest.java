package ptv.example.zoulinheng.androidutils.jtest;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Created by zoulinheng on 2020/3/23.
 * desc:自定义注解测试
 */
public class AnnotationTest {

    @MyAnnotation(name = "AnnotationTest", website = "main", revision = 1)
    public static void main(String[] args) throws ClassNotFoundException {
        System.out.println("I am main method");
        String clazz = AnnotationTest.class.getName();
        System.out.println("clazz = " + clazz);
        Method[] demoMethod = Objects.requireNonNull(AnnotationTest.class.getClassLoader()).loadClass(clazz).getMethods();

        for (Method method : demoMethod) {
            if (method.isAnnotationPresent(MyAnnotation.class)) {
                MyAnnotation annotationInfo = method.getAnnotation(MyAnnotation.class);
                System.out.println("method: " + method);
                System.out.println("name= " + Objects.requireNonNull(annotationInfo).name() +
                        " , website= " + annotationInfo.website()
                        + " , revision= " + annotationInfo.revision());
            }
        }
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    @MyAnnotation(name = "AnnotationTest", website = "demo", revision = 2)
    public void demo() {
        System.out.println("I am demo method");
    }

    @Documented
    @Target(ElementType.METHOD)
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @interface MyAnnotation {
        String name();

        String website() default "hello";

        int revision() default 1;
    }
}
