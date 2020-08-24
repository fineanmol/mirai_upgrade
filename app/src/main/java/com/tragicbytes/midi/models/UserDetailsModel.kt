package com.tragicbytes.midi.models

import java.io.Serializable

class UserDetailsModel {
    var userId: String=""
    var userAdvertisementDetails: UserAdvertisementDetails=UserAdvertisementDetails()
    var userPersonalDetails: UserPersonalDetails= UserPersonalDetails()
    var userWalletDetails: UserWalletDetails=UserWalletDetails()
}

class UserPersonalDetails {
    var firstName: String = ""
    var lastName: String = ""
    var gender: String = ""
    var phone: String = ""
    var email: String = ""
    var password: String = ""
    var userProfilePic: String = ""
    var dob: String = ""
    var company: String = ""
    var userAccountStatus: Boolean = true
}

class UserAdvertisementDetails:Serializable {
    var singleAdvertisementDetails: ArrayList<SingleAdvertisementDetails> = ArrayList()
}

class SingleAdvertisementDetails:Serializable{
    var advId:String?=null
    var advName:String=""
    var advDescription:String=""
    var advTagline:String=""
    var advBrandName:String=""
    var advUserBannerLogo:String=""
    var advBannerUrl:String=""
    var advGenderPref:String=""
    var advOverallStatus:String=""
    var advAgePref:ArrayList<String> =ArrayList()
    var advRange:String=""
    var startFrom:String=""
    var endOn:String=""
    var screens =ScreensLocationModel().screenData
    var rejectionCount:String="0"
    var advCost:String=""
    var advSubmittedOn:Long=0L

}

class UserWalletDetails {
    var totalAmount: String = "0"
    var transactionsDetails: ArrayList<TransactionDetails> = arrayListOf()
}
class TransactionDetails: Serializable {
    var transactionStatus:String=""
    var transactionAmount:String=""
    var transactionDate:Long= Long.MIN_VALUE
    var transactionId:String=""
    var orderId:String=""
    var transactionMode:String=""
    var email: String=""
    var phone: String = ""
    var transactionMessage:String=""
}