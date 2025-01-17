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

#include <stdio.h>
#include <string.h>
#include "jvmti.h"
#include "jvmti_common.h"

extern "C" {

static jvmtiEnv *jvmti_env = NULL;

jint Agent_OnLoad(JavaVM *jvm, char *options, void *reserved) {
    jint res = jvm->GetEnv((void **) &jvmti_env, JVMTI_VERSION_1_1);
    if (res != JNI_OK || jvmti_env == NULL) {
        printf("Wrong test_passed of a valid call to GetEnv!\n");
        return JNI_ERR;
    }

    return JNI_OK;
}

JNIEXPORT jboolean JNICALL
Java_GetAllThreadsNullTest_check(JNIEnv *env, jclass cls) {
    jvmtiError err;
    jint threadsCountPtr;
    jthread *threadsPtr;
    jboolean test_passed = JNI_TRUE;

    if (jvmti_env == NULL) {
        printf("JVMTI client was not properly loaded!\n");
        return JNI_FALSE;
    }

    printf(">>> (threadsCountPtr) null pointer check ...\n");

    err = jvmti_env->GetAllThreads(NULL, &threadsPtr);
    if (err != JVMTI_ERROR_NULL_POINTER) {
        printf("(threadsCountPtr) error expected: JVMTI_ERROR_NULL_POINTER,\n");
        printf("           got: %s (%d)\n", TranslateError(err), err);
        test_passed = JNI_FALSE;
    }

    printf(">>> (threadsPtr) null pointer check ...\n");

    err = jvmti_env->GetAllThreads(&threadsCountPtr, NULL);
    if (err != JVMTI_ERROR_NULL_POINTER) {
        printf("(threadsPtr) error expected: JVMTI_ERROR_NULL_POINTER,\n");
        printf("           got: %s (%d)\n", TranslateError(err), err);
        test_passed = JNI_FALSE;
    }

    err = jvmti_env->GetAllThreads(NULL, NULL);
    if (err != JVMTI_ERROR_NULL_POINTER) {
      printf("(threadsPtr) error expected: JVMTI_ERROR_NULL_POINTER,\n");
      printf("           got: %s (%d)\n", TranslateError(err), err);
      test_passed = JNI_FALSE;
    }

    printf(">>> done\n");

    return test_passed;
}

}
