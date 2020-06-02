package com.tragicbytes.midi.models

import java.io.Serializable

class AdDetailsModel {

    data class AdDetails(
        var adName: String,
        var adDesc: String,
        var adTagline: String,
        var adBrandName: String,
        var logoUrl: String = "drawable/ic_profile.png"
    ) : Serializable



    data class AdsCompleteDetails (

        var adId:String,
        var adName: String,
        var adDesc: String,
        var adTagline: String,
        var adBrandName: String,
        var logoUrl: String ="drawable/ic_profile.png",
        var gender:String,
        var ageGroup:String,
        var startDate:String,
        var endDate:String,
        var startTime:String,
        var endTime:String,
        var range:String,
        var bannerImageUrl:String

    ) : Serializable

}