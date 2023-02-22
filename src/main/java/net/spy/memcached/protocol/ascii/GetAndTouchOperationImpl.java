/**
 * Copyright (C) 2009-2011 Couchbase, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALING
 * IN THE SOFTWARE.
 */

package net.spy.memcached.protocol.ascii;

import net.spy.memcached.ops.GetAndTouchOperation;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Implementation of the get and touch operation.
 */
public class GetAndTouchOperationImpl extends BaseGetOpImpl implements
    GetAndTouchOperation {

  private static final String CMD = "gat";
  private final int exp;

  public GetAndTouchOperationImpl(String k, int e, GetAndTouchOperation.Callback cb) {
    this(Collections.singleton(k), e, cb);
  }

  public GetAndTouchOperationImpl(Collection<String> k, int e, GetAndTouchOperation.Callback cb) {
    super(CMD, cb, new HashSet<>(k));
    exp = e;
  }

  @Override
  public int getExpiration() {
    return exp;
  }

  @Override
  protected byte[] extraBytesBefore() {
    return (" " + exp).getBytes(StandardCharsets.US_ASCII);
  }
}
