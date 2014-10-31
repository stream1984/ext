package io.vertx.spi.cluster.impl.zookeeper;

import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.shareddata.impl.ClusterSerializable;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.zookeeper.CreateMode;

import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Stream.Liu
 */
abstract class ZKMap<K, V> {

  protected final CuratorFramework curator;
  protected final Vertx vertx;
  protected final AtomicBoolean nodeSplit = new AtomicBoolean(false);
  protected final String mapPath;

  protected static final String ZK_PATH_ASYNC_MAP = "asyncMap";
  protected static final String ZK_PATH_ASYNC_MULTI_MAP = "asyncMultiMap";
  protected static final String ZK_PATH_SYNC_MAP = "syncMap";

  protected ZKMap(CuratorFramework curator, Vertx vertx, String mapType, String mapPath) {
    this.curator = curator;
    this.vertx = vertx;
    this.mapPath = "/" + mapType + "/" + mapPath;
    this.curator.getConnectionStateListenable().addListener((client, newState) -> {
      if (newState == ConnectionState.LOST || newState == ConnectionState.SUSPENDED) {
        nodeSplit.set(true);
      } else {
        nodeSplit.set(false);
      }
    });
  }

  protected String keyPath(K k) {
    if (k == null) {
      throw new NullPointerException("key should not be null.");
    }
    return mapPath + "/" + k.toString();
  }

  protected String keyPath(K k, Object v) {
    if (v == null) {
      throw new NullPointerException("value should not be null.");
    }
    return keyPath(k) + "/" + v.hashCode();
  }

  protected String keyPath(String partPath, Object v) {
    if (v == null) {
      throw new NullPointerException("value should not be null.");
    }
    return partPath + "/" + v.hashCode();
  }

  protected void checkState() throws IllegalStateException {
    if (nodeSplit.get()) {
      throw new IllegalStateException("this zookeeper node have detached from cluster");
    }
  }

  protected byte[] asByte(Object object) throws IOException {
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    DataOutput dataOutput = new DataOutputStream(byteOut);
    if (object instanceof ClusterSerializable) {
      ClusterSerializable clusterSerializable = (ClusterSerializable) object;
      dataOutput.writeBoolean(true);
      dataOutput.writeUTF(object.getClass().getName());
      byte[] bytes = clusterSerializable.writeToBuffer().getBytes();
      dataOutput.writeInt(bytes.length);
      dataOutput.write(bytes);
    } else {
      dataOutput.writeBoolean(false);
      ByteArrayOutputStream javaByteOut = new ByteArrayOutputStream();
      ObjectOutput objectOutput = new ObjectOutputStream(javaByteOut);
      objectOutput.writeObject(object);
      dataOutput.write(javaByteOut.toByteArray());
    }
    return byteOut.toByteArray();
  }

