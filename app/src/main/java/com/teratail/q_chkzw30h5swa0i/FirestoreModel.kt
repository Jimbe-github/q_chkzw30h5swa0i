package com.teratail.q_chkzw30h5swa0i

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import java.util.Objects

class FirestoreModel {
  private val db: FirebaseFirestore

  init {
    db = FirebaseFirestore.getInstance()
  }

  fun getUserLiveData(address: String?): LiveData<User?> {
    val userLiveData = MutableLiveData<User?>()
    val registation = db.collection("users").document(address!!) //TODO: このリスナはいつ破棄するか
      .addSnapshotListener { snapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
        if (e != null) {
          Log.e(LOG_TAG, e.message, e)
          return@addSnapshotListener
        }
        if (snapshot != null) {
          userLiveData.postValue(snapshot.toObject(User::class.java))
        }
      }
    return userLiveData
  }

  fun getPostListLiveData(address: String?): LiveData<List<Post>> {
    val postListLiveData = MutableLiveData<List<Post>>(ArrayList())
    val registation = db.collection("users").document(address!!).collection("posts") //TODO: このリスナはいつ破棄するか
      .addSnapshotListener { snapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
        if (e != null) {
          Log.e(LOG_TAG, e.message, e)
          return@addSnapshotListener
        }
        if (snapshot != null) {
          val newPostList = mutableListOf<Post>().apply { //新しいオブジェクトを作ること
            addAll(postListLiveData.value ?: listOf())
          }
          for (dc in snapshot.documentChanges) {
            val type = dc.type
            Log.d(LOG_TAG, "getPostListLiveData: type=" + type + ", oldIndex=" + dc.oldIndex + ", newIndex=" + dc.newIndex + ", post=" + dc.document.toObject(Post::class.java))
            if (type == DocumentChange.Type.REMOVED || type == DocumentChange.Type.MODIFIED) {
              newPostList.removeAt(dc.oldIndex)
            }
            if (type == DocumentChange.Type.ADDED || type == DocumentChange.Type.MODIFIED) {
              newPostList.add(dc.newIndex, dc.document.toObject<Post>(Post::class.java))
            }
          }
          postListLiveData.postValue(newPostList)
        }
      }
    return postListLiveData
  }

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