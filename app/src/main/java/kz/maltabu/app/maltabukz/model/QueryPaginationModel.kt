package kz.maltabu.app.maltabukz.model


class QueryPaginationModel(val page: Int? = null, val order: String? = null, val category: Int? = null, val region: Int? = null, val word: String? = null, val price_to:Int? = null,
                           val price_from:Int? = null, val exchange:Int? = null, val image_required: Boolean? = null, val favorite: Boolean? = null){}