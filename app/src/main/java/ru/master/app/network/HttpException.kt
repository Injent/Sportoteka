package ru.master.app.network

import io.ktor.http.HttpStatusCode

class HttpException(val statusCode: HttpStatusCode, altMessage: String? = null) : Exception() {
    override val message: String = altMessage
        ?: when (statusCode) {
            HttpStatusCode.Unauthorized -> "Неправильный логин или пароль"
            HttpStatusCode.Forbidden -> "Доступ запрещен"
            HttpStatusCode.NotFound -> "Ничего не найдено"
            in HttpStatusCode.InternalServerError..HttpStatusCode.GatewayTimeout -> {
                "Ошибка на сервере"
            }
            HttpStatusCode.RequestTimeout -> "Медленный интернет"
            else -> "${statusCode.value} ${statusCode.description}"
        }
}