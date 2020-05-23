package com.iqonic.woobox.databinding;
import com.iqonic.woobox.R;
import com.iqonic.woobox.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ItemWishlistBindingImpl extends ItemWishlistBinding implements com.iqonic.woobox.generated.callback.OnClickListener.Listener {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.ivProduct, 5);
        sViewsWithIds.put(R.id.llProductColor, 6);
        sViewsWithIds.put(R.id.llProductSize, 7);
        sViewsWithIds.put(R.id.tvSize, 8);
        sViewsWithIds.put(R.id.tvProductPrice, 9);
        sViewsWithIds.put(R.id.tvProductActualPrice, 10);
        sViewsWithIds.put(R.id.llButton, 11);
    }
    // views
    // variables
    @Nullable
    private final android.view.View.OnClickListener mCallback5;
    @Nullable
    private final android.view.View.OnClickListener mCallback6;
    @Nullable
    private final android.view.View.OnClickListener mCallback4;
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ItemWishlistBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 12, sIncludes, sViewsWithIds));
    }
    private ItemWishlistBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.LinearLayout) bindings[4]
            , (android.widget.ImageView) bindings[5]
            , (android.widget.LinearLayout) bindings[0]
            , (android.widget.LinearLayout) bindings[11]
            , (android.widget.LinearLayout) bindings[3]
            , (android.widget.LinearLayout) bindings[6]
            , (android.widget.LinearLayout) bindings[7]
            , (android.widget.TextView) bindings[10]
            , (android.widget.TextView) bindings[1]
            , (android.widget.TextView) bindings[9]
            , (android.widget.TextView) bindings[2]
            , (android.widget.TextView) bindings[8]
            );
        this.btnRemove.setTag(null);
        this.listProductRaw.setTag(null);
        this.llMoveToCart.setTag(null);
        this.tvProductName.setTag(null);
        this.tvProductRate.setTag(null);
        setRootTag(root);
        // listeners
        mCallback5 = new com.iqonic.woobox.generated.callback.OnClickListener(this, 2);
        mCallback6 = new com.iqonic.woobox.generated.callback.OnClickListener(this, 3);
        mCallback4 = new com.iqonic.woobox.generated.callback.OnClickListener(this, 1);
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x4L;
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
            setModel((com.iqonic.woobox.models.WishListData) variable);
        }
        else if (BR.callback == variableId) {
            setCallback((com.iqonic.woobox.base.BaseRecyclerAdapter.OnClickListener) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setModel(@Nullable com.iqonic.woobox.models.WishListData Model) {
        this.mModel = Model;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        notifyPropertyChanged(BR.model);
        super.requestRebind();
    }
    public void setCallback(@Nullable com.iqonic.woobox.base.BaseRecyclerAdapter.OnClickListener Callback) {
        this.mCallback = Callback;
        synchronized(this) {
            mDirtyFlags |= 0x2L;
        }
        notifyPropertyChanged(BR.callback);
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
        com.iqonic.woobox.models.WishListData model = mModel;
        com.iqonic.woobox.models.Meta modelMeta = null;
        java.lang.String modelName = null;
        com.iqonic.woobox.base.BaseRecyclerAdapter.OnClickListener<?> callback = mCallback;
        java.lang.String modelMetaAverageRating = null;

        if ((dirtyFlags & 0x5L) != 0) {



                if (model != null) {
                    // read model.meta
                    modelMeta = model.getMeta();
                    // read model.name
                    modelName = model.getName();
                }


                if (modelMeta != null) {
                    // read model.meta.average_rating
                    modelMetaAverageRating = modelMeta.getAverage_rating();
                }
        }
        // batch finished
        if ((dirtyFlags & 0x4L) != 0) {
            // api target 1

            this.btnRemove.setOnClickListener(mCallback6);
            this.listProductRaw.setOnClickListener(mCallback4);
            this.llMoveToCart.setOnClickListener(mCallback5);
        }
        if ((dirtyFlags & 0x5L) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.tvProductName, modelName);
            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.tvProductRate, modelMetaAverageRating);
        }
    }
    // Listener Stub Implementations
    // callback impls
    public final void _internalCallbackOnClick(int sourceId , android.view.View callbackArg_0) {
        switch(sourceId) {
            case 2: {
                // localize variables for thread safety
                // model
                com.iqonic.woobox.models.WishListData model = mModel;
                // callback
                com.iqonic.woobox.base.BaseRecyclerAdapter.OnClickListener callback = mCallback;
                // callback != null
                boolean callbackJavaLangObjectNull = false;



                callbackJavaLangObjectNull = (callback) != (null);
                if (callbackJavaLangObjectNull) {




                    callback.onClick(callbackArg_0, model);
                }
                break;
            }
            case 3: {
                // localize variables for thread safety
                // model
                com.iqonic.woobox.models.WishListData model = mModel;
                // callback
                com.iqonic.woobox.base.BaseRecyclerAdapter.OnClickListener callback = mCallback;
                // callback != null
                boolean callbackJavaLangObjectNull = false;



                callbackJavaLangObjectNull = (callback) != (null);
                if (callbackJavaLangObjectNull) {




                    callback.onClick(callbackArg_0, model);
                }
                break;
            }
            case 1: {
                // localize variables for thread safety
                // model
                com.iqonic.woobox.models.WishListData model = mModel;
                // callback
                com.iqonic.woobox.base.BaseRecyclerAdapter.OnClickListener callback = mCallback;
                // callback != null
                boolean callbackJavaLangObjectNull = false;



                callbackJavaLangObjectNull = (callback) != (null);
                if (callbackJavaLangObjectNull) {




                    callback.onClick(callbackArg_0, model);
                }
                break;
            }
        }
    }
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): model
        flag 1 (0x2L): callback
        flag 2 (0x3L): null
    flag mapping end*/
    //end
}