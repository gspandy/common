package com.porpoise.common.delta;

import org.junit.Before;
import org.junit.Test;

public class DeltaTest {
    class A {
        String name;
        B bee;
    }

    class B {
        int something;
        A aye;
    }

    private A left;
    private A right;

    @Before
    public void setup() {
        this.left = new A();
        this.left.name = "red";
        this.left.bee = new B();
        this.left.bee.something = 4;
        this.left.bee.aye = new A();
        this.left.bee.aye.name = "purple";

        this.right = new A();
        this.right.name = "green";
        this.right.bee = new B();
        this.right.bee.something = 5;
        this.right.bee.aye = this.right;
    }

    /**
     * 
     */
    @Test
    public void testSimpleDiff() {
        // final Delta delta = Delta.valueOf(this.left, this.right, diffLookup);
        // System.out.println(delta);
    }
}
