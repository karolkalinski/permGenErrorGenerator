package com.kkalinski;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class PermGenErrorGenerator {

    /*
    http://stackoverflow.com/questions/14257357/loading-a-class-twice-in-jvm-using-different-loaders
    http://www.devsumo.com/technotes/2013/10/java-monitoring-your-permgen-space/
     */
    PermGenReader permGenReader = new PermGenReader();

    List<Class<?>> loaded = new ArrayList<>();
    List<ClassLoader> loaders = new ArrayList<>();

    private void generateOutOfMemoryErrorPermGenSpace() throws MalformedURLException, URISyntaxException, ClassNotFoundException {
        URL jarUrl = findJarOutsideClassPath();
        for (;;){
            ClassLoader cl = createNewClassLoader(jarUrl);
            Class<?> compiledClass =  cl.loadClass("com.sun.mail.imap.IMAPMessage");
            loaded.add(compiledClass);
            loaders.add(cl);
            show();
        }
    }

    private URLClassLoader createNewClassLoader(URL jarUrl) {
        return new URLClassLoader(new URL[]{jarUrl});
    }

    private URL findJarOutsideClassPath() throws MalformedURLException {
        File jar = new File("src/resources/mail-1.4.3.jar");
        return jar.toURI().toURL();
    }

    void show() {
        System.out.println(permGenReader.toString());
    }

    public static void main(String[] args) throws Exception {
        PermGenErrorGenerator generator = new PermGenErrorGenerator();
        generator.checkLoadingClassFromClassPathUsingDifferentClassLoadersDoesNotUseHeap();
        generator.generateOutOfMemoryErrorPermGenSpace();
    }

    private void checkLoadingClassFromClassPathUsingDifferentClassLoadersDoesNotUseHeap() throws URISyntaxException, MalformedURLException, ClassNotFoundException {
        URL fooUrl = this.getClass().getResource("Foo.class").toURI().toURL();

        for (int i=0; i<10; i++) {
            loadFooClass(fooUrl);
            show();
        }
        long prevUsed = permGenReader.getUsage().getUsed();
        loadFooClass(fooUrl);
        long newUsed = permGenReader.getUsage().getUsed();
        System.out.println(newUsed + ":" + prevUsed);
        assert  newUsed == prevUsed;
    }

    private void loadFooClass(URL fooUrl) throws ClassNotFoundException {
        ClassLoader cl = createNewClassLoader(fooUrl);
        Class<?> compiledClass =  cl.loadClass("com.kkalinski.Foo");
        loaded.add(compiledClass);
        loaders.add(cl);
    }
}
