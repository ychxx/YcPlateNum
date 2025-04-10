package com.yc.ycplatenum

import android.app.Activity
import android.os.Bundle
import com.yc.ycplatenum.databinding.PlateNumViewAcBinding


class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vb = PlateNumViewAcBinding.inflate(layoutInflater)
        setContentView(vb.root)
        vb.plateNumV.setPlateNum("A121212", "蓝")
    }
}
