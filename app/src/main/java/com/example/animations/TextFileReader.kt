package com.example.animations

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class TextFileReader {
    companion object{
        fun readRawTextFile(context:Context, resId:Int):String{
            val inputStream: InputStream = context.resources.openRawResource(resId)

            val inputReader = InputStreamReader(inputStream)
            val buffReader = BufferedReader(inputReader)
            var line: String?
            val text = StringBuilder()

            try {
                while (buffReader.readLine().also { line = it } != null) {
                    text.append(line)
                    text.append('\n')
                }
            } catch (e: IOException) {
                return "io exception bitch"
            }
            return text.toString()

        }
    }
}