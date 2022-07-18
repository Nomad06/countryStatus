package utils

object TemperatureUtils {

  def convertToFahrenheit(celsius: Float): Float = {
    //°F = (9/5 × °C) + 32
    (9f/5 * celsius) + 32
  }

  def convertToCelsius(fahrenheit: Float): Float = {
    //C = (5/9) x (F-32) conversion from Fahrenheit to Celsius
    5f/9 * (fahrenheit - 32)
  }


}
