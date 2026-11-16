package cn.edu.sicnu.myapplication4.data

import android.content.Context
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * 对象序列化存储工具：
 * 写文件使用 openFileOutput + ObjectOutputStream，读文件使用 openFileInput + ObjectInputStream，
 * 被读写的对象必须实现 Serializable。
 */
object ObjectFileStore {

    fun <T> read(context: Context, fileName: String, fallback: T): T {
        return try {
            context.openFileInput(fileName).use { fileInput ->
                ObjectInputStream(fileInput).use { input ->
                    @Suppress("UNCHECKED_CAST")
                    input.readObject() as T
                }
            }
        } catch (e: Exception) {
            // 文件不存在或内容损坏时使用默认值
            fallback
        }
    }

    fun write(context: Context, fileName: String, value: Any) {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { fileOutput ->
            ObjectOutputStream(fileOutput).use { output ->
                output.writeObject(value)
            }
        }
    }
}
