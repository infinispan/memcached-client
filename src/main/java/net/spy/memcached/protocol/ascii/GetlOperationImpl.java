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

import net.spy.memcached.ops.GetlOperation;

import java.nio.charset.StandardCharsets;

/**
 * Implementation of the getl operation.
 */
class GetlOperationImpl extends BaseGetOpImpl implements GetlOperation {

  private static final String CMD = "getl";

  private final int exp;

  public GetlOperationImpl(String key, int exp, GetlOperation.Callback c) {
    super(CMD, c, key);
    this.exp = exp;
  }

  @Override
  public int getExpiration() {
    return exp;
  }

  @Override
  protected byte[] extraBytesAfter() {
    return (" " + exp).getBytes(StandardCharsets.US_ASCII);
  }
}
