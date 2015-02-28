package de.kreth.libtestjava;

import junit.framework.TestCase;

public class MyClassTest extends TestCase {

    private MyClass myClass;

    @Override
    protected void setUp() throws Exception {
        myClass = new MyClass();
    }

    public void testSqr() throws Exception {
        int sqr = myClass.sqr(5);
        System.out.println("Running " + getClass().getSimpleName() + " tests...");
        assertEquals(25, sqr);

    }

    public void test1() {
        assertTrue(true);
    }
    public void test2() {
        assertTrue(true);
    }
    public void test3() {
        assertTrue(true);
    }
}