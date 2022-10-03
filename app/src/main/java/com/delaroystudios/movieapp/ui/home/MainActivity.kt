package com.delaroystudios.movieapp.ui.home

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.delaroystudios.movieapp.BuildConfig
import com.delaroystudios.movieapp.R
import com.delaroystudios.movieapp.data.model.Movie
import com.delaroystudios.movieapp.databinding.ActivityMainBinding
import com.delaroystudios.movieapp.ui.home.adapter.HomeAdapter
import com.delaroystudios.movieapp.ui.home.adapter.RecyclerViewHomeClickListener
import com.delaroystudios.movieapp.ui.home.viewmodel.HomeViewModel
import com.delaroystudios.movieapp.util.Resource
import com.delaroystudios.movieapp.util.contentView
import com.delaroystudios.movieapp.util.hasInternetConnection
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), RecyclerViewHomeClickListener {
    private val binding: ActivityMainBinding by contentView(R.layout.activity_main)
    private val homeViewModel: HomeViewModel by viewModels()
    private val homeAdapter: HomeAdapter by lazy { HomeAdapter(this, this@MainActivity) }

    private var hasMore: Boolean = false
    private var currentPage: MutableLiveData<Int> = MutableLiveData(1)
    private var totalPages: Int = 0
    private var perPageItem = 20

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.run {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//  set status text dark

            window.statusBarColor = ContextCompat.getColor(
                applicationContext,
                R.color.white
            )// set status background white

            recyclerView.apply {
                adapter = homeAdapter
            }

            ivPrevious.setOnClickListener {
                if (currentPage.value!! > 1) {
                    callAPI(currentPage.value!! - 1)
                }
            }

            ivNext.setOnClickListener {
                if (hasMore) {
                    callAPI(currentPage.value!! + 1)
                }
            }
        }

        callAPI(currentPage.value!!)
        observeUI()
    }

    private fun callAPI(pageNo: Int? = null) {
        if (hasInternetConnection()) {
            homeViewModel.fetchPopular(BuildConfig.MOVIE_DB_API_TOKEN, pageNo)
        } else
            Snackbar.make(binding.root, "No Internet Connection", Snackbar.LENGTH_LONG).show()
    }

    private fun observeUI() {
        homeViewModel.moviePopular.observe(this) {
            when (it) {
                is Resource.Success -> {
                    binding.progress.isVisible = false
                    binding.recyclerView.isVisible = true
                    val data = it.data!!.movies
                    hasMore = it.data.page < it.data.totalPages
                    totalPages = it.data.totalPages
                    currentPage.postValue(it.data.page)
                    homeAdapter.submitList(data!!)
                }
                is Resource.Error -> {
                    binding.progress.isVisible = true
                    binding.recyclerView.isVisible = false
                    it.message?.let { message ->
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    binding.progress.isVisible = true
                    binding.recyclerView.isVisible = false
                }
                else -> {}
            }

        }

        currentPage.observe(this) {
            it?.let { pageNo ->
                binding.tvPageNo.text = "$pageNo - $totalPages"

                binding.ivPrevious.isVisible = pageNo > 1
                binding.ivNext.isVisible = hasMore
            }
        }
    }

    override fun clickOnItem(data: Movie, card: View) {
        Snackbar.make(binding.root, data.title.toString(), Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
        homeViewModel.moviePopular.removeObservers(this)
        currentPage.removeObservers(this)
    }
}