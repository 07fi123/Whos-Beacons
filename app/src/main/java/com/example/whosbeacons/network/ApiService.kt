package com.example.whosbeacons.network

import com.example.whosbeacons.model.CellTowerResponse
import retrofit2.http.GET
import retrofit2.http.Query


    interface ApiService {
        @GET("cell/getInArea")
        suspend fun getCellTowers(
            @Query("key") apiKey: String,
            @Query("BBOX") bbox: String,
            @Query("format") format: String = "json"
        ): CellTowerResponse
    }
