package com.example.quizapp_boukenze

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.quizapp_boukenze.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            binding.ivProfile.setImageURI(result.data?.data)
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            @Suppress("DEPRECATION")
            val photo = result.data?.extras?.get("data") as? Bitmap
            binding.ivProfile.setImageBitmap(photo)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SystemBarHelper.apply(binding.rootView, 0, 0, 0)

        binding.etSchoolName.isFocusable = false
        binding.etSchoolName.isClickable = true
        binding.etSchoolName.setOnClickListener { showSchoolSelectionDialog() }

        binding.fabAddPhoto.setOnClickListener { showImageSourceDialog() }
        binding.bRegister.setOnClickListener { registerUser() }
    }

    private fun showSchoolSelectionDialog() {
        val schools = CampusValidator.allCampusNames()
        AlertDialog.Builder(this)
            .setTitle("Selectionner votre campus")
            .setItems(schools) { _, which ->
                binding.etSchoolName.setText(schools[which])
            }
            .show()
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Prendre une photo", "Galerie")
        AlertDialog.Builder(this)
            .setTitle("Image de profil")
            .setItems(options) { _, which ->
                if (which == 0) {
                    cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                } else {
                    galleryLauncher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
                }
            }
            .show()
    }

    private fun registerUser() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etMail.text.toString().trim()
        val school = binding.etSchoolName.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etPassword1.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || school.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val user = User(null, email, name, school)
                AuthHelper.register(email, password, user)
                Toast.makeText(this@Register, "Inscription reussie", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@Register, MainActivity::class.java))
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@Register, "Erreur: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
