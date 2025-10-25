package com.example.helloworld.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.helloworld.R
import com.example.helloworld.adapters.RecordsAdapter
import com.example.helloworld.data.AppDatabase
import com.example.helloworld.repository.ScoreRepository
import com.example.helloworld.viewmodels.ScoreViewModel
import kotlinx.coroutines.launch

class RecordsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecordsAdapter
    private lateinit var viewModel: ScoreViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_records, container, false)
        initViews(view)
        setupViewModel()
        loadRecords()
        return view
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.rvRecords)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = RecordsAdapter()
        recyclerView.adapter = adapter
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = ScoreRepository(database.scoreRecordDao())
        viewModel = ScoreViewModel(repository)
    }

    private fun loadRecords() {
        lifecycleScope.launch {
            viewModel.top10Records.collect { records ->
                adapter.submitList(records)
            }
        }
    }
}