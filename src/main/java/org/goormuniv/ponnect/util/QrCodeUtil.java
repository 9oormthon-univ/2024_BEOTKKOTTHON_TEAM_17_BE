package org.goormuniv.ponnect.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
@Slf4j
public class QrCodeUtil {

    public ByteArrayOutputStream generateQr(Long memberId) throws WriterException, IOException {
        // QR 정보
        int width = 200;
        int height = 200;
        String url = "https://www.ponnect.kro.kr/api/auth/member/" + memberId;

        BitMatrix encode = new MultiFormatWriter()
                .encode(url, BarcodeFormat.QR_CODE, width, height);
        ByteArrayOutputStream out = null;
        //output Stream
        out = new ByteArrayOutputStream();

        //Bitmatrix, file.format, outputStream
        MatrixToImageWriter.writeToStream(encode, "PNG", out);
        return out;

    }

}
