package com.example.testeapi

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var etCarBrand: EditText
    private lateinit var tvCarStatus: TextView
    private lateinit var carAdapter: CarAdapter
    private lateinit var rvCars: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cars)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etCarBrand = findViewById(R.id.etCarBrand)
        tvCarStatus = findViewById(R.id.tvCarStatus)
        rvCars = findViewById(R.id.rvCars)

        // Configura o RecyclerView com botões de editar e deletar
        carAdapter = CarAdapter(
            cars = emptyList(),
            onEditClick = { car -> showEditDialog(car) },
            onDeleteClick = { car -> car.id?.let { deleteCar(it) } }
        )
        rvCars.layoutManager = LinearLayoutManager(this)
        rvCars.adapter = carAdapter

        fetchCars()

        findViewById<Button>(R.id.btnSaveCar).setOnClickListener {
            val brand = etCarBrand.text.toString()
            if (brand.isNotEmpty()) {
                saveCar(Car(brand = brand))
            } else {
                Toast.makeText(this, "Digite uma marca", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchCars() {
        RetrofitClient.api.getCars().enqueue(object : Callback<List<Car>> {
            override fun onResponse(call: Call<List<Car>>, response: Response<List<Car>>) {
                if (response.isSuccessful) {
                    response.body()?.let { carAdapter.updateCars(it) }
                }
            }
            override fun onFailure(call: Call<List<Car>>, t: Throwable) {
                tvCarStatus.text = "Erro ao listar: ${t.message}"
            }
        })
    }

    private fun saveCar(car: Car) {
        RetrofitClient.api.createCar(car).enqueue(object : Callback<Car> {
            override fun onResponse(call: Call<Car>, response: Response<Car>) {
                if (response.isSuccessful) {
                    tvCarStatus.text = "Carro salvo!"
                    etCarBrand.text.clear()
                    fetchCars()
                }
            }
            override fun onFailure(call: Call<Car>, t: Throwable) {
                tvCarStatus.text = "Falha: ${t.message}"
            }
        })
    }

    private fun deleteCar(id: Int) {
        RetrofitClient.api.deleteCar(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    tvCarStatus.text = "Deletado!"
                    fetchCars()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                tvCarStatus.text = "Erro ao deletar: ${t.message}"
            }
        })
    }

    private fun updateCar(id: Int, car: Car) {
        RetrofitClient.api.updateCar(id, car).enqueue(object : Callback<Car> {
            override fun onResponse(call: Call<Car>, response: Response<Car>) {
                if (response.isSuccessful) {
                    tvCarStatus.text = "Editado com sucesso!"
                    fetchCars()
                }
            }
            override fun onFailure(call: Call<Car>, t: Throwable) {
                tvCarStatus.text = "Erro ao editar: ${t.message}"
            }
        })
    }

    private fun showEditDialog(car: Car) {
        val input = EditText(this)
        input.setText(car.brand)
        
        AlertDialog.Builder(this)
            .setTitle("Editar Marca")
            .setView(input)
            .setPositiveButton("Salvar") { _, _ ->
                val newBrand = input.text.toString()
                if (newBrand.isNotEmpty()) {
                    car.id?.let { updateCar(it, Car(id = it, brand = newBrand)) }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}