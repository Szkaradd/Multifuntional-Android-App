package com.szkarad.szkaradapp.common.utils

class Utils {
    companion object {
        fun String.addSpacesBeforeCapitals(): String {
            return this.replace(Regex("(?<!^)([A-Z])"), " $1")
        }

        fun String.removeActivityKeyword(): String {
            return this.replace(Regex("Activity"), "")
        }
    }

}