package com.freedomrun.features.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.freedomrun.R
import com.freedomrun.data.models.DestinationModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainFragment : Fragment(), DestinationsRVAdapter.IListener {

    lateinit var recyclerView: RecyclerView
    lateinit var fab: FloatingActionButton
    lateinit var destinationsRVAdapter: DestinationsRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        recyclerView = root.findViewById(R.id.recycler_view_destinations)
        fab = root.findViewById(R.id.fab_add_destination)
        destinationsRVAdapter = DestinationsRVAdapter(
            mutableListOf(
                DestinationModel("Москва"),
                DestinationModel("Сочи"),
                DestinationModel("Владивосток")
            ),
            this
        )
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = destinationsRVAdapter
        }
    }

    override fun onDestinationClicked(destination: DestinationModel) {
        Log.i("MyLogs", "Destination clicked: ${destination.address}")
    }

}