  protected Object asObject(byte[] bytes) throws Exception {
    ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
    DataInputStream in = new DataInputStream(byteIn);
    boolean isClusterSerializable = in.readBoolean();
    if (isClusterSerializable) {
      String className = in.readUTF();
      Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
      int length = in.readInt();
      byte[] body = new byte[length];
      in.readFully(body);
      try {
        ClusterSerializable clusterSerializable = (ClusterSerializable) clazz.newInstance();
        clusterSerializable.readFromBuffer(Buffer.buffer(body));
        return clusterSerializable;
      } catch (Exception e) {
        throw new IllegalStateException("Failed to load class " + e.getMessage(), e);
      }
    } else {
      byte[] body = new byte[in.available()];
      in.readFully(body);
      ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(body));
      return objectIn.readObject();
    }
  }

  protected <T, E> void forwardAsyncResult(Handler<AsyncResult<T>> completeHandler, AsyncResult<E> asyncResult) {
    if (asyncResult.succeeded()) {
      E result = asyncResult.result();
      if (result == null || result instanceof Void) {
        vertx.runOnContext(event -> completeHandler.handle(Future.completedFuture()));
      } else {
        vertx.runOnContext(event -> completeHandler.handle(Future.completedFuture((T) result)));
      }
    } else {
      vertx.runOnContext(aVoid -> completeHandler.handle(Future.completedFuture(asyncResult.cause())));
    }
  }

  protected <T, E> void forwardAsyncResult(Handler<AsyncResult<T>> completeHandler, AsyncResult<E> asyncResult, T result) {
    if (asyncResult.succeeded()) {
      vertx.runOnContext(event -> completeHandler.handle(Future.completedFuture(result)));
    } else {
      vertx.runOnContext(aVoid -> completeHandler.handle(Future.completedFuture(asyncResult.cause())));
    }
  }

  protected void checkExists(K k, AsyncResultHandler<Boolean> handler) {
    checkExists(keyPath(k), handler);
  }

  protected void checkExists(String path, AsyncResultHandler<Boolean> handler) {
    try {
      checkState();
      curator.checkExists().inBackground((client, event) -> {
        if (event.getType() == CuratorEventType.EXISTS) {
          if (event.getStat() == null) {
            vertx.runOnContext(aVoid -> handler.handle(Future.completedFuture(false)));
          } else {
            vertx.runOnContext(aVoid -> handler.handle(Future.completedFuture(true)));
          }
        }
      }).forPath(path);
    } catch (Exception e) {
      vertx.runOnContext(aVoid -> handler.handle(Future.completedFuture(e)));
    }
  }

  protected void create(K k, V v, Handler<AsyncResult<Void>> completionHandler) {
    create(keyPath(k), v, completionHandler);
  }

  protected void create(String path, V v, Handler<AsyncResult<Void>> completionHandler) {
    try {
      checkState();
      curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).inBackground((cl, el) -> {
        if (el.getType() == CuratorEventType.CREATE) {
          vertx.runOnContext(event -> completionHandler.handle(Future.completedFuture()));
        }
      }).forPath(path, asByte(v));
    } catch (Exception ex) {
      vertx.runOnContext(event -> completionHandler.handle(Future.completedFuture(ex)));
    }
  }

  protected void setData(K k, V v, Handler<AsyncResult<Void>> completionHandler) {
    setData(keyPath(k), v, completionHandler);
  }

  protected void setData(String path, V v, Handler<AsyncResult<Void>> completionHandler) {
    try {
      checkState();
      curator.setData().inBackground((client, event) -> {
        if (event.getType() == CuratorEventType.SET_DATA) {
          vertx.runOnContext(e -> completionHandler.handle(Future.completedFuture()));
        }
      }).forPath(path, asByte(v));
    } catch (Exception ex) {
      vertx.runOnContext(event -> completionHandler.handle(Future.completedFuture(ex)));
    }
  }

  protected void getData(K k, AsyncResultHandler<V> asyncResultHandler) {
    getData(keyPath(k), asyncResultHandler);
  }

  protected void getData(String path, AsyncResultHandler<V> asyncResultHandler) {
    try {
      checkState();
      curator.getData().inBackground((client, event) -> {
        if (event.getType() == CuratorEventType.GET_DATA) {
          if (event.getData() != null) {
            V result = (V) asObject(event.getData());
            vertx.runOnContext(handler -> asyncResultHandler.handle(Future.completedFuture(result)));
          } else {
            vertx.runOnContext(handler -> asyncResultHandler.handle(Future.completedFuture()));
          }
        }
      }).forPath(path);
    } catch (Exception e) {
      vertx.runOnContext(aVoid -> asyncResultHandler.handle(Future.completedFuture(e)));
    }
  }

  protected void delete(K k, V v, Handler<AsyncResult<V>> asyncResultHandler) {
    delete(keyPath(k), v, asyncResultHandler);
  }

  protected void delete(String path, V v, Handler<AsyncResult<V>> asyncResultHandler) {
    try {
      checkState();
      curator.delete().deletingChildrenIfNeeded().inBackground((client, event) -> {
        if (event.getType() == CuratorEventType.DELETE) {
          vertx.runOnContext(ea -> asyncResultHandler.handle(Future.completedFuture(v)));
        }
      }).forPath(path);
    } catch (Exception ex) {
      vertx.runOnContext(aVoid -> asyncResultHandler.handle(Future.completedFuture(ex)));
    }
  }


}