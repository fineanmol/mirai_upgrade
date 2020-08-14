package com.tragicbytes.midi.models

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

class UserAdvertisementDetails {
    var singleAdvertisementDetails: List<SingleAdvertisementDetails> = ArrayList()
}

class SingleAdvertisementDetails{
    var bannerUrl:String=""
    var screens: List<String>? =null
    var rejectionCount:String="0"
    var advCost:String=""
    var startFrom:String=""
    var endOn:String=""
}

class UserWalletDetails {
    var totalAmount: String = "0"
    var transactionsDetails: List<TransactionDetails> = ArrayList()
}
class TransactionDetails{
    var transactionStatus:String=""
    var transactionAmount:String=""
    var transactionDate:String=""
    var transactionId:String=""
    var transactionMode:String=""
}