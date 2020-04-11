package kz.maltabu.app.maltabukz.utils.yandexAds

import android.util.Pair
import android.util.SparseArray

class NativeBlock {
    private val mPositionViewType: SparseArray<Int> = SparseArray()
    fun setData(dataList: List<Pair<Int, Any>>) {
        mPositionViewType.clear()
        for (i in dataList.indices) {
            val pair = dataList[i]
            mPositionViewType.put(i, pair.first)
        }
    }

    fun getItemType(position: Int): Int {
        val type = mPositionViewType[position]
        return type ?: AdapterHolder.BlockContentProvider.NONE_TYPE
    }

    val itemCount: Int
        get() = mPositionViewType.size()

}