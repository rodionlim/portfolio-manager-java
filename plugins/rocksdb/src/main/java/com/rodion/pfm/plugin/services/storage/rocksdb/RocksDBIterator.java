package com.rodion.pfm.plugin.services.storage.rocksdb;

import static com.google.common.base.Preconditions.checkState;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.tuple.Pair;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The Rocks DB iterator. */
public class RocksDBIterator implements Iterator<Pair<byte[], byte[]>>, AutoCloseable {
  private static final Logger logger = LoggerFactory.getLogger(RocksDBIterator.class);

  private final RocksIterator rocksIterator;
  private final AtomicBoolean closed = new AtomicBoolean(false);

  private RocksDBIterator(final RocksIterator rocksIterator) {
    this.rocksIterator = rocksIterator;
  }

  /**
   * Create RocksDb iterator.
   *
   * @param rocksIterator the rocks iterator
   * @return the rocks db iterator
   */
  public static RocksDBIterator create(final RocksIterator rocksIterator) {
    return new RocksDBIterator(rocksIterator);
  }

  @Override
  public boolean hasNext() {
    assertOpen();
    return rocksIterator.isValid();
  }

  @Override
  public Pair<byte[], byte[]> next() {
    assertOpen();
    try {
      rocksIterator.status();
    } catch (final RocksDBException e) {
      logger.error(
          String.format("%s encountered a problem while iterating.", getClass().getSimpleName()),
          e);
    }
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    final byte[] key = rocksIterator.key();
    final byte[] value = rocksIterator.value();
    rocksIterator.next();
    return Pair.of(key, value);
  }

  /**
   * Next key.
   *
   * @return the byte [ ]
   */
  public byte[] nextKey() {
    assertOpen();
    try {
      rocksIterator.status();
    } catch (final RocksDBException e) {
      logger.error(
          String.format("%s encountered a problem while iterating.", getClass().getSimpleName()),
          e);
    }
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    final byte[] key = rocksIterator.key();
    rocksIterator.next();
    return key;
  }

  /**
   * To stream.
   *
   * @return the stream
   */
  public Stream<Pair<byte[], byte[]>> toStream() {
    assertOpen();
    final Spliterator<Pair<byte[], byte[]>> spliterator =
        Spliterators.spliteratorUnknownSize(
            this,
            Spliterator.IMMUTABLE
                | Spliterator.DISTINCT
                | Spliterator.NONNULL
                | Spliterator.ORDERED
                | Spliterator.SORTED);

    return StreamSupport.stream(spliterator, false).onClose(this::close);
  }

  /**
   * To stream keys.
   *
   * @return the stream
   */
  public Stream<byte[]> toStreamKeys() {
    assertOpen();
    final Spliterator<byte[]> spliterator =
        Spliterators.spliteratorUnknownSize(
            new Iterator<>() {
              @Override
              public boolean hasNext() {
                return RocksDBIterator.this.hasNext();
              }

              @Override
              public byte[] next() {
                return RocksDBIterator.this.nextKey();
              }
            },
            Spliterator.IMMUTABLE
                | Spliterator.DISTINCT
                | Spliterator.NONNULL
                | Spliterator.ORDERED
                | Spliterator.SORTED);

    return StreamSupport.stream(spliterator, false).onClose(this::close);
  }

  private void assertOpen() {
    checkState(
        !closed.get(),
        String.format("Attempt to read from a closed %s", getClass().getSimpleName()));
  }

  @Override
  public void close() {
    if (closed.compareAndSet(false, true)) {
      rocksIterator.close();
    }
  }
}
