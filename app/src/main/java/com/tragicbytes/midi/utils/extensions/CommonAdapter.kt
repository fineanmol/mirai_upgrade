package com.tragicbytes.midi.utils.extensions

import android.content.Context
import android.view.View
import com.tragicbytes.midi.R
import com.tragicbytes.midi.activity.SubCategoryActivity
import com.tragicbytes.midi.base.BaseRecyclerAdapter
import com.tragicbytes.midi.databinding.ItemCategory2Binding
import com.tragicbytes.midi.databinding.ItemCategoryBinding
import com.tragicbytes.midi.models.CategoryData
import com.tragicbytes.midi.utils.Constants

private var mColorArray = intArrayOf(R.color.cat_1, R.color.cat_2, R.color.cat_3, R.color.cat_4, R.color.cat_5)

fun Context.getCategoryAdapter(): BaseRecyclerAdapter<CategoryData, ItemCategoryBinding> {
    return object : BaseRecyclerAdapter<CategoryData, ItemCategoryBinding>() {
        override val layoutResId: Int = R.layout.item_category

        override fun onBindData(model: CategoryData, position: Int, dataBinding: ItemCategoryBinding) {
            dataBinding.ivCategory.changeBackgroundTint(
                color(mColorArray[position % mColorArray.size])
            )
            if (model.image !=null ){
                dataBinding.ivCategory.loadImageFromUrl(model.image,aPlaceHolderImage = R.drawable.cat_placeholder)
            }
            dataBinding.ivCategory.setStrokedBackground(color(mColorArray[position % mColorArray.size]),alpha=50f)
            dataBinding.tvCatName.setTextColor(color(mColorArray[position % mColorArray.size]))
            dataBinding.tvCatName.text = model.name.getHtmlString()
        }

        override fun onItemClick(view: View, model: CategoryData, position: Int, dataBinding: ItemCategoryBinding) {
            launchActivity<SubCategoryActivity> { putExtra(Constants.KeyIntent.DATA, model) }
        }

        override fun onItemLongClick(view: View, model: CategoryData, position: Int) {

        }
    }
}


fun Context.getCategory2Adapter(): BaseRecyclerAdapter<CategoryData, ItemCategory2Binding> {
    return object : BaseRecyclerAdapter<CategoryData, ItemCategory2Binding>() {
        override val layoutResId: Int = R.layout.item_category_2

        override fun onBindData(model: CategoryData, position: Int, dataBinding: ItemCategory2Binding) {
       /*     dataBinding.ivCategory.changeBackgroundTint(
                color(mColorArray[position % mColorArray.size])
            )*/
            if (model.image !=null ){
                dataBinding.ivCategory.loadImageFromUrl(model.image,aPlaceHolderImage = R.drawable.cat_placeholder)
            }
        /*    dataBinding.ivCategory.setStrokedBackground(color(mColorArray[position % mColorArray.size]),alpha=50f)
            dataBinding.tvCatName.setTextColor(color(mColorArray[position % mColorArray.size]))
        */    dataBinding.tvCatName.text = model.name.getHtmlString()
        }

        override fun onItemClick(view: View, model: CategoryData, position: Int, dataBinding: ItemCategory2Binding) {
            launchActivity<SubCategoryActivity> { putExtra(Constants.KeyIntent.DATA, model) }
        }

        override fun onItemLongClick(view: View, model: CategoryData, position: Int) {

        }
    }
}
