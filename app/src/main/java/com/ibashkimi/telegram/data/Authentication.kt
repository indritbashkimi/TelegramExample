package com.ibashkimi.telegram.data

enum class Authentication {
    UNAUTHENTICATED,
    WAIT_FOR_NUMBER,
    WAIT_FOR_CODE,
    WAIT_FOR_PASSWORD,
    AUTHENTICATED,
    UNKNOWN
}