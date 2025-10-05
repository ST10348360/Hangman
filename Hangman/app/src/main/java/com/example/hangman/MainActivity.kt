package com.example.hangman

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var wordTextView: TextView
    private lateinit var livesTextView: TextView
    private lateinit var lettersGrid: GridLayout
    private lateinit var restartButton: Button

    private val words = listOf("LEMON", "CAT", "LOVE", "APPLE", "HANGMAN", "COMPUTER", "PROGRAMMER")
    private var selectedWord = ""
    private var guessedLetters = mutableSetOf<Char>()
    private lateinit var hangmanImage: ImageView

    private var lives = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wordTextView = findViewById(R.id.wordTextView)
        hangmanImage = findViewById(R.id.hangmanImage)
        livesTextView = findViewById(R.id.livesTextView)
        lettersGrid = findViewById(R.id.lettersGrid)
        restartButton = findViewById(R.id.restartButton)

        restartButton.setOnClickListener { startGame() }

        setupLetterButtons()
        startGame()
    }

    private fun startGame() {
        selectedWord = words.random()
        guessedLetters.clear()
        lives = 6
        updateWordDisplay()
        updateLivesDisplay()
        enableAllButtons()
    }

    private fun updateHangmanDrawing() {
        val stage = 6 - lives
        val imageName = "hangman_$stage"
        val resId = resources.getIdentifier(imageName, "drawable", packageName)
        hangmanImage.setImageResource(resId)
    }

    private fun setupLetterButtons() {
        lettersGrid.removeAllViews()
        val columnCount = lettersGrid.columnCount
        val screenWidth = resources.displayMetrics.widthPixels
        val buttonSize = screenWidth / columnCount - 16 // subtract margin

        for (letter in 'A'..'Z') {
            val button = Button(this).apply {
                text = letter.toString()
                layoutParams = GridLayout.LayoutParams().apply {
                    width = buttonSize
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    setMargins(4, 4, 4, 4)
                }
                setOnClickListener {
                    it.isEnabled = false
                    checkGuess(letter)
                }
            }
            lettersGrid.addView(button)
        }
    }

    private fun checkGuess(letter: Char) {
        if (selectedWord.contains(letter)) {
            guessedLetters.add(letter)
        } else {
            lives--
            updateHangmanDrawing()
        }
        updateWordDisplay()
        updateLivesDisplay()
        checkGameStatus()
    }

    private fun updateWordDisplay() {
        val display = selectedWord.map { if (guessedLetters.contains(it)) it else '_' }.joinToString(" ")
        wordTextView.text = display
    }

    private fun updateLivesDisplay() {
        livesTextView.text = "Lives: $lives"
    }

    private fun checkGameStatus() {
        if (selectedWord.all { guessedLetters.contains(it) }) {
            showEndDialog("You won! 🎉")
        } else if (lives <= 0) {
            showEndDialog("You lost! The word was $selectedWord.")
        }
    }

    private fun showEndDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage(message)
            .setPositiveButton("Restart") { _, _ -> startGame() }
            .setCancelable(false)
            .show()
    }

    private fun enableAllButtons() {
        for (i in 0 until lettersGrid.childCount) {
            lettersGrid.getChildAt(i).isEnabled = true
        }
    }
}