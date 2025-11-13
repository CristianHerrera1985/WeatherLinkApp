package com.saintleo.weatherlinkapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saintleo.weatherlinkapp.R
import com.saintleo.weatherlinkapp.models.FavoriteCity

class FavoritesAdapter(
    private val cities: MutableList<FavoriteCity>,
    private val onDeleteClick: (FavoriteCity) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCityName: TextView = view.findViewById(R.id.tvCityName)
        val tvTemperature: TextView = view.findViewById(R.id.tvTemperature)
        val imgWeatherIcon: ImageView = view.findViewById(R.id.imgWeatherIcon)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_city, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val city = cities[position]
        holder.tvCityName.text = "${city.name}, ${city.country}"
        holder.tvTemperature.text = "${city.temperature}°C"

        // Cargar ícono de clima si existe
        val iconUrl = if (!city.iconUrl.isNullOrEmpty()) {
            "https:${city.iconUrl}"
        } else {
            null
        }

        Glide.with(holder.itemView.context)
            .load(iconUrl)
            .placeholder(R.drawable.placeholder) // ícono local por defecto
            .into(holder.imgWeatherIcon)

        // Acción de eliminar
        holder.btnDelete.setOnClickListener { onDeleteClick(city) }
    }

    override fun getItemCount() = cities.size
}
