package com.takari.sleeplock.sleeptimer.admin

/**
 * Result code values returned in onActivityResult when the user is asked to
 * enabled admin permissions.
 */
enum class PermissionResult(val code: Int) {
    UserCanceled(0), UserConfirmed(-1)
}
