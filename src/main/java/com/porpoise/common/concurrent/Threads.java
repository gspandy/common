package com.porpoise.common.concurrent;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.porpoise.common.log.Log;

public enum Threads {
	;
	private static ExecutorService	defaultThreadPool;

	// unintantiable

	/**
	 * @return an excecutor service
	 */
	public static ExecutorService newFixedThreadPool(final int threadCount) {
		final ThreadFactory factory = Threads.newLoggingThreadFactory();
		final ExecutorService threadPool = Executors.newFixedThreadPool(threadCount, factory);
		return threadPool;
	}

	/**
	 * Return the first call made prior to those of the given classes as a string.
	 * 
	 * 
	 * @return the first call made prior to those of the given classes as a string
	 */
	public static String getFirstPriorCallAsString(final Class<?>... classes) {
		final StackTraceElement firstNonLog4JStackElement = getFirstCallBefore(classes);

		if (firstNonLog4JStackElement == null) {
			return "";
		}
		String simpleClassName = firstNonLog4JStackElement.getClassName();
		final int firstUpper = CharMatcher.JAVA_UPPER_CASE.indexIn(simpleClassName);
		simpleClassName = simpleClassName.substring(firstUpper);
		return String.format("%s.%s[%d]", simpleClassName, firstNonLog4JStackElement.getMethodName(),
		        Integer.valueOf(firstNonLog4JStackElement.getLineNumber()));
	}

	/**
	 * @param c1ass
	 * @return the first calls stack trace element which occurs higher up the call chain than the given classes
	 */
	private static StackTraceElement getFirstCallBefore(final Class<?>... ignore) {
		final List<Class<?>> ignoreCallsList = Lists.newArrayList(ignore);
		ignoreCallsList.add(Threads.class);
		final List<String> ignoreClassNames = Lists.transform(ignoreCallsList, new Function<Class<?>, String>() {
			@Override
			public String apply(final Class<?> arg0) {
				return arg0.getName();
			}
		});
		StackTraceElement firstElement = null;

		// the top of the call stack returned will always be 'Thread.getCallStack',
		// so we return a sublist discounting that first call
		final StackTraceElement[] stackArray = Thread.currentThread().getStackTrace();
		final List<StackTraceElement> stack = Arrays.asList(stackArray).subList(1, stackArray.length);
		for (final StackTraceElement elm : stack) {
			if (!ignoreClassNames.contains(elm.getClassName())) {
				firstElement = elm;
				break;
			}
		}
		return firstElement;
	}

	/**
	 * @return A shared thread pool of worker threads
	 */
	public synchronized static ExecutorService getDefaultThreadPool() {
		if (defaultThreadPool == null) {
			defaultThreadPool = newFixedThreadPool(5);
		}
		return defaultThreadPool;

	}

	/**
	 * @return a new initialised thread factory
	 */
	public static ThreadFactory newLoggingThreadFactory() {
		final ThreadFactory factory = new ThreadFactory() {
			private int	count	= 1;

			@Override
			public Thread newThread(final Runnable runnable) {
				final Runnable wrappedRunnable = Runnables.proxyWithLogging(runnable);
				final Thread thread = new Thread(wrappedRunnable);
				final String name = String.format("Thread-Pool #%d", Integer.valueOf(this.count++));
				Log.debug("Starting thread %s", name);
				thread.setName(name);
				thread.setDaemon(true);
				thread.setPriority(Thread.MIN_PRIORITY);
				final UncaughtExceptionHandler handler = new UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(final Thread param, final Throwable error) {
						Log.error("Uncaught exception %s received on thread %s", error, param);
					}
				};
				thread.setUncaughtExceptionHandler(handler);

				return thread;
			}
		};
		return factory;
	}

}