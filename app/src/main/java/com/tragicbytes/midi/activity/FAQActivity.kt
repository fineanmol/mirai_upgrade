package com.tragicbytes.midi.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.base.IExpandableListAdapter
import com.tragicbytes.midi.databinding.ItemFaqHeadingBinding
import com.tragicbytes.midi.databinding.ItemFaqSubheadingBinding
import com.tragicbytes.midi.models.Category
import com.tragicbytes.midi.models.SubCategory
import com.tragicbytes.midi.utils.Constants.AppBroadcasts.CART_COUNT_CHANGE
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_faqactrivity.*
import kotlinx.android.synthetic.main.menu_cart.view.*
import kotlinx.android.synthetic.main.toolbar.*

class FAQActivity : AppBaseActivity() {
    private lateinit var mMenuCart: View
    private lateinit var mFaqAdapter: IExpandableListAdapter<Category, SubCategory, ItemFaqHeadingBinding, ItemFaqSubheadingBinding>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faqactrivity)
        title = getString(R.string.title_faq)
        setToolbar(toolbar)
        setFaq()


    }

    private fun setFaq() {
        mFaqAdapter = object : IExpandableListAdapter<Category, SubCategory, ItemFaqHeadingBinding, ItemFaqSubheadingBinding>(this) {
            override fun bindChildView(view: ItemFaqSubheadingBinding, childObject: SubCategory, groupPosition: Int, childPosition: Int): ItemFaqSubheadingBinding {
                return view
            }

            override fun bindGroupView(view: ItemFaqHeadingBinding, groupObject: Category, groupPosition: Int): ItemFaqHeadingBinding {
                return view
            }

            override val childItemResId: Int = R.layout.item_faq_subheading

            override val groupItemResId: Int = R.layout.item_faq_heading
        }
        exFaq.setAdapter(mFaqAdapter)
        addItems()
    }

    private fun addItems() {
        val mHeading = arrayOf(getString(R.string.lbl_account_deactivate), getString(R.string.lbl_quick_pay), getString(R.string.lbl_return_items), getString(R.string.lbl_replace_items))
        val mSubHeading = arrayOf(getString(R.string.lbl_account_deactivate), getString(R.string.lbl_quick_pay), getString(R.string.lbl_return_items), getString(R.string.lbl_replace_items))
        val map = HashMap<Category, ArrayList<SubCategory>>()
        val mFaqList = ArrayList<Category>()
        mHeading.forEachIndexed { i: Int, s: String ->
            val heading = Category()
            heading.category_name = s
            mFaqList.add(heading)
        }
        mFaqList.forEach {
            val list = ArrayList<SubCategory>()
            mSubHeading.forEach {
                val subCat = SubCategory()
                subCat.subcategory_name = it
                list.add(subCat)
            }
            map[it] = list
        }
        mFaqAdapter.addExpandableItems(mFaqList, map)
    }




}
