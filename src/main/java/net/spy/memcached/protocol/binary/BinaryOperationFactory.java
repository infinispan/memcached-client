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
 * 
 * 
 * Portions Copyright (C) 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * 
 * SPDX-License-Identifier: Apache-2.0
 */

package net.spy.memcached.protocol.binary;

import net.spy.memcached.ops.BaseOperationFactory;
import net.spy.memcached.ops.CASOperation;
import net.spy.memcached.ops.ConcatenationOperation;
import net.spy.memcached.ops.ConcatenationType;
import net.spy.memcached.ops.ConfigurationType;
import net.spy.memcached.ops.DeleteConfigOperation;
import net.spy.memcached.ops.DeleteOperation;
import net.spy.memcached.ops.FlushOperation;
import net.spy.memcached.ops.GetAndTouchOperation;
import net.spy.memcached.ops.GetConfigOperation;
import net.spy.memcached.ops.GetOperation;
import net.spy.memcached.ops.GetOperation.Callback;
import net.spy.memcached.ops.GetlOperation;
import net.spy.memcached.ops.GetsOperation;
import net.spy.memcached.ops.KeyedOperation;
import net.spy.memcached.ops.MultiGetOperationCallback;
import net.spy.memcached.ops.MultiGetsOperationCallback;
import net.spy.memcached.ops.MultiReplicaGetOperationCallback;
import net.spy.memcached.ops.Mutator;
import net.spy.memcached.ops.MutatorOperation;
import net.spy.memcached.ops.NoopOperation;
import net.spy.memcached.ops.ObserveOperation;
import net.spy.memcached.ops.Operation;
import net.spy.memcached.ops.OperationCallback;
import net.spy.memcached.ops.ReplicaGetOperation;
import net.spy.memcached.ops.ReplicaGetsOperation;
import net.spy.memcached.ops.SASLAuthOperation;
import net.spy.memcached.ops.SASLMechsOperation;
import net.spy.memcached.ops.SASLStepOperation;
import net.spy.memcached.ops.SetConfigOperation;
import net.spy.memcached.ops.StatsOperation;
import net.spy.memcached.ops.StoreOperation;
import net.spy.memcached.ops.StoreType;
import net.spy.memcached.ops.TapOperation;
import net.spy.memcached.ops.TouchOperation;
import net.spy.memcached.ops.UnlockOperation;
import net.spy.memcached.ops.VersionOperation;
import net.spy.memcached.tapmessage.RequestMessage;
import net.spy.memcached.tapmessage.TapOpcode;

import javax.security.sasl.SaslClient;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Factory for binary operations.
 */
public class BinaryOperationFactory extends BaseOperationFactory {

  public DeleteOperation
  delete(String key, DeleteOperation.Callback operationCallback) {
    return new DeleteOperationImpl(key, operationCallback);
  }

  public DeleteOperation delete(String key, long cas,
    DeleteOperation.Callback operationCallback) {
    return new DeleteOperationImpl(key, cas, operationCallback);
  }

  public UnlockOperation unlock(String key, long casId,
          OperationCallback cb) {
    return new UnlockOperationImpl(key, casId, cb);
  }
  public ObserveOperation observe(String key, long casId, int index,
          ObserveOperation.Callback cb) {
    return new ObserveOperationImpl(key, casId, index, cb);
  }
  public FlushOperation flush(int delay, OperationCallback cb) {
    return new FlushOperationImpl(cb);
  }

  public GetAndTouchOperation getAndTouch(String key, int expiration,
      GetAndTouchOperation.Callback cb) {
    return new GetAndTouchOperationImpl(key, expiration, cb);
  }

  public GetOperation get(String key, Callback callback) {
    return new GetOperationImpl(key, callback);
  }

  public ReplicaGetOperation replicaGet(String key, int index,
    ReplicaGetOperation.Callback callback) {
    return new ReplicaGetOperationImpl(key, index, callback);
  }

  public ReplicaGetsOperation replicaGets(String key, int index,
    ReplicaGetsOperation.Callback callback) {
    return new ReplicaGetsOperationImpl(key, index, callback);
  }

  public GetOperation get(Collection<String> value, Callback cb) {
    return new MultiGetOperationImpl(value, cb);
  }

  public GetlOperation getl(String key, int exp, GetlOperation.Callback cb) {
    return new GetlOperationImpl(key, exp, cb);
  }

  public GetsOperation gets(String key, GetsOperation.Callback cb) {
    return new GetsOperationImpl(key, cb);
  }

