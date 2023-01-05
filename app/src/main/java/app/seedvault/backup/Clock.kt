package app.seedvault.backup

/**
 * This class only exists, so we can mock the time in tests.
 */
class Clock {
    /**
     * Returns the current time in milliseconds (Unix time).
     */
    fun time(): Long {
        return System.currentTimeMillis()
    }
}
