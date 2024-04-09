package com.rodion.pfm.services.kvstore;

import static com.google.common.base.Preconditions.checkState;

import com.rodion.pfm.plugin.services.exception.StorageException;
import com.rodion.pfm.plugin.services.storage.SegmentIdentifier;
import com.rodion.pfm.plugin.services.storage.SegmentedKeyValueStorageTransaction;
import java.util.function.Supplier;

/** The Key value storage transaction validator decorator. */
public class SegmentedKeyValueStorageTransactionValidatorDecorator
    implements SegmentedKeyValueStorageTransaction {

  private final SegmentedKeyValueStorageTransaction transaction;
  private final Supplier<Boolean> isClosed;
  private boolean active = true;

  /**
   * Instantiates a new Key value storage transaction transition validator decorator.
   *
   * @param toDecorate the to decorate
   * @param isClosed supplier function to determine if the storage is closed
   */
  public SegmentedKeyValueStorageTransactionValidatorDecorator(
      final SegmentedKeyValueStorageTransaction toDecorate, final Supplier<Boolean> isClosed) {
    this.isClosed = isClosed;
    this.transaction = toDecorate;
  }

  @Override
  public void put(final SegmentIdentifier segmentId, final byte[] key, final byte[] value) {
    checkState(active, "Cannot invoke put() on a completed transaction.");
    checkState(!isClosed.get(), "Cannot invoke put() on a closed storage.");
    transaction.put(segmentId, key, value);
  }

  @Override
  public void remove(final SegmentIdentifier segmentId, final byte[] key) {
    checkState(active, "Cannot invoke remove() on a completed transaction.");
    checkState(!isClosed.get(), "Cannot invoke remove() on a closed storage.");
    transaction.remove(segmentId, key);
  }

  @Override
  public final void commit() throws StorageException {
    checkState(active, "Cannot commit a completed transaction.");
    checkState(!isClosed.get(), "Cannot invoke commit() on a closed storage.");
    active = false;
    transaction.commit();
  }

  @Override
  public final void rollback() {
    checkState(active, "Cannot rollback a completed transaction.");
    checkState(!isClosed.get(), "Cannot invoke rollback() on a closed storage.");
    active = false;
    transaction.rollback();
  }
}
