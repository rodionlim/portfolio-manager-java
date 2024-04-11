package com.rodion.adelie.plugin.services.storage;

// TODO(rl): change to adelie relevant format or remove altogether
/** Supported database storage format */
public enum DataStorageFormat {
  /** Original format. Store all tries */
  FOREST,
  /** New format. Store one trie, and trie logs to roll forward and backward */
  BONSAI;
}
