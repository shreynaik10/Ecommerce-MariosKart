package com.example.project1.interfaces

import com.example.project1.data.CartItem

interface CartInterface {
    public fun quantityChanged(item:CartItem,quantity:Int)

    public fun deleteItemFromCart(item:CartItem)

    public fun updateCartTotal()
}