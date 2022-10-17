package com.ngapp.portray.ui.collections.collection_detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.card.MaterialCardView
import com.ngapp.portray.R
import com.ngapp.portray.data.db.models.collection.CollectionWithPhoto
import com.ngapp.portray.data.db.models.photo.Photo
import com.ngapp.portray.databinding.ItemHomePhotoBinding
import com.ngapp.portray.databinding.ItemPhotoFirstBinding
import com.ngapp.portray.utils.DoubleClickListener
import com.ngapp.portray.utils.load


class CollectionPhotoListAdapter(
    private val onItemSingleClick: (photo: Photo, materialCardView: MaterialCardView) -> Unit,
    private val onItemDoubleClick: (photo: Photo) -> Unit,
    private val onLikeImageViewClick: (photo: Photo) -> Unit
) :
    PagingDataAdapter<Photo, CollectionPhotoListAdapter.HomeViewHolder>(HomeAdapterDiffUtilCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return HomeViewHolder(
            ItemHomePhotoBinding.inflate(inflater, parent, false),
            onItemSingleClick,
            onItemDoubleClick,
            onLikeImageViewClick
        )
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let { holder.bind(it) }
    }


    private var onItemClickListener: ((CollectionWithPhoto) -> Unit)? = null

    fun setOnItemClickListener(listener: (CollectionWithPhoto) -> Unit) {
        onItemClickListener = listener
    }

    class HomeViewHolder(
        private val binding: ItemHomePhotoBinding,
        private val onItemSingleClick: (photo: Photo, materialCardView: MaterialCardView) -> Unit,
        private val onItemDoubleClick: (photo: Photo) -> Unit,
        private val onLikeImageViewClick: (photo: Photo) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Photo) {
            binding.root.apply {

                binding.photoImageView.load(photo.urls.regular ?: "", R.drawable.broken_image)
                binding.avatarImageView.load(
                    photo.user.profileImage?.small ?: "",
                    R.drawable.ic_user_placeholder
                )
                binding.authorTextView.text = photo.user.name
                binding.accountTextView.text = "@${photo.user.username}"
                binding.likesTextView.text = photo.likes

                if (photo.likedByUser) {
                    binding.likeImageView.setImageResource(R.drawable.ic_like_filled)
                } else {
                    binding.likeImageView.setImageResource(R.drawable.ic_like_outline)
                }

                binding.likeImageView.setOnClickListener {
                    onLikeImageViewClick(photo)
                }

                binding.root.setOnClickListener(object : DoubleClickListener() {
                    override fun onSingleClick() {
                        binding.card.transitionName = photo.blurHash
                        onItemSingleClick(photo, binding.card)
                    }

                    override fun onDoubleClick() {
                        onItemDoubleClick(photo)
                    }
                })
            }
        }
    }

    object HomeAdapterDiffUtilCallBack : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }
    }
}







