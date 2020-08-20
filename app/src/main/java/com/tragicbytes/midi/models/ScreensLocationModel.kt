package com.tragicbytes.midi.models

import java.io.Serializable

class ScreensLocationModel() {
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
    var screenAgeGroups:String=""
    var screenImpressions:String=""
}