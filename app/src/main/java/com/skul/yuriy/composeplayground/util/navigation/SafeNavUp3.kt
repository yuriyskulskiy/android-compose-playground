package com.skul.yuriy.composeplayground.util.navigation

import android.view.Choreographer
import androidx.annotation.MainThread
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import java.util.WeakHashMap

/**
 * Nav3 "Up / Back" helpers.
 *
 * Problem:
 * Navigation 3 exposes a mutable back stack list (add/remove). Unlike Navigation 2 (NavController),
 * there is no built-in transactional guard, so multiple quick clicks or duplicated handlers can
 * pop multiple entries immediately.
 *
 * We intentionally do NOT use time-based debounce because it breaks a valid legacy behavior:
 * a user can quickly pop many screens in a short time.
 *
 * Legacy reference (Navigation 2):
 *
 * <pre>
 * @MainThread
 * public open fun navigateUp(): Boolean {
 *     if (destinationCountOnBackStack == 1) {
 *         val extras = activity?.intent?.extras
 *         if (extras?.getIntArray(KEY_DEEP_LINK_IDS) != null) {
 *             return tryRelaunchUpToExplicitStack()
 *         } else {
 *             return tryRelaunchUpToGeneratedStack()
 *         }
 *     } else {
 *         return popBackStack()
 *     }
 * }
 * </pre>
 *
 * Note:
 * Deep-link task/stack relaunch logic from Nav2 is not replicated here because Nav3 doesn't manage
 * activities/tasks. If needed, build the initial back stack yourself when handling intents.
 */


/**
 *Used to prevent more than one pop operation per frame for the same NavBackStack instance.
 *
 *WeakHashMap is used so the back stack is NOT strongly referenced (prevents leaks after recreation).
 */
private val frameLocked = WeakHashMap<Any, Boolean>()

/**
 * Frame-based guard.
 *
 * Allows fast consecutive pops across frames, but prevents duplicate pop calls within the same UI
 * frame for the same back stack instance.
 *
 * @return true if something was popped; false if blocked or stack is already at root.
 */
@MainThread
fun NavBackStack<NavKey>.popFrameLocked(): Boolean {
    if (size <= 1) return false
    if (frameLocked[this] == true) return false
    frameLocked[this] = true

    val result = removeLastOrNull() != null

    Choreographer.getInstance().postFrameCallback {
        frameLocked[this] = false
    }
    return result
}

/**
 * Used to prevent synchronous re-entrancy: multiple pop calls from the same call stack.
 */
private val inProgress = WeakHashMap<Any, Boolean>()

/**
 * Re-entrancy guard.
 *
 * Prevents nested/duplicated synchronous pop calls for the same back stack instance.
 * Does not block fast consecutive pops across frames.
 *
 * @return true if something was popped; false if blocked or stack is already at root.
 */
@MainThread
fun NavBackStack<NavKey>.popInProgressLocked(): Boolean {
    if (size <= 1) return false
    if (inProgress[this] == true) return false
    inProgress[this] = true
    return try {
        removeLastOrNull() != null
    } finally {
        inProgress[this] = false
    }
}
