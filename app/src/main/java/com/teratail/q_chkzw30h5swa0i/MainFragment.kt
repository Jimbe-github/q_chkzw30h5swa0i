package com.teratail.q_chkzw30h5swa0i

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teratail.q_chkzw30h5swa0i.FirestoreModel.Post

class MainFragment : Fragment(R.layout.fragment_main) {
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val model = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    val adapter = Adapter()
    val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
    recyclerView.adapter = adapter
    model.postList?.observe(viewLifecycleOwner) { postList: List<Post?> -> adapter.submitList(postList) }
  }

  private class Adapter : ListAdapter<Post, Adapter.ViewHolder>(DIFF_CALLBACK) {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      private val body: TextView
      private val likeCount: TextView

      init {
        body = itemView.findViewById(R.id.body)
        likeCount = itemView.findViewById(R.id.likeCount)
      }

      fun bind(post: Post?) {
        body.text = post!!.body
        likeCount.text = "" + post.likeCount
      }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.post_row, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.bind(getItem(position))
    }

    companion object {
      private val DIFF_CALLBACK: DiffUtil.ItemCallback<Post> = object : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldPost: Post, newPost: Post): Boolean {
          return oldPost.id == newPost.id
        }

        override fun areContentsTheSame(oldPost: Post, newPost: Post): Boolean {
          return oldPost == newPost
        }
      }
    }
  }

  companion object {
    private const val LOG_TAG = "MainFragment"
  }
}