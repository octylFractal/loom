/*
 * Copyright (c) 2003, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */


/*
 * @test
 *
 * @summary converted from VM Testbase nsk/jvmti/GetThreadInfo/thrinfo001.
 * VM Testbase keywords: [quick, jpda, jvmti, noras]
 * VM Testbase readme:
 * DESCRIPTION
 *     The test exercise JVMTI function GetThreadInfo.
 *     The test cases include:
 *     - user-defined and default thread name
 *     - main thread
 *     - user-defined and default thread group
 *     - norm, min and min+2 priorities
 *     - daemon and non-daemon threads
 * COMMENTS
 *     Fixed according to the 4387521 bug.
 *     Fixed according to the 4480280 bug.
 *     Ported from JVMDI.
 *
 * @library /test/lib
 * @run main/othervm/native -agentlib:thrinfo01 thrinfo01
 */


import java.io.PrintStream;

public class thrinfo01 {

    final static int JCK_STATUS_BASE = 95;

    static {
        try {
            System.loadLibrary("thrinfo01");
        } catch (UnsatisfiedLinkError ule) {
            System.err.println("Could not load thrinfo01 library");
            System.err.println("java.library.path:"
                + System.getProperty("java.library.path"));
            throw ule;
        }
    }

    native static void checkInfo(Thread thr, ThreadGroup thr_group, int ind);
    native static int getRes();

    public static void main(String args[]) {
        Thread.currentThread().setName("main");
        // produce JCK-like exit status.
        System.exit(run(args, System.out) + JCK_STATUS_BASE);
    }

    public static int run(String argv[], PrintStream ref) {
        Thread currThr = Thread.currentThread();
        checkInfo(currThr, currThr.getThreadGroup(), 0);

        ThreadGroup tg = new ThreadGroup("tg1");
        thrinfo01a t_a = new thrinfo01a(tg, "thread1");
        t_a.setPriority(Thread.MIN_PRIORITY + 2);
        t_a.setDaemon(true);
        checkInfo(t_a, tg, 1);
        t_a.start();
        try {
            t_a.join();
        } catch (InterruptedException e) {}
        checkInfo(t_a, t_a.getThreadGroup(), 1);

        thrinfo01b t_b = new thrinfo01b();
        t_b.setPriority(Thread.MIN_PRIORITY);
        t_b.setDaemon(true);
        checkInfo(t_b, t_b.getThreadGroup(), 2);
        t_b.start();
        try {
            t_b.join();
        } catch (InterruptedException e) {}
        checkInfo(t_b, t_b.getThreadGroup(), 2);

        Thread t_c = Thread.ofVirtual().name("vthread").unstarted(new thrinfo01c());
        checkInfo(t_c, t_c.getThreadGroup(), 3);
        t_c.start();
        try {
            t_c.join();
        } catch (InterruptedException e) {}
        checkInfo(t_c, t_c.getThreadGroup(), 3);

        return getRes();
    }
}

class thrinfo01a extends Thread {
    thrinfo01a(ThreadGroup tg, String name) {
        super(tg, name);
    }

    public void run() {
        Thread currThr = Thread.currentThread();
        thrinfo01.checkInfo(currThr, currThr.getThreadGroup(), 1);
    }
}

class thrinfo01b extends Thread {
    public void run() {
        Thread currThr = Thread.currentThread();
        thrinfo01.checkInfo(currThr, currThr.getThreadGroup(), 2);
    }
}

class thrinfo01c implements Runnable {
    public void run() {
        Thread currThr = Thread.currentThread();
        thrinfo01.checkInfo(currThr, currThr.getThreadGroup(), 3);
    }
}
