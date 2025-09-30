package com.example.helloworld.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.helloworld.R
import com.example.helloworld.adapters.AuthorsAdapter
import com.example.helloworld.model.Author

class AuthorsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_authors, container, false)

        val authorsList = listOf(
            Author("Алексей Петров", "Главный разработчик", R.drawable.scorpio),
            Author("Иван Николаев", "Разработчик игровой логики", R.drawable.virgo)
        )

        val listView: ListView = view.findViewById(R.id.authorsListView)
        val adapter = AuthorsAdapter(requireContext(), authorsList)
        listView.adapter = adapter

        // Обработка клика по элементу списка
        listView.setOnItemClickListener { parent, view, position, id ->
            val author = authorsList[position]
            Toast.makeText(requireContext(), "Выбран: ${author.name}", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}