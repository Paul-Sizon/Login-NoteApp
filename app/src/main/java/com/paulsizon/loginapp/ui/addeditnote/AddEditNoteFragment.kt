package com.paulsizon.loginapp.ui.addeditnote

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.paulsizon.loginapp.R
import com.paulsizon.loginapp.data.local.entities.Note
import com.paulsizon.loginapp.other.Constants.DEFAULT_NOTE_COLOR
import com.paulsizon.loginapp.other.Constants.KEY_LOGGED_IN_EMAIL
import com.paulsizon.loginapp.other.Constants.NO_EMAIL
import com.paulsizon.loginapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_edit_note.*
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddEditNoteFragment : BaseFragment(R.layout.fragment_add_edit_note){

    private val viewModel: AddEditNoteViewModel by viewModels()
    private val args: AddEditNoteFragmentArgs by navArgs()
    private var curNote: Note? = null
    private var curNoteColor = DEFAULT_NOTE_COLOR

    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(args.id.isNotEmpty()){
            viewModel.getNoteById(args.id)
            subscribeObservers()
        }
    }

    private fun subscribeObservers(){

    }

    override fun onPause() {
        super.onPause()
        saveNote()
    }

    private fun saveNote(){
        val authEmail = sharedPref.getString(KEY_LOGGED_IN_EMAIL, NO_EMAIL)?: NO_EMAIL

        val title = etNoteTitle.text.toString()
        val content = etNoteContent.text.toString()
        if (title.isEmpty() || content.isEmpty()){
            return
        }
        val date = System.currentTimeMillis()
        val color = curNoteColor
        val id = curNote?.id ?: UUID.randomUUID().toString()
        val owners = curNote?.owners ?: listOf(authEmail)
        val note = Note(title, content, date, owners, color, id = id)

        viewModel.insertNote(note)
    }
}