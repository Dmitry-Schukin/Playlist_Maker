package com.practicum.playlist_maker.data.dto

import android.util.Log

enum class ResponseTypeEnum  {
    INFORMATIONAL,
    SUCCESS,
    REDIRECTION,
    CLIENT_ERROR,
    SERVER_ERROR,
    UNKNOWN,
    DEFAULT;

    companion object {
        fun setResponseType(codeNumber: Int): ResponseTypeEnum {
            val numStr = codeNumber.toString()
            val firstSymbol = numStr[0].digitToInt()
            when (firstSymbol) {
                1 -> Log.d("RequestCodeFromServer", "Информационный код $numStr")
                2 -> Log.d("RequestCodeFromServer", "Код успешного выполнения запроса $numStr")
                3 -> Log.d("RequestCodeFromServer", "Код перенаправления $numStr")
                4 -> Log.e("RequestCodeFromServer", "Код ошибки клиента $numStr")
                5 -> Log.e("RequestCodeFromServer", "Код ошибки сервера $numStr")
                else -> Log.e("RequestCodeFromServer", "Неизвестный код  $numStr ¯\\_(ツ)_/¯")
            }
            return when (firstSymbol) {
                1 -> INFORMATIONAL
                2 -> SUCCESS
                3 -> REDIRECTION
                4 -> CLIENT_ERROR
                5 -> SERVER_ERROR
                else -> UNKNOWN
            }
        }
    }
}