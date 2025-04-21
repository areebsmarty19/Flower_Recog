package com.example.flower;
import android.content.Context;
import android.graphics.Bitmap;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.util.Arrays;
import java.util.List;

public class FlowerClassifier {
    private Interpreter interpreter;
    private final int inputSize = 224;
    private final List<String> labels = Arrays.asList("Daisy","Dandelion","Rose","sunflower", "tulip");

    public FlowerClassifier(Context context) throws IOException {
        MappedByteBuffer modelFile = FileUtil.loadMappedFile(context, "flower_model.tflite");
        interpreter = new Interpreter(modelFile);
    }
    public String classify(Bitmap bitmap, Context context) {
        bitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, false);

        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3);
        inputBuffer.order(ByteOrder.nativeOrder());

        int[] pixels = new int[inputSize * inputSize];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int pixel : pixels) {
            inputBuffer.putFloat(((pixel >> 16) & 0xFF) / 255.0f);
            inputBuffer.putFloat(((pixel >> 8) & 0xFF) / 255.0f);
            inputBuffer.putFloat((pixel & 0xFF) / 255.0f);
        }

        float[][] output = new float[1][labels.size()];
        interpreter.run(inputBuffer, output);

        int maxIndex = 0;
        for (int i = 1; i < labels.size(); i++) {
            if (output[0][i] > output[0][maxIndex]) {
                maxIndex = i;
            }
        }

        String label = labels.get(maxIndex);
        float confidence = output[0][maxIndex] * 100;

        android.widget.Toast.makeText(context, "Confidence: " + String.format("%.2f", confidence) + "%", android.widget.Toast.LENGTH_SHORT).show();

        return label;
    }
}
