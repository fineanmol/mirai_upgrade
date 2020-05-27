package com.tragicbytes.midi.models

import java.io.Serializable

data class AdDetails(
    var adName: String,
    var adDesc: String,
    var adTagline: String,
    var adBrandName: String,
    var logoUrl: String ="drawable/ic_profile.png"
) : Serializable