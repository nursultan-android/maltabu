package kz.maltabu.app.maltabukz.model


class QueryPaginationModel(var page: Int? = null, var order: String? = null, var category: Int? = null, var region: Int? = null, var city: Int? = null, var word: String? = null,
                           var price_to:Int? = null, var price_from:Int? = null, var exchange:Int? = null, var image_required: Boolean? = null, var favorite: Boolean? = null)