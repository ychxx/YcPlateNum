package com.yc.ycplatenum

import androidx.annotation.StringDef

object YcPlateNumData {
    /**
     * 车牌数字键盘：删除键
     */
    const val PLATE_NUM_KEYBOARD_TYPE_DEL = "plate_num_keyboard_type_del"

    /**
     * 车牌数字键盘:确定键
     */
    const val PLATE_NUM_KEYBOARD_TYPE_OK = "PLATE_NUM_KEYBOARD_TYPE_OK"

    /**
     * 车牌数字键盘：数据键
     */
    const val PLATE_NUM_KEYBOARD_TYPE_DATA = "PLATE_NUM_KEYBOARD_TYPE_DATA"

    /**
     * 车牌数字键盘 按键功能类型
     */
    @Target(AnnotationTarget.TYPE)
    @StringDef(
        PLATE_NUM_KEYBOARD_TYPE_DEL,
        PLATE_NUM_KEYBOARD_TYPE_OK,
        PLATE_NUM_KEYBOARD_TYPE_DATA
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class PlateNumKeyboardType

    /**
     *  车牌-无牌车辆
     */
    const val PLATE_NUM_TYPE_NONE = "无牌车辆"
    /**
     *  车牌-异常
     */
    const val PLATE_NUM_TYPE_ERROR = "0"
    /**
     *  车牌-蓝色
     */
    const val PLATE_NUM_TYPE_BLUE = "蓝"

    /**
     *  车牌-黄色
     */
    const val PLATE_NUM_TYPE_YELLOW = "黄"

    /**
     *  车牌-绿色
     */
    const val PLATE_NUM_TYPE_GREEN = "绿"

    /**
     *  车牌-白色
     */
    const val PLATE_NUM_TYPE_WHITE = "白"

    /**
     *  车牌-黑色
     */
    const val PLATE_NUM_TYPE_BLACK = "黑"

    /**
     *  车牌-黄绿色
     */
    const val PLATE_NUM_TYPE_YELLOW_GREEN = "黄绿"

    /**
     * 排除不能修改的车牌颜色(黑色，白色)
     */
    val PLATE_NUM_COLOR_LIMIT_LIST = listOf(
        PLATE_NUM_TYPE_BLUE,
        PLATE_NUM_TYPE_YELLOW,
        PLATE_NUM_TYPE_GREEN,
        PLATE_NUM_TYPE_YELLOW_GREEN
    )

    /**
     * 所有车牌颜色
     */
    val PLATE_NUM_COLOR_LIST = PLATE_NUM_COLOR_LIMIT_LIST + listOf(
        PLATE_NUM_TYPE_WHITE,
        PLATE_NUM_TYPE_BLACK,
    )

    /**
     * 车牌颜色类型
     */
    @Target(AnnotationTarget.TYPE, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
    @StringDef(
        PLATE_NUM_TYPE_ERROR,
        PLATE_NUM_TYPE_BLUE,
        PLATE_NUM_TYPE_YELLOW,
        PLATE_NUM_TYPE_GREEN,
        PLATE_NUM_TYPE_WHITE,
        PLATE_NUM_TYPE_BLACK,
        PLATE_NUM_TYPE_YELLOW_GREEN
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class YcPlateNumColorType

    /**
     * 根据车牌号推测车牌颜色
     */
    fun getPlateNumColorMaybe(ocrColor: @YcPlateNumColorType String?, plateNum: String): List<@YcPlateNumColorType String>? {
        var plateNumSpecial = ""
        if (plateNum.length < 7) {
            return null
        } else if (plateNum.length == 7) {
            plateNumSpecial = plateNum[6].toString()
        } else if (plateNum.length == 8) {
            if (PLATE_NUM_CONTENT_SPECIAL.contains(plateNum[7])) {
                plateNumSpecial = plateNum[7].toString()
            } else if (PLATE_NUM_CONTENT_SPECIAL.contains(plateNum[6])) {
                plateNumSpecial = plateNum[6].toString()
            }
        }

        when (plateNumSpecial) {
            "港", "澳", "领" -> {
                return if (hasChangeColor(ocrColor, PLATE_NUM_TYPE_BLACK)) {
                    listOf(PLATE_NUM_TYPE_BLACK)
                } else {
                    null
                }
            }

            "学" -> {
                return listOf(PLATE_NUM_TYPE_YELLOW)
            }

            "警" -> {
                return if (hasChangeColor(ocrColor, PLATE_NUM_TYPE_WHITE)) {
                    listOf(PLATE_NUM_TYPE_WHITE)
                } else {
                    null
                }
            }

            else -> {
                if (plateNum.length == 7) {
                    //燃油车
                    return listOf(PLATE_NUM_TYPE_BLUE, PLATE_NUM_TYPE_YELLOW)
                } else {
                    //新能源车
                    val plateNum7 = plateNum[7]
                    return if (plateNum7.toString().matches("[a-zA-Z]".toRegex())) {
                        listOf(PLATE_NUM_TYPE_YELLOW_GREEN, PLATE_NUM_TYPE_GREEN)
                    } else {
                        listOf(PLATE_NUM_TYPE_GREEN, PLATE_NUM_TYPE_YELLOW_GREEN)
                    }
                }
            }
        }
    }

    fun getYcPlateNumFrameBg(plateNumIndex: Int, plateNumColorType: @YcPlateNumColorType String, hasSelect: Boolean): Int {
        return when (plateNumColorType) {
            PLATE_NUM_TYPE_BLUE -> {
                if (hasSelect) {
                    R.drawable.yc_plate_num_frame_blue_select
                } else {
                    R.drawable.yc_plate_num_frame_blue_select_un
                }
            }

            PLATE_NUM_TYPE_YELLOW -> {
                if (hasSelect) {
                    R.drawable.yc_plate_num_frame_yellow_select
                } else {
                    R.drawable.yc_plate_num_frame_yellow_select_un
                }
            }

            PLATE_NUM_TYPE_GREEN -> {
                if (hasSelect) {
                    R.drawable.yc_plate_num_frame_green_select
                } else {
                    R.drawable.yc_plate_num_frame_green_select_un
                }
            }

            PLATE_NUM_TYPE_WHITE -> {
                if (hasSelect) {
                    R.drawable.yc_plate_num_frame_white_select
                } else {
                    R.drawable.yc_plate_num_frame_white_select_un
                }
            }

            PLATE_NUM_TYPE_BLACK -> {
                if (hasSelect) {
                    R.drawable.yc_plate_num_frame_black_select
                } else {
                    R.drawable.yc_plate_num_frame_black_select_un
                }
            }

            PLATE_NUM_TYPE_YELLOW_GREEN -> {
                if (plateNumIndex <= 1) {
                    if (hasSelect) {
                        R.drawable.yc_plate_num_frame_yellow_select
                    } else {
                        R.drawable.yc_plate_num_frame_yellow_select_un
                    }
                } else {
                    if (hasSelect) {
                        R.drawable.yc_plate_num_frame_green_select
                    } else {
                        R.drawable.yc_plate_num_frame_green_select_un
                    }
                }
            }

            else -> {
                if (hasSelect) {
                    R.drawable.yc_plate_num_frame_blue_select
                } else {
                    R.drawable.yc_plate_num_frame_blue_select_un
                }
            }
        }
    }

    const val PLATE_NUM_CONTENT_PROVINCE = "粤京冀沪津晋蒙辽吉黑苏浙皖闽赣鲁豫鄂湘桂琼渝川贵云藏陕甘青宁新"
    const val PLATE_NUM_CONTENT_SPECIAL = "港澳学领警"
    const val PLATE_NUM_CONTENT_LETTER = "ABCDEFGHJKLMNPQRSTUVWXYZ"
    const val PLATE_NUM_CONTENT_DIGIT = "0123456789"

    /**
     *  检测ocr识别的车牌颜色
     *  业务：ocr识别的车牌颜色不能修改为白色或黑色
     *  需求：识别成为白色或黑色牌照的车辆可手动修改为其他颜色，但识别成其他颜色车牌不能手动修改为白色或黑色，以免泊管员误操作导致订单金额与实际不符；
     */
    fun hasChangeColor(orcColor: @YcPlateNumColorType String?, changeColor: @YcPlateNumColorType String): Boolean {
        return when (orcColor) {
            PLATE_NUM_TYPE_WHITE, PLATE_NUM_TYPE_BLACK -> {
                true
            }

            else -> {
                when (changeColor) {
                    PLATE_NUM_TYPE_WHITE, PLATE_NUM_TYPE_BLACK -> {
                        false
                    }

                    else -> {
                        true
                    }
                }
            }
        }
    }

    fun ocrToPlateNumTypeColor(ocrColor: String): @YcPlateNumColorType String {
        return when (ocrColor) {
            "blue" -> {
                PLATE_NUM_TYPE_BLUE
            }

            "yellow" -> {
                PLATE_NUM_TYPE_YELLOW
            }

            "green" -> {
                PLATE_NUM_TYPE_GREEN
            }

            "white" -> {
                PLATE_NUM_TYPE_WHITE
            }

            "black" -> {
                PLATE_NUM_TYPE_BLACK
            }

            "yellow_green" -> {
                PLATE_NUM_TYPE_YELLOW_GREEN
            }

            else -> {
                PLATE_NUM_TYPE_ERROR
            }
        }
    }
    /**
     * 获取车牌颜色
     */
    fun getPlateNumTypeColorBg(plateNumType: @YcPlateNumColorType String?, call: (bgColorLeft: Int, textColor: Int, bgColorRight: Int) -> Unit) {
        when (plateNumType) {
            PLATE_NUM_TYPE_ERROR -> {
                call.invoke(R.drawable.yc_plate_num_bg_gray_left, R.color.yc_plate_num_text_gray, R.drawable.yc_plate_num_bg_gray_right)
            }

            PLATE_NUM_TYPE_BLUE -> {
                call.invoke(R.drawable.yc_plate_num_bg_blue_left, R.color.yc_plate_num_text_blue, R.drawable.yc_plate_num_bg_blue_right)
            }


            PLATE_NUM_TYPE_YELLOW -> {
                call.invoke(R.drawable.yc_plate_num_bg_yellow_left, R.color.yc_plate_num_text_yellow, R.drawable.yc_plate_num_bg_yellow_right)
            }

            PLATE_NUM_TYPE_GREEN -> {
                call.invoke(R.drawable.yc_plate_num_bg_green_left, R.color.yc_plate_num_text_green, R.drawable.yc_plate_num_bg_green_right)
            }

            PLATE_NUM_TYPE_WHITE -> {
                call.invoke(R.drawable.yc_plate_num_bg_white_left, R.color.yc_plate_num_text_white, R.drawable.yc_plate_num_bg_white_right)
            }


            PLATE_NUM_TYPE_BLACK -> {
                call.invoke(R.drawable.yc_plate_num_bg_black_left, R.color.yc_plate_num_text_black, R.drawable.yc_plate_num_bg_black_right)
            }

            PLATE_NUM_TYPE_YELLOW_GREEN -> {
                call.invoke(R.drawable.yc_plate_num_bg_yellow_green_left, R.color.yc_plate_num_text_yellow_green, R.drawable.yc_plate_num_bg_yellow_green_right)
            }

            PLATE_NUM_TYPE_NONE -> {
                call.invoke(R.drawable.yc_plate_num_bg_none_left, R.color.yc_plate_num_text_none, R.drawable.yc_plate_num_bg_none_right)
            }

            else -> {
                call.invoke(R.drawable.yc_plate_num_bg_blue_left, R.color.yc_plate_num_text_blue, R.drawable.yc_plate_num_bg_blue_right)
            }
        }
    }
}