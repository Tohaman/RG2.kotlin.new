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
