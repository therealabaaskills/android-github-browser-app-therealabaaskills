package com.example.githubbrowser.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubbrowser.R
import com.example.githubbrowser.adapter.RepositoryRecyclerViewAdapter
import com.example.githubbrowser.model.Repository

class RepositoryActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository)

        //Receiving the list of repositories
        val listOfRepos = intent?.getParcelableArrayListExtra<Repository>(KEY_REPOSITORY_DATA)

        //Ensuring the list of repos in not null
        listOfRepos?.let {
            //Show the total number of the repo items
            val numberOfRepositories = getString(R.string.number_of_repos, it.size)

            //Show the list of repo list
            findViewById<TextView>(R.id.textViewNumberOfRepos)?.text = numberOfRepositories

            //Show the list of repositories
            showRepos(it)
        }
    }

    private fun showRepos(listOfRepositories: ArrayList<Repository>) {

        //Initialize the adapter
        val recyclerViewAdapter = RepositoryRecyclerViewAdapter(listOfRepositories)

        //Initialize the recycler view
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        //Configure the recycler view
        recyclerView?.apply{
            adapter = recyclerViewAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }
    }
    companion object{
        const val KEY_REPOSITORY_DATA = "keyRepositoryData"
    }
}
