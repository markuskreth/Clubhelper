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
        assertEquals(25, sqr);

    }
}