package com.pnj.gudang.item

data class Item(
    var name: String?=null,
    var quantity: Int?=null,
    var invoice: Int?=null,
    var warehouse: String?=null,
    var date: String?=null
)
