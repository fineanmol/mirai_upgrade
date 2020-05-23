package com.iqonic.woobox.databinding;
import com.iqonic.woobox.R;
import com.iqonic.woobox.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityProductDetailBindingImpl extends ActivityProductDetailBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.app_bar, 2);
        sViewsWithIds.put(R.id.toolbar_layout, 3);
        sViewsWithIds.put(R.id.rlViewPager, 4);
        sViewsWithIds.put(R.id.productViewPager, 5);
        sViewsWithIds.put(R.id.dots, 6);
        sViewsWithIds.put(R.id.toolbar, 7);
        sViewsWithIds.put(R.id.ivBack, 8);
        sViewsWithIds.put(R.id.ivFavourite, 9);
        sViewsWithIds.put(R.id.rlInfo, 10);
        sViewsWithIds.put(R.id.tvItemProductDiscount, 11);
        sViewsWithIds.put(R.id.tvPrice, 12);
        sViewsWithIds.put(R.id.tvItemProductOriginalPrice, 13);
        sViewsWithIds.put(R.id.txAvailability, 14);
        sViewsWithIds.put(R.id.tvAvailability, 15);
        sViewsWithIds.put(R.id.btnOutOfStock, 16);
        sViewsWithIds.put(R.id.btnAddCard, 17);
        sViewsWithIds.put(R.id.tvSelectedQuantity, 18);
        sViewsWithIds.put(R.id.txtDescription, 19);
        sViewsWithIds.put(R.id.tvColors, 20);
        sViewsWithIds.put(R.id.rvColors, 21);
        sViewsWithIds.put(R.id.tvSize, 22);
        sViewsWithIds.put(R.id.rvSize, 23);
        sViewsWithIds.put(R.id.llMoreInfo, 24);
        sViewsWithIds.put(R.id.tvBrand, 25);
        sViewsWithIds.put(R.id.tvColour, 26);
        sViewsWithIds.put(R.id.llLength, 27);
        sViewsWithIds.put(R.id.tvLength, 28);
        sViewsWithIds.put(R.id.llHeight, 29);
        sViewsWithIds.put(R.id.tvHeight, 30);
        sViewsWithIds.put(R.id.llWidth, 31);
        sViewsWithIds.put(R.id.tvWidth, 32);
        sViewsWithIds.put(R.id.tvClouser, 33);
        sViewsWithIds.put(R.id.tvSolid, 34);
        sViewsWithIds.put(R.id.tvBrandName, 35);
        sViewsWithIds.put(R.id.tvAllReviews, 36);
    }
    // views
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityProductDetailBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 37, sIncludes, sViewsWithIds));
    }
    private ActivityProductDetailBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (com.google.android.material.appbar.AppBarLayout) bindings[2]
            , (com.google.android.material.button.MaterialButton) bindings[17]
            , (com.google.android.material.button.MaterialButton) bindings[16]
            , (com.iqonic.woobox.utils.dotsindicator.DotsIndicator) bindings[6]
            , (androidx.coordinatorlayout.widget.CoordinatorLayout) bindings[0]
            , (android.widget.ImageView) bindings[8]
            , (android.widget.ImageView) bindings[9]
            , (android.widget.LinearLayout) bindings[29]
            , (android.widget.LinearLayout) bindings[27]
            , (android.widget.LinearLayout) bindings[24]
            , (android.widget.LinearLayout) bindings[31]
            , (androidx.viewpager.widget.ViewPager) bindings[5]
            , (android.widget.RelativeLayout) bindings[10]
            , (android.widget.RelativeLayout) bindings[4]
            , (androidx.recyclerview.widget.RecyclerView) bindings[21]
            , (androidx.recyclerview.widget.RecyclerView) bindings[23]
            , (androidx.appcompat.widget.Toolbar) bindings[7]
            , (com.google.android.material.appbar.CollapsingToolbarLayout) bindings[3]
            , (android.widget.TextView) bindings[36]
            , (android.widget.TextView) bindings[15]
            , (android.widget.TextView) bindings[25]
            , (android.widget.TextView) bindings[35]
            , (android.widget.TextView) bindings[33]
            , (android.widget.TextView) bindings[20]
            , (android.widget.TextView) bindings[26]
            , (android.widget.TextView) bindings[30]
            , (android.widget.TextView) bindings[11]
            , (android.widget.TextView) bindings[13]
            , (android.widget.TextView) bindings[28]
            , (android.widget.TextView) bindings[1]
            , (android.widget.TextView) bindings[12]
            , (android.widget.TextView) bindings[18]
            , (android.widget.TextView) bindings[22]
            , (android.widget.TextView) bindings[34]
            , (android.widget.TextView) bindings[32]
            , (android.widget.TextView) bindings[14]
            , (com.iqonic.woobox.utils.ExpandableTextView) bindings[19]
            );
        this.htabMaincontent.setTag(null);
        this.tvName.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x2L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
        if (BR.model == variableId) {
            setModel((com.iqonic.woobox.models.ProductDataNew) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setModel(@Nullable com.iqonic.woobox.models.ProductDataNew Model) {
        this.mModel = Model;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        notifyPropertyChanged(BR.model);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        com.iqonic.woobox.models.ProductDataNew model = mModel;
        java.lang.String modelName = null;

        if ((dirtyFlags & 0x3L) != 0) {



                if (model != null) {
                    // read model.name
                    modelName = model.getName();
                }
        }
        // batch finished
        if ((dirtyFlags & 0x3L) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.tvName, modelName);
        }
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): model
        flag 1 (0x2L): null
    flag mapping end*/
    //end
}