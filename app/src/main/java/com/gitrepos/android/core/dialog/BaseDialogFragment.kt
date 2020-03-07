package com.gitrepos.android.core.dialog


import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.gitrepos.android.R

/**
 * A simple [Fragment] subclass.
 */
open class BaseDialogFragment(private val dialogEventListener: DialogEventListener? = null) :
    DialogFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.setWindowAnimations(R.style.dialogAnimation_slide_in_right)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        dialogEventListener?.onCancelled(dialog)
    }

    /**
     * Callbacks to caller for dialog events
     */
    interface DialogEventListener {
        fun onPositiveButtonClicked(Object: Any)
        fun onNegativeButtonClicked(Object: Any)
        fun onCancelled(dialog: DialogInterface)
    }

}
