package io.xconn.wampproto.interoperability

import io.xconn.wampproto.messages.Message
import io.xconn.wampproto.serializers.JSONSerializer
import io.xconn.wampproto.serializers.Serializer
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.BufferedReader
import java.io.InputStreamReader

fun runCommand(command: String): String {
    val processBuilder = ProcessBuilder(*command.split(" ").toTypedArray())
    val process = processBuilder.start()

    // Capture the output
    val output =
        BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
            reader.readText().trim()
        }

    // Wait for the process to finish and check the exit code
    val exitCode = process.waitFor()
    assertTrue(exitCode == 0) { "Command execution failed with exit code $exitCode" }

    return output
}

fun runCommandAndDeserialize(serializer: Serializer, command: String): Message {
    val output = runCommand(command)
    val outputBytes = output.hexStringToByteArray()

    if (serializer is JSONSerializer) {
        return serializer.deserialize(String(outputBytes))
    }

    return serializer.deserialize(outputBytes)
}

// Extension function to convert hex string to byte array
fun String.hexStringToByteArray(): ByteArray {
    return this.chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}
