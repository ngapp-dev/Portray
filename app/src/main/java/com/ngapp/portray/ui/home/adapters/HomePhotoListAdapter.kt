package com.ngapp.portray.ui.home.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.ngapp.portray.R
import com.ngapp.portray.data.db.models.photo.Photo
import com.ngapp.portray.databinding.ItemHomePhotoBinding
import com.ngapp.portray.databinding.ItemPhotoFirstBinding
import com.ngapp.portray.utils.DoubleClickListener
import com.ngapp.portray.utils.load


class HomePhotoListAdapter(
    private val onItemSingleClick: (photo: Photo, materialCardView: MaterialCardView) -> Unit,
    private val onItemDoubleClick: (photo: Photo) -> Unit,
    private val onLikeImageViewClick: (photo: Photo) -> Unit
) :
    PagingDataAdapter<Photo, RecyclerView.ViewHolder>(HomeAdapterDiffUtilCallBack()) {

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> FIRST_PHOTO
            else -> LIST_PHOTO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            FIRST_PHOTO -> {
                FirstPhotoViewHolder(
                    ItemPhotoFirstBinding.inflate(inflater, parent, false),
                    onItemSingleClick,
                    onItemDoubleClick,
                    onLikeImageViewClick
                )
            }
            else -> HomeViewHolder(
                ItemHomePhotoBinding.inflate(inflater, parent, false),
                onItemSingleClick,
                onItemDoubleClick,
                onLikeImageViewClick
            )
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let {
            when (position) {
                0 -> {
                    val layoutParams = holder.itemView.layoutParams
                    if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                        layoutParams.isFullSpan = true
                    }
                    (holder as FirstPhotoViewHolder).bind(it)
                }
                else -> (holder as HomeViewHolder).bind(it)
            }
        }
    }

    class HomeViewHolder(
        private val binding: ItemHomePhotoBinding,
        private val onItemSingleClick: (photo: Photo, materialCardView: MaterialCardView) -> Unit,
        private val onItemDoubleClick: (photo: Photo) -> Unit,
        private val onLikeImageViewClick: (photo: Photo) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Photo) {
            binding.root.apply {

//                binding.photoImageView.load(photo.urls.regular ?: "", R.drawable.broken_image)

                Glide.with(itemView)
                    .load(photo.urls.regular)
                    .thumbnail(0.05f)
//                    .placeholder(R.drawable.broken_image)
                    .into(binding.photoImageView)

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

    class FirstPhotoViewHolder(
        private val binding: ItemPhotoFirstBinding,
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

    fun <T> diffList(
        oldList: List<T>,
        newList: List<T>,
        sameItem: (a: T, b: T) -> Boolean
    ): DiffUtil.DiffResult {
        val callback: DiffUtil.Callback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldList.size
            }

            override fun getNewListSize(): Int {
                return newList.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return sameItem(oldList[oldItemPosition], newList[newItemPosition])
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }
        }

        return DiffUtil.calculateDiff(callback)
    }

    class HomeAdapterDiffUtilCallBack : DiffUtil.ItemCallback<Photo>() {

        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: Photo, newItem: Photo): Any? {
            return super.getChangePayload(oldItem, newItem)
        }

    }

    companion object {
        private const val FIRST_PHOTO = 1
        private const val LIST_PHOTO = 2
    }
}







