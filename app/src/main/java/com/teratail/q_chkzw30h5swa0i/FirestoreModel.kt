package com.teratail.q_chkzw30h5swa0i

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.util.Objects

class FirestoreModel {
  private val db: FirebaseFirestore

  init {
    db = FirebaseFirestore.getInstance()
  }

  fun getUserLiveData(address: String): LiveData<User> =
    DocumentLiveData(db.collection("users").document(address), User::class.java)

  fun getUserListLiveData(): LiveData<List<User>> =
    QueryLiveData(db.collection("users"), User::class.java)

  fun getPostListLiveData(address: String): LiveData<List<Post>> =
    QueryLiveData(db.collection("users").document(address).collection("posts"), Post::class.java)

  class Post {
    @DocumentId
    var id: String? = null
    var body: String? = null
    var likeCount = 0

    override fun equals(o: Any?): Boolean {
      if (this === o) return true
      if (o == null || javaClass != o.javaClass) return false
      val post = o as Post
      return likeCount == post.likeCount && id == post.id && body == post.body
    }

    override fun hashCode(): Int {
      return Objects.hash(id, body, likeCount)
    }

    override fun toString(): String {
      return "Post{id='$id', body='$body', likeCount=$likeCount}"
    }
  }

  class User {
    @DocumentId
    var address: String? = null
    var introduction: String? = null
    var name: String? = null
  }

  companion object {
    private const val LOG_TAG = "FiestoreModel"
  }
}

class DocumentLiveData<T>(val reference: DocumentReference, val clazz: Class<T>) : MutableLiveData<T>() {
  var registration: ListenerRegistration? = null
  val listener = object: EventListener<DocumentSnapshot> {
    override fun onEvent(snapshot: DocumentSnapshot?, e: FirebaseFirestoreException?) {
      if (e != null) {
        Log.e(LOG_TAG, e.message, e)
      }
      if (snapshot != null) {
        postValue(snapshot.toObject(clazz))
      }
    }
  }

  override fun onActive() {
    super.onActive()
    registration = reference.addSnapshotListener(listener)
  }

  override fun onInactive() {
    super.onInactive()
    registration?.remove()
    registration = null
  }

  companion object {
    private const val LOG_TAG = "DocumentLiveData"
  }
}

class QueryLiveData<T>(val reference: Query, val clazz: Class<T>) : MutableLiveData<List<T>>() {
  var registration: ListenerRegistration? = null
  val listener = object: EventListener<QuerySnapshot> {
    override fun onEvent(snapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
      if (e != null) {
        Log.e(LOG_TAG, e.message, e)
      }
      if (snapshot != null) {
        val newList = mutableListOf<T>().apply {  //新しいオブジェクトを作ること
          addAll(value ?: listOf())
        }
        for (dc in snapshot.documentChanges) {
          val type = dc.type
          Log.d(LOG_TAG, "onEvent: type=" + type + ", oldIndex=" + dc.oldIndex + ", newIndex=" + dc.newIndex + ", post=" + dc.document.toObject(FirestoreModel.Post::class.java))
          if (type == DocumentChange.Type.REMOVED || type == DocumentChange.Type.MODIFIED) {
            newList.removeAt(dc.oldIndex)
          }
          if (type == DocumentChange.Type.ADDED || type == DocumentChange.Type.MODIFIED) {
            newList.add(dc.newIndex, dc.document.toObject(clazz))
          }
        }
        postValue(newList)
      }
    }
  }

  override fun onActive() {
    super.onActive()
    registration = reference.addSnapshotListener(listener)
  }

  override fun onInactive() {
    super.onInactive()
    registration?.remove()
    registration = null
  }

  companion object {
    private const val LOG_TAG = "QueryLiveData"
  }
}
