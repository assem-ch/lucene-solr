/*

Copyright (c) 2001, Dr Martin Porter
Copyright (c) 2002, Richard Boulton
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
    * this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
    * notice, this list of conditions and the following disclaimer in the
    * documentation and/or other materials provided with the distribution.
    * Neither the name of the copyright holders nor the names of its contributors
    * may be used to endorse or promote products derived from this software
    * without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */
package org.tartarus.snowball;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;


public final class Among {
    public Among (String s, int substring_i, int result) {
        this.s = s.toCharArray();
        this.substring_i = substring_i;
	this.result = result;
	this.method = null;
    }

    public Among (String s, int substring_i, int result, String methodname,
		  Class<? extends SnowballProgram> programclass) {
        this.s = s.toCharArray();
        this.substring_i = substring_i;
	this.result = result;
	try {
	    this.method = programclass.getDeclaredMethod(methodname);
	} catch (NoSuchMethodException e) {
	    throw new RuntimeException(e);
	}
    }

    public final char[] s; /* search string */
    public final int substring_i; /* index to longest matching substring */
    public final int result; /* result of the lookup */
    public final MethodHandle method; /* method to use if substring matches */
};