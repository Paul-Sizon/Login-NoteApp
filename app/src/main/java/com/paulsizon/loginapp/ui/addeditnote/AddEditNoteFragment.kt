package com.paulsizon.loginapp.ui.addeditnote

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.paulsizon.loginapp.R
import com.paulsizon.loginapp.data.local.entities.Note
import com.paulsizon.loginapp.databinding.FragmentAddEditNoteBinding
import com.paulsizon.loginapp.other.Constants.DEFAULT_NOTE_COLOR
import com.paulsizon.loginapp.other.Constants.KEY_LOGGED_IN_EMAIL
import com.paulsizon.loginapp.other.Constants.NO_EMAIL
import com.paulsizon.loginapp.other.Status
import com.paulsizon.loginapp.ui.BaseFragment
import com.paulsizon.loginapp.ui.dialogs.ColorPickerDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

const val FRAGMENT_TAG = "AddEditNoteFragment"

@AndroidEntryPoint
class AddEditNoteFragment : BaseFragment() {

    private val viewModel: AddEditNoteViewModel by viewModels()
    private val args: AddEditNoteFragmentArgs by navArgs()
    private var curNote: Note? = null
    private var curNoteColor = DEFAULT_NOTE_COLOR

    @Inject
    lateinit var sharedPref: SharedPreferences

    lateinit var binding: FragmentAddEditNoteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddEditNoteBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.id.isNotEmpty()) {
            viewModel.getNoteById(args.id)
            subscribeObservers()
        }

        if (savedInstanceState != null) {
            val colorPickerDialog =
                parentFragmentManager.findFragmentByTag(FRAGMENT_TAG) as ColorPickerDialogFragment?
            colorPickerDialog?.setPositiveListener {
                changeColorViewNote(it)
            }
        }

        binding.viewNoteColor.setOnClickListener {
            ColorPickerDialogFragment().apply {
                setPositiveListener {
                    changeColorViewNote(it)
                }
            }.show(parentFragmentManager, FRAGMENT_TAG)
        }
    }

    private fun changeColorViewNote(colorString: String) {
        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.circle_shape, null)
        drawable?.let {
            val wrappedDrawable = DrawableCompat.wrap(it)
            val color = Color.parseColor("#${colorString}")
            DrawableCompat.setTint(wrappedDrawable, color)
            binding.viewNoteColor.background = wrappedDrawable
            curNoteColor = colorString
        }
    }

    private fun subscribeObservers() {
        viewModel.note.observe(viewLifecycleOwner, {
            it?.getContentIfnotHandled()?.let { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        val note = result.data!!
                        curNote = note
                        binding.etNoteTitle.setText(note.title)
                        binding.etNoteContent.setText(note.content)
                        changeColorViewNote(note.color)
                    }
                    Status.ERROR -> {
                        showSnackbar(result.message ?: "Note not found")
                    }
                    Status.LOADING -> {
//                        NO-OP
                    }
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        saveNote()
    }

    private fun saveNote() {
        val authEmail = sharedPref.getString(KEY_LOGGED_IN_EMAIL, NO_EMAIL) ?: NO_EMAIL

        val title = binding.etNoteTitle.text.toString()
        val content = binding.etNoteContent.text.toString()
        if (title.isEmpty() || content.isEmpty()) {
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