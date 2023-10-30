package com.asepssp.storyapps.ui.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.asepssp.storyapps.data.remote.response.ListStoryItem
import com.asepssp.storyapps.databinding.ItemListStoryBinding
import com.asepssp.storyapps.ui.detail.DetailActivity
import com.bumptech.glide.Glide

class MainAdapter :
    PagingDataAdapter<ListStoryItem, MainAdapter.ListStoryHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListStoryHolder {
        val binding =
            ItemListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListStoryHolder(binding)
    }

    override fun onBindViewHolder(holder: ListStoryHolder, position: Int) {
        val user = getItem(position)
        if (user != null) {
            holder.bind(user)
        }
    }

    inner class ListStoryHolder(private val binding: ItemListStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(itemName: ListStoryItem) {
            binding.tvItemName.text = itemName.name
            Glide.with(binding.root)
                .load(itemName.photoUrl)
                .into(binding.ivItemPhoto)
            binding.root.setOnClickListener {
                val intentDetail = Intent(binding.root.context, DetailActivity::class.java)
                intentDetail.putExtra(DetailActivity.ID, itemName.id)
                intentDetail.putExtra(DetailActivity.NAME, itemName.name)
                intentDetail.putExtra(DetailActivity.DESCRIPTION, itemName.description)
                intentDetail.putExtra(DetailActivity.PICTURE, itemName.photoUrl)
                binding.root.context.startActivity(intentDetail)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}