package com.example.retailplatform.user.util;

import org.apache.logging.log4j.ThreadContext;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Utility class for MDC context propagation to async threads.
 */
public class MdcUtil {

    /**
     * Wrap a Runnable so that it preserves the MDC context.
     */
    public static Runnable wrap(Runnable runnable) {
        Map<String, String> contextMap = ThreadContext.getImmutableContext();
        return () -> {
            ThreadContext.putAll(contextMap);
            try {
                runnable.run();
            } finally {
                ThreadContext.clearMap();
            }
        };
    }

    /**
     * Wrap a Callable so that it preserves the MDC context.
     */
    public static <V> Callable<V> wrap(Callable<V> callable) {
        Map<String, String> contextMap = ThreadContext.getImmutableContext();
        return () -> {
            ThreadContext.putAll(contextMap);
            try {
                return callable.call();
            } finally {
                ThreadContext.clearMap();
            }
        };
    }
}
