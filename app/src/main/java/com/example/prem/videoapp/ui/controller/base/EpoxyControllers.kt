package com.example.prem.videoapp.ui.controller.base

import androidx.recyclerview.widget.*
import com.airbnb.epoxy.*

abstract class EpoxyController4<T, U, V, W> : Typed4EpoxyController<T, U, V, W>() {
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) = recyclerView.recycleChildrenOnDetach()
}

abstract class EpoxyController3<T, U, V> : Typed3EpoxyController<T, U, V>() {
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) = recyclerView.recycleChildrenOnDetach()
}

abstract class EpoxyController2<T, U> : Typed2EpoxyController<T, U>() {
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) = recyclerView.recycleChildrenOnDetach()
}

abstract class EpoxyController<T> : TypedEpoxyController<T>() {
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) = recyclerView.recycleChildrenOnDetach()
}

private fun RecyclerView.recycleChildrenOnDetach() {
    when (layoutManager) {
        is LinearLayoutManager -> (layoutManager as LinearLayoutManager).recycleChildrenOnDetach = true
        is GridLayoutManager -> (layoutManager as GridLayoutManager).recycleChildrenOnDetach = true
    }
}