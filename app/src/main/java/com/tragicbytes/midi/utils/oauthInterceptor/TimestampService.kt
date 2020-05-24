package com.tragicbytes.midi.utils.oauthInterceptor

interface TimestampService {
    val timestampInSeconds: String
    val nonce: String
}