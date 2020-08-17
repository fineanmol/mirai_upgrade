package com.tragicbytes.midi.models

class ScreensLocationModel() {
    var screenData: List<ScreenDataModel> = listOf()
}
class ScreenDataModel {
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