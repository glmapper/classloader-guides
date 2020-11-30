package com.glmapper.bridge.boot.customs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author: glmapper_2018@163.com 2020/11/27 3:20 下午
 * @since:
 **/
public class TestClassLoader extends ClassLoader {

    /**
     * 重写父类方法，返回一个Class对象
     * ClassLoader中对于这个方法的注释是:
     * This method should be overridden by class loader implementations
     */
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class clazz = null;
        String classFilename = name + ".class";
        File classFile = new File(classFilename);

        if (classFile.exists()) {
            FileChannel fileChannel = null;
            try {
                fileChannel = new FileInputStream(classFile).getChannel();
                MappedByteBuffer mappedByteBuffer = fileChannel
                        .map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
                byte[] b = mappedByteBuffer.array();
                clazz = defineClass(name, b, 0, b.length);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileChannel != null){
                    try {
                        fileChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (clazz == null) {
            throw new ClassNotFoundException(name);
        }
        return clazz;
    }

    public static void main(String[] args) throws Exception {
        TestClassLoader myClassLoader = new TestClassLoader();
        Class clazz = myClassLoader.loadClass("com.glmapper.bridge.boot.customs.Hello");
        Method sayHello = clazz.getMethod("hello");
        sayHello.invoke(null, null);
    }
}

