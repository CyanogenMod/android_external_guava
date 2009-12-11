/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 * Other contributors include Andrew Wright, Jeffrey Hayes,
 * Pat Fisher, Mike Judd.
 */

package com.google.common.collect;

import com.google.common.collect.CustomConcurrentHashMap.SimpleStrategy;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * A copy of {@link java.util.concurrent.ConcurrentHashMap} used to test {@link
 * com.google.common.collect.CustomConcurrentHashMap}. This also serves
 * as the examples in the CustomConcurrentHashMap Javadocs.
 */
public class Jsr166HashMap<K, V> extends ForwardingConcurrentMap<K, V>
    implements Serializable {
  private static class Strategy<K, V> extends SimpleStrategy<K, V>
      implements Serializable {
    private static final long serialVersionUID = 0;
  }

  /* ---------------- Public operations -------------- */

  /**
   * The default initial capacity for this table,
   * used when not otherwise specified in a constructor.
   */
  static final int DEFAULT_INITIAL_CAPACITY = 16;

  /**
   * The default load factor for this table, used when not
   * otherwise specified in a constructor.
   */
  static final float DEFAULT_LOAD_FACTOR = 0.75f;

  /**
   * The default concurrency level for this table, used when not
   * otherwise specified in a constructor.
   */
  static final int DEFAULT_CONCURRENCY_LEVEL = 16;

  final ConcurrentMap<K, V> delegate;

  @Override protected ConcurrentMap<K, V> delegate() {
    return delegate;
  }

  /**
   * Creates a new, empty map with the specified initial capacity, load factor
   * and concurrency level.
   *
   * @param initialCapacity  the initial capacity. The implementation performs
   *                         internal sizing to accommodate this many
   *                         elements.
   * @param loadFactor       the load factor threshold, used to control
   *                         resizing. Resizing may be performed when the
   *                         average number of elements per bin exceeds this
   *                         threshold.
   * @param concurrencyLevel the estimated number of concurrently updating
   *                         threads. The implementation performs internal
   *                         sizing to try to accommodate this many threads.
   * @throws IllegalArgumentException if the initial capacity is negative or
   *                                  the load factor or concurrencyLevel are
   *                                  nonpositive.
   */
  public Jsr166HashMap(int initialCapacity,
      float loadFactor, int concurrencyLevel) {
    // just ignore loadFactor.
    this.delegate = new CustomConcurrentHashMap.Builder()
        .initialCapacity(initialCapacity)
        .concurrencyLevel(concurrencyLevel)
        .buildMap(new Strategy<K, V>());
  }

  /**
   * Creates a new, empty map with the specified initial capacity and load
   * factor and with the default concurrencyLevel (16).
   *
   * @param initialCapacity The implementation performs internal sizing to
   *                        accommodate this many elements.
   * @param loadFactor      the load factor threshold, used to control
   *                        resizing. Resizing may be performed when the
   *                        average number of elements per bin exceeds this
   *                        threshold.
   * @throws IllegalArgumentException if the initial capacity of elements is
   *                                  negative or the load factor is
   *                                  nonpositive
   * @since 1.6
   */
  public Jsr166HashMap(int initialCapacity, float loadFactor) {
    this(initialCapacity, loadFactor, DEFAULT_CONCURRENCY_LEVEL);
  }

  /**
   * Creates a new, empty map with the specified initial capacity, and with
   * default load factor (0.75) and concurrencyLevel (16).
   *
   * @param initialCapacity the initial capacity. The implementation performs
   *                        internal sizing to accommodate this many
   *                        elements.
   * @throws IllegalArgumentException if the initial capacity of elements is
   *                                  negative.
   */
  public Jsr166HashMap(int initialCapacity) {
    this(initialCapacity, DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL);
  }

  /**
   * Creates a new, empty map with a default initial capacity (16), load
   * factor (0.75) and concurrencyLevel (16).
   */
  public Jsr166HashMap() {
    this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR,
        DEFAULT_CONCURRENCY_LEVEL);
  }

  /**
   * Creates a new map with the same mappings as the given map. The map is
   * created with a capacity of 1.5 times the number of mappings in the given
   * map or 16 (whichever is greater), and a default load factor (0.75) and
   * concurrencyLevel (16).
   *
   * @param m the map
   */
  public Jsr166HashMap(Map<? extends K, ? extends V> m) {
    this(Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1,
        DEFAULT_INITIAL_CAPACITY),
        DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL);
    putAll(m);
  }
}