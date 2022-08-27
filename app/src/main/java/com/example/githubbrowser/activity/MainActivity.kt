package com.example.githubbrowser.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.githubbrowser.R
import com.example.githubbrowser.backend.RetrofitClient
import com.example.githubbrowser.model.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity: AppCompatActivity(), TextView.OnEditorActionListener {

    //Defining the vars
    private var editTextUsername: EditText? = null
    private var progressBar: ProgressBar? = null
    private var imm: InputMethodManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initializing the view
        editTextUsername = findViewById(R.id.editTextUsername)
        progressBar = findViewById(R.id.progressBar)

        //initialize the imm
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        //Setting editText listener
        editTextUsername?.setOnEditorActionListener(this)
    }

    override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent): Boolean{
        return if (p0 == editTextUsername){

            //Grt the username from editorText
            val username: String? = editTextUsername?.text?.trim().toString()

            //Check editText is not empty
            if (username != null){
                if (username.isEmpty() || username.isBlank()){

                    //Show error message
                    editTextUsername?.error = getString(R.string.username_can_not_be_empty)

                }else{

                    //Hide the Keyboard
                    imm?.hideSoftInputFromWindow(editTextUsername?.windowToken, 0)

                    //Show progress icon
                    progressBar?.visibility = View.VISIBLE

                    //Make request
                    getRepositoriesForUsername(username)
                }
            }
            true
        }else{
            return false
        }
    }
    private fun getRepositoriesForUsername(username: String){
        RetrofitClient
            .instance
            .getRepositoriesForUser(username)
            .enqueue(object: Callback<List<Repository>> {

                override fun onFailure(call: Call<List<Repository>>, t: Throwable){

                    //Log error
                    Log.e(TAG, "Error getting repos: ${t.localizedMessage}")

                    //Show an error message to the user
                    Toast.makeText(this@MainActivity, R.string.unable_to_get_repo, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<List<Repository>>,
                    response: Response<List<Repository>>
                ){
                    //Hide progress bar
                    progressBar?.visibility = View.INVISIBLE

                    if (response.isSuccessful){

                        //Getting the list of repositories
                        val listOfRepos = response.body() as? ArrayList<Repository>

                        //Passing data to the next activity
                        listOfRepos?.let{
                            // Create and intent
                            val intent = Intent(this@MainActivity, RepositoryActivity::class.java)

                            //Pass data to the activity
                            intent.putParcelableArrayListExtra(RepositoryActivity.KEY_REPOSITORY_DATA, it)

                            //Start the new activity
                            startActivity(intent)
                        }

                    }else{

                        //Create message to the user based on the outcome
                        val message = when(response.code()){
                            500 -> R.string.internal_server_error
                            401 -> R.string.unauthorized
                            403 -> R.string.forbidden
                            404 -> R.string.user_not_found
                            else -> R.string.try_another_user
                        }

                        //Show message to the user
                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                        Log.e(TAG, getString(message))
                    }
                }

            })
    }
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
    }