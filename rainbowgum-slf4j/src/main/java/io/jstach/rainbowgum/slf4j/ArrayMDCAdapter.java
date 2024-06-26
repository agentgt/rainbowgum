package io.jstach.rainbowgum.slf4j;

import static java.util.Objects.requireNonNull;

import java.util.Deque;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.spi.MDCAdapter;

import io.jstach.rainbowgum.KeyValues.MutableKeyValues;

class ArrayMDCAdapter implements MDCAdapter {

	final ThreadLocal<MutableKeyValues> copyOnThreadLocal = new ThreadLocal<>();

	private static final int WRITE_OPERATION = 1;

	private static final int MAP_COPY_OPERATION = 2;

	// keeps track of the last operation performed
	final ThreadLocal<Integer> lastOperation = new ThreadLocal<Integer>();

	private Integer getAndSetLastOperation(int op) {
		Integer lastOp = lastOperation.get();
		lastOperation.set(op);
		return lastOp;
	}

	private boolean wasLastOpReadOrNull(@Nullable Integer lastOp) {
		return lastOp == null || lastOp.intValue() == MAP_COPY_OPERATION;
	}

	private MutableKeyValues duplicateAndInsertNewMap(@Nullable MutableKeyValues oldMap) {

		MutableKeyValues newMap;

		if (oldMap != null) {
			// we don't want the parent thread modifying oldMap while we are
			// iterating over it
			synchronized (oldMap) {
				newMap = oldMap.copy();
			}
		}
		else {
			newMap = MutableKeyValues.of();
		}

		copyOnThreadLocal.set(newMap);
		return newMap;
	}

	@Override
	public void put(@NonNull String key, @Nullable String val) throws NullPointerException {
		requireNonNull(key, "key cannot be null");

		MutableKeyValues oldMap = copyOnThreadLocal.get();
		Integer lastOp = getAndSetLastOperation(WRITE_OPERATION);

		if (wasLastOpReadOrNull(lastOp) || oldMap == null) {
			MutableKeyValues newMap = duplicateAndInsertNewMap(oldMap);
			newMap.accept(key, val);
		}
		else {
			synchronized (oldMap) {
				oldMap.accept(key, val);
			}
		}
	}

	@Override
	public void remove(@Nullable String key) {
		if (key == null) {
			return;
		}
		MutableKeyValues oldMap = copyOnThreadLocal.get();
		if (oldMap == null)
			return;

		Integer lastOp = getAndSetLastOperation(WRITE_OPERATION);

		if (wasLastOpReadOrNull(lastOp)) {
			MutableKeyValues newMap = duplicateAndInsertNewMap(oldMap);
			newMap.remove(key);
		}
		else {
			synchronized (oldMap) {
				oldMap.remove(key);
			}
		}
	}

	@Override
	public void clear() {
		lastOperation.set(WRITE_OPERATION);
		copyOnThreadLocal.remove();
	}

	@Override
	public @Nullable String get(String key) {
		if (Objects.isNull(key)) {
			return null;
		}
		final MutableKeyValues map = copyOnThreadLocal.get();
		if (map != null) {
			return map.getValueOrNull(key);
		}
		else {
			return null;
		}
	}

	/**
	 * Get the current thread's MDC as a map. This method is intended to be used
	 * internally.
	 * @return mutable key values.
	 */
	public @Nullable MutableKeyValues mutableKeyValuesOrNull() {
		lastOperation.set(MAP_COPY_OPERATION);
		return copyOnThreadLocal.get();
	}

	// /**
	// * Returns the keys in the MDC as a {@link Set}. The returned value can be null.
	// * @return keys.
	// */
	// public @Nullable Set<String> getKeys() {
	// MutableKeyValues map = mutableKeyValuesOrNull();
	//
	// if (map != null) {
	// return map.copyToMap().keySet();
	// }
	// else {
	// return null;
	// }
	// }

	/**
	 * Return a copy of the current thread's context map. Returned value may be null.
	 * @return map copy.
	 */

	@Override
	public @Nullable Map<String, @Nullable String> getCopyOfContextMap() {
		MutableKeyValues hashMap = copyOnThreadLocal.get();
		if (hashMap == null) {
			return null;
		}
		else {
			return hashMap.copyToMap();
		}
	}

	@Override
	public void setContextMap(Map<String, @Nullable String> contextMap) {
		lastOperation.set(WRITE_OPERATION);

		MutableKeyValues newMap = MutableKeyValues.of(contextMap.size());
		newMap.putAll(contextMap);

		// the newMap replaces the old one for serialisation's sake
		copyOnThreadLocal.set(newMap);
	}

	@Override
	public void pushByKey(String key, String value) {
	}

	@Override
	public @Nullable String popByKey(String key) {
		return null;
	}

	@Override
	public @Nullable Deque<String> getCopyOfDequeByKey(String key) {
		return null;
	}

	@Override
	public void clearDequeByKey(String key) {

	}

}