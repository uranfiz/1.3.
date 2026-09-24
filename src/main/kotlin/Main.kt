const val SECONDS_IN_MINUTE = 60
const val SECONDS_IN_HOUR = 60 * 60
const val SECONDS_IN_DAY = 24 * 60 * 60

fun main() {
    // задача 1. проверка agoToText
    println("--- Задача 1: agoToText ---")
    agoToText(30)                 // был только что
    agoToText(60)                 // был только что (граница)
    agoToText(61)                 // был 1 минуту назад
    agoToText(3 * 60)             // был 3 минуты назад
    agoToText(5 * 60)             // был 5 минут назад
    agoToText(11 * 60)            // был 11 минут назад
    agoToText(21 * 60)            // был 21 минуту назад
    agoToText(59 * 60)            // был 59 минут назад
    agoToText(SECONDS_IN_HOUR + 1) //был 1 час назад
    agoToText(3 * SECONDS_IN_HOUR) // был 3 часа назад
    agoToText(5 * SECONDS_IN_HOUR) // был 5 часов назад
    agoToText(21 * SECONDS_IN_HOUR) // был 21 час назад
    agoToText(SECONDS_IN_DAY)      // был вчера
    agoToText(2 * SECONDS_IN_DAY)  // был позавчера
    agoToText(3 * SECONDS_IN_DAY + 1) // был давно

    //  pадача 2. gроверка transferCommission 
    println("\n--- Задача 2: transferCommission ---")
    // мир - комиссии нет
    println(transferCommission("Мир", 0.0, 100_000.0))
    // mastercard, лимит не превышен - комиссии нет
    println(transferCommission("Mastercard", 0.0, 50_000.0))
    // mastercard, 150 000 при прошлых 0 - комиссия 470
    println(transferCommission("Mastercard", 0.0, 150_000.0))
    // mastercard, лимит уже превышен ранее - 0.6% + 20
    println(transferCommission("Mastercard", 80_000.0, 10_000.0))
    // visa - 0.75%, минимум 35
    println(transferCommission("Visa", 0.0, 1000.0))   // 35.0
    println(transferCommission("Visa", 0.0, 10_000.0)) // 75.0
    // превышение суточного лимита
    println(transferCommission("Мир", 0.0, 200_000.0))
    // превышение месячного лимита
    println(transferCommission("Мир", 500_000.0, 150_000.0))
}

// задача 1. когда собеседник был онлайн

fun agoToText(secondsAgo: Int) {
    val text = when {
        secondsAgo in 0..SECONDS_IN_MINUTE -> "был(а) только что"
        secondsAgo in (SECONDS_IN_MINUTE + 1)..SECONDS_IN_HOUR ->
            "был(а) ${minutesToText(secondsAgo / SECONDS_IN_MINUTE)} назад"
        secondsAgo in (SECONDS_IN_HOUR + 1)..SECONDS_IN_DAY ->
            "был(а) ${hoursToText(secondsAgo / SECONDS_IN_HOUR)} назад"
        secondsAgo in (SECONDS_IN_DAY + 1)..(2 * SECONDS_IN_DAY) -> "был(а) вчера"
        secondsAgo in (2 * SECONDS_IN_DAY + 1)..(3 * SECONDS_IN_DAY) -> "был(а) позавчера"
        else -> "был(а) давно"
    }
    println(text)
}

/** возвращает правильную форму для количества минут, например "3 минуты" */
fun minutesToText(minutes: Int): String {
    val form = when {
        minutes % 100 in 11..14 -> "минут"
        minutes % 10 == 1 -> "минуту"
        minutes % 10 in 2..4 -> "минуты"
        else -> "минут"
    }
    return "$minutes $form"
}

/** возвращает правильную форму для количества часов, например "3 часа" */
fun hoursToText(hours: Int): String {
    val form = when {
        hours % 100 in 11..14 -> "часов"
        hours % 10 == 1 -> "час"
        hours % 10 in 2..4 -> "часа"
        else -> "часов"
    }
    return "$hours $form"
}

// задача 2. разная комиссия

const val DAILY_LIMIT = 150_000.0
const val MONTHLY_LIMIT = 600_000.0
const val MASTERCARD_FREE_LIMIT = 75_000.0
const val MASTERCARD_PERCENT = 0.006
const val MASTERCARD_FIXED = 20.0
const val VISA_PERCENT = 0.0075
const val VISA_MIN_COMMISSION = 35.0

fun transferCommission(
    cardType: String = "Мир",
    previousMonthly: Double = 0.0,
    amount: Double,
): String {
    if (amount > DAILY_LIMIT) {
        return "операция заблокирована: превышен суточный лимит ($DAILY_LIMIT руб.)"
    }

    // проверка месячного лимита
    if (previousMonthly + amount > MONTHLY_LIMIT) {
        return "операция заблокирована: превышен месячный лимит ($MONTHLY_LIMIT руб.)"
    }

    val commission = when (cardType.lowercase()) {
        "mastercard" -> {
            val remainingFree = (MASTERCARD_FREE_LIMIT - previousMonthly).coerceAtLeast(0.0)
            if (remainingFree >= amount) {
                // весь перевод попадает в бесплатный лимит
                0.0
            } else {
                // облагаемая часть - всё, что выходит за бесплатный лимит
                val taxable = amount - remainingFree
                taxable * MASTERCARD_PERCENT + MASTERCARD_FIXED
            }
        }
        "visa" -> {
            val percentCommission = amount * VISA_PERCENT
            percentCommission.coerceAtLeast(VISA_MIN_COMMISSION)
        }
        "мир", "mir" -> 0.0
        else -> return "неизвестный тип карты: $cardType"
    }
    return "комиссия: %.2f руб.".format(commission)
}
