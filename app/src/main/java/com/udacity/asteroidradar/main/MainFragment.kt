package com.udacity.asteroidradar.main

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.asteroidRecycler.adapter = AdapterMain(AdapterMain.AsteroidClickListener{
            viewModel.onAsteroidClicked(it)
        })

        viewModel.detailAsteroidNavigator.observe(viewLifecycleOwner,Observer{
            if(null !=it){
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.onDisplayAsteroidCompleted()
            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    private val asteroidAdapter = AdapterMain(AdapterMain.AsteroidClickListener { asteroid ->
        viewModel.onAsteroidClicked(asteroid)
    })

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.asteroidList.observe(viewLifecycleOwner, Observer<List<Asteroid>> { asteroid ->
            asteroid.apply {
                asteroidAdapter.submitList(this)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.onChangeFilter(
            when (item.itemId) {
                R.id.show_rent_menu -> {
                    Constants.AsteroidsFilter.TODAY
                }
                R.id.show_all_menu -> {
                    Constants.AsteroidsFilter.WEEK
                }
                else -> {
                    Constants.AsteroidsFilter.ALL
                }
            }
        )
        return true
    }
}
