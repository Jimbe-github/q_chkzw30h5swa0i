package com.teratail.q_chkzw30h5swa0i

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.teratail.q_chkzw30h5swa0i.FirestoreModel.Post

class MainViewModel : ViewModel() {
  private var fmodel: FirestoreModel? = null
  fun setFirestoreModel(fmodel: FirestoreModel?) {
    this.fmodel = fmodel
  }

  private val myAddressLiveData = MutableLiveData<String?>()
  fun setMyAddress(address: String?) {
    myAddressLiveData.value = address
  }

  val postList: LiveData<List<Post>>?
    get() =
      if (myAddressLiveData.value == null || fmodel == null) null //この辺はテキトウ
      else Transformations.switchMap(myAddressLiveData) { address: String? -> fmodel!!.getPostListLiveData(address) }

  companion object {
    private const val LOG_TAG = "MainViewModel"
  }
}