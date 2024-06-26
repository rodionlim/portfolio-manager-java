package com.rodion.adelie.plugin.services.storage;

import com.rodion.adelie.plugin.Unstable;
import com.rodion.adelie.plugin.services.exception.StorageException;

/** A transaction that can atomically commit a sequence of operations to a key-value store. */
@Unstable
public interface KeyValueStorageTransaction {

  /**
   * Associates the specified value with the specified key.
   *
   * <p>If a previous value had been store against the given key, the old value is replaced by the
   * given value.
   *
   * @param key the given value is to be associated with.
   * @param value associated with the specified key.
   */
  void put(byte[] key, byte[] value);

  /**
   * When the given key is present, the key and mapped value will be removed from storage.
   *
   * @param key the key and mapped value that will be removed.
   */
  void remove(byte[] key);

  /**
   * Performs an atomic commit of all the operations queued in the transaction.
   *
   * @throws StorageException problem was encountered preventing the commit
   */
  void commit() throws StorageException;

  /** Reset the transaction to a state prior to any operations being queued. */
  void rollback();
}
