package com.tragicbytes.midi.models

import java.io.Serializable

class ScreensLocationModel {
    var screenData: ArrayList<ScreenDataModel> = ArrayList()
}
class ScreenDataModel:Serializable {
    var screenId:String=""
    var screenCity:String=""
    var screenLocation:String=""
    var screenPincode:String=""
    var screenPrice:String=""
    var screenStatus:String=""
    var screenActiveTime:String=""
    var screenGenderRatio:String=""
    var screenAgeGroupPref=AgeGroupDetail()
    var screenImpressions:String=""
    var screenAdvApprovedOn:String=""
    var screenAdminComment:String="Your Advertisement is under Review"
    var screenApprovedStatus: String = ""
}
class AgeGroupDetail:Serializable {
    var generationZ: String = "0"
    var generationY: String = "0"
    var generationX: String = "0"
    var babyBoomers: String = "0"
}