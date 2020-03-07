package com.gitrepos.android.core.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.gitrepos.android.R


class NativeAlertDialogFragment private constructor(private val dialogEventListener: DialogEventListener) :
    BaseDialogFragment(dialogEventListener) {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it ->

            val builder = AlertDialog.Builder(it)

            arguments?.let {
                builder.setIcon(it.getInt(CustomDialogParams.imgResource))
                builder.setTitle(it.getString(CustomDialogParams.title))
                builder.setMessage(it.getString(CustomDialogParams.message))
                builder.setCancelable(it.getBoolean(CustomDialogParams.isCancellable))
                if (it.getBoolean(CustomDialogParams.isNegativeButton)) {
                    builder.setNegativeButton(
                        it.getString(CustomDialogParams.negativeButtonText)
                    ) { _, id ->
                        dialogEventListener.onNegativeButtonClicked(id)
                    }
                }

                if (it.getBoolean(CustomDialogParams.isPositiveButton)) {
                    builder.setPositiveButton(
                        it.getString(CustomDialogParams.positiveButtonText)
                    ) { _, id ->
                        dialogEventListener.onPositiveButtonClicked(id)
                    }
                }
            }

            dialog?.setOnKeyListener { dialogInterface, keyCode, _ ->
                if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
                    dialogEventListener.onCancelled(dialogInterface)
                    true
                } else {
                    false
                }
            }

            val dialog = builder.create()
            dialog.window?.setWindowAnimations(R.style.dialogAnimation_slide_in_right)
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {

        /**
         * Fragment tag
         */
        const val TAG = "NativeAlertDialogFragment"

        /**
         * Params to be passed for dialog construction
         */
        object CustomDialogParams {
            const val imgResource = "imgResource"
            const val isPositiveButton = "isPositiveButton"
            const val positiveButtonText = "positiveButtonText"
            const val isNegativeButton = "isNegativeButton"
            const val negativeButtonText = "negativeButtonText"
            const val isCancellable = "isCancellable"
            const val title = "title"
            const val message = "message"
        }

        /**
         * Creates instance of current dialog fragment
         */
        private fun newInstance(builder: NativeAlertDialogBuilder): NativeAlertDialogFragment {
            val args = Bundle()
            args.putInt(CustomDialogParams.imgResource, builder.imgResource)
            args.putBoolean(CustomDialogParams.isCancellable, builder.isCancellable)
            args.putBoolean(CustomDialogParams.isPositiveButton, builder.isPositiveButton)
            args.putBoolean(CustomDialogParams.isNegativeButton, builder.isNegativeButton)
            args.putString(CustomDialogParams.positiveButtonText, builder.positiveButtonText)
            args.putString(CustomDialogParams.negativeButtonText, builder.negativeButtonText)
            args.putString(CustomDialogParams.title, builder.title)
            args.putString(CustomDialogParams.message, builder.message)
            return NativeAlertDialogFragment(builder.dialogEventListener).apply { arguments = args }
        }

    }

    /**
     * Class to hold dialog builder data
     */
    data class NativeAlertDialogBuilder(
        val dialogEventListener: DialogEventListener,
        var imgResource: Int = 0,
        val tag: String = TAG,
        var message: String? = null,
        var title: String? = null,
        var positiveButtonText: String? = null,
        var negativeButtonText: String? = null,
        var isPositiveButton: Boolean = false,
        var isNegativeButton: Boolean = false,
        var isCancellable: Boolean = false
    ) {
        fun build() = newInstance(this)
    }

}