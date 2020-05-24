package com.tragicbytes.midi.models

enum class TrackStatus {
    PENDING,
    DELIVERED,
    SHIPPED,
    CANCELLED,
    ARRIVED,
    OUTFORDELIVERY,
    NOTDONE,
    DELIVERING,
    pending, processing, on_hold, completed, cancelled, refunded, failed, trash
}