package com.ngapp.portray.ui.profile.view_pager.user_collections.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ngapp.portray.R
import com.ngapp.portray.data.db.models.collection.Collection
import com.ngapp.portray.databinding.ItemCollectionBinding
import com.ngapp.portray.utils.load
import com.ngapp.portray.utils.setText


class UserCollectionListAdapter(
    private val onItemSingleClick: (collection: Collection) -> Unit
) :
    PagingDataAdapter<Collection, UserCollectionListAdapter.CollectionListViewHolder>(
        CollectionListAdapterDiffUtilCallBack()
    ) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CollectionListViewHolder(
            ItemCollectionBinding.inflate(inflater, parent, false),
            onItemSingleClick
        )
    }


    override fun onBindViewHolder(
        holder: CollectionListViewHolder,
        position: Int
    ) {
        val currentItem = getItem(position)
        currentItem?.let { holder.bind(it) }
    }

    inner class CollectionListViewHolder(
        private val binding: ItemCollectionBinding,
        private val onItemSingleClick: (collection: Collection) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(collection: Collection) {
            binding.root.apply {
                binding.photoImageView.load(collection.coverPhoto?.urls?.regular ?: "", R.drawable.broken_image)
                binding.avatarImageView.load(
                    collection.user?.profileImage?.small ?: "",
                    R.drawable.ic_user_placeholder
                )
                binding.collectionTitleTextView.text = collection.title
                setText(
                    context,
                    binding.collectionSizeTextView,
                    R.string.number_photos,
                    collection.totalPhotos.toString()
                )
                binding.authorTextView.text = collection.user?.name
                binding.accountTextView.text = "@${collection.user?.username}"

                binding.root.setOnClickListener {
                    onItemSingleClick(collection)
                }
            }
        }
    }

    class CollectionListAdapterDiffUtilCallBack : DiffUtil.ItemCallback<Collection>() {
        override fun areItemsTheSame(oldItem: Collection, newItem: Collection): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Collection, newItem: Collection): Boolean {
            return oldItem == newItem
        }
    }
}