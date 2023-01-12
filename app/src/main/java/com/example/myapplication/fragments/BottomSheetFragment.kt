package com.example.myapplication.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.R
import com.example.myapplication.data.sql_helper.FavoriteInfo
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet.view.*

class BottomSheetFragment: BottomSheetDialogFragment() {

    companion object {
        fun newInstance(): BottomSheetFragment {
            val args = Bundle()
            val fragment = BottomSheetFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet, container)
        setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
        view.bottom_sheet_button.setOnClickListener {
            this.dismiss()
        }
        return view
    }


}