  public StatsOperation keyStats(String key, StatsOperation.Callback cb) {
    return new KeyStatsOperationImpl(key, cb);
  }

  public GetConfigOperation getConfig(ConfigurationType type, GetConfigOperation.Callback callback) {
    return new GetConfigOperationImpl(type, callback);
  }
  
  public SetConfigOperation setConfig(ConfigurationType type, int flags, byte[] data, OperationCallback cb){
    return new SetConfigOperationImpl(type, flags, data, cb);
  }
  
  public DeleteConfigOperation deleteConfig(ConfigurationType type, OperationCallback cb) {
    return new DeleteConfigOperationImpl(type, cb);
  }

  public MutatorOperation mutate(Mutator m, String key, long by, long def,
      int exp, OperationCallback cb) {
    return new MutatorOperationImpl(m, key, by, def, exp, cb);
  }

  public StatsOperation stats(String arg,
      net.spy.memcached.ops.StatsOperation.Callback cb) {
    return new StatsOperationImpl(arg, cb);
  }

  public StoreOperation store(StoreType storeType, String key, int flags,
      int exp, byte[] data, StoreOperation.Callback cb) {
    return new StoreOperationImpl(storeType, key, flags, exp, data, 0, cb);
  }

  public TouchOperation touch(String key, int expiration,
      OperationCallback cb) {
    return new TouchOperationImpl(key, expiration, cb);
  }

  public Operation refreshCertificate(OperationCallback cb) {
    throw new UnsupportedOperationException("Certificate refresh is not supported in binary mode");
  }

  public VersionOperation version(OperationCallback cb) {
    return new VersionOperationImpl(cb);
  }

  public NoopOperation noop(OperationCallback cb) {
    return new NoopOperationImpl(cb);
  }

  public CASOperation cas(StoreType type, String key, long casId, int flags,
      int exp, byte[] data, StoreOperation.Callback cb) {
    return new StoreOperationImpl(type, key, flags, exp, data, casId, cb);
  }

  public ConcatenationOperation cat(ConcatenationType catType, long casId,
      String key, byte[] data, OperationCallback cb) {
    return new ConcatenationOperationImpl(catType, key, data, casId, cb);
  }

  @Override
  protected Collection<? extends Operation> cloneGet(KeyedOperation op) {
    Collection<Operation> rv = new ArrayList<Operation>();
    GetOperation.Callback getCb = null;
    GetsOperation.Callback getsCb = null;
    ReplicaGetOperation.Callback replicaGetCb = null;
    if (op.getCallback() instanceof GetOperation.Callback) {
      getCb =
          new MultiGetOperationCallback(op.getCallback(), op.getKeys().size());
    } else if(op.getCallback() instanceof ReplicaGetOperation.Callback) {
      replicaGetCb =
       new MultiReplicaGetOperationCallback(op.getCallback(), op.getKeys().size());
    } else {
      getsCb =
          new MultiGetsOperationCallback(op.getCallback(), op.getKeys().size());
    }
    for (String k : op.getKeys()) {
      if(getCb != null) {
        rv.add(get(k, getCb));
      } else if(getsCb != null) {
        rv.add(gets(k, getsCb));
      } else {
        rv.add(replicaGet(k, ((ReplicaGetOperationImpl)op).getReplicaIndex() ,replicaGetCb));
      }
    }
    return rv;
  }

  @Override
  public SASLAuthOperation saslAuth(SaslClient sc, OperationCallback cb) {
    return new SASLAuthOperationImpl(sc, cb);
  }

  @Override
  public SASLMechsOperation saslMechs(OperationCallback cb) {
    return new SASLMechsOperationImpl(cb);
  }

  @Override
  public SASLStepOperation saslStep(SaslClient sc, byte[] ch, OperationCallback cb) {
    return new SASLStepOperationImpl(sc, ch, cb);
  }

  public TapOperation tapBackfill(String id, long date, OperationCallback cb) {
    return new TapBackfillOperationImpl(id, date, cb);
  }

  public TapOperation tapCustom(String id, RequestMessage message,
      OperationCallback cb) {
    return new TapCustomOperationImpl(id, message, cb);
  }

  public TapOperation
  tapAck(TapOpcode opcode, int opaque, OperationCallback cb) {
    return new TapAckOperationImpl(opcode, opaque, cb);
  }

  public TapOperation tapDump(String id, OperationCallback cb) {
    return new TapDumpOperationImpl(id, cb);
  }
}
