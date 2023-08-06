package com.teratail.q_chkzw30h5swa0i

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val fmodel = FirestoreModel()

    val model = ViewModelProvider(this).get(MainViewModel::class.java)
    model.setFirestoreModel(fmodel)
    model.setMyAddress("11:11:11:11:11:11") //本当なら自分の Bluetooth デバイスの MAC アドレス

    supportFragmentManager.beginTransaction()
      .replace(R.id.main, MainFragment())
      .commit()
  }
}