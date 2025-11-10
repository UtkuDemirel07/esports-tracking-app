package com.example.projectgroup.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.projectgroup.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup

class FilterBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.bottomsheet_filter, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val group = view.findViewById<ChipGroup>(R.id.chipGroup)
        val btnApply = view.findViewById<MaterialButton>(R.id.btnApply)

        btnApply.setOnClickListener {
            val selected = when (group.checkedChipId) {
                R.id.chip_lol -> "LoL"
                R.id.chip_valorant -> "Valorant"
                R.id.chip_cs2 -> "CS2"
                else -> "ALL"
            }
            setFragmentResult("filter_request", bundleOf("filter" to selected))
            dismiss()
        }
    }
}
