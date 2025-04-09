package com.yc.ycplatenum

class YcPlateNumViewBean {
    /**
     * 车牌可选颜色
     */
//    var plateNumColorAllList: MutableList<@YcPlateNumData.YcPlateNumColorType String> = mutableListOf<@YcPlateNumData.YcPlateNumColorType String>()

    /**
     * 选中的车牌颜色
     */
    var plateNumColorSelect: @YcPlateNumData.YcPlateNumColorType String = YcPlateNumData.PLATE_NUM_TYPE_BLUE

    /**
     * Orc识别到的车牌颜色
     */
    var plateNumColorSelectOcr: @YcPlateNumData.YcPlateNumColorType String = YcPlateNumData.PLATE_NUM_TYPE_ERROR

    /**
     * 车牌选中的位置
     */
    var mPlateNumSelectIndex = 0
    var plateNumBtnList: MutableList<String> = mutableListOf<String>().apply {
        for (i in 0..7) {
            add("")
        }
    }
}
