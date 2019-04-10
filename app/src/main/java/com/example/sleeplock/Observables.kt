package com.example.sleeplock

import io.reactivex.subjects.BehaviorSubject

// User selected time gets sent to the view model's observer
val dialogTime: BehaviorSubject<Long> = BehaviorSubject.create<Long>()!!

// Index of item clicked observed by ViewModel
val itemIndex = BehaviorSubject.create<Int>()!!
