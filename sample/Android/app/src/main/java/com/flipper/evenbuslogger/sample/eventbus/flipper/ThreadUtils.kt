package io.bloco.template.eventbus

fun getStackTraceString(stackTraceElements: Array<StackTraceElement>?, skipPreElements: Int): String? {
    if (stackTraceElements == null || stackTraceElements.size <= skipPreElements) {
        return null
    }
    val stackTrace = StringBuffer()
    for (index in skipPreElements until stackTraceElements.size) {
        stackTrace
                .append(stackTraceElements[index].className)
                .append(".")
                .append(stackTraceElements[index].methodName)
                .append("() at Line ")
                .append(stackTraceElements[index].lineNumber)
                .append("\n\n")
    }
    return stackTrace.toString()
}
