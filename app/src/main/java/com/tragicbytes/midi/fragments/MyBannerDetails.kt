package com.tragicbytes.midi.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tragicbytes.midi.R
import com.tragicbytes.midi.models.SingleAdvertisementDetails
import com.tragicbytes.midi.utils.Constants


class MyBannerDetails : BaseFragment() {

    private var singleAdvertisementDetails=SingleAdvertisementDetails()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_banner_details, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)




    }

}