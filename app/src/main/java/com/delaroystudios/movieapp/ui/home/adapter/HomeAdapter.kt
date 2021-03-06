package com.delaroystudios.movieapp.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delaroystudios.movieapp.data.model.Movie
import com.delaroystudios.movieapp.databinding.MovieCardBinding

class HomeAdapter(
    val context: Context,
    private val recyclerViewHome: RecyclerViewHomeClickListener
) : RecyclerView.Adapter<ViewHolder>() {
    private lateinit var recyclerView: RecyclerView
    lateinit var mActivity: AppCompatActivity

    private val TAG: String = "AppDebug"

    var items: MutableList<Movie> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            MovieCardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        item.let {
            holder.apply {
                bind(item, isLinearLayoutManager())
                itemView.tag = item
            }
        }

        holder.itemView.setOnClickListener {
            recyclerViewHome.clickOnItem(
                item,
                holder.itemView
            )
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(itemList: List<Movie>) {
        items = itemList as MutableList<Movie>
        notifyDataSetChanged()
    }

    fun appendList(itemList: List<Movie>) {
        items.addAll(itemList)
        notifyDataSetChanged()
    }

    private fun isLinearLayoutManager() = recyclerView.layoutManager is LinearLayoutManager
}

class ViewHolder(private val binding: MovieCardBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Movie, isLinearLayoutManager: Boolean) {
        binding.apply {
            doc = item
            executePendingBindings()
        }
    }
}

interface RecyclerViewHomeClickListener {
    fun clickOnItem(data: Movie, card: View)
}
