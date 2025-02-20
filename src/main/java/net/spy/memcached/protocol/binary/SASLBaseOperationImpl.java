/**
 * Copyright (C) 2006-2009 Dustin Sallings
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

package net.spy.memcached.protocol.binary;

import net.spy.memcached.ops.OperationCallback;
import net.spy.memcached.ops.OperationState;
import net.spy.memcached.ops.OperationStatus;
import net.spy.memcached.ops.StatusCode;

import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;
import java.io.IOException;

/**
 * SASL authenticator.
 */
public abstract class SASLBaseOperationImpl extends OperationImpl {

  private static final byte SASL_CONTINUE = 0x21;

  protected final SaslClient sc;
  protected final byte[] ch;

  public SASLBaseOperationImpl(byte c, SaslClient sasl, byte[] challenge, OperationCallback cb) {
    super(c, generateOpaque(), cb);
    sc = sasl;
    ch = challenge;
  }

  @Override
  public void initialize() {
    try {
      byte[] response = buildResponse();
      String mechanism = sc.getMechanismName();

      getLogger().debug("Using SASL auth mechanism: " + mechanism);

      prepareBuffer(mechanism, 0, response);
    } catch (SaslException e) {
      // XXX: Probably something saner can be done here.
      throw new RuntimeException("Can't make SASL go.", e);
    }
  }

  protected abstract byte[] buildResponse() throws SaslException;

  @Override
  protected void decodePayload(byte[] pl) {
    getLogger().debug("Auth response:  %s", new String(pl));
  }

  @Override
  protected void finishedPayload(byte[] pl) throws IOException {
    if (errorCode == SASL_CONTINUE) {
      getCallback().receivedStatus(new OperationStatus(true, new String(pl),
        StatusCode.SUCCESS));
      transitionState(OperationState.COMPLETE);
    } else if (errorCode == 0) {
      getCallback().receivedStatus(new OperationStatus(true, "",
        StatusCode.SUCCESS));
      transitionState(OperationState.COMPLETE);
    } else {
      super.finishedPayload(pl);
    }
  }

  @Override
  public String toString() {
    return "SASL base operation";
  }
}
