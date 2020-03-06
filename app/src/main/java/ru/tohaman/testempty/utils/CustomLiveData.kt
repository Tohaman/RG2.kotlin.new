package ru.tohaman.testempty.utils

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

class NonNullMutableLiveData<T>(private val defaultValue: T) :
    MutableLiveData<T>() {
    override fun getValue(): T {
        return super.getValue() ?: defaultValue
    }
}

class NonNullMediatorLiveData<T>(private val defaultValue: T) :
    MediatorLiveData<T>() {
    override fun getValue(): T {
        return super.getValue() ?: defaultValue
    }
}

fun <T> T.toMutableLiveData(): MutableLiveData<T> {
    return MutableLiveData<T>()
        .also { it.postValue( this) }
}


fun <T> MutableLiveData<List<T>>.add(item: T) {
    val updatedItems = this.value as ArrayList
    updatedItems.add(item)
    this.postValue(updatedItems)
}

fun <T> MutableLiveData<List<T>>.remove(item: T) {
    val updatedItems = this.value as ArrayList
    updatedItems.remove(item)
    this.postValue(updatedItems)
}

